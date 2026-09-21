package br.com.fiap.aguiabranca.ai.domain;

public final class InsightText {

    public static final String DISCLAIMER =
            "Insight gerado a partir dos dados do sistema; não é um fato independente.";
    public static final int PROMPT_VERSION = 1;

    private InsightText() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > 8000) {
            return trimmed.substring(0, 4000) + "...";
        }
        return trimmed;
    }

    public static String fallback(double overallRoiPercent, double totalInvestment, int totalProjects) {
        return "Não foi possível gerar um insight automático agora. Com base nos dados internos: ROI consolidado de "
                + plain(overallRoiPercent)
                + "%, investimento de "
                + plain(totalInvestment)
                + " e "
                + totalProjects
                + " projeto(s). Revise projetos em execução e o volume de ideias pendentes antes de novas alocações. "
                + "Este texto é um fallback local, não uma análise de IA.";
    }

    private static String plain(double value) {
        if (!Double.isFinite(value)) {
            return "0";
        }
        return Double.toString(value);
    }
}
