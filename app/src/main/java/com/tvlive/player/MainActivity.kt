package com.tvlive.player

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.tvlive.player.adapter.ChannelAdapter
import com.tvlive.player.database.ChannelDatabase
import com.tvlive.player.databinding.ActivityMainBinding
import com.tvlive.player.model.Channel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null
    private lateinit var channelDatabase: ChannelDatabase
    private var channels: List<Channel> = emptyList()
    private var currentChannelIndex = 0
    private var isPanelVisible = false
    private val handler = Handler(Looper.getMainLooper())

    private val hidePanelRunnable = Runnable {
        hidePanel()
        hideChannelName()
    }

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Log.e(TAG, "Player error: ${error.message}", error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            channelDatabase = ChannelDatabase(this)
            loadChannels()
            setupButtons()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }

    private fun loadChannels() {
        try {
            channels = channelDatabase.getAllChannels()
            if (channels.isNotEmpty()) {
                setupChannelList()
                playChannel(currentChannelIndex)
            } else {
                showNoChannelHint()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading channels", e)
            showNoChannelHint()
        }
    }

    private fun setupChannelList() {
        try {
            binding.channelsRecycler.layoutManager = LinearLayoutManager(this)
            val adapter = ChannelAdapter(channels) { channel ->
                val index = channels.indexOf(channel)
                if (index != -1) {
                    currentChannelIndex = index
                    playChannel(currentChannelIndex)
                    resetHideTimer()
                }
            }
            binding.channelsRecycler.adapter = adapter
            adapter.setSelectedPosition(currentChannelIndex)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up channel list", e)
        }
    }

    private fun setupButtons() {
        try {
            binding.btnManage.setOnClickListener {
                startActivity(Intent(this, ChannelManageActivity::class.java))
            }
            binding.btnSettings.setOnClickListener {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up buttons", e)
        }
    }

    private fun playChannel(index: Int) {
        if (index < 0 || index >= channels.size) return

        try {
            val channel = channels[index]
            currentChannelIndex = index

            showChannelName(channel.name)
            updateSelectedChannel()

            player?.removeListener(playerListener)
            player?.release()
            
            player = ExoPlayer.Builder(this).build().also {
                it.addListener(playerListener)
                binding.playerView.player = it
                try {
                    val mediaItem = MediaItem.fromUri(channel.streamUrl)
                    it.setMediaItem(mediaItem)
                    it.prepare()
                    it.play()
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting up player", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing channel", e)
        }
    }

    private fun showChannelName(name: String) {
        try {
            binding.currentChannelName.text = name
            binding.currentChannelName.visibility = View.VISIBLE
            resetHideTimer()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing channel name", e)
        }
    }

    private fun hideChannelName() {
        try {
            binding.currentChannelName.visibility = View.GONE
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding channel name", e)
        }
    }

    private fun showPanel() {
        try {
            isPanelVisible = true
            binding.channelsPanel.visibility = View.VISIBLE
            binding.channelsRecycler.requestFocus()
            resetHideTimer()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing panel", e)
        }
    }

    private fun hidePanel() {
        try {
            isPanelVisible = false
            binding.channelsPanel.visibility = View.GONE
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding panel", e)
        }
    }

    private fun showNoChannelHint() {
        try {
            binding.noChannelHint.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e(TAG, "Error showing no channel hint", e)
        }
    }

    private fun updateSelectedChannel() {
        try {
            (binding.channelsRecycler.adapter as? ChannelAdapter)?.setSelectedPosition(currentChannelIndex)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating selected channel", e)
        }
    }

    private fun resetHideTimer() {
        try {
            handler.removeCallbacks(hidePanelRunnable)
            handler.postDelayed(hidePanelRunnable, 5000)
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting hide timer", e)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (isPanelVisible) {
                        return false
                    }
                    changeChannel(-1)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (isPanelVisible) {
                        return false
                    }
                    changeChannel(1)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                    if (!isPanelVisible) {
                        showPanel()
                        return true
                    }
                    return false
                }
                KeyEvent.KEYCODE_CHANNEL_UP -> {
                    changeChannel(-1)
                    return true
                }
                KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                    changeChannel(1)
                    return true
                }
                KeyEvent.KEYCODE_MENU -> {
                    if (isPanelVisible) {
                        hidePanel()
                    } else {
                        showPanel()
                    }
                    return true
                }
                KeyEvent.KEYCODE_BACK -> {
                    if (isPanelVisible) {
                        hidePanel()
                        return true
                    }
                    return super.onKeyDown(keyCode, event)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling key event", e)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun changeChannel(direction: Int) {
        if (channels.isEmpty()) return

        try {
            var newIndex = currentChannelIndex + direction
            if (newIndex < 0) newIndex = channels.size - 1
            if (newIndex >= channels.size) newIndex = 0

            playChannel(newIndex)
        } catch (e: Exception) {
            Log.e(TAG, "Error changing channel", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            loadChannels()
            player?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            player?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            player?.removeListener(playerListener)
            player?.release()
            player = null
            handler.removeCallbacks(hidePanelRunnable)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
    }
}
