package com.alexkolydas.mygarage.core.data.repository

import com.alexkolydas.mygarage.core.data.model.ServiceRecord
import com.alexkolydas.mygarage.core.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface GarageRepository {
    val allVehicles: Flow<List<Vehicle>>
    val allServices: Flow<List<ServiceRecord>>
    fun servicesForVehicle(vehicleId: Long): Flow<List<ServiceRecord>>
    suspend fun addVehicle(vehicle: Vehicle): Long
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun updateVehiclePhoto(vehicleId: Long, uri: String)
    suspend fun deleteVehicle(vehicleId: Long)
    suspend fun addService(record: ServiceRecord): Long
}
