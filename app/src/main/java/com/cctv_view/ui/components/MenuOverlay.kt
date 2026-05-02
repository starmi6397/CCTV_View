package com.cctv_view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MenuItem(
    val id: Int,
    val name: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOverlay(
    items: List<MenuItem>,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var selectedIndex by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }

    // 防止重复移动的标记
    var lastKeyTime by remember { mutableLongStateOf(0L) }

    // 处理键盘导航
    fun navigate(direction: Int) {
        val currentTime = System.currentTimeMillis()
        // 如果距离上次按键小于 200ms，忽略（防抖）
        if (currentTime - lastKeyTime < 200) return
        lastKeyTime = currentTime

        selectedIndex = ((selectedIndex + direction) % items.size + items.size) % items.size
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .shadow(8.dp)
                .focusRequester(focusRequester)
                .focusable(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .onKeyEvent { event ->
                        when (event.nativeKeyEvent.keyCode) {
                            android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                                navigate(-1)
                                true
                            }
                            android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                navigate(1)
                                true
                            }
                            android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                            android.view.KeyEvent.KEYCODE_ENTER -> {
                                items[selectedIndex].onClick()
                                onDismiss()
                                true
                            }
                            android.view.KeyEvent.KEYCODE_BACK -> {
                                onDismiss()
                                true
                            }
                            else -> false
                        }
                    },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEachIndexed { index, item ->
                    MenuButton(
                        item = item,
                        isSelected = index == selectedIndex,
                        onClick = {
                            item.onClick()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun MenuButton(
    item: MenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasFocus by remember { mutableStateOf(false) }

    // 防止按钮自身重复触发
    var lastClickTime by remember { mutableLongStateOf(0L) }

    val handleClick = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 300) {
            lastClickTime = currentTime
            onClick()
        }
    }

    Surface(
        modifier = modifier
            .height(80.dp)
            .focusable()
            .onFocusChanged { focusState ->
                hasFocus = focusState.isFocused
            },
        shape = RoundedCornerShape(12.dp),
        color = when {
            isSelected -> MaterialTheme.colorScheme.primary
            hasFocus -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        tonalElevation = if (isSelected || hasFocus) 0.dp else 4.dp,
        onClick = handleClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.size(32.dp)) {
                item.icon()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                item.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected || hasFocus) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isSelected -> Color.White
                    hasFocus -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}