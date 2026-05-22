package com.tvlive.player

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.tvlive.player.adapter.ChannelAdapter
import com.tvlive.player.database.ChannelDatabase
import com.tvlive.player.databinding.ActivityMainBinding
import com.tvlive.player.model.Channel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SimpleMainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null
    private lateinit var channelDatabase: ChannelDatabase
    private var channels: List<Channel> = emptyList()
    private var currentChannelIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "onCreate started")
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Log.d(TAG, "Layout inflated")

            channelDatabase = ChannelDatabase(this)
            Log.d(TAG, "Database initialized")

            loadChannels()
            Log.d(TAG, "Channels loaded")

            setupButtons()
            Log.d(TAG, "Buttons setup")

            Log.d(TAG, "onCreate completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }

    private fun loadChannels() {
        try {
            channels = channelDatabase.getAllChannels()
            Log.d(TAG, "Loaded ${channels.size} channels")

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
        if (index < 0 || index >= channels.size) {
            Log.w(TAG, "Invalid channel index: $index")
            return
        }

        try {
            val channel = channels[index]
            Log.d(TAG, "Playing channel: ${channel.name}")
            currentChannelIndex = index

            showChannelName(channel.name)
            updateSelectedChannel()

            releasePlayer()
            initializePlayer(channel.streamUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error playing channel", e)
        }
    }

    private fun initializePlayer(streamUrl: String) {
        try {
            Log.d(TAG, "Initializing player with URL: $streamUrl")
            
            player = ExoPlayer.Builder(this).build()
            binding.playerView.player = player
            
            val mediaItem = MediaItem.fromUri(streamUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
            
            Log.d(TAG, "Player initialized and playing")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing player", e)
        }
    }

    private fun releasePlayer() {
        try {
            player?.release()
            player = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing player", e)
        }
    }

    private fun showChannelName(name: String) {
        try {
            binding.currentChannelName.text = name
            binding.currentChannelName.visibility = View.VISIBLE
            
            binding.currentChannelName.postDelayed({
                try {
                    binding.currentChannelName.visibility = View.GONE
                } catch (e: Exception) {
                    Log.e(TAG, "Error hiding channel name", e)
                }
            }, 3000)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing channel name", e)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            Log.d(TAG, "Key pressed: $keyCode")
            
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
                    changeChannel(-1)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                    changeChannel(1)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_MENU -> {
                    togglePanel()
                    return true
                }
                KeyEvent.KEYCODE_BACK -> {
                    if (binding.channelsPanel.visibility == View.VISIBLE) {
                        hidePanel()
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling key event", e)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun togglePanel() {
        try {
            if (binding.channelsPanel.visibility == View.VISIBLE) {
                hidePanel()
            } else {
                showPanel()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling panel", e)
        }
    }

    private fun showPanel() {
        try {
            binding.channelsPanel.visibility = View.VISIBLE
            binding.channelsRecycler.requestFocus()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing panel", e)
        }
    }

    private fun hidePanel() {
        try {
            binding.channelsPanel.visibility = View.GONE
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding panel", e)
        }
    }

    private fun changeChannel(direction: Int) {
        if (channels.isEmpty()) {
            Log.d(TAG, "No channels to change")
            return
        }

        try {
            var newIndex = currentChannelIndex + direction
            if (newIndex < 0) newIndex = channels.size - 1
            if (newIndex >= channels.size) newIndex = 0

            Log.d(TAG, "Changing channel to index: $newIndex")
            playChannel(newIndex)
        } catch (e: Exception) {
            Log.e(TAG, "Error changing channel", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Log.d(TAG, "onResume")
            loadChannels()
            player?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            Log.d(TAG, "onPause")
            player?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Log.d(TAG, "onDestroy")
            releasePlayer()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
    }
}
