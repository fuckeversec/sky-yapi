package com.sky.build.util;

/**
 * @author gangyf
 * @since 2020/12/11 10:35 PM
 */
public class RequestUtil {

    /**
     * Switch request method string.
     *
     * @param requestMethod the request method
     * @return the string
     */
    public static String switchRequestMethod(String requestMethod) {
        if (requestMethod.contains("GET")) {
            return "GET";
        } else if (requestMethod.contains("POST")) {
            return "POST";
        } else if (requestMethod.contains("PUT")) {
            return "PUT";
        } else if (requestMethod.contains("DELETE")) {
            return "DELETE";
        } else if (requestMethod.contains("PATCH")) {
            return "PATCH";
        } else {
            return "";
        }
    }

}
