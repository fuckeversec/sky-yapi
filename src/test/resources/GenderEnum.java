package com.sky.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别d
 * @author gangyf
 * @since 2020/11/26 11:51 AM
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    /**
     * 0, 男
     */
    MALE(0, "男"),

    /**
     * 1, 女
     */
    FEMALE(1, "女"),

    ;

    private final int value;
    private final String desc;

}
