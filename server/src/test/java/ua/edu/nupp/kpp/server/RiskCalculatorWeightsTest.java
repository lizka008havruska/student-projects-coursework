package ua.edu.nupp.kpp.server;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class RiskCalculatorWeightsTest {
    @Test
    void weightsSumToOne() {
        assertEquals(1.0, RiskCalculator.W1 + RiskCalculator.W2 + RiskCalculator.W3, 1e-9);
    }

    @Test
    void resultIsRoundedToTwoDecimals() {
        // 0,40·33,333 + 0,35·66,666 + 0,25·11,111 = 13,3332 + 23,3331 + 2,77775 = 39,44405 → 39,44
        assertEquals(39.44, RiskCalculator.calculate(33.333, 66.666, 11.111), 1e-9);
    }

    @Test
    void factorsAffectResultByTheirWeights() {
        assertEquals(40.0, RiskCalculator.calculate(100, 0, 0), 1e-9);
        assertEquals(35.0, RiskCalculator.calculate(0, 100, 0), 1e-9);
        assertEquals(25.0, RiskCalculator.calculate(0, 0, 100), 1e-9);
    }

    @Test
    void factor2RejectsScoreOutsideRange() {
        assertThrows(IllegalArgumentException.class, () -> RiskCalculator.factor2(List.of(50.0, 101.0)));
    }
}
