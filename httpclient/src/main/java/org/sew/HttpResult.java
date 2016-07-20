package org.sew;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

class HttpResult implements Result {

    private int code;
    private byte[] content;

    HttpResult(HttpURLConnection connection) throws IOException {
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
