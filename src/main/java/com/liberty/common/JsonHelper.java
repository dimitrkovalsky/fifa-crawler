package com.liberty.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JsonHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
    }

    private JsonHelper() {

    }

    public static <T> ObjectNode toJson(T data) {
        return objectMapper.convertValue(data, ObjectNode.class);
    }

    public static <T> String toJsonString(T data) {
        return toString(objectMapper.convertValue(data, ObjectNode.class));
    }

    public static String toString(JsonNode data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> toEntity(String data, Class clazz) {
        try {
            return Optional.of((T) objectMapper.readValue(data, clazz));
        } catch (IOException e) {
            log.error("Unable convert to entry", e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> toEntitySilently(String data, Class clazz) {
        try {
            return Optional.of((T) objectMapper.readValue(data, clazz));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JsonHelper.objectMapper = objectMapper;
    }

}
