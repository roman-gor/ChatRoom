package com.gorman.chatroom

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
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.chatroom.presentation.navigation.AppNavigation
import com.gorman.chatroom.presentation.ui.theme.ChatRoomTheme
import com.gorman.chatroom.presentation.viewmodel.MainScreenViewModel
import com.gorman.chatroom.presentation.viewmodel.MoreScreenViewModel
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