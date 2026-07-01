package com.alexkolydas.mygarage.feature.detail

import com.alexkolydas.mygarage.core.ui.model.ServiceUi
import com.alexkolydas.mygarage.core.ui.model.VehicleDetailUi

object DetailContract {

    data class State(
        val detail: VehicleDetailUi? = null,
        val isLoading: Boolean = true,
        val showServiceSheet: Boolean = false,
        val serviceForm: ServiceForm = ServiceForm(),
        val editingServiceId: Long? = null,
        val contextMenuServiceId: Long? = null,
        val showDeleteConfirm: Boolean = false,
        val pendingDeleteId: Long? = null,
    )

    sealed interface Intent {
        data object LogServiceClicked : Intent
        data object CloseServiceSheet : Intent
        data class UpdateServiceKm(val value: String) : Intent
        data class UpdateServiceWork(val value: String) : Intent
        data class UpdateServiceNextKm(val value: String) : Intent
        data object SaveService : Intent
        data object BackClicked : Intent
        data class UpdatePhoto(val uri: String) : Intent
        data class LongClickService(val serviceId: Long) : Intent
        data object DismissContextMenu : Intent
        data class EditServiceClicked(val service: ServiceUi) : Intent
        data class DeleteServiceClicked(val serviceId: Long) : Intent
        data object ConfirmDelete : Intent
        data object DismissDelete : Intent
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }

    data class ServiceForm(
        val km: String = "",
        val work: String = "",
        val nextKm: String = "",
    ) {
        val isValid: Boolean get() = km.isNotBlank() && work.isNotBlank()
    }
}
