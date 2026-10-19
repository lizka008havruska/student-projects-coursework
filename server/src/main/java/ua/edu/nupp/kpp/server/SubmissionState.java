package ua.edu.nupp.kpp.server;

/** Стани життєвого циклу подання: ЧЕРНЕТКА → ПОДАНО → ПЕРЕВІРКА → ПРИЙНЯТО. */
public enum SubmissionState {
    DRAFT("ЧЕРНЕТКА"),
    SUBMITTED("ПОДАНО"),
    REVIEW("ПЕРЕВІРКА"),
    ACCEPTED("ПРИЙНЯТО");

    private final String title;

    SubmissionState(String title) { this.title = title; }

    public String getTitle() { return title; }

    /** Допустимий лише перехід до наступного стану за порядком. */
    public boolean canMoveTo(SubmissionState target) {
        return target != null && target.ordinal() == this.ordinal() + 1;
    }
}
