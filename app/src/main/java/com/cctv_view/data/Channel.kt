package com.cctv_view.data

data class Channel(
    val id: Int,
    val number: Int,
    val name: String,
    val url: String,
    val category: ChannelCategory,
    val logo: String? = null
)

enum class ChannelCategory {
    CCTV,
    LOCAL
}