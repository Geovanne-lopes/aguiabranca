package br.com.fiap.challengeaguiabranca.data.remote.mapper

import br.com.fiap.challengeaguiabranca.data.remote.dto.AdviceSlipDto
import br.com.fiap.challengeaguiabranca.domain.model.DailyInsight

object InsightMapper {

    fun fromAdviceSlip(dto: AdviceSlipDto): DailyInsight =
        DailyInsight(
            id = dto.id,
            message = dto.advice
        )
}
