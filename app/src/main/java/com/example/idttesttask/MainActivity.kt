package com.example.idttesttask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.idttesttask.presentation.navigation.AppNavHost
import com.example.idttesttask.ui.theme.IdtTestTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IdtTestTaskTheme {
                AppNavHost()
            }
        }
    }
}
