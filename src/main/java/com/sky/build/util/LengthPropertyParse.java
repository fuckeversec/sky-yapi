package com.sky.build.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiField;
import java.util.Optional;

/**
 * 数据字段长度验证
 *
 * @author gangyf
 * @since 2020/12/29 11:50 AM
 */
public class LengthPropertyParse {

    /**
     * Max length optional.
     *
     * @param psiField the psi field
     * @return the optional
     */
    public static Optional<Integer> maxLength(PsiField psiField) {

        return parse(psiField, "max");
    }

    /**
     * Min length optional.
     *
     * @param psiField the psi field
     * @return the optional
     */
    public static Optional<Integer> minLength(PsiField psiField) {
        return parse(psiField, "min");
    }

    private static Optional<Integer> parse(PsiField psiField, String property) {
        for (PsiAnnotation annotation : psiField.getAnnotations()) {

            String qualifiedName = annotation.getQualifiedName();

            if (qualifiedName == null) {
                return Optional.empty();
            }

            switch (qualifiedName) {
                case "Size":
                case "javax.validation.constraints.Size":
                case "Length":
                case "org.hibernate.validator.constraints.Length":
                    PsiAnnotationMemberValue propertyMemberValue = annotation.findAttributeValue(property);
                    if (propertyMemberValue != null) {
                        return Optional.of(Integer.valueOf(propertyMemberValue.getText()));
                    }
                    break;
                default:
                    break;
            }

        }
        return Optional.empty();
    }
}
