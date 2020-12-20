package com.sky.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

/**
 * @author gangyf
 * @since 2020/11/30 10:01 PM
 */
public class JsonUtil {

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);


    public static String writeValueAsString(Object obj) {
        if (obj == null) {
            return "";
        }
        return writeValueAsString(obj, true);
    }

    public static String writeValueAsString(Object obj, boolean pretty) {
        try {
            if (pretty) {
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return OBJECT_MAPPER.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    /**
     * 封装异常处理
     *
     * @param <T> the type parameter
     * @param json the json
     * @param clazz the clazz
     * @return the optional
     */
    public static <T> Optional<T> readValue(String json, Class<T> clazz) {

        try {
            return Optional.of(OBJECT_MAPPER.readValue(json, clazz));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * 复杂对象
     *
     * @param <T> the type parameter
     * @param json the json
     * @param typeReference the type reference
     * @return the optional
     */
    public static <T> Optional<T> readValue(String json, TypeReference<T> typeReference) {

        try {
            return Optional.of(OBJECT_MAPPER.readValue(json, typeReference));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}
