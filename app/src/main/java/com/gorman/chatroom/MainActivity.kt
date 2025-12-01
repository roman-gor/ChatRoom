package com.gorman.chatroom

import android.Manifest
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.chatroom.ui.navigation.AppNavigation
import com.gorman.chatroom.ui.ui.theme.ChatRoomTheme
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel
import com.gorman.chatroom.ui.viewmodel.MoreScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
            ChatRoomTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    var hasNotificationPermission by remember {
                        mutableStateOf(
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        )
                    }

                    val permissionLauncher =
                        rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission(),
                            onResult = { isGranted ->
                                hasNotificationPermission = isGranted
                                if (!isGranted) {
                                    TODO()
                                }
                            }
                        )
                    LaunchedEffect(key1 = true) {
                        if (!hasNotificationPermission) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    AppNavigation(onLangChange = { newLang ->
                        viewModel.changeLanguage(newLang)
                        setLocaleAndRestart(newLang, this)
                    })
                }
            }
        }
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
}