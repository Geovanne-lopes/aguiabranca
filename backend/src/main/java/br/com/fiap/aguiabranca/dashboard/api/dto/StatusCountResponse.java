package br.com.fiap.aguiabranca.dashboard.api.dto;

public record StatusCountResponse(
        String status,
        String label,
        int count
) {
}
