package ua.edu.nupp.kpp.server;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * /submissions — GET: список подань;
 * POST action=add | update | delete | transition (id, state) | factors (id, f1, f3).
 * Допустимість переходу й обчислення показника виконує SubmissionService.
 */
public class SubmissionServlet extends BaseServlet {
    private final SubmissionService service = new SubmissionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(req, resp, () -> service.getAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(req, resp, () -> {
            String action = action(req);
            switch (action) {
                case "add":
                    return service.add(read(req, null));
                case "update":
                    return service.update(read(req, requireLong(req, "id")));
                case "delete":
                    service.delete(requireLong(req, "id"));
                    return ok("deleted", true);
                case "transition":
                    return service.changeState(requireLong(req, "id"), parseState(param(req, "state")));
                case "factors":
                    return service.setFactors(requireLong(req, "id"),
                            requireDouble(req, "f1"), requireDouble(req, "f3"));
                default:
                    throw unknownAction(action);
            }
        });
    }

    private Submission read(HttpServletRequest req, Long id) {
        Submission s = new Submission();
        s.setId(id);
        s.setProjectId(requireLong(req, "projectId"));
        s.setStudentId(requireLong(req, "studentId"));
        s.setRepoUrl(param(req, "repoUrl"));
        return s;
    }

    private static SubmissionState parseState(String value) {
        if (value == null) throw new IllegalArgumentException("Не вказано параметр state");
        try {
            return SubmissionState.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Невідомий стан: " + value);
        }
    }
}
