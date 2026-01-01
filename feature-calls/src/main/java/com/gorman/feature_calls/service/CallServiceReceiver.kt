package com.gorman.feature_calls.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallServiceReceiver: BroadcastReceiver() {
    @Inject
    lateinit var serviceRepository: CallServiceRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == CallServiceConstants.ACTION_EXIT.value) {
            serviceRepository.stopService()
            context?.let { ctx ->
                val intentToMain = Intent().apply {
                    setClassName(ctx.packageName, "com.gorman.chatroom.MainActivity")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("action", "open_close_screen")
                }
                ctx.startActivity(intentToMain)
            }
        }
    }
}
