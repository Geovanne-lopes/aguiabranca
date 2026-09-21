package br.com.fiap.aguiabranca.suggestion.infra;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SuggestionRepository extends MongoRepository<SuggestionDocument, String> {
}
