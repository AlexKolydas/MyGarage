package com.alexkolydas.mygarage.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val model: String,
    val year: String,
    val km: Int,
    val photoUri: String? = null,
)
