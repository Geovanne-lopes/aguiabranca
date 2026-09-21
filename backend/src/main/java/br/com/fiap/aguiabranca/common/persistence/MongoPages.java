package br.com.fiap.aguiabranca.common.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public final class MongoPages {

    private MongoPages() {
    }

    public static <T> Page<T> find(MongoTemplate mongoTemplate, Query query, Pageable pageable, Class<T> type) {
        long total = mongoTemplate.count(query, type);
        List<T> content = mongoTemplate.find(Query.of(query).with(pageable), type);
        return new PageImpl<>(content, pageable, total);
    }
}
