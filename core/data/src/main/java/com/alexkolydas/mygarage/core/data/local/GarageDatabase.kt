package com.alexkolydas.mygarage.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexkolydas.mygarage.core.data.model.ServiceRecord
import com.alexkolydas.mygarage.core.data.model.Vehicle

@Database(
    entities = [Vehicle::class, ServiceRecord::class],
    version = 1,
    exportSchema = false,
)
abstract class GarageDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceRecordDao(): ServiceRecordDao
}
