package com.alexkolydas.mygarage.feature.detail.di

import com.alexkolydas.mygarage.feature.detail.DetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailModule = module {
    viewModel { params -> DetailViewModel(params.get(), get()) }
}
