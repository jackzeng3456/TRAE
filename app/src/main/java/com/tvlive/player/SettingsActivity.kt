package com.tvlive.player

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tvlive.player.api.ApiResponse
import com.tvlive.player.api.RetrofitClient
import com.tvlive.player.database.ChannelDatabase
import com.tvlive.player.databinding.ActivitySettingsBinding
import com.tvlive.player.model.Channel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var channelDatabase: ChannelDatabase

    companion object {
        private const val PREFS_NAME = "TVLiveSettings"
        private const val KEY_SERVER_ADDRESS = "server_address"
        private const val DEFAULT_SERVER = "http://192.168.1.100:8080"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        channelDatabase = ChannelDatabase(this)

        loadServerAddress()
        setupButtons()
    }

    private fun loadServerAddress() {
        val savedAddress = sharedPreferences.getString(KEY_SERVER_ADDRESS, DEFAULT_SERVER)
        binding.etServerAddress.setText(savedAddress)
    }

    private fun setupButtons() {
        binding.btnSaveServer.setOnClickListener {
            val address = binding.etServerAddress.text.toString().trim()
            if (address.isNotEmpty()) {
                sharedPreferences.edit().putString(KEY_SERVER_ADDRESS, address).apply()
                RetrofitClient.setBaseUrl(address)
                Toast.makeText(this, "服务器地址已保存", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSyncChannels.setOnClickListener {
            syncChannels()
        }
    }

    private fun syncChannels() {
        val address = binding.etServerAddress.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "请先设置服务器地址", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.setBaseUrl(address)
        val apiService = RetrofitClient.getApiService()

        apiService.getChannels().enqueue(object : Callback<List<Channel>> {
            override fun onResponse(call: Call<List<Channel>>, response: Response<List<Channel>>) {
                if (response.isSuccessful && response.body() != null) {
                    val channels = response.body()!!
                    channelDatabase.replaceAllChannels(channels)
                    Toast.makeText(this@SettingsActivity, "同步成功，共${channels.size}个频道", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SettingsActivity, "同步失败：${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Channel>>, t: Throwable) {
                Toast.makeText(this@SettingsActivity, "同步失败：${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
