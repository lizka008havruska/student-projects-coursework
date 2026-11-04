package ua.edu.nupp.kpp.server;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** /reviews — GET: усі рецензії або ?submissionId=...; POST action=add | update | delete. */
public class ReviewServlet extends BaseServlet {
    private final ReviewService service = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(req, resp, () -> {
            if (param(req, "submissionId") != null) return service.getBySubmission(requireLong(req, "submissionId"));
            return service.getAll();
        });
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

    private Review read(HttpServletRequest req, Long id) {
        return new Review(id, requireLong(req, "submissionId"), param(req, "reviewer"),
                param(req, "comment"), requireDouble(req, "score"), param(req, "reviewDate"));
    }
}
