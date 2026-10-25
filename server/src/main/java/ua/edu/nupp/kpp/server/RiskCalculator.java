package ua.edu.nupp.kpp.server;

import java.util.List;

/** Обчислення інтегрального показника I = 0,40·F1 + 0,35·F2 + 0,25·F3 (усі фактори 0..100). */
public class RiskCalculator {
    public static final double W1 = 0.40;
    public static final double W2 = 0.35;
    public static final double W3 = 0.25;

    /** F1 — повнота реалізації, F2 — результат перевірок, F3 — якість документації. */
    public static double calculate(double f1, double f2, double f3) {
        check(f1, "F1"); check(f2, "F2"); check(f3, "F3");
        return round2(W1 * f1 + W2 * f2 + W3 * f3);
    }

    /** F2 = середня оцінка рецензій; без рецензій дорівнює 0. */
    public static double factor2(List<Double> reviewScores) {
        if (reviewScores == null || reviewScores.isEmpty()) return 0.0;
        double sum = 0;
        for (double score : reviewScores) {
            check(score, "Оцінка рецензії");
            sum += score;
        }
        return round2(sum / reviewScores.size());
    }

    private static void check(double value, String name) {
        if (Double.isNaN(value) || value < 0 || value > 100)
            throw new IllegalArgumentException(name + " має бути в діапазоні 0..100");
    }

    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}
