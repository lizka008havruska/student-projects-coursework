package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Рецензії створюються лише для подань у стані ПЕРЕВІРКА; після змін перераховується F2 подання. */
public class ReviewService extends BaseService {
    private final ReviewDAO dao = new ReviewDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final SubmissionService submissionService = new SubmissionService();

    public List<Review> getAll() throws SQLException { return dao.getAll(); }

    public List<Review> getBySubmission(long submissionId) throws SQLException {
        return dao.getBySubmission(submissionId);
    }

    public Review getById(long id) throws SQLException { return dao.getById(id); }

    public Review add(Review r) throws SQLException {
        return run("Додавання рецензії", () -> {
            validate(r);
            requireReviewable(r.getSubmissionId());
            if (r.getReviewDate() == null || r.getReviewDate().isBlank()) r.setReviewDate(LocalDate.now().toString());
            r.setId(null);
            dao.add(r);
            submissionService.refreshFactors(r.getSubmissionId());
            return r;
        });
    }

    public Review update(Review r) throws SQLException {
        return run("Редагування рецензії", () -> {
            validate(r);
            Review current = require(r.getId());
            requireReviewable(current.getSubmissionId());
            r.setSubmissionId(current.getSubmissionId());
            if (r.getReviewDate() == null || r.getReviewDate().isBlank()) r.setReviewDate(current.getReviewDate());
            dao.update(r);
            submissionService.refreshFactors(current.getSubmissionId());
            return r;
        });
    }

    public void delete(long id) throws SQLException {
        run("Видалення рецензії", () -> {
            Review current = require(id);
            requireReviewable(current.getSubmissionId());
            dao.delete(id);
            submissionService.refreshFactors(current.getSubmissionId());
            return null;
        });
    }

    static void validate(Review r) {
        if (r == null) throw new IllegalArgumentException("Дані рецензії відсутні");
        Validation.positiveId(r.getSubmissionId(), "Подання");
        r.setReviewer(Validation.requireText(r.getReviewer(), "Рецензент", 120));
        Validation.optionalText(r.getComment(), "Коментар", 1000);
        Validation.range(r.getScore(), 0, 100, "Оцінка");
    }

    private Review require(Long id) throws SQLException {
        Review r = id == null ? null : dao.getById(id);
        if (r == null) throw new IllegalArgumentException("Рецензію не знайдено");
        return r;
    }

    private void requireReviewable(long submissionId) throws SQLException {
        Submission s = submissionDAO.getById(submissionId);
        if (s == null) throw new IllegalArgumentException("Подання не знайдено");
        if (s.getState() != SubmissionState.REVIEW)
            throw new IllegalArgumentException("Рецензії можна змінювати лише для подання у стані ПЕРЕВІРКА");
    }
}
