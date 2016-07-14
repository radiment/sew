package org.sew.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter writer = resp.getWriter()) {
            String id = req.getParameter("id");
            if (id != null) {
                writer.append(id).append(" is ").append(req.getParameter("state"));
            } else {
                writer.append("ok");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        startServer();
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
