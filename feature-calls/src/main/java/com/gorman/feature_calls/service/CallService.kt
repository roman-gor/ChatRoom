package com.gorman.feature_calls.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.gorman.core.R
import com.gorman.core.domain.models.CallModel
import com.gorman.core.domain.models.CallModelType
import com.gorman.core.domain.models.isValid
import com.gorman.feature_calls.data.datasource.webrtc.CallAudioManager
import com.gorman.feature_calls.domain.repository.CallRepository
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

private val TAG = CallServiceConstants.SERVICE_NAME.value

@AndroidEntryPoint
class CallService: Service(), CallRepository.Listener {
    @Inject lateinit var callRepository: CallRepository
    private var isServiceRunning = false

    private var savedUsername: String? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var callAudioManager: CallAudioManager
    private var isPreviousCallStateVideo = true

    companion object {
        var listener: Listener? = null
        var endCallListener:EndCallListener?=null
        var localSurfaceView: SurfaceViewRenderer?=null
        var remoteSurfaceView: SurfaceViewRenderer?=null
        var screenPermissionIntent : Intent?=null
    }

    override fun onCreate() {
        super.onCreate()
        callAudioManager = CallAudioManager(this)
        notificationManager = getSystemService(NotificationManager::class.java)
        showInitialNotification()
    }

    private fun showInitialNotification() {
        val notificationChannel = NotificationChannel(
            "channel1", "foreground", NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, "channel1")
            .setSmallIcon(R.drawable.ic_call)
            .setContentTitle("Подключение...")
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                CallServiceActions.START_SERVICE.name -> handleStartService(incomingIntent)
                CallServiceActions.SETUP_VIEWS.name -> handleSetupViews(incomingIntent)
                CallServiceActions.END_CALL.name -> handleEndCall()
                CallServiceActions.ACCEPT_CALL.name -> handleAcceptCall()
                CallServiceActions.SWITCH_CAMERA.name -> handleSwitchCamera()
                CallServiceActions.TOGGLE_AUDIO.name -> handleToggleAudio(incomingIntent)
                CallServiceActions.TOGGLE_VIDEO.name -> handleToggleVideo(incomingIntent)
                CallServiceActions.TOGGLE_AUDIO_DEVICE.name -> handleToggleAudioDevice(incomingIntent)
                CallServiceActions.TOGGLE_SCREEN_SHARE.name -> handleToggleScreenShare(incomingIntent)
                CallServiceActions.STOP_SERVICE.name -> handleStopService()
                else -> Unit
            }
        }

        return START_STICKY
    }

    private fun handleAcceptCall() {
        callRepository.startCall()
    }

    private fun handleEndCall() {
        callAudioManager.stop()
        if (::callRepository.isInitialized) {
            callRepository.sendEndCall()
        }
        endCallAndRestartRepository()
    }

    private fun handleStartService(incomingIntent: Intent) {
        val username = incomingIntent.getStringExtra("username")
        if (username != null) {
            savedUsername = username
        }
        if (!isServiceRunning) {
            isServiceRunning = true
            startServiceWithNotification()
            callAudioManager.start()
        }
        savedUsername?.let { name ->
            callRepository.listener = this
            callRepository.initWebRTCAndFirebase(name)
            Log.d(TAG, "WebRTC Client re-initialized for user: $name")
        }
    }

    private fun handleStopService() {
        callRepository.endCall()
    }

    private fun handleToggleScreenShare(incomingIntent: Intent) {
        val isStarting = incomingIntent.getBooleanExtra("isStarting", true)
        if (isStarting) {
            if (screenPermissionIntent == null) {
                return
            }
            if (Build.VERSION.SDK_INT >= 34) {
                val notification = NotificationCompat.Builder(this, "channel1")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Screen Sharing Active")
                    .build()

                try {
                    startForeground(
                        1,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or
                                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
                                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } catch (e: Exception) {
                    Log.e("CallService", "${e.message}")
                    return
                }
            }
            if (isPreviousCallStateVideo) {
                callRepository.toggleVideo(true)
            }
            callRepository.setScreenCaptureIntent(screenPermissionIntent!!)
            callRepository.toggleScreenShare(true)

        } else {
            callRepository.toggleScreenShare(false)
            if (isPreviousCallStateVideo) {
                callRepository.toggleVideo(false)
            }
        }
    }

    private fun handleToggleAudioDevice(incomingIntent: Intent) {
        val type = incomingIntent.getStringExtra("type")
        val useSpeaker = type == CallServiceConstants.SPEAKER_PHONE.value
        callAudioManager.setSpeakerphoneOn(useSpeaker)
    }

    private fun handleToggleVideo(incomingIntent: Intent) {
        val shouldBeMuted = incomingIntent.getBooleanExtra("shouldBeMuted",true)
        this.isPreviousCallStateVideo = !shouldBeMuted
        callRepository.toggleVideo(shouldBeMuted)
    }

    private fun handleToggleAudio(incomingIntent: Intent) {
        val shouldBeMuted = incomingIntent.getBooleanExtra("shouldBeMuted",true)
        callRepository.toggleAudio(shouldBeMuted)
    }

    private fun handleSwitchCamera() {
        callRepository.switchCamera()
    }

    private fun endCallAndRestartRepository(){
        if (::callRepository.isInitialized) {
            callRepository.endCall()
        }
        endCallListener?.onCallEnded()
    }

    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun handleSetupViews(incomingIntent: Intent) {
        val isCaller = incomingIntent.getBooleanExtra("isCaller", false)
        val isVideoCall = incomingIntent.getBooleanExtra("isVideoCall", true)
        val target = incomingIntent.getStringExtra("target")
        this.isPreviousCallStateVideo = isVideoCall
        savedUsername?.let { name ->
            callRepository.initWebRTCAndFirebase(name)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val serviceType = if (isVideoCall) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                } else {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                }
                val notification = NotificationCompat.Builder(this, "channel1")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build()
                startForeground(1, notification, serviceType)

            } catch (e: Exception) {
                Log.e(TAG, "Error upgrading foreground service", e)
            }
        }

        val localView = localSurfaceView
        val remoteView = remoteSurfaceView

        if (::callRepository.isInitialized && target != null && localView != null && remoteView != null) {
            callRepository.setTarget(target)
            localView.post {
                callRepository.initLocalSurfaceView(localView, isVideoCall)
                callRepository.initRemoteSurfaceView(remoteView)
                if (!isCaller) {
                    callRepository.startCall()
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startServiceWithNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val notificationChannel = NotificationChannel(
            "channel1", "foreground", NotificationManager.IMPORTANCE_HIGH
        )
        val intent = Intent(this, CallServiceReceiver::class.java).apply {
            action = "ACTION_EXIT"
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, "channel1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(R.drawable.ic_end_call, "Exit", pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                startForeground(
                    1,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } catch (e: Exception) {
                if (Build.VERSION.SDK_INT < 34) {
                    startForeground(1, notification)
                }
                Log.e(TAG, "Error starting foreground service with type", e)
            }
        } else {
            startForeground(1, notification)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLatestEventReceived(data: CallModel) {
        if (data.isValid()) {
            when (data.type) {
                CallModelType.StartVideoCall,
                CallModelType.StartAudioCall -> {
                    listener?.onCallReceived(data)
                }
                CallModelType.EndCall -> {
                    endCall()
                }
                else -> Unit
            }
        }
    }

    override fun endCall() {
        endCallAndRestartRepository()
    }

    interface Listener {
        fun onCallReceived(model: CallModel)
    }

    interface EndCallListener {
        fun onCallEnded()
    }
}
