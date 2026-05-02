package com.cctv_view.ui.screens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cctv_view.ui.theme.CCTVViewTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CCTVViewTheme {
                SettingsScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("cctv_view", Context.MODE_PRIVATE)

    var fontSize by remember {
        mutableStateOf(prefs.getString("text_size", "22") ?: "22")
    }
    var directChannelChange by remember {
        mutableStateOf(prefs.getBoolean("direct_channel_change", false))
    }
    var dualWebView by remember {
        mutableStateOf(prefs.getBoolean("dual_webview", true))
    }
    var showProgramInfo by remember {
        mutableStateOf(prefs.getBoolean("show_program_info", true))
    }
    var overlayDuration by remember {
        mutableStateOf(prefs.getInt("overlay_duration", 5))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "设置",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 字体大小设置
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "字体大小",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "当前: ${fontSize}sp",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("18", "22", "25", "30").forEach { size ->
                                FilterChip(
                                    selected = fontSize == size,
                                    onClick = {
                                        fontSize = size
                                        prefs.edit().putString("text_size", size).apply()
                                    },
                                    label = { Text("${size}sp") },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 直接换台模式
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "直接换台模式",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (directChannelChange) "上下键直接换台" else "上下键显示频道列表",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = directChannelChange,
                            onCheckedChange = {
                                directChannelChange = it
                                prefs.edit().putBoolean("direct_channel_change", it).apply()
                            }
                        )
                    }
                }
            }

            // 双缓冲 WebView
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "双缓冲 WebView",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (dualWebView) "换台更流畅（推荐）" else "换台可能有短暂黑屏",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = dualWebView,
                            onCheckedChange = {
                                dualWebView = it
                                prefs.edit().putBoolean("dual_webview", it).apply()
                            }
                        )
                    }
                }
            }

            // 显示节目信息
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "显示节目信息",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (showProgramInfo) "切换频道时显示当前节目" else "不显示节目信息",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = showProgramInfo,
                            onCheckedChange = {
                                showProgramInfo = it
                                prefs.edit().putBoolean("show_program_info", it).apply()
                            }
                        )
                    }
                }
            }

            // 浮层显示时长
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "浮层显示时长",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${overlayDuration} 秒",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                        Slider(
                            value = overlayDuration.toFloat(),
                            onValueChange = {
                                overlayDuration = it.toInt()
                                prefs.edit().putInt("overlay_duration", overlayDuration).apply()
                            },
                            valueRange = 2f..10f,
                            steps = 8,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 关于
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "关于",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "CCTV View TV 版 v1.0.0\n专为 Android TV 优化",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // 重置按钮
            item {
                Button(
                    onClick = {
                        prefs.edit().clear().apply()
                        fontSize = "22"
                        directChannelChange = false
                        dualWebView = true
                        showProgramInfo = true
                        overlayDuration = 5
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("恢复默认设置", fontSize = 16.sp)
                }
            }
        }
    }
}