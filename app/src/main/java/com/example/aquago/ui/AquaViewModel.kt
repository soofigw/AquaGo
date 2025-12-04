package com.example.aquago.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aquago.data.UserProfile
import com.example.aquago.data.WaterLog
import com.example.aquago.data.WaterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DayEfficiency(
    val date: LocalDate,
    val totalLiters: Float,
    val efficiencyPercent: Float
)

class AquaViewModel(private val repository: WaterRepository) : ViewModel() {

    //config usuario
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, UserProfile())

    private val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val todayEnd = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val litrosHoy: StateFlow<Float> = repository.getLitrosToday(todayStart, todayEnd)
        .map { it ?: 0f }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val dineroHoy: StateFlow<Float> = repository.getCostoToday(todayStart, todayEnd)
        .map { it ?: 0f }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    //heatmap historico
    val historialHeatmap: StateFlow<List<DayEfficiency>> = combine(
        repository.allLogs,
        userProfile
    ) { logs, profile ->
        calcularEficienciaHistorica(logs, profile.metaDiariaLitros)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun actualizarPerfil(nuevoPerfil: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(nuevoPerfil)
        }
    }

    fun registrarActividad(actividad: String, valorInput: Float, esTiempo: Boolean) {
        viewModelScope.launch {
            val perfil = userProfile.value

            val litrosConsumidos = if (esTiempo) {
                val flujo = if (actividad == "Ducha") perfil.flujoRegadera else 10f
                valorInput * flujo
            } else {
                val volumen = if (actividad == "Inodoro") perfil.litrosInodoro else perfil.litrosLavadora
                valorInput * volumen
            }

            val costo = (litrosConsumidos / 1000) * perfil.precioMetroCubico

            val log = WaterLog(
                tipoActividad = actividad,
                cantidadLitros = litrosConsumidos,
                costoCalculado = costo,
                fecha = System.currentTimeMillis()
            )
            repository.insertLog(log)
        }
    }

    private fun calcularEficienciaHistorica(logs: List<WaterLog>, meta: Float): List<DayEfficiency> {
        if (logs.isEmpty()) return emptyList()

        val logsPorDia = logs.groupBy {
            Instant.ofEpochMilli(it.fecha).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        val dias = mutableListOf<DayEfficiency>()
        val hoy = LocalDate.now()

        //ultimos 60dias
        for (i in 0 until 60) {
            val fecha = hoy.minusDays(i.toLong())
            val logsDelDia = logsPorDia[fecha] ?: emptyList()
            val totalLitros = logsDelDia.map { it.cantidadLitros }.sum()
            val eficiencia = if (meta > 0) totalLitros / meta else 0f

            dias.add(DayEfficiency(fecha, totalLitros, eficiencia))
        }
        return dias.sortedBy { it.date }
    }
}

class AquaViewModelFactory(private val repository: WaterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AquaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AquaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}