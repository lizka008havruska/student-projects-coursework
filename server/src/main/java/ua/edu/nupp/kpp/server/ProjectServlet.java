package ua.edu.nupp.kpp.server;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** /projects — GET: список; POST action=add | update | delete. */
public class ProjectServlet extends BaseServlet {
    private final ProjectService service = new ProjectService();

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

    private Project read(HttpServletRequest req, Long id) {
        return new Project(id, param(req, "title"), param(req, "description"),
                param(req, "technology"), requireInt(req, "difficulty"));
    }
}
