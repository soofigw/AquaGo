package com.example.aquago.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository: Una capa de abstracción para el acceso a datos.
 * Es el punto de conexión entre el ViewModel y la Base de Datos.
 */
class WaterRepository(private val waterDao: WaterDao) {


    // obtiene la config del usuario (precios, flujos)
    val userProfile: Flow<UserProfile?> = waterDao.getUserProfile()

    // obtiene todoel historial
    val allLogs: Flow<List<WaterLog>> = waterDao.getAllLogs()

    // consumo del dia
    fun getLitrosToday(start: Long, end: Long): Flow<Float?> {
        return waterDao.getLitrosByDateRange(start, end)
    }

    // costo del dia en base al tiempo
    fun getCostoToday(start: Long, end: Long): Flow<Float?> {
        return waterDao.getCostoByDateRange(start, end)
    }

    suspend fun insertLog(log: WaterLog) {
        waterDao.insertLog(log)
    }

    suspend fun updateProfile(profile: UserProfile) {
        waterDao.insertOrUpdateProfile(profile)
    }
}