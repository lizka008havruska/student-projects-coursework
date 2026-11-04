package ua.edu.nupp.kpp.server;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** /students — GET: список; POST action=add | update | delete. */
public class StudentServlet extends BaseServlet {
    private final StudentService service = new StudentService();

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
                default:
                    throw unknownAction(action);
            }
        });
    }

    private Student read(HttpServletRequest req, Long id) {
        return new Student(id, param(req, "fullName"), param(req, "groupName"), param(req, "email"));
    }
}
