package com.sky.build.util;

import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.sky.util.JsonUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间格式化
 *
 * @author gangyf
 * @since 2020/12/17 11:17 PM
 */
public class TimeFormatUtil {

    private static final Map<String, String> TIME_TYPES = new HashMap<>();

    static {
        TIME_TYPES.put("java.util.Date", JsonUtil.writeValueAsString(new Date()));
        TIME_TYPES.put("java.time.LocalDate", JsonUtil.writeValueAsString(LocalDate.now()));
        TIME_TYPES.put("java.time.LocalTime", JsonUtil.writeValueAsString(LocalTime.now()));
        TIME_TYPES.put("java.time.LocalDateTime", JsonUtil.writeValueAsString(LocalDateTime.now()));
        TIME_TYPES.put("java.sql.Date", JsonUtil.writeValueAsString(new java.sql.Date(System.currentTimeMillis())));
    }

    /**
     * Is time type boolean.
     *
     * @param psiType the psi type
     * @return the boolean
     */
    public static boolean isTimeType(PsiType psiType) {
        return TIME_TYPES.containsKey(psiType.getCanonicalText());
    }

    /**
     * Default value string.
     *
     * @param psiType the psi type
     * @return the string
     */
    public static String defaultValue(PsiType psiType) {
        return TIME_TYPES.get(psiType.getCanonicalText());
    }

    /**
     * Format value string.
     *
     * @param psiField the psi field
     * @return the string
     */
    public static String formatValue(PsiField psiField) {

        for (PsiAnnotation annotation : psiField.getAnnotations()) {

            if (Strings.isNullOrEmpty(annotation.getQualifiedName())) {
                continue;
            }

            switch (annotation.getQualifiedName()) {
                case "com.fasterxml.jackson.annotation.JsonFormat":
                    PsiAnnotationMemberValue memberValue = annotation.findAttributeValue("pattern");
                    if (memberValue == null) {
                        break;
                    }
                    String pattern = memberValue.getText();
                    if (!Strings.isNullOrEmpty(pattern)) {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                        switch (psiField.getType().getCanonicalText()) {
                            case "java.time.LocalDate":
                                return dateTimeFormatter.format(LocalDate.now());
                            case "java.time.LocalTime":
                                return dateTimeFormatter.format(LocalTime.now());
                            case "java.time.LocalDateTime":
                                return dateTimeFormatter.format(LocalDateTime.now());
                            default:
                                break;
                        }
                    }
                default:
                    break;
            }

        }
        return TIME_TYPES.get(psiField.getType().getCanonicalText());
    }
}
