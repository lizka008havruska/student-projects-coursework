package ua.edu.nupp.kpp.client;

import java.util.List;

/** Підсумок CSV-імпорту: кількість успішних записів і причини пропущених рядків. */
public class ImportResult {
    private final int imported;
    private final List<String> errors;

    public ImportResult(int imported, List<String> errors) {
        this.imported = imported;
        this.errors = List.copyOf(errors);
    }

    public int getImported() { return imported; }

    public int getSkipped() { return errors.size(); }

    public List<String> getErrors() { return errors; }

    public String summary() {
        return "Імпортовано: " + imported + ", пропущено: " + errors.size();
    }

    /** Підсумок та перші 10 причин пропуску — для вікна повідомлення. */
    public String details() {
        StringBuilder sb = new StringBuilder(summary());
        if (!errors.isEmpty()) {
            sb.append("\n\nПричини пропуску:");
            int shown = Math.min(errors.size(), 10);
            for (int i = 0; i < shown; i++) sb.append("\n").append(errors.get(i));
            if (errors.size() > shown) sb.append("\n… і ще ").append(errors.size() - shown);
        }
        return sb.toString();
    }
}
