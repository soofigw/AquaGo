package com.example.aquago.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: Long = System.currentTimeMillis(),
    val tipoActividad: String, // "Ducha", "Inodoro", etc.
    val cantidadLitros: Float,
    val costoCalculado: Float
)