package org.sew;

import org.eclipse.jetty.io.RuntimeIOException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.sew.Methods.*;

public class HttpPage implements Page {

    private HttpSewEngine engine;
    private String protocol = "http";
    private String host;
    private int port;
    private String rootPath = "/";
    private String path;
    private Methods method = GET;
    private Map<String, String> params;
    private Map<String, String> headers;
    private ResultHandler handler;
    private Result result;
    private byte[] credentials;

    HttpPage(HttpSewEngine engine) {
        this.engine = engine;
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    private void reset() {
        params.clear();
    }

    @Override
    public void async(ResultHandler handler) {
        this.handler = handler;
        async();
    }

    @Override
    public void async() {
        this.engine.executorService.submit(() -> {
            try {
                makeRequest();
                if (this.handler != null) {
                    this.handler.handle(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public Result sync() {
        try {
            makeRequest();
            return result;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public Page basicAuth(String username, String password) {
        try {
            credentials = (username + ":" + password).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeIOException(e);
        }
        return this;
    }

    private void makeRequest() throws IOException {
        URL url = new URL(protocol, host, port, makePath());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method.name());
        setHeaders(connection);
        setParams(connection);
        connection.connect();
        reset();
        result = new HttpResult(connection);
    }

    private void setHeaders(HttpURLConnection connection) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }

        if (credentials != null) {
            connection.setRequestProperty("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString(credentials));
        }
    }

    private void setParams(HttpURLConnection connection) throws IOException {
        if (!params.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            putParams(sb);
            byte[] postDataBytes = sb.toString().getBytes(UTF_8.name());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            connection.setDoOutput(true);
            connection.getOutputStream().write(postDataBytes);
        }
    }

    private String makePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(rootPath);
        if (!rootPath.endsWith("/")) {
            sb.append("/");
        }
        sb.append(path.startsWith("/") ? path.substring(1) : path);
        if (GET.equals(method) && !params.isEmpty()) {
            sb.append("?");
            putParams(sb);
        }
        return sb.toString();
    }

    private void putParams(StringBuilder sb) {
        try {
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (sb.length() != 0) sb.append('&');
                sb.append(URLEncoder.encode(param.getKey(), UTF_8.name()));
                sb.append('=');
                sb.append(URLEncoder.encode(String.valueOf(param.getValue()), UTF_8.name()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page rootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    @Override
    public Page cookie(String key, String value) {
        engine.getCookiesForHost(host).put(key, value);
        return this;
    }

    @Override
    public Page header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public Page param(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    @Override
    public Page params(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    @Override
    public Map<String, String> cookies() {
        return engine.getCookiesForHost(host);
    }

    @Override
    public Page http(String host, int port) {
        this.host = host;
        this.port = port;
        this.protocol = "http";
        return this;
    }

    @Override
    public Page https(String host, int port) {
        this.host = host;
        this.port = port;
        this.protocol = "https";
        return this;
    }

    @Override
    public Page get(String path) {
        this.path = path;
        this.method = GET;
        return this;
    }

    @Override
    public Page post(String path) {
        this.path = path;
        this.method = POST;
        return this;
    }

    @Override
    public Page delete(String path) {
        this.path = path;
        this.method = DELETE;
        return this;
    }

    @Override
    public Page put(String path) {
        this.path = path;
        this.method = PUT;
        return this;
    }

    @Override
    public Result result() {
        return result;
    }

}
