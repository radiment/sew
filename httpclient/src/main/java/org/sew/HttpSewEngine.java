package org.sew;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpSewEngine implements SewEngine {

    public static final int DEFAULT_PORT = 80;
    Map<String, Map<String, String>> cookies = new ConcurrentHashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public Map<String, String> getCookiesForHost(String host) {
        Map<String, String> result = cookies.get(host);
        if (result == null) {
            result = new ConcurrentHashMap<>();
            cookies.put(host, result);
        }
        return result;
    }

    @Override
    public Page http(String host) {
        return http(host, DEFAULT_PORT);
    }

    @Override
    public Page http(String host, int port) {
        return getHttpPage().http(host, port);
    }

    private HttpPage getHttpPage() {
        return new HttpPage(this);
    }

    @Override
    public Page https(String host) {
        return https(host, DEFAULT_PORT);
    }

    @Override
    public Page https(String host, int port) {
        return getHttpPage().https(host, port);
    }
}
