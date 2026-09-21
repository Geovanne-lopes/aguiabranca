package br.com.fiap.aguiabranca.ai.infra;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.fiap.aguiabranca.ai.domain.InsightRecordStatus;

public interface AiInsightRepository extends MongoRepository<AiInsightDocument, String> {

    List<AiInsightDocument> findByRequestedByUserIdAndStatusInOrderByCreatedAtDesc(
            String requestedByUserId,
            Collection<InsightRecordStatus> statuses
    );
}
