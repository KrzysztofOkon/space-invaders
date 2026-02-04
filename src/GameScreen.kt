package com.example.spaceinvaders

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun GameScreen(vm: GameViewModel,onExitToMenu: () -> Unit) {
    val state by vm.gameState
    val ctx = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        if (!vm.hasStarted) {
            vm.startGame()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> vm.pauseGame()
                Lifecycle.Event.ON_RESUME -> vm.resumeGame()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var initialized by remember { mutableStateOf(false) }


    val alienBitmap = remember {
        BitmapFactory.decodeStream(ctx.assets.open("alien1.png")).asImageBitmap()
    }
    val alienAndroidBitmap = remember { alienBitmap.asAndroidBitmap() }
    val paint = remember { Paint() }

    Box(Modifier.fillMaxSize()) {

        Button(
            onClick = { vm.pauseGame() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .zIndex(1f)
        ) {
            Text("Pause")
        }



        Canvas(
            Modifier
                .fillMaxSize()
                .onSizeChanged { sz ->
                    vm.updateScreenSize(sz.width.toFloat(), sz.height.toFloat())
                    if (!initialized && sz.width > 0 && sz.height > 0) {
                        initialized = true
                    }

                }
                .pointerInput(state.isRunning && !state.isPaused) {
                    if (state.isRunning && !state.isPaused) {
                        detectDragGestures { change, drag ->
                            vm.movePlayer(state.player.x + drag.x)
                            change.consume()
                        }
                    }
                }


        ) {
            if (state.isRunning) {
                // Player
                drawRect(
                    Color.Green,
                    topLeft = Offset(state.player.x, size.height - 50f),
                    size = Size(50f, 20f)
                )

                // Aliens
                drawIntoCanvas { canvas ->
                    state.aliens.forEach { a ->
                        val rect = android.graphics.RectF(
                            a.x,
                            a.y,
                            a.x + 50f,
                            a.y + 30f
                        )
                        canvas.nativeCanvas.drawBitmap(
                            alienAndroidBitmap,
                            null,
                            rect,
                            paint.asFrameworkPaint()
                        )
                    }
                }

                // Player bullet
                drawRect(
                    Color.Blue,
                    topLeft = Offset(state.bullet.x, state.bullet.y),
                    size = Size(10f, 30f)
                )

                // Enemy bullets
                state.enemyBullets.forEach { b ->
                    drawRect(
                        Color.Magenta,
                        topLeft = Offset(b.x, b.y),
                        size = Size(10f, 25f)
                    )
                }

                // Obstacles
                state.obstacles.forEach { obs ->
                    drawRect(
                        Color.Gray,
                        topLeft = Offset(obs.x, obs.y),
                        size = Size(obs.width, obs.height)
                    )
                }
            }
        }

        if (state.isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .zIndex(2f),
                contentAlignment = Alignment.Center
            )
 {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PAUSED", fontSize = 48.sp, color = Color.White)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = { vm.resumeGame() }) {
                        Text("Resume")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        vm.startGame()
                    }) {
                        Text("Restart")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        vm.pauseGame()
                        onExitToMenu()
                    }) {
                        Text("Main Menu")
                    }
                }
            }
        }


        // Score and wave
        Text(
            "Score: ${state.score}",
            color = Color.Red,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            "Wave: ${state.wave}",
            color = Color.Red,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
        )

        // Game Over screen
        if (state.isGameOver && initialized) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("GAME OVER", fontSize = 48.sp, color = Color.Red)
                Text("Score: ${state.score}", fontSize = 32.sp, color = Color.Red)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { vm.startGame() }) {
                    Text("Restart")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    vm.pauseGame()
                    onExitToMenu()
                }) {
                    Text("Main Menu")
                }
            }
        }}}

