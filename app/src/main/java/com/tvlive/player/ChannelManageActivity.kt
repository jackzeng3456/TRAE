package com.tvlive.player

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tvlive.player.adapter.ChannelManageAdapter
import com.tvlive.player.database.ChannelDatabase
import com.tvlive.player.databinding.ActivityChannelManageBinding
import com.tvlive.player.model.Channel

class ChannelManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelManageBinding
    private lateinit var channelDatabase: ChannelDatabase
    private lateinit var channels: MutableList<Channel>
    private lateinit var adapter: ChannelManageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelDatabase = ChannelDatabase(this)
        loadChannels()
        setupRecyclerView()
        setupAddButton()
    }

    private fun loadChannels() {
        channels = channelDatabase.getAllChannels().toMutableList()
    }

    private fun setupRecyclerView() {
        binding.manageChannelsRecycler.layoutManager = LinearLayoutManager(this)
        adapter = ChannelManageAdapter(
            channels,
            onEditClick = { channel ->
                showEditDialog(channel)
            },
            onDeleteClick = { channel ->
                showDeleteConfirmDialog(channel)
            }
        )
        binding.manageChannelsRecycler.adapter = adapter
    }

    private fun setupAddButton() {
        binding.btnAdd.setOnClickListener {
            showEditDialog(null)
        }
    }

    private fun showEditDialog(channel: Channel?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_channel_edit, null)
        
        val nameEditText = dialogView.findViewById<EditText>(R.id.et_channel_name)
        val urlEditText = dialogView.findViewById<EditText>(R.id.et_channel_url)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinner_stream_type)

        val streamTypes = listOf("HTTP", "UDP", "RTP")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, streamTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = spinnerAdapter

        if (channel != null) {
            nameEditText.setText(channel.name)
            urlEditText.setText(channel.streamUrl)
            typeSpinner.setSelection(streamTypes.indexOf(channel.streamType.displayName))
        }

        AlertDialog.Builder(this)
            .setTitle(if (channel == null) R.string.add_channel else R.string.edit_channel)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = nameEditText.text.toString().trim()
                val url = urlEditText.text.toString().trim()
                val type = Channel.StreamType.fromDisplayName(typeSpinner.selectedItem as String)

                if (name.isNotEmpty() && url.isNotEmpty()) {
                    if (channel == null) {
                        val newChannel = Channel(name = name, streamUrl = url, streamType = type)
                        channelDatabase.addChannel(newChannel)
                        channels.add(newChannel)
                        adapter.notifyItemInserted(channels.size - 1)
                    } else {
                        channel.name = name
                        channel.streamUrl = url
                        channel.streamType = type
                        channelDatabase.updateChannel(channel)
                        val position = channels.indexOf(channel)
                        adapter.notifyItemChanged(position)
                    }
                } else {
                    Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmDialog(channel: Channel) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_channel)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton("确定") { _, _ ->
                channelDatabase.deleteChannel(channel.id)
                adapter.removeChannel(channel)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
