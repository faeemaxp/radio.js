package com.robco.pipboyradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.robco.pipboyradio.ui.PipBoyRadioScreen
import com.robco.pipboyradio.ui.theme.PipBoyTheme
import com.robco.pipboyradio.ui.viewmodel.RadioViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RadioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PipBoyTheme {
                PipBoyRadioScreen(viewModel = viewModel)
            }
        }
    }
}
