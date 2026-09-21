package br.com.fiap.aguiabranca.project.domain;

/**
 * ROI de um projeto. Não é persistido: a resposta HTTP calcula na leitura.
 * I &lt;= 0 produz 0, nunca NaN.
 */
public final class ProjectRoi {

    private ProjectRoi() {
    }

    public static double percent(double investmentAmount, double obtainedProfit) {
        if (!(investmentAmount > 0.0)) {
            return 0.0;
        }
        return ((obtainedProfit - investmentAmount) / investmentAmount) * 100.0;
    }
}
