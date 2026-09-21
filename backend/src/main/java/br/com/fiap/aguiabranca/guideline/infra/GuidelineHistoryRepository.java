package br.com.fiap.aguiabranca.guideline.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.fiap.aguiabranca.guideline.domain.GuidelineHistoryAction;

public interface GuidelineHistoryRepository extends MongoRepository<GuidelineHistoryDocument, String> {

    Page<GuidelineHistoryDocument> findByGuidelineId(String guidelineId, Pageable pageable);

    boolean existsByGuidelineIdAndAction(String guidelineId, GuidelineHistoryAction action);
}
