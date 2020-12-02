package com.sky.upload;

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

}
