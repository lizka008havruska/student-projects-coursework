package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;

public class ProjectService extends BaseService {
    private final ProjectDAO dao = new ProjectDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    public List<Project> getAll() throws SQLException { return dao.getAll(); }

    public Project getById(long id) throws SQLException { return dao.getById(id); }

    public Project add(Project p) throws SQLException {
        return run("Додавання проєкту", () -> {
            validate(p);
            p.setId(null);
            dao.add(p);
            return p;
        });
    }

    public Project update(Project p) throws SQLException {
        return run("Редагування проєкту", () -> {
            validate(p);
            if (p.getId() == null || dao.getById(p.getId()) == null)
                throw new IllegalArgumentException("Проєкт не знайдено");
            dao.update(p);
            return p;
        });
    }

    public void delete(long id) throws SQLException {
        run("Видалення проєкту", () -> {
            if (dao.getById(id) == null) throw new IllegalArgumentException("Проєкт не знайдено");
            if (submissionDAO.countByProject(id) > 0)
                throw new IllegalArgumentException("Неможливо видалити проєкт, для якого є подання");
            dao.delete(id);
            return null;
        });
    }

    static void validate(Project p) {
        if (p == null) throw new IllegalArgumentException("Дані проєкту відсутні");
        p.setTitle(Validation.requireText(p.getTitle(), "Назва", 200));
        p.setTechnology(Validation.requireText(p.getTechnology(), "Технологія", 80));
        Validation.optionalText(p.getDescription(), "Опис", 1000);
        Validation.range(p.getDifficulty(), 1, 5, "Складність");
    }
}
