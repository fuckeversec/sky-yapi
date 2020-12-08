package com.sky.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.apache.commons.lang.StringUtils;

/**
 * @author gangyf
 * @since 2020/11/30 5:08 PM
 */
public class CookieUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 功能描述: 解析firefox格式的cookie文件到Cookie的JSON数组字符串<br>
     *
     * @author gangyanfeng@eversec.cn
     * 2018/11/9 14:46
     */
    public static String parseFirefoxCookie2Json(String cookieStr) {
        if (StringUtils.isEmpty(cookieStr)) {
            return null;
        }
        List<javax.servlet.http.Cookie> list;
        try {
            list = new ArrayList<>();
            String[] strings = cookieStr.split("[\n]");
            for (String string : strings) {
                // ignore comment
                if (string.startsWith("#")) {
                    continue;
                }

                if (StringUtils.isBlank(string)) {
                    continue;
                }

                String[] split = string.split("\t");
                javax.servlet.http.Cookie cookie = new Cookie(split[5], split[6]);
                cookie.setDomain(split[0]);
                cookie.setPath(split[2]);
                cookie.setSecure(Boolean.parseBoolean(split[3]));
                cookie.setMaxAge((int) Long.parseLong(split[4]));
                list.add(cookie);
            }
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void main(String[] args) throws JsonProcessingException {
        List<com.sky.upload.Cookie> cookie2Json = objectMapper.readValue("[\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1637923424,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"Hm_lvt_156e88c022bf41570bf96e74d090ced7\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"1603888732,1604998243,1606130488\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1616918560,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"UM_distinctid\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"174ce95d631153-0dee1fa9c473d1-31697304-13c680-174ce95d6326b0\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1916183537,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"grwng_uid\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"fa0003c2-8f70-4322-8258-71c840f536da\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1921806094,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"gr_user_id\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"bda10e66-75b2-41bd-b216-a13e1af902f1\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \"yapi.kaikeba.com\",\n"
                + "        \"expirationDate\": 1607506953.13208,\n"
                + "        \"hostOnly\": true,\n"
                + "        \"httpOnly\": true,\n"
                + "        \"name\": \"_yapi_token\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjYwOCwiaWF0IjoxNjA2OTAyMTUzLCJleHAiOjE2MDc1MDY5NTN9.wmMrkNbabLKBJDtA4SFy55lXY9yAAUjco8enPAgc4b0\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1668240986,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"_ga\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"GA1.2.370407192.1605168675\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \"yapi.kaikeba.com\",\n"
                + "        \"expirationDate\": 1607506953.132145,\n"
                + "        \"hostOnly\": true,\n"
                + "        \"httpOnly\": true,\n"
                + "        \"name\": \"_yapi_uid\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"608\"\n"
                + "    },\n"
                + "    {\n"
                + "        \"domain\": \".kaikeba.com\",\n"
                + "        \"expirationDate\": 1916183537,\n"
                + "        \"hostOnly\": false,\n"
                + "        \"httpOnly\": false,\n"
                + "        \"name\": \"kd_user_id\",\n"
                + "        \"path\": \"/\",\n"
                + "        \"sameSite\": \"unspecified\",\n"
                + "        \"secure\": false,\n"
                + "        \"session\": false,\n"
                + "        \"storeId\": null,\n"
                + "        \"value\": \"a6c93472-3c40-4580-9c21-3e1f55bc9495\"\n"
                + "    }\n"
                + "]", new TypeReference<>() {});

        System.out.println(cookie2Json.size());
    }
}
