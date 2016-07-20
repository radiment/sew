package org.sew;

import java.util.Map;

public interface Page {

    void async(ResultHandler handler);

    void async();

    Result sync();

    Page basicAuth(String username, String password);

    Page rootPath(String rootPath);

    Page cookie(String key, String value);

    Page removeCookie(String key);

    Page header(String key, String value);

    Page param(String key, String value);

    Page params(Map<String, String> params);

    Map<String, String> cookies();

    Page http(String host, int port);

    Page https(String host, int port);

    Page get(String path);

    Page post(String path);

    Page delete(String path);

    Page put(String path);

    Result result();

    interface ResultHandler {
        void handle(Result result);
    }

}
