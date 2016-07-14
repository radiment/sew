package org.sew.http;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sew.HttpSewEngine;
import org.sew.Page;
import org.sew.Result;
import org.sew.SewEngine;

import static org.assertj.core.api.Assertions.*;

public class HttpSewEngineTest {

    private static Server server;

    SewEngine engine = new HttpSewEngine();

    @BeforeClass
    public static void init() throws Exception {
        server = TestServlet.startServer();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.stop();
    }

    @Test
    public void testEngine() {
        Page page = getPage();
        Result result = page.get("/").sync();
        assertThat(result.asString()).isEqualTo("ok");
    }

    @Test
    public void testParams() {
        Page page = getPage();
        Result result = page.get("/")
                .param("id", "windows")
                .param("state", "sucks")
                .sync();
        assertThat(result.asString()).isEqualTo("windows is sucks");
    }

    @Test
    public void testPostParams() {
        Page page = getPage();
        Result result = page.post("/")
                .param("id", "windows")
                .param("state", "sucks")
                .sync();
        assertThat(result.asString()).isEqualTo("windows is sucks");
    }

    private Page getPage() {
        return engine.http("localhost", 4444);
    }

}