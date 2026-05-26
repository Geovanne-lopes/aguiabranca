package br.com.fiap.challengeaguiabranca.ui.util

import androidx.compose.ui.graphics.Color
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary

fun parseAvatarColor(avatarUrl: String?): Color {
    if (avatarUrl?.startsWith("color:") == true) {
        val hex = avatarUrl.removePrefix("color:").removePrefix("#")
        return runCatching {
            Color(android.graphics.Color.parseColor("#$hex"))
        }.getOrDefault(InnovatePrimary)
    }
    return InnovatePrimary
}
