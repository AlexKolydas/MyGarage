package com.alexkolydas.mygarage.core.data.repository

import com.alexkolydas.mygarage.core.data.local.ServiceRecordDao
import com.alexkolydas.mygarage.core.data.local.VehicleDao
import com.alexkolydas.mygarage.core.data.model.ServiceRecord
import com.alexkolydas.mygarage.core.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

internal class GarageRepositoryImpl(
    private val vehicleDao: VehicleDao,
    private val serviceRecordDao: ServiceRecordDao,
) : GarageRepository {

    override val allVehicles: Flow<List<Vehicle>> = vehicleDao.getAll()
    override val allServices: Flow<List<ServiceRecord>> = serviceRecordDao.getAll()

    override fun servicesForVehicle(vehicleId: Long): Flow<List<ServiceRecord>> =
        serviceRecordDao.getForVehicle(vehicleId)

    override suspend fun addVehicle(vehicle: Vehicle): Long = vehicleDao.insert(vehicle)
    override suspend fun updateVehicle(vehicle: Vehicle) = vehicleDao.update(vehicle)
    override suspend fun updateVehiclePhoto(vehicleId: Long, uri: String) =
        vehicleDao.updatePhoto(vehicleId, uri)
    override suspend fun deleteVehicle(vehicleId: Long) = vehicleDao.deleteById(vehicleId)
    override suspend fun addService(record: ServiceRecord): Long = serviceRecordDao.insert(record)
    override suspend fun updateService(record: ServiceRecord) = serviceRecordDao.update(record)
    override suspend fun deleteService(record: ServiceRecord) = serviceRecordDao.delete(record)
}
