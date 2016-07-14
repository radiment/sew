package org.sew;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.io.RuntimeIOException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.sew.Methods.GET;
import static org.sew.Methods.POST;

public class HttpPage implements Page {

    ContentResponse response;

    private HttpSewEngine engine;
    private String protocol = "http";
    private String host;
    private int port;
    private String rootPath = "/";
    private String path;
    private Methods method = GET;
    private Map<String, String> params;
    private Map<String, String> headers;

    protected HttpPage(HttpSewEngine engine) {
        this.engine = engine;
        this.params = new HashMap<>();
    }

    private void reset() {
        params.clear();
    }

    @Override
    public void async() {

    }

    @Override
    public Result sync() {
        try {
            URL url = new URL(protocol, host, port, makePath());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.name());
            setParams(connection);
            connection.connect();
            reset();
            return new HttpResult(connection);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
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
        return null;
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
    public byte[] content() {
        return response.getContent();
    }

    @Override
    public String strContent() {
        return response.getContentAsString();
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

}
