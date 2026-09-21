package br.com.fiap.aguiabranca.dashboard.domain;

import java.util.Comparator;
import java.util.List;

public final class RankingSorter {

    private RankingSorter() {
    }

    public static List<RankedAuthor> sort(List<RankedAuthor> authors) {
        return authors.stream()
                .sorted(Comparator.comparingInt(RankedAuthor::ideasSubmitted).reversed()
                        .thenComparing(RankedAuthor::name, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }
}
