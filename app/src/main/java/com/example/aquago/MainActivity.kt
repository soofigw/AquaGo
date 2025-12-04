package com.example.aquago

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.aquago.data.AppDB
import com.example.aquago.data.WaterRepository
import com.example.aquago.ui.AquaViewModel
import com.example.aquago.ui.AquaViewModelFactory
import com.example.aquago.ui.screens.HomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ini bdd
        val database = AppDB.getDatabase(applicationContext)
        val repository = WaterRepository(database.waterDao())

        // ini viewmodel
        val viewModel: AquaViewModel by viewModels {
            AquaViewModelFactory(repository)
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}