package com.cctv_view.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

        // 处理遥控器按键的透明层
        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onKeyEvent { event ->
                    when (event.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_DPAD_UP -> {
                            viewModel.previousChannel()
                            true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_DOWN -> {
                            viewModel.nextChannel()
                            true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                        android.view.KeyEvent.KEYCODE_ENTER -> {
                            if (!uiState.showChannelList && !uiState.showMenu && !uiState.showNumberInput) {
                                viewModel.toggleChannelList()
                            } else if (uiState.showChannelList || uiState.showMenu) {
                                viewModel.hideAllOverlays()
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_MENU,
                        android.view.KeyEvent.KEYCODE_M -> {
                            if (!uiState.showChannelList && !uiState.showMenu) {
                                viewModel.toggleMenu()
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_BACK -> {
                            if (uiState.showChannelList || uiState.showMenu || uiState.showNumberInput) {
                                viewModel.hideAllOverlays()
                                true
                            } else {
                                false
                            }
                        }
                        in android.view.KeyEvent.KEYCODE_0..android.view.KeyEvent.KEYCODE_9 -> {
                            val number = event.nativeKeyEvent.keyCode - android.view.KeyEvent.KEYCODE_0
                            viewModel.appendNumber(number)
                            true
                        }
                        else -> false
                    }
                }
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}