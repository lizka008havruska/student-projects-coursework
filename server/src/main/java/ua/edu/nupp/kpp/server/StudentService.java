package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;

public class StudentService extends BaseService {
    private final StudentDAO dao = new StudentDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    public List<Student> getAll() throws SQLException { return dao.getAll(); }

    public Student getById(long id) throws SQLException { return dao.getById(id); }

    public Student add(Student s) throws SQLException {
        return run("Додавання студента", () -> {
            validate(s);
            requireUniqueEmail(s);
            s.setId(null);
            dao.add(s);
            return s;
        });
    }

    public Student update(Student s) throws SQLException {
        return run("Редагування студента", () -> {
            validate(s);
            if (s.getId() == null || dao.getById(s.getId()) == null)
                throw new IllegalArgumentException("Студента не знайдено");
            requireUniqueEmail(s);
            dao.update(s);
            return s;
        });
    }

    public void delete(long id) throws SQLException {
        run("Видалення студента", () -> {
            if (dao.getById(id) == null) throw new IllegalArgumentException("Студента не знайдено");
            if (submissionDAO.countByStudent(id) > 0)
                throw new IllegalArgumentException("Неможливо видалити студента, у якого є подання");
            dao.delete(id);
            return null;
        });
    }

    static void validate(Student s) {
        if (s == null) throw new IllegalArgumentException("Дані студента відсутні");
        s.setFullName(Validation.requireText(s.getFullName(), "ПІБ", 160));
        s.setGroupName(Validation.requireText(s.getGroupName(), "Група", 20));
        s.setEmail(Validation.requireText(s.getEmail(), "Email", 120));
        if (!Validation.isEmail(s.getEmail())) throw new IllegalArgumentException("Email має некоректний формат");
    }

    private void requireUniqueEmail(Student s) throws SQLException {
        Student other = dao.getByEmail(s.getEmail());
        if (other != null && !other.getId().equals(s.getId()))
            throw new IllegalArgumentException("Студент з таким email уже існує");
    }
}
