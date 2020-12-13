package com.sky.dto;

import com.intellij.psi.PsiVariable;
import com.jgoodies.common.base.Strings;
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

    private PsiVariable origin;

    public void full(ValueWrapper valueWrapper) {
        if (Strings.isNotBlank(valueWrapper.getName())) {
            this.setName(valueWrapper.getName());
        }
        if (Strings.isNotBlank(valueWrapper.getDesc())) {
            this.setDesc(valueWrapper.getDesc());
        }
        if (Strings.isNotBlank(valueWrapper.getRequired())) {
            this.setRequired(valueWrapper.getRequired());
        }
        if (Strings.isNotBlank(valueWrapper.getExample())) {
            this.setExample(valueWrapper.getExample());
        }
    }
}