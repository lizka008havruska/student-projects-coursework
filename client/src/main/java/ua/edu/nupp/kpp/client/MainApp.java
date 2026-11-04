package ua.edu.nupp.kpp.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * JavaFX-клієнт. Усі дані надходять із сервера через HttpStudentProjectsClient (HTTP/JSON).
 * Тут немає JDBC, SQL, перевірки переходів станів і розрахунку інтегрального показника:
 * ці правила виконує сервер, а клієнт лише відображає результат.
 */
public class MainApp extends Application {
    private final HttpStudentProjectsClient api = new HttpStudentProjectsClient();

    private final TableView<Student> studentTable = new TableView<>();
    private final TableView<Project> projectTable = new TableView<>();
    private final TableView<Submission> submissionTable = new TableView<>();
    private final TableView<Review> reviewTable = new TableView<>();

    private final ComboBox<Project> projectBox = new ComboBox<>();
    private final ComboBox<Student> studentBox = new ComboBox<>();
    private final ComboBox<Submission> reviewSubmissionBox = new ComboBox<>();

    private final Map<Long, String> studentNames = new HashMap<>();
    private final Map<Long, String> projectTitles = new HashMap<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(studentTab(), projectTab(), submissionTab(), reviewTab());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        stage.setTitle("Подання та перевірка студентських програмних проєктів");
        stage.setScene(new Scene(tabs, 1150, 680));
        stage.show();
        Platform.runLater(() -> safe(this::refreshAll));
    }

    // ================= Оновлення даних із сервера =================

    private void refreshAll() {
        List<Student> students = api.getStudents();
        studentNames.clear();
        for (Student s : students) studentNames.put(s.getId(), s.getFullName());
        studentTable.getItems().setAll(students);
        studentBox.getItems().setAll(students);

        List<Project> projects = api.getProjects();
        projectTitles.clear();
        for (Project p : projects) projectTitles.put(p.getId(), p.getTitle());
        projectTable.getItems().setAll(projects);
        projectBox.getItems().setAll(projects);

        List<Submission> submissions = api.getSubmissions();
        submissionTable.getItems().setAll(submissions);
        reviewSubmissionBox.getItems().setAll(submissions);

        reviewTable.getItems().setAll(api.getReviews());
    }

    // ================= Вкладка «Студенти» =================

    private Tab studentTab() {
        TextField fullName = new TextField();
        TextField groupName = new TextField();
        TextField email = new TextField();

        studentTable.getColumns().add(col("ID", 60, (Student s) -> String.valueOf(s.getId())));
        studentTable.getColumns().add(col("ПІБ", 320, (Student s) -> nz(s.getFullName())));
        studentTable.getColumns().add(col("Група", 100, (Student s) -> nz(s.getGroupName())));
        studentTable.getColumns().add(col("Email", 280, (Student s) -> nz(s.getEmail())));

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, s) -> {
            if (s == null) return;
            fullName.setText(nz(s.getFullName()));
            groupName.setText(nz(s.getGroupName()));
            email.setText(nz(s.getEmail()));
        });

        Button add = new Button("Додати");
        add.setOnAction(e -> safe(() -> {
            api.addStudent(fullName.getText(), groupName.getText(), email.getText());
            refreshAll();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            Student s = selected(studentTable);
            api.updateStudent(s.getId(), fullName.getText(), groupName.getText(), email.getText());
            refreshAll();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            api.deleteStudent(selected(studentTable).getId());
            refreshAll();
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));

        return buildTab("Студенти", studentTable,
                form("ПІБ", fullName, "Група", groupName, "Email", email),
                new HBox(8, add, update, delete, refresh));
    }

    // ================= Вкладка «Проєкти» =================

    private Tab projectTab() {
        TextField title = new TextField();
        TextField description = new TextField();
        TextField technology = new TextField();
        TextField difficulty = new TextField();

        projectTable.getColumns().add(col("ID", 60, (Project p) -> String.valueOf(p.getId())));
        projectTable.getColumns().add(col("Назва", 300, (Project p) -> nz(p.getTitle())));
        projectTable.getColumns().add(col("Технологія", 130, (Project p) -> nz(p.getTechnology())));
        projectTable.getColumns().add(col("Складність", 90, (Project p) -> String.valueOf(p.getDifficulty())));
        projectTable.getColumns().add(col("Опис", 400, (Project p) -> nz(p.getDescription())));

        projectTable.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p == null) return;
            title.setText(nz(p.getTitle()));
            description.setText(nz(p.getDescription()));
            technology.setText(nz(p.getTechnology()));
            difficulty.setText(String.valueOf(p.getDifficulty()));
        });

        Button add = new Button("Додати");
        add.setOnAction(e -> safe(() -> {
            api.addProject(title.getText(), description.getText(), technology.getText(),
                    parseInt(difficulty.getText(), "Складність"));
            refreshAll();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            Project p = selected(projectTable);
            api.updateProject(p.getId(), title.getText(), description.getText(), technology.getText(),
                    parseInt(difficulty.getText(), "Складність"));
            refreshAll();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            api.deleteProject(selected(projectTable).getId());
            refreshAll();
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));

        // Початок CSV-імпорту (етап 3): читається лише перший рядок даних і передається серверу.
        Button csv = new Button("Імпорт 1-го рядка з CSV");
        csv.setOnAction(e -> safe(() -> {
            Project p = CsvProjectImporter.readFirst(CsvProjectImporter.DEFAULT_PATH);
            api.addProject(p.getTitle(), p.getDescription(), p.getTechnology(), p.getDifficulty());
            refreshAll();
            info("Із CSV імпортовано проєкт: " + p.getTitle());
        }));

        return buildTab("Проєкти", projectTable,
                form("Назва", title, "Технологія", technology, "Опис", description, "Складність (1–5)", difficulty),
                new HBox(8, add, update, delete, refresh, csv));
    }

    // ================= Вкладка «Подання» =================

    private Tab submissionTab() {
        TextField repoUrl = new TextField();
        TextField f1 = new TextField();
        TextField f3 = new TextField();

        projectBox.setConverter(converter((Project p) -> p.getId() + " — " + nz(p.getTitle())));
        studentBox.setConverter(converter((Student s) -> nz(s.getFullName())));
        projectBox.setPrefWidth(300);
        studentBox.setPrefWidth(260);

        ComboBox<SubmissionState> stateBox =
                new ComboBox<>(FXCollections.observableArrayList(SubmissionState.values()));
        stateBox.setConverter(converter((SubmissionState st) -> st.getTitle()));

        submissionTable.getColumns().add(col("ID", 50, (Submission s) -> String.valueOf(s.getId())));
        submissionTable.getColumns().add(col("Проєкт", 220,
                (Submission s) -> projectTitles.getOrDefault(s.getProjectId(), "?")));
        submissionTable.getColumns().add(col("Студент", 200,
                (Submission s) -> studentNames.getOrDefault(s.getStudentId(), "?")));
        submissionTable.getColumns().add(col("Стан", 100, (Submission s) -> stateTitle(s)));
        submissionTable.getColumns().add(col("Дата", 90, (Submission s) -> nz(s.getSubmittedDate())));
        submissionTable.getColumns().add(col("F1", 55, (Submission s) -> fmt(s.getF1())));
        submissionTable.getColumns().add(col("F2", 55, (Submission s) -> fmt(s.getF2())));
        submissionTable.getColumns().add(col("F3", 55, (Submission s) -> fmt(s.getF3())));
        submissionTable.getColumns().add(col("Показник I", 90, (Submission s) -> fmt(s.getIntegralIndex())));
        submissionTable.getColumns().add(col("Репозиторій", 260, (Submission s) -> nz(s.getRepoUrl())));

        submissionTable.getSelectionModel().selectedItemProperty().addListener((obs, old, s) -> {
            if (s == null) return;
            select(projectBox, p -> p.getId() == s.getProjectId());
            select(studentBox, st -> st.getId() == s.getStudentId());
            repoUrl.setText(nz(s.getRepoUrl()));
            stateBox.getSelectionModel().select(s.getState());
            f1.setText(String.valueOf(s.getF1()));
            f3.setText(String.valueOf(s.getF3()));
        });

        Button add = new Button("Додати (ЧЕРНЕТКА)");
        add.setOnAction(e -> safe(() -> {
            Project p = projectBox.getValue();
            Student st = studentBox.getValue();
            if (p == null || st == null) throw new IllegalArgumentException("Виберіть проєкт і студента");
            api.addSubmission(p.getId(), st.getId(), repoUrl.getText());
            refreshAll();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            Submission s = selected(submissionTable);
            Project p = projectBox.getValue();
            Student st = studentBox.getValue();
            if (p == null || st == null) throw new IllegalArgumentException("Виберіть проєкт і студента");
            api.updateSubmission(s.getId(), p.getId(), st.getId(), repoUrl.getText());
            refreshAll();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            api.deleteSubmission(selected(submissionTable).getId());
            refreshAll();
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));

        Button transition = new Button("Перевести у вибраний стан");
        transition.setOnAction(e -> safe(() -> {
            Submission s = selected(submissionTable);
            SubmissionState target = stateBox.getValue();
            if (target == null) throw new IllegalArgumentException("Виберіть стан");
            api.changeSubmissionState(s.getId(), target);   // перевірку переходу виконує сервер
            refreshAll();
        }));
        Button factors = new Button("Виставити F1 і F3");
        factors.setOnAction(e -> safe(() -> {
            Submission s = selected(submissionTable);
            api.setSubmissionFactors(s.getId(), parseDouble(f1.getText(), "F1"), parseDouble(f3.getText(), "F3"));
            refreshAll();
        }));

        return buildTab("Подання", submissionTable,
                form("Проєкт", projectBox, "Студент", studentBox, "Репозиторій", repoUrl, "Стан", stateBox,
                        "F1 (повнота), 0–100", f1, "F3 (документація), 0–100", f3),
                new VBox(8, new HBox(8, add, update, delete, refresh), new HBox(8, transition, factors)));
    }

    // ================= Вкладка «Рецензії» =================

    private Tab reviewTab() {
        TextField reviewer = new TextField();
        TextField score = new TextField();
        TextField comment = new TextField();
        TextField reviewDate = new TextField();
        reviewDate.setPromptText("рррр-мм-дд (необов'язково)");

        reviewSubmissionBox.setConverter(converter((Submission s) -> "№" + s.getId() + " — "
                + projectTitles.getOrDefault(s.getProjectId(), "?") + " / "
                + studentNames.getOrDefault(s.getStudentId(), "?") + " [" + stateTitle(s) + "]"));
        reviewSubmissionBox.setPrefWidth(420);

        reviewTable.getColumns().add(col("ID", 50, (Review r) -> String.valueOf(r.getId())));
        reviewTable.getColumns().add(col("Подання №", 90, (Review r) -> String.valueOf(r.getSubmissionId())));
        reviewTable.getColumns().add(col("Рецензент", 160, (Review r) -> nz(r.getReviewer())));
        reviewTable.getColumns().add(col("Оцінка", 70, (Review r) -> fmt(r.getScore())));
        reviewTable.getColumns().add(col("Дата", 100, (Review r) -> nz(r.getReviewDate())));
        reviewTable.getColumns().add(col("Коментар", 500, (Review r) -> nz(r.getComment())));

        reviewTable.getSelectionModel().selectedItemProperty().addListener((obs, old, r) -> {
            if (r == null) return;
            select(reviewSubmissionBox, s -> s.getId() == r.getSubmissionId());
            reviewer.setText(nz(r.getReviewer()));
            score.setText(String.valueOf(r.getScore()));
            comment.setText(nz(r.getComment()));
            reviewDate.setText(nz(r.getReviewDate()));
        });

        Button add = new Button("Додати");
        add.setOnAction(e -> safe(() -> {
            Submission s = reviewSubmissionBox.getValue();
            if (s == null) throw new IllegalArgumentException("Виберіть подання");
            api.addReview(s.getId(), reviewer.getText(), comment.getText(),
                    parseDouble(score.getText(), "Оцінка"), reviewDate.getText());
            refreshAll();
        }));
        Button update = new Button("Змінити");
        update.setOnAction(e -> safe(() -> {
            Review r = selected(reviewTable);
            api.updateReview(r.getId(), r.getSubmissionId(), reviewer.getText(), comment.getText(),
                    parseDouble(score.getText(), "Оцінка"), reviewDate.getText());
            refreshAll();
        }));
        Button delete = new Button("Видалити");
        delete.setOnAction(e -> safe(() -> {
            api.deleteReview(selected(reviewTable).getId());
            refreshAll();
        }));
        Button refresh = new Button("Оновити");
        refresh.setOnAction(e -> safe(this::refreshAll));

        return buildTab("Рецензії", reviewTable,
                form("Подання", reviewSubmissionBox, "Рецензент", reviewer, "Оцінка, 0–100", score,
                        "Дата", reviewDate, "Коментар", comment),
                new HBox(8, add, update, delete, refresh));
    }

    // ================= Допоміжні методи інтерфейсу =================

    private Tab buildTab(String title, TableView<?> table, Node form, Node buttons) {
        VBox box = new VBox(10, table, form, buttons);
        box.setPadding(new Insets(10));
        VBox.setVgrow(table, Priority.ALWAYS);
        Tab tab = new Tab(title, box);
        tab.setClosable(false);
        return tab;
    }

    /** Форма з пар «підпис, елемент»; по дві пари в рядку. */
    private static GridPane form(Object... items) {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        for (int k = 0; k < items.length / 2; k++) {
            int row = k / 2;
            int base = (k % 2) * 2;
            grid.add(new Label((String) items[2 * k]), base, row);
            grid.add((Node) items[2 * k + 1], base + 1, row);
        }
        return grid;
    }

    private static <T> TableColumn<T, String> col(String title, double width, Function<T, String> value) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        return column;
    }

    private static <T> StringConverter<T> converter(Function<T, String> toText) {
        return new StringConverter<T>() {
            @Override
            public String toString(T item) {
                return item == null ? "" : toText.apply(item);
            }

            @Override
            public T fromString(String text) {
                return null;
            }
        };
    }

    private static <T> void select(ComboBox<T> box, Predicate<T> match) {
        for (T item : box.getItems()) {
            if (match.test(item)) {
                box.getSelectionModel().select(item);
                return;
            }
        }
        box.getSelectionModel().clearSelection();
    }

    private static <T> T selected(TableView<T> table) {
        T item = table.getSelectionModel().getSelectedItem();
        if (item == null) throw new IllegalArgumentException("Виберіть запис у таблиці");
        return item;
    }

    private static String stateTitle(Submission s) {
        return s.getState() == null ? "" : s.getState().getTitle();
    }

    private static String nz(String value) {
        return value == null ? "" : value;
    }

    private static String fmt(double value) {
        return String.format("%.2f", value);
    }

    /** Технічне перетворення тексту поля в число; правила діапазонів перевіряє сервер. */
    private static int parseInt(String text, String field) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + field + "» має бути цілим числом");
        }
    }

    private static double parseDouble(String text, String field) {
        try {
            return Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + field + "» має бути числом");
        }
    }

    // ================= Повідомлення та обробка помилок =================

    private interface ThrowingAction {
        void run() throws Exception;
    }

    private static void safe(ThrowingAction action) {
        try {
            action.run();
        } catch (Exception e) {
            showError(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Інформація");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
