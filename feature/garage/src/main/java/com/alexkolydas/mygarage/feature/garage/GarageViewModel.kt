package com.alexkolydas.mygarage.feature.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexkolydas.mygarage.core.data.model.ServiceRecord
import com.alexkolydas.mygarage.core.data.model.Vehicle
import com.alexkolydas.mygarage.core.data.repository.GarageRepository
import com.alexkolydas.mygarage.core.ui.model.VehicleUi
import com.alexkolydas.mygarage.core.ui.util.computeUrgency
import com.alexkolydas.mygarage.core.ui.util.formatKm
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GarageViewModel(
    private val repository: GarageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GarageContract.State())
    val state: StateFlow<GarageContract.State> = _state.asStateFlow()

    private val _effects = Channel<GarageContract.Effect>(Channel.BUFFERED)
    val effects: Flow<GarageContract.Effect> = _effects.receiveAsFlow()

    private val allServicesFlow = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.allVehicles.first().let { if (it.isEmpty()) seedDefaults() }
        }
        viewModelScope.launch {
            combine(repository.allVehicles, allServicesFlow) { vehicles, services ->
                vehicles.map { v ->
                    val vSvcs  = services.filter { it.vehicleId == v.id }.sortedByDescending { it.km }
                    val latest = vSvcs.firstOrNull()
                    v.toUi(latest)
                }
            }.collect { list ->
                _state.update { it.copy(vehicles = list, isLoading = false) }
            }
        }
    }

    fun onIntent(intent: GarageContract.Intent) {
        when (intent) {
            GarageContract.Intent.ToggleFab ->
                _state.update { it.copy(fabExpanded = !it.fabExpanded) }

            GarageContract.Intent.CloseFab ->
                _state.update { it.copy(fabExpanded = false) }

            GarageContract.Intent.OpenVehicleSheet ->
                _state.update { it.copy(
                    activeSheet = GarageContract.Sheet.NewVehicle,
                    fabExpanded = false,
                    vehicleForm = GarageContract.VehicleForm(),
                    editingVehicleId = null,
                ) }

            is GarageContract.Intent.OpenServiceSheet ->
                _state.update { it.copy(
                    activeSheet = GarageContract.Sheet.AddService,
                    fabExpanded = false,
                    serviceForm = GarageContract.ServiceForm(
                        vehicleId = intent.preselectedId ?: it.vehicles.firstOrNull()?.id,
                    ),
                ) }

            GarageContract.Intent.CloseSheet ->
                _state.update { it.copy(
                    activeSheet = GarageContract.Sheet.None,
                    editingVehicleId = null,
                ) }

            // Vehicle form
            is GarageContract.Intent.UpdateVehicleModel ->
                _state.update { it.copy(vehicleForm = it.vehicleForm.copy(model = intent.value)) }
            is GarageContract.Intent.UpdateVehicleName ->
                _state.update { it.copy(vehicleForm = it.vehicleForm.copy(name = intent.value)) }
            is GarageContract.Intent.UpdateVehicleYear ->
                _state.update { it.copy(vehicleForm = it.vehicleForm.copy(year = intent.value)) }
            is GarageContract.Intent.UpdateVehicleKm ->
                _state.update { it.copy(vehicleForm = it.vehicleForm.copy(km = intent.value)) }

            GarageContract.Intent.SaveVehicle ->
                if (_state.value.editingVehicleId != null) updateVehicle() else addVehicle()

            // Service form
            is GarageContract.Intent.SelectServiceVehicle ->
                _state.update { it.copy(serviceForm = it.serviceForm.copy(vehicleId = intent.id)) }
            is GarageContract.Intent.UpdateServiceKm ->
                _state.update { it.copy(serviceForm = it.serviceForm.copy(km = intent.value)) }
            is GarageContract.Intent.UpdateServiceWork ->
                _state.update { it.copy(serviceForm = it.serviceForm.copy(work = intent.value)) }
            is GarageContract.Intent.UpdateServiceNextKm ->
                _state.update { it.copy(serviceForm = it.serviceForm.copy(nextKm = intent.value)) }
            GarageContract.Intent.SaveService -> saveService()

            is GarageContract.Intent.VehicleClicked -> viewModelScope.launch {
                _effects.send(GarageContract.Effect.NavigateToDetail(intent.vehicleId))
            }
            is GarageContract.Intent.UpdateVehiclePhoto -> viewModelScope.launch {
                repository.updateVehiclePhoto(intent.vehicleId, intent.uri)
            }

            // Context menu
            is GarageContract.Intent.LongPressVehicle ->
                _state.update { it.copy(
                    contextMenuVehicle = intent.vehicle,
                    fabExpanded = false,
                ) }

            GarageContract.Intent.DismissContextMenu ->
                _state.update { it.copy(contextMenuVehicle = null, showDeleteConfirm = false) }

            GarageContract.Intent.RequestDeleteVehicle ->
                _state.update { it.copy(showDeleteConfirm = true) }

            GarageContract.Intent.CancelDeleteVehicle ->
                _state.update { it.copy(showDeleteConfirm = false) }

            GarageContract.Intent.ConfirmDeleteVehicle -> {
                val id = _state.value.contextMenuVehicle?.id ?: return
                viewModelScope.launch { repository.deleteVehicle(id) }
                _state.update { it.copy(contextMenuVehicle = null, showDeleteConfirm = false) }
            }

            is GarageContract.Intent.OpenEditVehicleSheet ->
                _state.update { it.copy(
                    contextMenuVehicle = null,
                    activeSheet = GarageContract.Sheet.EditVehicle,
                    editingVehicleId = intent.vehicle.id,
                    vehicleForm = GarageContract.VehicleForm(
                        model = intent.vehicle.model,
                        name  = intent.vehicle.name,
                        year  = intent.vehicle.year,
                        km    = intent.vehicle.km.toString(),
                    ),
                ) }
        }
    }

    private fun addVehicle() {
        val form = _state.value.vehicleForm
        if (!form.isValid) return
        viewModelScope.launch {
            repository.addVehicle(
                Vehicle(
                    name  = form.name.trim().ifBlank { "Vehicle" },
                    model = form.model.trim(),
                    year  = form.year.trim().ifBlank { "—" },
                    km    = form.km.toIntOrNull() ?: 0,
                )
            )
        }
        _state.update { it.copy(activeSheet = GarageContract.Sheet.None) }
    }

    private fun updateVehicle() {
        val form    = _state.value.vehicleForm
        val editId  = _state.value.editingVehicleId ?: return
        if (!form.isValid) return
        val existing = _state.value.vehicles.find { it.id == editId } ?: return
        viewModelScope.launch {
            repository.updateVehicle(
                Vehicle(
                    id       = editId,
                    name     = form.name.trim().ifBlank { "Vehicle" },
                    model    = form.model.trim(),
                    year     = form.year.trim().ifBlank { "—" },
                    km       = form.km.toIntOrNull() ?: existing.km,
                    photoUri = existing.photoUri,
                )
            )
        }
        _state.update { it.copy(activeSheet = GarageContract.Sheet.None, editingVehicleId = null) }
    }

    private fun saveService() {
        val form = _state.value.serviceForm
        if (!form.isValid || form.vehicleId == null) return
        val kmInt = form.km.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
        viewModelScope.launch {
            repository.addService(
                ServiceRecord(
                    vehicleId = form.vehicleId,
                    km        = kmInt,
                    work      = form.work.trim(),
                    nextKm    = form.nextKm.trim().takeIf { it.isNotBlank() }
                        ?.replace(Regex("[^0-9]"), "")?.toIntOrNull(),
                )
            )
        }
        _state.update { it.copy(activeSheet = GarageContract.Sheet.None) }
    }

    private suspend fun seedDefaults() {
        val v1 = repository.addVehicle(Vehicle(name = "Yamaha",   model = "MT-07",     year = "2021", km = 18400))
        val v2 = repository.addVehicle(Vehicle(name = "Honda",    model = "CB500X",    year = "2019", km = 41200))
        val v3 = repository.addVehicle(Vehicle(name = "Kawasaki", model = "Ninja 400", year = "2023", km = 6150))
        listOf(
            ServiceRecord(vehicleId = v1, km = 12000, work = "Engine oil & filter change, chain clean + lube, tyre pressures checked.", nextKm = 18000),
            ServiceRecord(vehicleId = v1, km =  8200, work = "Front & rear brake pads replaced, brake fluid flushed.", nextKm = 16000),
            ServiceRecord(vehicleId = v1, km =  4100, work = "First service: oil change, valve clearance inspection, bolt torque check.", nextKm = 10000),
            ServiceRecord(vehicleId = v2, km = 38000, work = "Major service: air filter, spark plugs, oil & filter, coolant top-up.", nextKm = 42000),
            ServiceRecord(vehicleId = v2, km = 30500, work = "Chain & sprocket kit replaced, throttle cables adjusted.", nextKm = 36000),
            ServiceRecord(vehicleId = v3, km =  6000, work = "Oil change and chain tension adjustment.", nextKm = 12000),
        ).forEach { repository.addService(it) }
    }

    private fun Vehicle.toUi(latestService: ServiceRecord?): VehicleUi = VehicleUi(
        id          = id,
        name        = name,
        model       = model,
        year        = year,
        km          = km,
        kmFormatted = km.formatKm(),
        photoUri    = photoUri,
        urgency     = computeUrgency(km, latestService?.nextKm),
    )
}
