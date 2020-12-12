package com.sky.util;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void tagPattern() {

        Matcher matcher = DesUtil.SEE_OR_LINK_TAG.matcher("GenderEnum#getValue()");

        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group(1)).isEqualTo("GenderEnum");
        assertThat(matcher.group(2)).isEqualTo("getValue");

        matcher = DesUtil.SEE_OR_LINK_TAG.matcher("GenderEnum");
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group(1)).isEqualTo("GenderEnum");

        matcher = DesUtil.SEE_OR_LINK_TAG.matcher("GenderEnum#getValue(Long, Integer)");
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group(1)).isEqualTo("GenderEnum");
        assertThat(matcher.group(2)).isEqualTo("getValue");
    }
}
