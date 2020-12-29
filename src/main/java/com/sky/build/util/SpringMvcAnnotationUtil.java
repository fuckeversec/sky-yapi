package com.sky.build.util;

import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.sky.dto.ValueWrapper;
import java.util.Objects;

/**
 * @author gangyf
 * @since 2020/12/13 2:29 PM
 */
public class SpringMvcAnnotationUtil {

    public static void anotherAnnotationParse(PsiAnnotation annotation, PsiParameter psiParameter, ValueWrapper valueWrapper) {

        if (Strings.isNullOrEmpty(annotation.getQualifiedName())) {
            return;
        }

        switch (annotation.getQualifiedName()) {
            case "DateTimeFormat":
            case "org.springframework.format.annotation.DateTimeFormat":
                String dateExample = TimeFormatUtil.formatValue(annotation, psiParameter);
                valueWrapper.setExample(dateExample);
                break;
            default:
                break;
        }
    }

    /**
     * Parse request param.
     *
     * @param annotation the annotation
     * @param psiParameter the psi parameter
     * @param yapiQueryDto the yapi query dto
     */
    public static void parseRequestParam(PsiAnnotation annotation, PsiParameter psiParameter,
            ValueWrapper yapiQueryDto) {

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

    }
}
