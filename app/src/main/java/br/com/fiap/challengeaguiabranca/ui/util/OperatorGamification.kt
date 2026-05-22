package br.com.fiap.challengeaguiabranca.ui.util

data class GamificationState(
    val level: Int,
    val points: Int,
    /** 0f–1f até o próximo nível */
    val progressFraction: Float,
    val ideasUntilNextLevel: Int
)

object OperatorGamification {

    const val MAX_LEVEL = 5

    fun fromIdeasCount(ideasCount: Int): GamificationState {
        val level = minOf(MAX_LEVEL, 1 + ideasCount / 2)
        val points = 250 + ideasCount * 200
        if (level >= MAX_LEVEL) {
            return GamificationState(
                level = MAX_LEVEL,
                points = points,
                progressFraction = 1f,
                ideasUntilNextLevel = 0
            )
        }
        val ideasAtLevelStart = (level - 1) * 2
        val ideasAtNextLevel = level * 2
        val span = (ideasAtNextLevel - ideasAtLevelStart).coerceAtLeast(1)
        val progress = ((ideasCount - ideasAtLevelStart).toFloat() / span).coerceIn(0f, 1f)
        val untilNext = (ideasAtNextLevel - ideasCount).coerceAtLeast(0)
        return GamificationState(
            level = level,
            points = points,
            progressFraction = progress,
            ideasUntilNextLevel = untilNext
        )
    }
}
