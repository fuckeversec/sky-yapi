package com.sky.build.util;

import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.javadoc.PsiDocTag;
import com.sky.dto.ValueWrapper;
import com.sky.util.DesUtil;
import java.util.Objects;

/**
 * @author gangyf
 * @since 2020/12/13 2:29 PM
 */
public class SpringMvcAnnotationUtil {

    /**
     * Parse request param.
     *
     * @param annotation the annotation
     * @param psiParameter the psi parameter
     * @param psiMethod the psi method
     * @return value wrapper
     */
    public static ValueWrapper parseRequestParam(PsiAnnotation annotation, PsiParameter psiParameter,
            PsiMethod psiMethod) {

        ValueWrapper yapiQueryDto = new ValueWrapper();

        for (PsiNameValuePair attribute : annotation.getParameterList().getAttributes()) {

            // only has default value or name
            if (Strings.isNullOrEmpty(attribute.getName())) {
                yapiQueryDto.setName(attribute.getLiteralValue());
                break;
            }

            switch (Objects.requireNonNull(attribute.getName())) {

                case "name":
                case "value":
                    yapiQueryDto.setName(attribute.getLiteralValue());
                    break;
                case "required":
                    if ("false".equals(attribute.getLiteralValue())) {
                        yapiQueryDto.setRequired("0");
                    }
                    break;
                case "defaultValue":
                    yapiQueryDto.setExample(attribute.getLiteralValue());
                    break;
                default:
                    break;
            }

        }

        if (Strings.isNullOrEmpty(yapiQueryDto.getName())) {
            yapiQueryDto.setName(psiParameter.getName());
        }

        yapiQueryDto.setExample(ExampleValueUtil.psiTypeToExample(psiParameter.getType()));


        yapiQueryDto.setDesc(DesUtil.paramDesc(psiMethod, psiParameter));

        return yapiQueryDto;
    }
}
