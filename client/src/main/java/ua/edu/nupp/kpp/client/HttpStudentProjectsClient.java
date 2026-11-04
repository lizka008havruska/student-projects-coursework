package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Єдиний клас, через який JavaFX звертається до сервера: HTTP GET/POST + JSON.
 * Тут немає JDBC, SQL і правил предметної області.
 */
public class HttpStudentProjectsClient {
    public static final String BASE =
            System.getProperty("api.base", "http://localhost:8080/student-projects-server");

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // ---------- Студенти ----------
    public List<Student> getStudents() {
        return getList("/students", new TypeReference<List<Student>>() { });
    }

    public void addStudent(String fullName, String groupName, String email) {
        post("/students", params("action", "add", "fullName", fullName, "groupName", groupName, "email", email));
    }

    public void updateStudent(long id, String fullName, String groupName, String email) {
        post("/students", params("action", "update", "id", String.valueOf(id),
                "fullName", fullName, "groupName", groupName, "email", email));
    }

    public void deleteStudent(long id) {
        post("/students", params("action", "delete", "id", String.valueOf(id)));
    }

    // ---------- Проєкти ----------
    public List<Project> getProjects() {
        return getList("/projects", new TypeReference<List<Project>>() { });
    }

    public void addProject(String title, String description, String technology, int difficulty) {
        post("/projects", params("action", "add", "title", title, "description", description,
                "technology", technology, "difficulty", String.valueOf(difficulty)));
    }

    public void updateProject(long id, String title, String description, String technology, int difficulty) {
        post("/projects", params("action", "update", "id", String.valueOf(id), "title", title,
                "description", description, "technology", technology, "difficulty", String.valueOf(difficulty)));
    }

    public void deleteProject(long id) {
        post("/projects", params("action", "delete", "id", String.valueOf(id)));
    }

    // ---------- Подання ----------
    public List<Submission> getSubmissions() {
        return getList("/submissions", new TypeReference<List<Submission>>() { });
    }

    public void addSubmission(long projectId, long studentId, String repoUrl) {
        post("/submissions", params("action", "add", "projectId", String.valueOf(projectId),
                "studentId", String.valueOf(studentId), "repoUrl", repoUrl));
    }

    public void updateSubmission(long id, long projectId, long studentId, String repoUrl) {
        post("/submissions", params("action", "update", "id", String.valueOf(id),
                "projectId", String.valueOf(projectId), "studentId", String.valueOf(studentId), "repoUrl", repoUrl));
    }

    public void deleteSubmission(long id) {
        post("/submissions", params("action", "delete", "id", String.valueOf(id)));
    }

    /** Клієнт лише передає бажаний стан; чи дозволений перехід — вирішує сервер. */
    public void changeSubmissionState(long id, SubmissionState state) {
        post("/submissions", params("action", "transition", "id", String.valueOf(id), "state", state.name()));
    }

    public void setSubmissionFactors(long id, double f1, double f3) {
        post("/submissions", params("action", "factors", "id", String.valueOf(id),
                "f1", String.valueOf(f1), "f3", String.valueOf(f3)));
    }

    // ---------- Рецензії ----------
    public List<Review> getReviews() {
        return getList("/reviews", new TypeReference<List<Review>>() { });
    }

    public void addReview(long submissionId, String reviewer, String comment, double score, String reviewDate) {
        post("/reviews", params("action", "add", "submissionId", String.valueOf(submissionId),
                "reviewer", reviewer, "comment", comment, "score", String.valueOf(score), "reviewDate", reviewDate));
    }

    public void updateReview(long id, long submissionId, String reviewer, String comment, double score, String reviewDate) {
        post("/reviews", params("action", "update", "id", String.valueOf(id),
                "submissionId", String.valueOf(submissionId), "reviewer", reviewer, "comment", comment,
                "score", String.valueOf(score), "reviewDate", reviewDate));
    }

    public void deleteReview(long id) {
        post("/reviews", params("action", "delete", "id", String.valueOf(id)));
    }

    // ---------- Низький рівень ----------
    private <T> List<T> getList(String path, TypeReference<List<T>> type) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(10)).GET().build();
        String json = send(request);
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new ApiException("Некоректна відповідь сервера: " + e.getOriginalMessage());
        }
    }

    private String post(String path, Map<String, String> params) {
        String body = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                throw new ApiException(errorText(response.body(), response.statusCode()));
            }
            return response.body();
        } catch (IOException e) {
            throw new ApiException("Сервер недоступний (" + BASE + "). Перевірте, чи запущено Tomcat. " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Запит перервано");
        }
    }

    private String errorText(String body, int status) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node != null && node.get("error") != null) return node.get("error").asText();
        } catch (IOException ignored) {
            // тіло відповіді не є JSON — повертаємо код статусу
        }
        return "Помилка сервера, код " + status;
    }

    /** Пари «ім'я, значення»; значення null пропускаються. */
    private static Map<String, String> params(String... keyValues) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            if (keyValues[i + 1] != null) map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
