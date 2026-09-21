package br.com.fiap.aguiabranca.insight.infra;

public record AdviceSlipPayload(Slip slip) {

    public record Slip(int id, String advice) {
    }
}
