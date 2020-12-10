package com.sky.util;

import static com.sky.util.DesUtil.LINK_CLASS;
import static com.sky.util.DesUtil.SEE_CLASS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import org.junit.Test;

/**
 * @author gangyf
 * @since 2020/12/8 11:43 AM
 */
public class UtilTest {


    @Test
    public void path() {
        String path = "http://www.baidu.com//test";

        assertThat(path.replaceAll("(?<!https?:)/{2,}", "/")).isEqualTo("http://www.baidu.com/test");

        path = "https://www.baidu.com//test";
        assertThat(path.replaceAll("(?<!https?:)/{2,}", "/")).isEqualTo("https://www.baidu.com/test");

        path = "/user//";
        assertThat(path.replaceAll("(?<!https?:)/{2,}", "/")).isEqualTo("/user/");

        path = "/user///";
        assertThat(path.replaceAll("(?<!https?:)/{2,}", "/")).isEqualTo("/user/");
    }

    @Test
    public void linkAnnotationExtract() {

        Matcher matcher = LINK_CLASS.matcher("{@link ProfessorDayOfWeekEnum}");

        String result = null;
        while (matcher.find()) {
            result = matcher.group(1);
        }

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("ProfessorDayOfWeekEnum");

        result = null;
        matcher = LINK_CLASS.matcher("{@linkProfessorDayOfWeekEnum}");
        while (matcher.find()) {
            result = matcher.group(1);
        }

        assertThat(result).isNull();

        result = null;
        matcher = LINK_CLASS.matcher("{@link  ProfessorDayOfWeekEnum }");
        while (matcher.find()) {
            result = matcher.group(1);
        }

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("ProfessorDayOfWeekEnum");
    }

    @Test
    public void seeAnnotationExtract() {
        Matcher matcher = SEE_CLASS.matcher("@see ProfessorDayOfWeekEnum");

        String result = null;
        while (matcher.find()) {
            result = matcher.group(1);
        }

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("ProfessorDayOfWeekEnum");

        result = null;
        matcher = SEE_CLASS.matcher("@see ProfessorDayOfWeekEnum}");
        while (matcher.find()) {
            result = matcher.group(1);
        }

        assertThat(result).isEqualTo("ProfessorDayOfWeekEnum");
    }

    @Test
    public void name() {

        Function<String, String> seeClassExtract = (String text) -> {
            Matcher matcher = SEE_CLASS.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return text;
        };

        Function<String, String> linkClassExtract = (String text) -> {
            Matcher matcher = LINK_CLASS.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return null;
        };

        String test = seeClassExtract.andThen(linkClassExtract).apply("test");
        System.out.println(test);

        List<Function<String, String>> functions = Arrays.asList(seeClassExtract, linkClassExtract);

        String text = "jsafoie";
        functions.stream().map(function -> function.apply(text)).filter(Objects::nonNull).findFirst()
                .ifPresent(className -> {

                });
    }

}
