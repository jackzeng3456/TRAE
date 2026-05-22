package com.tvlive.player.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tvlive.player.model.Channel

class ChannelDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "channels.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CHANNELS = "channels"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_STREAM_URL = "stream_url"
        private const val COLUMN_STREAM_TYPE = "stream_type"
        private const val COLUMN_LOGO_URL = "logo_url"
        private const val COLUMN_IS_FAVORITE = "is_favorite"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_CHANNELS (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_STREAM_URL TEXT NOT NULL,
                $COLUMN_STREAM_TYPE TEXT NOT NULL,
                $COLUMN_LOGO_URL TEXT,
                $COLUMN_IS_FAVORITE INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)

        insertDefaultChannels(db)
    }

    private fun insertDefaultChannels(db: SQLiteDatabase) {
        val defaultChannels = listOf(
            Channel(name = "中央一台", streamUrl = "http://example.com/cctv1", streamType = Channel.StreamType.HTTP),
            Channel(name = "中央二台", streamUrl = "http://example.com/cctv2", streamType = Channel.StreamType.HTTP),
            Channel(name = "湖南卫视", streamUrl = "http://example.com/hunan", streamType = Channel.StreamType.HTTP)
        )

        defaultChannels.forEach { channel ->
            val values = ContentValues().apply {
                put(COLUMN_ID, channel.id)
                put(COLUMN_NAME, channel.name)
                put(COLUMN_STREAM_URL, channel.streamUrl)
                put(COLUMN_STREAM_TYPE, channel.streamType.name)
                put(COLUMN_LOGO_URL, channel.logoUrl)
                put(COLUMN_IS_FAVORITE, if (channel.isFavorite) 1 else 0)
            }
            db.insert(TABLE_CHANNELS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHANNELS")
        onCreate(db)
    }

    fun getAllChannels(): List<Channel> {
        val channels = mutableListOf<Channel>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_CHANNELS, null, null, null, null, null, "$COLUMN_ID ASC")

        if (cursor.moveToFirst()) {
            do {
                channels.add(cursorToChannel(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return channels
    }

    fun addChannel(channel: Channel): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, channel.id)
            put(COLUMN_NAME, channel.name)
            put(COLUMN_STREAM_URL, channel.streamUrl)
            put(COLUMN_STREAM_TYPE, channel.streamType.name)
            put(COLUMN_LOGO_URL, channel.logoUrl)
            put(COLUMN_IS_FAVORITE, if (channel.isFavorite) 1 else 0)
        }
        return db.insert(TABLE_CHANNELS, null, values)
    }

    fun updateChannel(channel: Channel): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, channel.name)
            put(COLUMN_STREAM_URL, channel.streamUrl)
            put(COLUMN_STREAM_TYPE, channel.streamType.name)
            put(COLUMN_LOGO_URL, channel.logoUrl)
            put(COLUMN_IS_FAVORITE, if (channel.isFavorite) 1 else 0)
        }
        return db.update(TABLE_CHANNELS, values, "$COLUMN_ID = ?", arrayOf(channel.id.toString()))
    }

    fun deleteChannel(channelId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_CHANNELS, "$COLUMN_ID = ?", arrayOf(channelId.toString()))
    }

    fun replaceAllChannels(channels: List<Channel>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_CHANNELS, null, null)
            channels.forEach { channel ->
                val values = ContentValues().apply {
                    put(COLUMN_ID, channel.id)
                    put(COLUMN_NAME, channel.name)
                    put(COLUMN_STREAM_URL, channel.streamUrl)
                    put(COLUMN_STREAM_TYPE, channel.streamType.name)
                    put(COLUMN_LOGO_URL, channel.logoUrl)
                    put(COLUMN_IS_FAVORITE, if (channel.isFavorite) 1 else 0)
                }
                db.insert(TABLE_CHANNELS, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun cursorToChannel(cursor: Cursor): Channel {
        return Channel(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            streamUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STREAM_URL)),
            streamType = Channel.StreamType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STREAM_TYPE))),
            logoUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOGO_URL)),
            isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
        )
    }
}
