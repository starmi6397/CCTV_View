package com.cctv_view.ui.screens

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cctv_view.MainActivity
import com.cctv_view.ui.components.*
import com.cctv_view.viewmodel.MainViewModel

@Composable
fun PlayerScreen(
    onOpenSettings: () -> Unit,
    viewModel: MainViewModel = viewModel(
        factory = MainActivity.ViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    val cctvChannels = remember { viewModel.getCCTVChannels() }
    val localChannels = remember { viewModel.getLocalChannels() }

    val menuItems = remember {
        listOf(
            MenuItem(0, "刷新", { Icon(Icons.Default.Refresh, contentDescription = null) }) {
                viewModel.refreshPage()
            },
            MenuItem(1, "播放/暂停", { Icon(Icons.Default.PlayArrow, contentDescription = null) }) { },
            MenuItem(2, "全屏", { Icon(Icons.Default.Fullscreen, contentDescription = null) }) { },
            MenuItem(3, "放大", { Icon(Icons.Default.ZoomIn, contentDescription = null) }) { },
            MenuItem(4, "缩小", { Icon(Icons.Default.ZoomOut, contentDescription = null) }) { },
            MenuItem(5, "设置", { Icon(Icons.Default.Settings, contentDescription = null) }) {
                onOpenSettings()
            }
        )
    }

    // 使用 AndroidView 包装的按键处理器（更可靠）
    AndroidView(
        factory = { ctx ->
            android.view.View(ctx).apply {
                isFocusable = true
                isFocusableInTouchMode = true
                isClickable = true

                setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        Log.d("PlayerScreen", "按键被捕获: $keyCode")
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                Log.d("PlayerScreen", "按了上键")
                                viewModel.previousChannel()
                                true
                            }
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                Log.d("PlayerScreen", "按下键")
                                viewModel.nextChannel()
                                true
                            }
                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                Log.d("PlayerScreen", "按了左键")
                                true
                            }
                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                Log.d("PlayerScreen", "按了右键")
                                true
                            }
                            KeyEvent.KEYCODE_DPAD_CENTER,
                            KeyEvent.KEYCODE_ENTER -> {
                                Log.d("PlayerScreen", "按了确认键")
                                if (!uiState.showChannelList && !uiState.showMenu && !uiState.showNumberInput) {
                                    viewModel.toggleChannelList()
                                } else if (uiState.showChannelList || uiState.showMenu) {
                                    viewModel.hideAllOverlays()
                                }
                                true
                            }
                            KeyEvent.KEYCODE_MENU -> {
                                Log.d("PlayerScreen", "按了菜单键")
                                if (!uiState.showChannelList && !uiState.showMenu) {
                                    viewModel.toggleMenu()
                                }
                                true
                            }
                            KeyEvent.KEYCODE_BACK -> {
                                Log.d("PlayerScreen", "按了返回键")
                                if (uiState.showChannelList || uiState.showMenu || uiState.showNumberInput) {
                                    viewModel.hideAllOverlays()
                                    true
                                } else {
                                    false
                                }
                            }
                            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                                val number = keyCode - KeyEvent.KEYCODE_0
                                Log.d("PlayerScreen", "按了数字键: $number")
                                viewModel.appendNumber(number)
                                true
                            }
                            else -> {
                                Log.d("PlayerScreen", "未处理的按键: $keyCode")
                                false
                            }
                        }
                    } else {
                        false
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester),
        update = { view ->
            view.requestFocus()
        }
    )

    // 主内容区域
    Box(modifier = Modifier.fillMaxSize()) {
        // WebView 播放器
        TVWebView(
            channel = uiState.currentChannel,
            onPageFinished = { info -> viewModel.onPageFinished(info) },
            modifier = Modifier.fillMaxSize()
        )

        // 加载中指示器
        if (uiState.isChangingChannel) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 6.dp
                )
            }
        }

        // 频道信息浮层
        ChannelOverlay(
            message = uiState.overlayMessage,
            isVisible = uiState.showOverlay,
            modifier = Modifier
        )

        // 数字输入浮层
        NumberInputOverlay(
            input = uiState.numberInputBuffer,
            isVisible = uiState.showNumberInput,
            modifier = Modifier
        )

        // 频道列表
        ChannelListDrawer(
            cctvChannels = cctvChannels,
            localChannels = localChannels,
            currentChannelId = uiState.currentChannel?.id ?: -1,
            selectedCategory = uiState.channelCategory,
            onCategorySelected = { viewModel.setChannelCategory(it) },
            onChannelSelected = {
                viewModel.changeChannel(it)
                viewModel.hideAllOverlays()
            },
            onDismiss = { viewModel.hideAllOverlays() },
            isVisible = uiState.showChannelList
        )

        // 菜单
        MenuOverlay(
            items = menuItems,
            isVisible = uiState.showMenu,
            onDismiss = { viewModel.hideAllOverlays() },
            modifier = Modifier
        )

        // 测试按钮 - 确认界面是否正常
        Button(
            onClick = {
                Log.d("PlayerScreen", "测试按钮被点击")
                onOpenSettings()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("测试设置")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}