package com.example.spaceinvaders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HighScoreScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scores = HighScoreManager.getScores(context)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "HIGH SCORES",
                fontSize = 40.sp,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (scores.isEmpty()) {
                Text("No scores yet", color = Color.Black)
            } else {
                scores.forEachIndexed { index, hs ->
                    Text(
                        text = "${index + 1}. Score: ${hs.score}  |  Wave: ${hs.wave}",
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onBack) {
                Text("Back to Menu")
            }
        }
    }
}
