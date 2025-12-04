package com.example.aquago.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.aquago.data.UserProfile
import com.example.aquago.ui.DayEfficiency

@Composable
fun ShowerSimulatorDialog(
    flujoRegadera: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var minutos by remember { mutableFloatStateOf(5.0f) }
    val litrosEstimados = minutos * flujoRegadera

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Shower, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Simulador de Ducha", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Flujo de ${flujoRegadera.toInt()} Litros/min", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                Text("${minutos.toInt()} min", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                Text("= ${litrosEstimados.toInt()} Litros", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))
                Slider(value = minutos, onValueChange = { minutos = it }, valueRange = 1f..30f, steps = 29)
                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { onConfirm(minutos) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrar Consumo")
                }
            }
        }
    }
}

//heatmap grid
@Composable
fun HeatmapGrid(
    history: List<DayEfficiency>,
    onDayClick: (DayEfficiency) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Historial de consumo (60 días) ⌛",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false
        ) {
            items(history) { day ->
                HeatmapCell(day, onClick = { onDayClick(day) })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bien", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(end = 4.dp))
            HeatmapLegendBox(Color(0xFF2E7D32))
            HeatmapLegendBox(Color(0xFFFFB74D))
            HeatmapLegendBox(Color(0xFFE53935))
            Text("Mal", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
fun HeatmapCell(day: DayEfficiency, onClick: () -> Unit) {
    val color = when {
        day.totalLiters == 0f -> Color.LightGray.copy(alpha = 0.3f)
        day.efficiencyPercent > 1.1f -> Color(0xFFE53935)
        day.efficiencyPercent > 0.9f -> Color(0xFFFFB74D)
        day.efficiencyPercent > 0.5f -> Color(0xFFA5D6A7)
        else -> Color(0xFF2E7D32)
    }

    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .clickable { onClick() }
    )
}

@Composable
fun HeatmapLegendBox(color: Color) {
    Box(modifier = Modifier.padding(horizontal = 2.dp).size(10.dp).clip(RoundedCornerShape(2.dp)).background(color))
}

@Composable
fun ProfileConfigDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var precio by remember { mutableStateOf(currentProfile.precioMetroCubico.toString()) }
    var meta by remember { mutableStateOf(currentProfile.metaDiariaLitros.toString()) }

    //si el flujo es menor a 9 es ecologica
    var esRegaderaEcologica by remember { mutableStateOf(currentProfile.flujoRegadera < 9.0f) }

    val uriHandler = LocalUriHandler.current

    Dialog(onDismissRequest = onDismiss) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Configuración", style = MaterialTheme.typography.headlineSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))

                //tarifa
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Tarifa del Agua ($/m³)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                        append("Consulta tu tarifa aquí")
                    }
                    addStringAnnotation(
                        tag = "URL",
                        annotation = "https://www.gob.mx/conagua", // link para consultar
                        start = 0,
                        end = 23
                    )
                }
                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                uriHandler.openUri(annotation.item)
                            }
                    },
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = meta,
                    onValueChange = { meta = it },
                    label = { Text("Meta Diaria (Litros)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tipo de Regadera", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // opcion normal
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { esRegaderaEcologica = false }) {
                    RadioButton(selected = !esRegaderaEcologica, onClick = { esRegaderaEcologica = false })
                    Text("Normal (12 L/min)")
                }

                // opcion ecologica
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { esRegaderaEcologica = true }) {
                    RadioButton(selected = esRegaderaEcologica, onClick = { esRegaderaEcologica = true })
                    Text("Ecológica (6 L/min)")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val flujoSeleccionado = if (esRegaderaEcologica) 6.0f else 12.0f

                        val newProfile = currentProfile.copy(
                            precioMetroCubico = precio.toFloatOrNull() ?: currentProfile.precioMetroCubico,
                            metaDiariaLitros = meta.toFloatOrNull() ?: currentProfile.metaDiariaLitros,
                            flujoRegadera = flujoSeleccionado
                        )
                        onSave(newProfile)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}