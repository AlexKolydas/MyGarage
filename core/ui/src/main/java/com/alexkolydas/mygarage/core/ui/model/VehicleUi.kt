package com.alexkolydas.mygarage.core.ui.model

data class VehicleUi(
    val id: Long,
    val name: String,
    val model: String,
    val year: String,
    val km: Int,
    val kmFormatted: String,
    val photoUri: String?,
    val urgency: UrgencyUi,
)

data class ServiceUi(
    val id: Long,
    val vehicleId: Long,
    val km: Int,
    val kmFormatted: String,
    val work: String,
    val nextKm: Int?,
    val nextFormatted: String,
    val isLatest: Boolean,
    val urgency: UrgencyUi,
)

data class VehicleDetailUi(
    val vehicle: VehicleUi,
    val nextFormatted: String,
    val statusText: String,
    val services: List<ServiceUi>,
)
