package ua.edu.nupp.kpp.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidationTest {
    @Test
    void studentRequiresName() {
        assertThrows(IllegalArgumentException.class,
                () -> StudentService.validate(new Student(null, " ", "202-ТН", "a@b.ua")));
    }

    @Test
    void studentRejectsBadEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> StudentService.validate(new Student(null, "Іваненко І.І.", "202-ТН", "not-an-email")));
    }

    @Test
    void validStudentPasses() {
        assertDoesNotThrow(() -> StudentService.validate(new Student(null, "Іваненко І.І.", "202-ТН", "iva@nenko.ua")));
    }

    @Test
    void projectDifficultyMustBeOneToFive() {
        assertThrows(IllegalArgumentException.class,
                () -> ProjectService.validate(new Project(null, "Назва", "", "Java", 6)));
        assertThrows(IllegalArgumentException.class,
                () -> ProjectService.validate(new Project(null, "Назва", "", "Java", 0)));
    }

    @Test
    void submissionRepoUrlMustBeHttp() {
        assertThrows(IllegalArgumentException.class,
                () -> SubmissionService.validate(new Submission(null, 1, 1, null, "ftp://x", null, 0, 0, 0, 0)));
        assertDoesNotThrow(
                () -> SubmissionService.validate(new Submission(null, 1, 1, null, "https://github.com/x/y", null, 0, 0, 0, 0)));
    }

    @Test
    void submissionRequiresPositiveIds() {
        assertThrows(IllegalArgumentException.class,
                () -> SubmissionService.validate(new Submission(null, 0, 1, null, null, null, 0, 0, 0, 0)));
    }

    @Test
    void reviewScoreMustBeInRange() {
        assertThrows(IllegalArgumentException.class,
                () -> ReviewService.validate(new Review(null, 1, "Заїка С.О.", "ok", 120, null)));
    }

    @Test
    void reviewRequiresReviewer() {
        assertThrows(IllegalArgumentException.class,
                () -> ReviewService.validate(new Review(null, 1, "", "ok", 50, null)));
    }
}
