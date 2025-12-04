// Archivo: build.gradle.kts (Project: AquaGo)
plugins {
    // Definimos las versiones AQUÍ para que no haya conflictos
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    // Usamos Kotlin 2.0.0 para que sea compatible con el plugin de Compose nuevo
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    // Plugin de Compose Compiler para Kotlin 2.0
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    // KSP para la base de datos (versión compatible con Kotlin 2.0)
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}