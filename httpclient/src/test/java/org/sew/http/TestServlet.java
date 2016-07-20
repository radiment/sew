package org.sew.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TestServlet extends HttpServlet {

    public static final String SESSION = "session";
    private Map<String, Process> processes;

    @Override
    public void init() throws ServletException {
        super.init();
        processes = new HashMap<>();
        processes.put("/simple", (req, resp) -> resp.getWriter().append("ok"));
        processes.put("/params", (req, resp) -> resp.getWriter().append(req.getParameter("id"))
                .append(" is ").append(req.getParameter("state")));
        processes.put("/params/post", (req, resp) -> resp.getWriter().append(req.getParameter("id"))
                .append(" is ").append(req.getParameter("state")));
        processes.put("/header", (req, resp) -> resp.getWriter().append(req.getHeader("test-header")));
        processes.put("/auth", (req, resp) -> {
            String authHeader = req.getHeader("Authorization");
            String coded = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(coded), "UTF-8");
            resp.getWriter().append(credentials);
        });
        processes.put("/cookie", (req, resp) -> {
            String cookieResult = getCookieValue(req, "test-cookie");
            resp.getWriter().append(cookieResult);
        });
        processes.put("/serverCookie", (req, resp) -> {
            Cookie cookie = new Cookie(SESSION, "session1");
            cookie.setMaxAge(-1);
            resp.addCookie(cookie);
            resp.getWriter().append("ok");
        });
        processes.put("/cookieCheck", (req, resp) -> {
            String session = getCookieValue(req, SESSION);
            if ("session1".equals(session)) {
                resp.getWriter().append("session is ok");
            } else {
                resp.getWriter().append("session is failed");
            }
        });
        processes.put("/serverCookieRemove", (req, resp) -> {
            Cookie cookie = getCookie(req, SESSION);
            if (cookie != null) {
                cookie.setValue("");
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
        });
    }

    private String getCookieValue(HttpServletRequest req, String name) {
        Cookie cookie = getCookie(req, name);
        if (cookie != null) return cookie.getValue();
        return null;
    }

    private Cookie getCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        processes.get(path).doProcess(req, resp);
    }

    public static void main(String[] args) throws Exception {
        startServer();
    }

    interface Process {
        void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException;
    }

    public static Server startServer() throws Exception {
        Server server = new Server(4444);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(TestServlet.class, "/");
        server.setHandler(handler);
        server.start();
        return server;
    }
}
