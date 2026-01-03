package com.gorman.chatroom

import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.gorman.chatroom.ui.navigation.AppNavigation
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel
import com.gorman.chatroom.ui.viewmodel.MoreScreenViewModel
import com.gorman.core.domain.models.CallModel
import com.gorman.core.domain.models.CallModelType
import com.gorman.core.ui.navigation.Destination
import com.gorman.core.ui.theme.ChatRoomTheme
import com.gorman.feature_calls.service.CallService
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mainScreenViewModel: MainScreenViewModel by viewModels()
        splashScreen.setKeepOnScreenCondition {
            !mainScreenViewModel.isUserIdLoaded.value
        }
        setContent {
            val viewModel: MoreScreenViewModel = hiltViewModel()
            val settingsState by viewModel.uiState.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            CallService.listener = object : CallService.Listener {
                override fun onCallReceived(model: CallModel) {
                    val isVideo = model.type == CallModelType.StartVideoCall
                    navController.navigate(Destination.IncomingCall(
                        senderId = model.sender ?: "",
                        isVideo = isVideo
                    ))
                }
            }
            CallService.endCallListener = object : CallService.EndCallListener {
                override fun onCallEnded() {
                    runOnUiThread {
                        navController.navigate(Destination.MainGraph) {
                            popUpTo<Destination.MainGraph> { inclusive = true }
                        }
                    }
                }
            }
            ChatRoomTheme(darkTheme = settingsState.isDarkMode) {
                CheckNotificationsPermission()
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    AppNavigation(
                        navController = navController,
                        onLangChange = { newLang ->
                            viewModel.changeLanguage(newLang)
                            setLocaleAndRestart(newLang, this)
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CallService.listener = null
        CallService.endCallListener = null
    }

    private fun setLocaleAndRestart(lang: String, context: Context) {
        if (lang != context.resources.configuration.locales[0].language) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val localeManager = getSystemService(LocaleManager::class.java)
                localeManager.applicationLocales = LocaleList(Locale.forLanguageTag(lang))
            } else {
                @Suppress("DEPRECATION")
                val locale = Locale(lang)
                Locale.setDefault(locale)
                val config = resources.configuration
                config.setLocale(locale)
                @Suppress("DEPRECATION")
                resources.updateConfiguration(config, resources.displayMetrics)
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("ComposeUnstableReceiver")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun CheckNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionState = rememberPermissionState(
                Manifest.permission.POST_NOTIFICATIONS
            )
            LaunchedEffect(key1 = notificationPermissionState.status) {
                if (!notificationPermissionState.status.isGranted) {
                    notificationPermissionState.launchPermissionRequest()
                }
            }
            if (notificationPermissionState.status.shouldShowRationale) { TODO() }
        }
    }
}
