package com.github.prontera.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import java.io.IOException;

/**
 * @author Zhao Junjian
 */
public final class Jacksons {

    public static final String EMPTY = "{}";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private Jacksons() {

    }

    public static <T> String parse(T obj) {
        try {
            String json;
            if (obj != null) {
                json = MAPPER.writeValueAsString(obj);
            } else {
                json = EMPTY;
            }
            return json;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> String parseInPrettyMode(T obj) {
        try {
            String json;
            if (obj != null) {
                json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                json = EMPTY;
            }
            return json;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T convert(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<T>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T convert(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static boolean isNotEmpty(String json) {
        return !Strings.isNullOrEmpty(json) && !EMPTY.equals(json);
    }

    public static boolean isEmpty(String json) {
        return !isNotEmpty(json);
    }

}
