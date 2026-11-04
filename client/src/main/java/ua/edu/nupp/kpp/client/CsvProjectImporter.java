package ua.edu.nupp.kpp.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * Початок CSV-імпорту (етап 3): читання файлу, пропуск заголовка, розбір одного рядка.
 * Формат: title;description;technology;difficulty (UTF-8, роздільник «;», перший рядок — заголовок).
 * Фоновий імпорт усіх рядків через Task і ProgressBar буде реалізовано на етапі 4.
 */
public class CsvProjectImporter {
    public static final Path DEFAULT_PATH = Path.of("sample-data", "projects.csv");
    private static final int COLUMNS = 4;

    /** Перший рядок даних (після заголовка). */
    public static Project readFirst(Path path) throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException("Файл не знайдено: " + path
                    + " (запускайте клієнт із каталогу client)");
        }
        for (int i = 1; i < lines.size(); i++) {   // рядок 0 — заголовок
            if (!lines.get(i).isBlank()) return parseLine(lines.get(i));
        }
        throw new IllegalArgumentException("У файлі " + path + " немає рядків даних");
    }

    /** Розбір одного рядка. Некоректний рядок → IllegalArgumentException, об'єкт не створюється. */
    public static Project parseLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != COLUMNS) {
            throw new IllegalArgumentException("Очікується " + COLUMNS + " стовпці, отримано " + parts.length);
        }
        String title = parts[0].trim();
        String technology = parts[2].trim();
        if (title.isEmpty()) throw new IllegalArgumentException("Порожня назва проєкту");
        if (technology.isEmpty()) throw new IllegalArgumentException("Порожня технологія");
        int difficulty;
        try {
            difficulty = Integer.parseInt(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Складність має бути цілим числом: " + parts[3]);
        }
        return new Project(null, title, parts[1].trim(), technology, difficulty);
    }
}
