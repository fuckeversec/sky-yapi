package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author gangyf
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ValueWrapper {

    /**
     * 是否必填
     */
    private String required = "1";

    /**
     * 描述
     */
    private String desc;

    /**
     * 示例
     */
    private String example;

    /**
     * 参数名字
     */
    private String name;

    /**
     * 参数值
     */
    private String value;

}