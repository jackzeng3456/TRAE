package com.tvlive.player.model

data class Channel(
    val id: Long = System.currentTimeMillis(),
    var name: String = "",
    var streamUrl: String = "",
    var streamType: StreamType = StreamType.HTTP,
    var logoUrl: String? = null,
    var isFavorite: Boolean = false
) {
    enum class StreamType(val displayName: String) {
        UDP("UDP"),
        RTP("RTP"),
        HTTP("HTTP");

        companion object {
            fun fromDisplayName(name: String): StreamType {
                return values().find { it.displayName == name } ?: HTTP
            }
        }
    }
}
