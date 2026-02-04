package com.example.spaceinvaders

data class GameState(
    val player: Player = Player(0f),
    val aliens: List<Alien> = createAliens(),
    val bullet: Bullet = Bullet(0f, -1f),
    val enemyBullets: List<Bullet> = emptyList(),
    val obstacles: List<Obstacle> = createObstacles(),
    val score: Int = 0,
    val wave: Int = 1,
    val alienSpeed: Float = 1f,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false
)

fun createAliens(): List<Alien> {
    val rows = 5
    val cols = 8
    return List(rows * cols) { index ->
        val row = index / cols
        val col = index % cols
        Alien(
            x = col * 100f + 60f,
            y = row * 80f + 40f
        )
    }
}

fun createObstacles(): List<Obstacle> {
    val clusters = 4
    val obstaclePerClusterX = 3
    val obstaclePerClusterY = 2
    val spacing = 5f
    val startY = 1800f
    val clusterWidth = obstaclePerClusterX * 20f + (obstaclePerClusterX - 1) * spacing

    val obstacles = mutableListOf<Obstacle>()

    for (c in 0 until clusters) {
        val clusterX = (c + 1) * 200f - clusterWidth / 2
        for (i in 0 until obstaclePerClusterX) {
            for (j in 0 until obstaclePerClusterY) {
                val x = clusterX + i * (20f + spacing)
                val y = startY + j * (20f + spacing)
                obstacles.add(Obstacle(x, y))
            }
        }
    }

    return obstacles
}

data class Player(val x: Float)
data class Alien(val x: Float, val y: Float)
data class Bullet(val x: Float, val y: Float, val isFromEnemy: Boolean = false)
data class Obstacle(
    val x: Float,
    val y: Float,
    val width: Float = 20f,
    val height: Float = 20f
)
