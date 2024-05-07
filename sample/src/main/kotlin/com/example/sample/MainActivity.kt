package com.example.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>()
            val theme by viewModel.theme.collectAsState(ThemeMode.Auto)

            SampleAppTheme(
                darkTheme = when (theme) {
                    ThemeMode.Dark -> true
                    ThemeMode.Light -> false
                    ThemeMode.Auto -> isSystemInDarkTheme()
                }
            ) {
                MainScreen(viewModel)
            }
        }
    }
}
