package br.com.fiap.challengeaguiabranca.domain.model

enum class ProjectStatus(val label: String, val colorHex: Long) {
    BACKLOG("Planejamento", 0xFF6B7280),
    IN_DEVELOPMENT("Em execução", 0xFF3B82F6),
    URGENT_DEADLINE("Prazo urgente", 0xFFEF4444),
    AVERAGE_TICKET("Ticket médio", 0xFFF59E0B),
    COMPLETED("Concluído", 0xFF10B981)
}
