package org.sew;

import java.util.Map;

public interface Page {

    void async();

    Result sync();

    Page rootPath(String rootPath);

    Page cookie(String key, String value);

    Page param(String key, String value);

    Page params(Map<String, String> params);

    Map<String, String> cookies();

    Page http(String host, int port);

    Page https(String host, int port);

    byte[] content();

    String strContent();

    Page get(String path);

    Page post(String path);

}
