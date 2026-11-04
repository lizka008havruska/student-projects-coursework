package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Спільні допоміжні засоби Servlet-рівня: читання параметрів, JSON-відповідь, JSON-помилка.
 * SQL і предметних правил тут немає — їх виконують Service і DAO.
 */
abstract class BaseServlet extends HttpServlet {
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());

    @FunctionalInterface
    protected interface Handler { Object handle() throws Exception; }

    /** Виконує обробник і перетворює результат або виняток на JSON. */
    protected void respond(HttpServletRequest req, HttpServletResponse resp, Handler handler) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        try {
            writeJson(resp, HttpServletResponse.SC_OK, handler.handle());
        } catch (IllegalArgumentException e) {
            // порушення правил Service або некоректні параметри запиту
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Помилка обробки запиту " + req.getMethod() + " " + req.getRequestURI(), e);
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Внутрішня помилка сервера");
        }
    }

    protected void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(MAPPER.writeValueAsString(body));
    }

    protected void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", message == null ? "Помилка запиту" : message);
        writeJson(resp, status, body);
    }

    protected static Map<String, Object> ok(String key, Object value) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "ok");
        body.put(key, value);
        return body;
    }

    /** Значення параметра або null, якщо його немає чи він порожній. */
    protected static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    protected static String action(HttpServletRequest req) {
        String a = param(req, "action");
        if (a == null) throw new IllegalArgumentException("Не вказано параметр action");
        return a;
    }

    protected static long requireLong(HttpServletRequest req, String name) {
        String v = param(req, name);
        if (v == null) throw new IllegalArgumentException("Не вказано параметр " + name);
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр " + name + " має бути цілим числом");
        }
    }

    protected static int requireInt(HttpServletRequest req, String name) {
        String v = param(req, name);
        if (v == null) throw new IllegalArgumentException("Не вказано параметр " + name);
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр " + name + " має бути цілим числом");
        }
    }

    protected static double requireDouble(HttpServletRequest req, String name) {
        String v = param(req, name);
        if (v == null) throw new IllegalArgumentException("Не вказано параметр " + name);
        try {
            return Double.parseDouble(v.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр " + name + " має бути числом");
        }
    }

    protected static IllegalArgumentException unknownAction(String action) {
        return new IllegalArgumentException("Невідома дія: " + action);
    }
}
