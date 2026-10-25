package ua.edu.nupp.kpp.server;

import java.util.List;

/**
 * Перевірка CRUD, Service-правил, інтегрального показника і життєвого циклу на реальній MySQL.
 * Запуск: mvn compile exec:java -Dexec.mainClass=ua.edu.nupp.kpp.server.CrudCheck
 * Створені тестові записи видаляються наприкінці.
 */
public class CrudCheck {
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        StudentService students = new StudentService();
        ProjectService projects = new ProjectService();
        SubmissionService submissions = new SubmissionService();
        ReviewService reviews = new ReviewService();
        ReviewDAO reviewDAO = new ReviewDAO();
        SubmissionDAO submissionDAO = new SubmissionDAO();

        Student st = students.add(new Student(null, "Тестовий Студент", "202-ТН", "crud.check@example.com"));
        check(st.getId() != null && students.getById(st.getId()) != null, "CREATE/READ студента");
        st.setGroupName("203-ТН");
        students.update(st);
        check("203-ТН".equals(students.getById(st.getId()).getGroupName()), "UPDATE студента");

        Project pr = projects.add(new Project(null, "Тестовий проєкт", "Опис", "Java", 3));
        check(projects.getById(pr.getId()) != null, "CREATE/READ проєкту");

        Submission sub = submissions.add(new Submission(null, pr.getId(), st.getId(), null,
                "https://github.com/example/crud-check", null, 0, 0, 0, 0));
        check(sub.getState() == SubmissionState.DRAFT, "Нове подання у стані ЧЕРНЕТКА");

        expectRejected(() -> submissions.changeState(sub.getId(), SubmissionState.ACCEPTED), "Заборонено ЧЕРНЕТКА → ПРИЙНЯТО");
        submissions.changeState(sub.getId(), SubmissionState.SUBMITTED);
        submissions.changeState(sub.getId(), SubmissionState.REVIEW);
        expectRejected(() -> submissions.changeState(sub.getId(), SubmissionState.DRAFT), "Заборонено ПЕРЕВІРКА → ЧЕРНЕТКА");
        expectRejected(() -> submissions.changeState(sub.getId(), SubmissionState.ACCEPTED), "Заборонено прийняти без рецензії");

        Review rv = reviews.add(new Review(null, sub.getId(), "Заїка С.О.", "Добре", 70, null));
        check(rv.getId() != null, "CREATE рецензії");
        Submission afterReview = submissions.setFactors(sub.getId(), 80, 60);
        // I = 0,40·80 + 0,35·70 + 0,25·60 = 32 + 24,5 + 15 = 71,5
        check(Math.abs(afterReview.getIntegralIndex() - 71.5) < 1e-9, "Інтегральний показник = 71,5 (отримано " + afterReview.getIntegralIndex() + ")");
        check(Math.abs(submissionDAO.getById(sub.getId()).getIntegralIndex() - 71.5) < 1e-9, "Показник збережено в MySQL");

        rv.setScore(90);
        reviews.update(rv);
        // F2 = 90 → I = 32 + 31,5 + 15 = 78,5
        check(Math.abs(submissionDAO.getById(sub.getId()).getIntegralIndex() - 78.5) < 1e-9, "Показник перераховано після редагування рецензії");

        submissions.changeState(sub.getId(), SubmissionState.ACCEPTED);
        check(submissionDAO.getById(sub.getId()).getState() == SubmissionState.ACCEPTED, "Подання прийнято");
        expectRejected(() -> submissions.delete(sub.getId()), "Прийняте подання не видаляється");
        expectRejected(() -> students.delete(st.getId()), "Студента з поданням не видаляється");
        expectRejected(() -> submissions.add(new Submission(null, 999999, st.getId(), null, null, null, 0, 0, 0, 0)), "Неіснуючий проєкт відхилено");

        // Прибирання тестових даних
        reviewDAO.deleteBySubmission(sub.getId());
        submissionDAO.delete(sub.getId());
        students.delete(st.getId());
        projects.delete(pr.getId());
        List<Student> left = students.getAll();
        check(left.stream().noneMatch(s -> "crud.check@example.com".equals(s.getEmail())), "DELETE студента, дані прибрано");

        System.out.println(failed == 0 ? "\nУСІ ПЕРЕВІРКИ ПРОЙДЕНО" : "\nПОМИЛОК: " + failed);
    }

    private interface Action { void run() throws Exception; }

    private static void expectRejected(Action a, String name) {
        try { a.run(); check(false, name + " (операцію не відхилено)"); }
        catch (IllegalArgumentException e) { check(true, name + " — «" + e.getMessage() + "»"); }
        catch (Exception e) { check(false, name + " (інша помилка: " + e + ")"); }
    }

    private static void check(boolean ok, String name) {
        System.out.println((ok ? "[OK]   " : "[FAIL] ") + name);
        if (!ok) failed++;
    }
}
