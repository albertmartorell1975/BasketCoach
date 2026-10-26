package com.martorell.albert.basketcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.martorell.albert.basketcoach.ui.BasketCoachApp
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasketCoachTheme {
                BasketCoachApp()
            }
        }
    }
}
