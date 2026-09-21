package br.com.fiap.aguiabranca.guideline.infra;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuidelineRepository extends MongoRepository<GuidelineDocument, String> {

    boolean existsByTitle(String title);

    Optional<GuidelineDocument> findByTitle(String title);
}
