package org.sew;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class HttpResult implements Result {

    int code;
    byte[] content;

    public HttpResult(int code, byte[] content) {
        this.code = code;
        this.content = content;
    }

    public HttpResult(HttpURLConnection connection) throws IOException {
        this.code = connection.getResponseCode();
        this.content = IOUtils.toByteArray(connection.getInputStream());
    }

    @Override
    public int responseCode() {
        return code;
    }

    @Override
    public byte[] content() {
        return content;
    }

    @Override
    public String asString() {
        return new String(content, StandardCharsets.UTF_8);
    }

    @Override
    public <T> T as(Class<T> clazz) {
        return null;
    }
}
