package com.example.aquago.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    // datos dinancieros (Base: MX)
    val precioMetroCubico: Float = 18.50f,
    // datos tecnicos (L/min o L/uso)
    val flujoRegadera: Float = 12.0f,
    val flujoGrifo: Float = 8.0f,
    val litrosInodoro: Float = 6.0f,
    val litrosLavadora: Float = 50.0f,

    // meta base
    val metaDiariaLitros: Float = 150.0f
)