package com.example.spaceinvaders

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(
    private val appContext: Context
) : ViewModel() {

    var gameState = mutableStateOf(GameState())
        private set

    var hasStarted = false
        private set

    private var screenWidth = 0f
    private var screenHeight = 0f
    private var gameJob: Job? = null
    private var alienDirection = 10f
    private var scoreSaved = false

    fun updateScreenSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
    }

    fun startGame() {
        if (screenWidth == 0f || screenHeight == 0f) return

        hasStarted = true
        scoreSaved = false
        gameJob?.cancel()
        alienDirection = 10f

        gameState.value = GameState(
            player = Player(screenWidth / 2f),
            aliens = createAliens(),
            bullet = Bullet(screenWidth / 2f, screenHeight - 70f),
            enemyBullets = emptyList(),
            obstacles = createObstacles(),
            score = 0,
            wave = 1,
            alienSpeed = 1f,
            isRunning = true,
            isPaused = false,
            isGameOver = false
        )

        gameLoop()
    }

    fun movePlayer(newX: Float) {
        gameState.value = gameState.value.copy(
            player = gameState.value.player.copy(
                x = newX.coerceIn(0f, screenWidth - 50f)
            )
        )
    }

    fun pauseGame() {
        gameJob?.cancel()
        gameState.value = gameState.value.copy(isPaused = true)
    }

    fun resumeGame() {
        if (!gameState.value.isPaused || gameState.value.isGameOver) return
        gameState.value = gameState.value.copy(isPaused = false)
        gameLoop()
    }

    private fun gameLoop() {
        gameJob = viewModelScope.launch {
            while (gameState.value.isRunning && !gameState.value.isPaused) {
                updateGame()
                delay(50L)
            }
        }
    }

    private fun updateGame() {
        val state = gameState.value
        val speed = state.alienSpeed
        var shouldMoveDown = false

        // Move aliens
        val aliensMoved = state.aliens.map {
            val newX = it.x + speed * alienDirection
            if (newX < 0f || newX > screenWidth - 60f) shouldMoveDown = true
            it.copy(x = newX)
        }

        val aliensFinal = if (shouldMoveDown) {
            alienDirection *= -1
            aliensMoved.map {
                it.copy(
                    x = it.x + alienDirection * speed,
                    y = it.y + 40f
                )
            }
        } else aliensMoved

        // Player bullet
        var bullet = state.bullet
        bullet =
            if (bullet.y < 0f) Bullet(state.player.x + 20f, screenHeight - 70f)
            else bullet.copy(y = bullet.y - 50f)

        // Enemy bullets
        val enemyBullets = state.enemyBullets.mapNotNull {
            if (it.y < screenHeight + 50f) it.copy(y = it.y + 25f) else null
        }.toMutableList()

        // Bullet hits alien
        val hitAlien = aliensFinal.firstOrNull { isCollision(it, bullet) }
        val aliensAfterHit = aliensFinal.filterNot { it == hitAlien }
        if (hitAlien != null) {
            bullet = Bullet(state.player.x + 20f, screenHeight - 70f)
        }

        // Obstacles
        val obstacles = state.obstacles.toMutableList()

        // Player bullet vs obstacles
        obstacles.removeAll {
            val hit = isCollisionWithObstacle(bullet, it)
            if (hit) bullet = Bullet(state.player.x + 20f, screenHeight - 70f)
            hit
        }

        // Enemy bullets vs obstacles
        val filteredEnemyBullets = mutableListOf<Bullet>()
        enemyBullets.forEach { b ->
            var hit = false
            obstacles.removeAll {
                val c = isCollisionWithObstacle(b, it)
                if (c) hit = true
                c
            }
            if (!hit) filteredEnemyBullets.add(b)
        }

        // Enemy shooting
        if (aliensAfterHit.isNotEmpty() && (0..100).random() < 5) {
            val shooter = aliensAfterHit.random()
            filteredEnemyBullets.add(
                Bullet(shooter.x + 20f, shooter.y + 30f, true)
            )
        }

        // Player hit?
        val playerHit = filteredEnemyBullets.any { isCollisionWithPlayer(it) }
        val alienReached = aliensAfterHit.any { it.y >= screenHeight - 70f }
        val isGameOver = playerHit || alienReached

        val newScore =
            state.score + if (hitAlien != null) 10 * state.wave else 0

        // Save score

        if (isGameOver && !scoreSaved) {
            scoreSaved = true
            HighScoreManager.saveScore(
                appContext,
                newScore,
                state.wave
            )
        }

        // Next wave
        if (aliensAfterHit.isEmpty()) {
            gameState.value = state.copy(
                aliens = createAliens(),
                bullet = Bullet(state.player.x + 20f, screenHeight - 70f),
                enemyBullets = emptyList(),
                obstacles = createObstacles(),
                score = newScore,
                wave = state.wave + 1,
                alienSpeed = state.alienSpeed + 0.5f,
                isRunning = true,
                isGameOver = false
            )
            return
        }

        gameState.value = state.copy(
            aliens = aliensAfterHit,
            bullet = bullet,
            enemyBullets = filteredEnemyBullets,
            obstacles = obstacles,
            score = newScore,
            isRunning = !isGameOver,
            isGameOver = isGameOver
        )
    }

    // COLLISIONS

    private fun isCollision(a: Alien, b: Bullet): Boolean {
        val dx = b.x + 5f - (a.x + 25f)
        val dy = b.y + 15f - (a.y + 15f)
        return dx * dx + dy * dy < 900f
    }

    private fun isCollisionWithPlayer(b: Bullet): Boolean {
        val px = gameState.value.player.x
        val py = screenHeight - 50f

        return px < b.x + 10f &&
                px + 50f > b.x &&
                py < b.y + 30f &&
                py + 20f > b.y
    }

    private fun isCollisionWithObstacle(b: Bullet, o: Obstacle): Boolean {
        val h = if (b.isFromEnemy) 30f else 50f
        return o.x < b.x + 10f &&
                o.x + o.width > b.x &&
                o.y < b.y + h &&
                o.y + o.height > b.y
    }
}
