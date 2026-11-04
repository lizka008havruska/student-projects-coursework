package ua.edu.nupp.kpp.client;

/**
 * Стани життєвого циклу подання (ті самі значення, що й на сервері).
 * Допустимість переходів перевіряє лише сервер (SubmissionService).
 */
public enum SubmissionState {
    DRAFT("ЧЕРНЕТКА"),
    SUBMITTED("ПОДАНО"),
    REVIEW("ПЕРЕВІРКА"),
    ACCEPTED("ПРИЙНЯТО");

    private final String title;

    SubmissionState(String title) { this.title = title; }

    public String getTitle() { return title; }
}
