package ua.edu.nupp.kpp.client;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 * Фоновий імпорт проєктів із CSV. Виконується поза потоком JavaFX, тому інтерфейс не блокується.
 * Рядки розбираються на клієнті лише технічно, а предметну валідацію і запис у MySQL виконує сервер.
 * Помилка в окремому рядку не зупиняє імпорт; недоступність сервера, відсутній файл і
 * неправильний заголовок зупиняють його повністю.
 */
public class ImportProjectsTask extends Task<ImportResult> {
    private static final Logger LOG = Logger.getLogger(ImportProjectsTask.class.getName());
    /** Затримка між рядками, щоб на невеликому файлі було видно хід виконання ProgressBar. */
    private static final long DEMO_DELAY_MS = 300;

    private final HttpStudentProjectsClient api;
    private final Path path;

    public ImportProjectsTask(HttpStudentProjectsClient api, Path path) {
        this.api = api;
        this.path = path;
    }

    @Override
    protected ImportResult call() throws Exception {
        LOG.info("Початок імпорту CSV: " + path);
        updateMessage("Читання файлу…");
        updateProgress(0, 1);

        List<String> lines = readLines();
        CsvProjectImporter.checkHeader(lines.get(0));

        List<Integer> dataRows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) dataRows.add(i);
        }
        if (dataRows.isEmpty()) {
            throw new IllegalArgumentException("У файлі немає рядків даних");
        }

        int total = dataRows.size();
        int imported = 0;
        List<String> errors = new ArrayList<>();
        for (int k = 0; k < total; k++) {
            if (isCancelled()) break;
            int lineNumber = dataRows.get(k) + 1;
            updateMessage("Обробка рядка " + lineNumber + " (" + (k + 1) + " з " + total + ")");
            try {
                Project p = CsvProjectImporter.parseLine(lines.get(dataRows.get(k)));
                api.addProject(p.getTitle(), p.getDescription(), p.getTechnology(), p.getDifficulty());
                imported++;
            } catch (IllegalArgumentException e) {
                skip(errors, lineNumber, e.getMessage());
            } catch (ApiException e) {
                if (e.isUnavailable()) {
                    LOG.severe("Імпорт перервано: " + e.getMessage());
                    throw e;
                }
                skip(errors, lineNumber, e.getMessage());
            }
            updateProgress(k + 1, total);
            Thread.sleep(DEMO_DELAY_MS);
        }

        LOG.info("Імпорт завершено: імпортовано " + imported + ", пропущено " + errors.size());
        updateMessage("Імпорт завершено");
        return new ImportResult(imported, errors);
    }

    private List<String> readLines() throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException("CSV-файл не знайдено: " + path);
        } catch (MalformedInputException e) {
            throw new IllegalArgumentException("Файл " + path + " не є коректним UTF-8");
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній: " + path);
        }
        return lines;
    }

    private static void skip(List<String> errors, int lineNumber, String reason) {
        String text = "Рядок " + lineNumber + ": " + reason;
        errors.add(text);
        LOG.warning("Пропущено. " + text);
    }
}
