package br.com.fiap.aguiabranca.idea.infra;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IdeaRepository extends MongoRepository<IdeaDocument, String> {
}
