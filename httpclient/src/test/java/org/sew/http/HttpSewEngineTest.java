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
        Result result = page.get("/simple").sync();
        assertThat(result.asString()).isEqualTo("ok");
    }

    @Test
    public void testParams() {
        Page page = getPage();
        Result result = page.get("/params")
                .param("id", "windows")
                .param("state", "sucks")
                .sync();
        assertThat(result.asString()).isEqualTo("windows is sucks");
    }

    @Test
    public void testPostParams() {
        Page page = getPage();
        Result result = page.post("/params/post")
                .param("id", "windows")
                .param("state", "sucks")
                .sync();
        assertThat(result.asString()).isEqualTo("windows is sucks");
    }

    @Test
    public void testHeader() {
        Page page = getPage();
        Result result = page.get("/header")
                .header("test-header", "header is worked")
                .sync();
        assertThat(result.asString()).isEqualTo("header is worked");
    }

    @Test
    public void testAuth() {
        Page page = getPage();
        Result result = page.post("/auth")
                .basicAuth("test", "test")
                .sync();
        assertThat(result.asString()).isEqualTo("test:test");
    }

    @Test
    public void testCookie() {
        Page page = getPage();
        Result result = page.get("/cookie")
                .cookie("test-cookie", "tasty")
                .sync();
        assertThat(result.asString()).isEqualTo("tasty");
        result = page.get("/cookie").sync();
        assertThat(result.asString()).isEqualTo("tasty");
        result = page.get("/cookie").removeCookie("test-cookie").sync();
        assertThat(result.asString()).isNotEqualTo("tasty");
    }

    @Test
    public void testServerCookie() {
        Page page = getPage();
        page.get("/serverCookie").sync();
        Result result = page.get("/cookieCheck").sync();
        assertThat(result.asString()).isEqualTo("session is ok");
        page.get("/serverCookieRemove").sync();
        result = page.get("/cookieCheck").sync();
        assertThat(result.asString()).isEqualTo("session is failed");
    }

    private Page getPage() {
        return engine.http("localhost", 4444);
    }

}