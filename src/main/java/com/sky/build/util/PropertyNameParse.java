package com.sky.build.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gangyf
 * @since 2020/12/16 7:16 PM
 */
public class PropertyNameParse {

    private static List<String> propertyAnnotations;

    static {
        propertyAnnotations = new ArrayList<>();
        propertyAnnotations.add("com.fasterxml.jackson.annotation.JsonProperty");
    }

}
