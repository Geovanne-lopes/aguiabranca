package br.com.fiap.challengeaguiabranca.domain.util

import java.util.UUID

object IdGenerator {
    fun newId(): String = UUID.randomUUID().toString()
}
