package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.ui.AppMainUi
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local database & components
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.appDao()
        val apiService = ApiService.create()
        val repository = AppRepository(dao, apiService)

        // Instantiate AppViewModel
        val factory = ViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            MyApplicationTheme(darkTheme = isDark) {
                AppMainUi(viewModel = viewModel)
            }
        }
    }
}
