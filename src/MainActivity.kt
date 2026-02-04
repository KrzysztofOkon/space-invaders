package com.example.spaceinvaders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spaceinvaders.ui.theme.SpaceInvadersTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpaceInvadersTheme {

                val vm: GameViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return GameViewModel(applicationContext) as T
                        }
                    }
                )

                var screen by remember { mutableStateOf(UiScreen.MENU) }

                when (screen) {
                    UiScreen.MENU -> MainMenuScreen(
                        onStartGame = { screen = UiScreen.GAME },
                        onHighScores = { screen = UiScreen.HIGHSCORES }
                    )

                    UiScreen.GAME -> GameScreen(
                        vm = vm,
                        onExitToMenu = { screen = UiScreen.MENU }
                    )

                    UiScreen.HIGHSCORES -> HighScoreScreen(
                        onBack = { screen = UiScreen.MENU }
                    )
                }
            }
        }
    }
}
