package com.cctv_view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cctv_view.data.Channel
import com.cctv_view.data.ChannelCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelListDrawer(
    cctvChannels: List<Channel>,
    localChannels: List<Channel>,
    currentChannelId: Int,
    selectedCategory: ChannelCategory,
    onCategorySelected: (ChannelCategory) -> Unit,
    onChannelSelected: (Channel) -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(400.dp)
                .shadow(8.dp),
            shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .focusable()
            ) {
                // 标题
                Text(
                    text = "频道列表",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 分类 Tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    TabRow(
                        selectedTabIndex = if (selectedCategory == ChannelCategory.CCTV) 0 else 1,
                        containerColor = Color.Transparent,
                        indicator = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("央视频道", "地方频道").forEachIndexed { index, title ->
                            Tab(
                                selected = (selectedCategory == ChannelCategory.CCTV && index == 0) ||
                                        (selectedCategory == ChannelCategory.LOCAL && index == 1),
                                onClick = {
                                    onCategorySelected(if (index == 0) ChannelCategory.CCTV else ChannelCategory.LOCAL)
                                },
                                text = {
                                    Text(
                                        title,
                                        fontSize = 18.sp,
                                        fontWeight = if ((selectedCategory == ChannelCategory.CCTV && index == 0) ||
                                            (selectedCategory == ChannelCategory.LOCAL && index == 1)) FontWeight.Bold
                                        else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // 频道列表
                val channels = if (selectedCategory == ChannelCategory.CCTV) cctvChannels else localChannels
                val listState = rememberLazyListState()

                LaunchedEffect(selectedCategory) {
                    val index = channels.indexOfFirst { it.id == currentChannelId }
                    if (index >= 0) {
                        listState.scrollToItem(index)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(channels) { channel ->
                        ChannelListItem(
                            channel = channel,
                            isSelected = channel.id == currentChannelId,
                            onClick = { onChannelSelected(channel) }
                        )
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun ChannelListItem(
    channel: Channel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        else Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${channel.number}  ${channel.name}",
                fontSize = 18.sp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Text("▶", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            }
        }
    }
}