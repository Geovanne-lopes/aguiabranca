package br.com.fiap.aguiabranca.project.infra;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<ProjectDocument, String> {

    boolean existsByIdeaId(String ideaId);

    Optional<ProjectDocument> findByIdeaId(String ideaId);
}
