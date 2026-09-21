package br.com.fiap.challengeaguiabranca.data.remote.mapper

import java.time.Instant

internal fun String?.toEpochMillis(): Long = toEpochMillisOrNull() ?: 0L

internal fun String?.toEpochMillisOrNull(): Long? {
    if (this.isNullOrBlank()) return null
    return runCatching { Instant.parse(this).toEpochMilli() }.getOrNull()
}

internal fun Long?.toIsoInstant(): String? {
    if (this == null || this <= 0L) return null
    return Instant.ofEpochMilli(this).toString()
}
