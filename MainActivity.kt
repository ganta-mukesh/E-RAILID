package com.example.railid

import android.os.Bundle
import androidx.fragment.app.FragmentActivity  // ✅ Use FragmentActivity instead of ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.railid.ui.theme.RailIdTheme

class MainActivity : FragmentActivity() { // ✅ Changed here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RailIdTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}
