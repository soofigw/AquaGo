package com.example.aquago.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquago.R
import com.example.aquago.ui.AquaViewModel
import com.example.aquago.ui.DayEfficiency
import com.example.aquago.ui.components.QuickActionButton
import com.example.aquago.ui.components.WaterGauge
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun HomeScreen(viewModel: AquaViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf("dashboard") }

    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        when (currentScreen) {
            "dashboard" -> DashboardContent(
                viewModel = viewModel,
                onNavigateToHistory = { currentScreen = "history" }
            )
            "history" -> HistoryScreen(
                viewModel = viewModel,
                onBack = { currentScreen = "dashboard" }
            )
        }
    }
}

//splash
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.size(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.aquago),
                    contentDescription = "Logo AquaGo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "AquaGo",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//dashboard
@Composable
fun DashboardContent(
    viewModel: AquaViewModel,
    onNavigateToHistory: () -> Unit
) {
    val litrosHoy by viewModel.litrosHoy.collectAsState()
    val dineroHoy by viewModel.dineroHoy.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showShowerSimulator by remember { mutableStateOf(false) }
    var showProfileConfig by remember { mutableStateOf(false) }

    val consejos = remember {
        listOf(
            "Una fuga de gota por segundo desperdicia 30 litros al día!",
            "Cierra el grifo al cepillarte: ahorras hasta 12 litros por minuto.",
            "Usar la lavadora llena ahorra más agua que dos cargas medias.",
            "Riega las plantas de noche para evitar la evaporación rápida 🌙",
            "Una ducha de 5 minutos ahorra 100 litros comparado con una ducha larga 🚿"
        )
    }
    val consejoDelDia = remember { consejos.random() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Bienvenido a AquaGo!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { showProfileConfig = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Configuración")
            }
        }

        // metrica
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("CONSUMO DEL DIA", style = MaterialTheme.typography.labelSmall)
                    Text("${litrosHoy.toInt()} L", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Meta: ${userProfile.metaDiariaLitros.toInt()} L", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("GASTOS EST.", style = MaterialTheme.typography.labelSmall)
                    Text("$${String.format("%.2f", dineroHoy)}", style = MaterialTheme.typography.displaySmall, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                    Text("Tarifa por /m³: $${userProfile.precioMetroCubico}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        WaterGauge(currentLiters = litrosHoy, maxLiters = userProfile.metaDiariaLitros)

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            QuickActionButton(Icons.Default.WaterDamage, "Inodoro", { viewModel.registrarActividad("Inodoro", 1f, false) }, Color(0xFF4DD0E1))
            QuickActionButton(Icons.Default.LocalLaundryService, "Lavadora", { viewModel.registrarActividad("Lavadora", 1f, false) }, Color(0xFF7986CB))
            QuickActionButton(Icons.Default.Shower, "Ducha", { showShowerSimulator = true }, Color(0xFFBA68C8))
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (litrosHoy > userProfile.metaDiariaLitros) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "¡ALERTA! Has excedido tu meta diaria.",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {

            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // consejos
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Verde suave
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.EmojiObjects, contentDescription = "Tip", tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Consejo Ecológico", style = MaterialTheme.typography.labelLarge, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                        Text(consejoDelDia, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                    }
                }
            }

            //historial
            Card(
                onClick = onNavigateToHistory,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                modifier = Modifier.width(80.dp).fillMaxHeight()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Ver Historial",
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Historial",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFF57F17)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }

    if (showShowerSimulator) {
        ShowerSimulatorDialog(
            flujoRegadera = userProfile.flujoRegadera,
            onDismiss = { showShowerSimulator = false },
            onConfirm = { minutos ->
                viewModel.registrarActividad("Ducha", minutos, true)
                showShowerSimulator = false
            }
        )
    }

    if (showProfileConfig) {
        ProfileConfigDialog(
            currentProfile = userProfile,
            onDismiss = { showProfileConfig = false },
            onSave = { newProfile ->
                viewModel.actualizarPerfil(newProfile)
                showProfileConfig = false
            }
        )
    }
}

//pantalla historial
@Composable
fun HistoryScreen(
    viewModel: AquaViewModel,
    onBack: () -> Unit
) {
    val historial by viewModel.historialHeatmap.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var selectedDay by remember { mutableStateOf<DayEfficiency?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Historial de Consumo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Análisis de 60 Días", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Toca cualquier cuadro para ver el detalle de ese día.", style = MaterialTheme.typography.bodyMedium)
            }
        }

        //heatmap
        HeatmapGrid(
            history = historial,
            onDayClick = { day -> selectedDay = day }
        )

        Spacer(modifier = Modifier.weight(1f))
    }

    selectedDay?.let { day ->
        AlertDialog(
            onDismissRequest = { selectedDay = null },
            title = { Text("Detalle: ${day.date}") },
            text = {
                Column {
                    Text("Consumo Total: ${day.totalLiters.toInt()} Litros")
                    Text("Tue eficiencia del dia fue: ${(day.efficiencyPercent * 100).toInt()}")
                    if (day.totalLiters > userProfile.metaDiariaLitros) {
                        Text("Excediste la meta! ⚠️", color = Color.Red, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Sigue asi! estas dentro de la meta 🎉", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDay = null }) { Text("Cerrar") }
            }
        )
    }
}