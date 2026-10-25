package ua.edu.nupp.kpp.server;

/** Спільні перевірки значень для Service-рівня. Порушення правил → IllegalArgumentException. */
final class Validation {
    private Validation() {}

    static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Поле «" + field + "» є обов'язковим");
        if (value.length() > maxLength)
            throw new IllegalArgumentException("Поле «" + field + "» не може перевищувати " + maxLength + " символів");
        return value.trim();
    }

    static void optionalText(String value, String field, int maxLength) {
        if (value != null && value.length() > maxLength)
            throw new IllegalArgumentException("Поле «" + field + "» не може перевищувати " + maxLength + " символів");
    }

    static void range(double value, double min, double max, String field) {
        if (Double.isNaN(value) || value < min || value > max)
            throw new IllegalArgumentException("Поле «" + field + "» має бути в діапазоні " + (long) min + ".." + (long) max);
    }

    static void positiveId(long id, String field) {
        if (id <= 0) throw new IllegalArgumentException("Поле «" + field + "» має бути додатним числом");
    }

    static boolean isEmail(String v) {
        return v != null && v.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
