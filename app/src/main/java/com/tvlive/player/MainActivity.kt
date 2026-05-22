package com.tvlive.player

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelDatabase = ChannelDatabase(this)
        loadChannels()
        setupButtons()
    }

    private fun loadChannels() {
        channels = channelDatabase.getAllChannels()
        if (channels.isNotEmpty()) {
            setupChannelList()
            playChannel(currentChannelIndex)
        } else {
            showNoChannelHint()
        }
    }

    private fun setupChannelList() {
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
    }

    private fun setupButtons() {
        binding.btnManage.setOnClickListener {
            startActivity(Intent(this, ChannelManageActivity::class.java))
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun playChannel(index: Int) {
        if (index < 0 || index >= channels.size) return

        val channel = channels[index]
        currentChannelIndex = index

        showChannelName(channel.name)
        updateSelectedChannel()

        player?.release()
        player = ExoPlayer.Builder(this).build().also {
            binding.playerView.player = it
            val mediaItem = MediaItem.fromUri(channel.streamUrl)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    private fun showChannelName(name: String) {
        binding.currentChannelName.text = name
        binding.currentChannelName.visibility = View.VISIBLE
        resetHideTimer()
    }

    private fun hideChannelName() {
        binding.currentChannelName.visibility = View.GONE
    }

    private fun showPanel() {
        isPanelVisible = true
        binding.channelsPanel.visibility = View.VISIBLE
        binding.channelsRecycler.requestFocus()
        resetHideTimer()
    }

    private fun hidePanel() {
        isPanelVisible = false
        binding.channelsPanel.visibility = View.GONE
    }

    private fun showNoChannelHint() {
        binding.noChannelHint.visibility = View.VISIBLE
    }

    private fun updateSelectedChannel() {
        (binding.channelsRecycler.adapter as? ChannelAdapter)?.setSelectedPosition(currentChannelIndex)
    }

    private fun resetHideTimer() {
        handler.removeCallbacks(hidePanelRunnable)
        handler.postDelayed(hidePanelRunnable, 5000)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
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
        return super.onKeyDown(keyCode, event)
    }

    private fun changeChannel(direction: Int) {
        if (channels.isEmpty()) return

        var newIndex = currentChannelIndex + direction
        if (newIndex < 0) newIndex = channels.size - 1
        if (newIndex >= channels.size) newIndex = 0

        playChannel(newIndex)
    }

    override fun onResume() {
        super.onResume()
        loadChannels()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
        handler.removeCallbacks(hidePanelRunnable)
    }
}
