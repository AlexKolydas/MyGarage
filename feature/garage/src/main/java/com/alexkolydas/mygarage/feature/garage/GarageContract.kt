package com.alexkolydas.mygarage.feature.garage

import com.alexkolydas.mygarage.core.ui.model.VehicleUi

object GarageContract {

    data class State(
        val vehicles: List<VehicleUi> = emptyList(),
        val isLoading: Boolean = true,
        val fabExpanded: Boolean = false,
        val activeSheet: Sheet = Sheet.None,
        val vehicleForm: VehicleForm = VehicleForm(),
        val serviceForm: ServiceForm = ServiceForm(),
        // Long-press context menu
        val contextMenuVehicle: VehicleUi? = null,
        val showDeleteConfirm: Boolean = false,
        // Non-null when editing an existing vehicle
        val editingVehicleId: Long? = null,
    )

    sealed interface Intent {
        data object ToggleFab : Intent
        data object CloseFab : Intent
        data object OpenVehicleSheet : Intent
        data class OpenServiceSheet(val preselectedId: Long? = null) : Intent
        data object CloseSheet : Intent

        // Vehicle form
        data class UpdateVehicleModel(val value: String) : Intent
        data class UpdateVehicleName(val value: String) : Intent
        data class UpdateVehicleYear(val value: String) : Intent
        data class UpdateVehicleKm(val value: String) : Intent
        data object SaveVehicle : Intent

        // Service form
        data class SelectServiceVehicle(val id: Long) : Intent
        data class UpdateServiceKm(val value: String) : Intent
        data class UpdateServiceWork(val value: String) : Intent
        data class UpdateServiceNextKm(val value: String) : Intent
        data object SaveService : Intent

        // Actions
        data class VehicleClicked(val vehicleId: Long) : Intent
        data class UpdateVehiclePhoto(val vehicleId: Long, val uri: String) : Intent

        // Long-press context menu
        data class LongPressVehicle(val vehicle: VehicleUi) : Intent
        data object DismissContextMenu : Intent
        data object RequestDeleteVehicle : Intent
        data object ConfirmDeleteVehicle : Intent
        data object CancelDeleteVehicle : Intent
        data class OpenEditVehicleSheet(val vehicle: VehicleUi) : Intent
    }

    sealed interface Effect {
        data class NavigateToDetail(val vehicleId: Long) : Effect
    }

    sealed interface Sheet {
        data object None : Sheet
        data object NewVehicle : Sheet
        data object EditVehicle : Sheet
        data object AddService : Sheet
    }

    data class VehicleForm(
        val model: String = "",
        val name: String = "",
        val year: String = "",
        val km: String = "",
    ) {
        val isValid: Boolean get() = model.isNotBlank()
    }

    data class ServiceForm(
        val vehicleId: Long? = null,
        val km: String = "",
        val work: String = "",
        val nextKm: String = "",
    ) {
        val isValid: Boolean get() = km.isNotBlank() && work.isNotBlank()
    }
}
