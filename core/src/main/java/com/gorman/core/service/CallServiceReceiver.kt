package com.gorman.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gorman.chatroom.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallServiceReceiver: BroadcastReceiver() {
    @Inject
    lateinit var serviceRepository: CallServiceRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == CallServiceConstants.ACTION_EXIT.value) {
            serviceRepository.stopService()
            val openIntent = Intent(context, MainActivity::class.java).apply {
                Intent.setFlags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                Intent.putExtra("action", "open_close_screen")
            }
            context?.startActivity(openIntent)
        }
    }
}
