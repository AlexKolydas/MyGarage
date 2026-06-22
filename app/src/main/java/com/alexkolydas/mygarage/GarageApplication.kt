package com.alexkolydas.mygarage

import android.app.Application
import com.alexkolydas.mygarage.core.data.di.dataModule
import com.alexkolydas.mygarage.feature.detail.di.detailModule
import com.alexkolydas.mygarage.feature.garage.di.garageModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GarageApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GarageApplication)
            modules(dataModule, garageModule, detailModule)
        }
    }
}
