package com.example.aquago.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Insert
    suspend fun insertLog(log: WaterLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("SELECT * FROM water_logs ORDER BY fecha DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    // Suma de Litros en un rango de tiempo
    @Query("SELECT SUM(cantidadLitros) FROM water_logs WHERE fecha BETWEEN :inicio AND :fin")
    fun getLitrosByDateRange(inicio: Long, fin: Long): Flow<Float?>

    // Suma de Dinero en un rango de tiempo
    @Query("SELECT SUM(costoCalculado) FROM water_logs WHERE fecha BETWEEN :inicio AND :fin")
    fun getCostoByDateRange(inicio: Long, fin: Long): Flow<Float?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>
}