package org.sew;

import java.util.Map;

public interface SewEngine {

    Map<String, String> getCookiesForHost(String host);

    Page http(String host);

    Page http(String host, int port);

    Page https(String host);

    Page https(String host, int port);
}
