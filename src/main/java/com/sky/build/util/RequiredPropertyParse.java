package com.sky.build.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Abstract required parse.
 *
 * @author gangyf
 * @since 2020 /12/16 6:49 PM
 */
public class RequiredPropertyParse {

    private static List<String> notNullAnnotations = new ArrayList<>();

    static {
        notNullAnnotations.add("javax.validation.constraints.NotNull");
    }

    /**
     * 存在任何一个非空注解
     *
     * @param psiField the psi field
     * @return true, 存在非空注解
     */
    public static boolean required(PsiField psiField) {
        List<String> fieldAnnotations = Arrays.stream(psiField.getAnnotations())
                .map(PsiAnnotation::getQualifiedName)
                .collect(Collectors.toList());

        return notNullAnnotations.stream().anyMatch(fieldAnnotations::contains);
    }

}
