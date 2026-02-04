package com.example.spaceinvaders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen(
    onStartGame: () -> Unit,
    onHighScores: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "SPACE INVADERS",
                fontSize = 48.sp,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onStartGame) {
                Text("Start Game", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onHighScores) {
                Text("High Scores", fontSize = 20.sp)
            }
        }
    }
}
