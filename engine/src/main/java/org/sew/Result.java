package org.sew;

public interface Result {

    int responseCode();

    byte [] content();

    String asString();

    <T> T as(Class<T> clazz);
}
