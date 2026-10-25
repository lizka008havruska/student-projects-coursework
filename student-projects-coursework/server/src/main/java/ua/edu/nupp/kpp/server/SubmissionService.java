package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Предметна логіка подання: валідація, життєвий цикл
 * ЧЕРНЕТКА → ПОДАНО → ПЕРЕВІРКА → ПРИЙНЯТО та інтегральний показник.
 */
public class SubmissionService extends BaseService {
    private final SubmissionDAO dao = new SubmissionDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public List<Submission> getAll() throws SQLException { return dao.getAll(); }

    public Submission getById(long id) throws SQLException { return dao.getById(id); }

    /** Нове подання завжди створюється у стані DRAFT з нульовими факторами. */
    public Submission add(Submission s) throws SQLException {
        return run("Створення подання", () -> {
            validate(s);
            requireProject(s.getProjectId());
            requireStudent(s.getStudentId());
            s.setId(null);
            s.setState(SubmissionState.DRAFT);
            s.setSubmittedDate(null);
            s.setF1(0); s.setF2(0); s.setF3(0); s.setIntegralIndex(0);
            dao.add(s);
            return s;
        });
    }

    /** Змінювати проєкт, автора та посилання можна лише в чернетці. */
    public Submission update(Submission s) throws SQLException {
        return run("Редагування подання", () -> {
            validate(s);
            if (s.getId() == null) throw new IllegalArgumentException("Подання не знайдено");
            Submission current = require(s.getId());
            if (current.getState() != SubmissionState.DRAFT)
                throw new IllegalArgumentException("Редагувати можна лише подання у стані ЧЕРНЕТКА");
            requireProject(s.getProjectId());
            requireStudent(s.getStudentId());
            dao.update(s);
            current.setProjectId(s.getProjectId());
            current.setStudentId(s.getStudentId());
            current.setRepoUrl(s.getRepoUrl());
            return current;
        });
    }

    /** Перехід між станами; допустимість перевіряється тут, а не в DAO чи клієнті. */
    public Submission changeState(long id, SubmissionState next) throws SQLException {
        return run("Зміна стану подання", () -> {
            Submission s = require(id);
            if (!isAllowedTransition(s.getState(), next))
                throw new IllegalArgumentException("Недопустимий перехід: "
                        + s.getState().getTitle() + " → " + (next == null ? "?" : next.getTitle()));
            String date = s.getSubmittedDate();
            if (next == SubmissionState.SUBMITTED) {
                if (s.getRepoUrl() == null || s.getRepoUrl().isBlank())
                    throw new IllegalArgumentException("Для подання необхідно вказати посилання на репозиторій");
                date = LocalDate.now().toString();
            }
            if (next == SubmissionState.ACCEPTED && reviewDAO.getBySubmission(id).isEmpty())
                throw new IllegalArgumentException("Прийняти подання можна лише за наявності рецензії");
            dao.updateState(id, next, date);
            s.setState(next);
            s.setSubmittedDate(date);
            return s;
        });
    }

    /** Виставлення F1 (повнота реалізації) і F3 (якість документації) під час перевірки. */
    public Submission setFactors(long id, double f1, double f3) throws SQLException {
        return run("Виставлення факторів F1 і F3", () -> {
            Validation.range(f1, 0, 100, "F1");
            Validation.range(f3, 0, 100, "F3");
            Submission s = require(id);
            if (s.getState() != SubmissionState.REVIEW)
                throw new IllegalArgumentException("Фактори можна виставляти лише у стані ПЕРЕВІРКА");
            s.setF1(f1);
            s.setF3(f3);
            return recalc(s);
        });
    }

    /** Перерахунок F2 (середня оцінка рецензій) та інтегрального показника I. */
    public Submission refreshFactors(long id) throws SQLException {
        return run("Перерахунок інтегрального показника", () -> recalc(require(id)));
    }

    public void delete(long id) throws SQLException {
        run("Видалення подання", () -> {
            Submission s = require(id);
            if (s.getState() == SubmissionState.ACCEPTED)
                throw new IllegalArgumentException("Прийняте подання видаляти не можна");
            reviewDAO.deleteBySubmission(id);
            dao.delete(id);
            return null;
        });
    }

    public static boolean isAllowedTransition(SubmissionState from, SubmissionState to) {
        return from != null && from.canMoveTo(to);
    }

    static void validate(Submission s) {
        if (s == null) throw new IllegalArgumentException("Дані подання відсутні");
        Validation.positiveId(s.getProjectId(), "Проєкт");
        Validation.positiveId(s.getStudentId(), "Студент");
        Validation.optionalText(s.getRepoUrl(), "Посилання на репозиторій", 255);
        String url = s.getRepoUrl();
        if (url != null && !url.isBlank() && !(url.startsWith("http://") || url.startsWith("https://")))
            throw new IllegalArgumentException("Посилання на репозиторій має починатися з http:// або https://");
    }

    private Submission recalc(Submission s) throws SQLException {
        List<Double> scores = new ArrayList<>();
        for (Review r : reviewDAO.getBySubmission(s.getId())) scores.add(r.getScore());
        double f2 = RiskCalculator.factor2(scores);
        double index = RiskCalculator.calculate(s.getF1(), f2, s.getF3());
        dao.updateFactors(s.getId(), s.getF1(), f2, s.getF3(), index);
        s.setF2(f2);
        s.setIntegralIndex(index);
        return s;
    }

    private Submission require(long id) throws SQLException {
        Submission s = dao.getById(id);
        if (s == null) throw new IllegalArgumentException("Подання не знайдено");
        return s;
    }

    private void requireProject(long id) throws SQLException {
        if (projectDAO.getById(id) == null) throw new IllegalArgumentException("Проєкт не знайдено");
    }

    private void requireStudent(long id) throws SQLException {
        if (studentDAO.getById(id) == null) throw new IllegalArgumentException("Студента не знайдено");
    }
}
