package ua.edu.nupp.kpp.server;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class RiskCalculatorTest {
    @Test
    void weightedSum() {
        assertEquals(85.75, RiskCalculator.calculate(90, 85, 80), 1e-9);
    }

    @Test
    void zeroFactorsGiveZero() {
        assertEquals(0.0, RiskCalculator.calculate(0, 0, 0), 1e-9);
    }

    @Test
    void maxFactorsGiveHundred() {
        assertEquals(100.0, RiskCalculator.calculate(100, 100, 100), 1e-9);
    }

    @Test
    void rejectsFactorAboveRange() {
        assertThrows(IllegalArgumentException.class, () -> RiskCalculator.calculate(101, 50, 50));
    }

    @Test
    void rejectsNegativeFactor() {
        assertThrows(IllegalArgumentException.class, () -> RiskCalculator.calculate(50, -1, 50));
    }

    @Test
    void factor2IsAverageOfReviewScores() {
        assertEquals(75.0, RiskCalculator.factor2(List.of(70.0, 80.0)), 1e-9);
    }

    @Test
    void factor2WithoutReviewsIsZero() {
        assertEquals(0.0, RiskCalculator.factor2(List.of()), 1e-9);
    }
}
