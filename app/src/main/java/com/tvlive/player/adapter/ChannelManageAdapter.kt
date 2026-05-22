package com.tvlive.player.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tvlive.player.R
import com.tvlive.player.model.Channel

class ChannelManageAdapter(
    private val channels: MutableList<Channel>,
    private val onEditClick: (Channel) -> Unit,
    private val onDeleteClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelManageAdapter.ChannelManageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelManageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_manage, parent, false)
        return ChannelManageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelManageViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size

    fun removeChannel(channel: Channel) {
        val position = channels.indexOf(channel)
        if (position != -1) {
            channels.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class ChannelManageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val channelName: TextView = itemView.findViewById(R.id.manage_channel_name)
        private val channelUrl: TextView = itemView.findViewById(R.id.manage_channel_url)
        private val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: Button = itemView.findViewById(R.id.btn_delete)

        fun bind(channel: Channel) {
            channelName.text = channel.name
            channelUrl.text = "${channel.streamType.displayName} - ${channel.streamUrl}"
            
            btnEdit.setOnClickListener {
                onEditClick(channel)
            }
            
            btnDelete.setOnClickListener {
                onDeleteClick(channel)
            }
        }
    }
}
