package com.alexkolydas.mygarage.feature.garage.di

import com.alexkolydas.mygarage.feature.garage.GarageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val garageModule = module {
    viewModel { GarageViewModel(get()) }
}
