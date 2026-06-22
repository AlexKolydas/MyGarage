package com.alexkolydas.mygarage.core.data.di

import androidx.room.Room
import com.alexkolydas.mygarage.core.data.local.GarageDatabase
import com.alexkolydas.mygarage.core.data.repository.GarageRepository
import com.alexkolydas.mygarage.core.data.repository.GarageRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), GarageDatabase::class.java, "garage.db").build()
    }
    single { get<GarageDatabase>().vehicleDao() }
    single { get<GarageDatabase>().serviceRecordDao() }
    single<GarageRepository> { GarageRepositoryImpl(get(), get()) }
}
