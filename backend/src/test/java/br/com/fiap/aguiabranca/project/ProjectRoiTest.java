package br.com.fiap.aguiabranca.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

import br.com.fiap.aguiabranca.project.domain.ProjectRoi;

class ProjectRoiTest {

    @Test
    void zeroInvestmentYieldsZeroRoi() {
        assertThat(ProjectRoi.percent(0, 100)).isEqualTo(0.0);
        assertThat(ProjectRoi.percent(-10, 100)).isEqualTo(0.0);
    }

    @Test
    void profitBelowInvestmentIsNegativeRoi() {
        assertThat(ProjectRoi.percent(200, 100)).isCloseTo(-50.0, within(0.000001));
    }

    @Test
    void profitAboveInvestmentMatchesAcceptanceExample() {
        assertThat(ProjectRoi.percent(1000, 1500)).isCloseTo(50.0, within(0.000001));
    }
}
