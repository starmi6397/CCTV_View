package com.cctv_view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cctv_view.ui.screens.PlayerScreen
import com.cctv_view.ui.SettingsActivity
import com.cctv_view.ui.theme.CCTVViewTheme
import com.cctv_view.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CCTVViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerScreen(
                        onOpenSettings = { openSettings() }
                    )
                }
            }
        }

        // 调试：在 Activity 层面捕获按键
        window.decorView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                Log.d("MainActivity", "Activity 捕获按键: $keyCode")
            }
            false // 返回 false 让事件继续传递
        }
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    class ViewModelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(applicationContext.applicationContext as android.app.Application) as T
        }
    }
}