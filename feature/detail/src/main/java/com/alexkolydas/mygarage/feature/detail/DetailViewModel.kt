package com.alexkolydas.mygarage.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexkolydas.mygarage.core.data.model.ServiceRecord
import com.alexkolydas.mygarage.core.data.repository.GarageRepository
import com.alexkolydas.mygarage.core.ui.model.ServiceUi
import com.alexkolydas.mygarage.core.ui.model.VehicleDetailUi
import com.alexkolydas.mygarage.core.ui.model.VehicleUi
import com.alexkolydas.mygarage.core.ui.util.computeUrgency
import com.alexkolydas.mygarage.core.ui.util.formatKm
import com.alexkolydas.mygarage.core.ui.util.statusText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(
    private val vehicleId: Long,
    private val repository: GarageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DetailContract.State())
    val state: StateFlow<DetailContract.State> = _state.asStateFlow()

    private val _effects = Channel<DetailContract.Effect>(Channel.BUFFERED)
    val effects: Flow<DetailContract.Effect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.allVehicles,
                repository.servicesForVehicle(vehicleId),
            ) { vehicles, services ->
                val vehicle = vehicles.find { it.id == vehicleId } ?: return@combine null
                val sorted  = services.sortedByDescending { it.km }
                val latest  = sorted.firstOrNull()
                val urgency = computeUrgency(vehicle.km, latest?.nextKm)
                val vehicleUi = VehicleUi(
                    id          = vehicle.id,
                    name        = vehicle.name,
                    model       = vehicle.model,
                    year        = vehicle.year,
                    km          = vehicle.km,
                    kmFormatted = vehicle.km.formatKm(),
                    photoUri    = vehicle.photoUri,
                    urgency     = urgency,
                )
                val serviceUis = sorted.mapIndexed { index, svc ->
                    ServiceUi(
                        id           = svc.id,
                        vehicleId    = svc.vehicleId,
                        kmFormatted  = svc.km.formatKm(),
                        work         = svc.work,
                        nextFormatted = svc.nextKm?.formatKm() ?: "—",
                        isLatest     = index == 0,
                        urgency      = computeUrgency(vehicle.km, svc.nextKm),
                    )
                }
                VehicleDetailUi(
                    vehicle      = vehicleUi,
                    nextFormatted = latest?.nextKm?.formatKm() ?: "—",
                    statusText   = statusText(urgency),
                    services     = serviceUis,
                )
            }.collect { detail ->
                _state.update { it.copy(detail = detail, isLoading = false) }
            }
        }
    }

    fun onIntent(intent: DetailContract.Intent) {
        when (intent) {
            DetailContract.Intent.LogServiceClicked ->
                _state.update { it.copy(showServiceSheet = true, serviceForm = DetailContract.ServiceForm()) }

            DetailContract.Intent.CloseServiceSheet ->
                _state.update { it.copy(showServiceSheet = false) }

            is DetailContract.Intent.UpdateServiceKm ->
                _state.update { it.copy(serviceForm = it.serviceForm.copy(km = intent.value)) }

            is DetailContract.Intent.UpdateServiceWork ->
                _state.update { it.copy(serviceForm = it.serviceForm.copy(work = intent.value)) }

            is DetailContract.Intent.UpdateServiceNextKm ->
                _state.update { it.copy(serviceForm = it.serviceForm.copy(nextKm = intent.value)) }

            DetailContract.Intent.SaveService -> saveService()

            DetailContract.Intent.BackClicked -> viewModelScope.launch {
                _effects.send(DetailContract.Effect.NavigateBack)
            }

            is DetailContract.Intent.UpdatePhoto -> viewModelScope.launch {
                repository.updateVehiclePhoto(vehicleId, intent.uri)
            }
        }
    }

    private fun saveService() {
        val form = _state.value.serviceForm
        if (!form.isValid) return
        val kmInt = form.km.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
        viewModelScope.launch {
            repository.addService(
                ServiceRecord(
                    vehicleId = vehicleId,
                    km        = kmInt,
                    work      = form.work.trim(),
                    nextKm    = form.nextKm.trim().takeIf { it.isNotBlank() }
                        ?.replace(Regex("[^0-9]"), "")?.toIntOrNull(),
                )
            )
        }
        _state.update { it.copy(showServiceSheet = false) }
    }
}
