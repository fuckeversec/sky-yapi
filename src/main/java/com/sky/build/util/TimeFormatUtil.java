package com.sky.build.util;

import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.sky.util.JsonUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * 时间格式化
 *
 * @author gangyf
 * @since 2020/12/17 11:17 PM
 */
public class TimeFormatUtil {

    private static final String DATE_TIME_FORMAT_DATE = "DATE";
    private static final String DATE_TIME_FORMAT_TIME = "TIME";
    private static final String DATE_TIME_FORMAT_DATE_TIME = "DATE_TIME";

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

            String dateTimeFormatter = formatDateByAnnotation(annotation, psiField.getType());

            if (dateTimeFormatter != null) {
                return dateTimeFormatter;
            }

        }
        return defaultValue(psiField.getType());
    }

    /**
     * Format value string.
     *
     * @param psiAnnotation the psi field
     * @param psiParameter
     * @return the string
     */
    public static String formatValue(PsiAnnotation psiAnnotation, PsiParameter psiParameter) {

        if (Strings.isNullOrEmpty(psiAnnotation.getQualifiedName())) {
            return defaultValue(psiParameter.getType());
        }

        String dateTimeFormatter = formatDateByAnnotation(psiAnnotation, psiParameter.getType());

        if (dateTimeFormatter != null) {
            return dateTimeFormatter;
        }

        return defaultValue(psiParameter.getType());
    }

    @Nullable
    private static String formatDateByAnnotation(PsiAnnotation psiAnnotation, PsiType psiType) {
        switch (psiAnnotation.getQualifiedName()) {
            case "DateTimeFormat":
            case "org.springframework.format.annotation.DateTimeFormat":
            case "com.fasterxml.jackson.annotation.JsonFormat":
                PsiAnnotationMemberValue memberValue = psiAnnotation.findAttributeValue("pattern");
                String pattern = null;
                if (memberValue != null) {
                    pattern = memberValue.getText();
                }
                if (!Strings.isNullOrEmpty(pattern)) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                    switch (psiType.getCanonicalText()) {
                        case "java.time.LocalDate":
                            return dateTimeFormatter.format(LocalDate.now());
                        case "java.time.LocalTime":
                            return dateTimeFormatter.format(LocalTime.now());
                        case "java.time.LocalDateTime":
                            return dateTimeFormatter.format(LocalDateTime.now());
                        default:
                            break;
                    }
                } else {
                    PsiAnnotationMemberValue iso = psiAnnotation.findAttributeValue("iso");
                    if (iso == null) {
                        break;
                    }
                    String text = iso.getText();
                    if (text.endsWith(DATE_TIME_FORMAT_DATE_TIME)) {
                        return "2000-10-31T01:30:00.000-05:00";
                    } else if (text.endsWith(DATE_TIME_FORMAT_TIME)) {
                        return "01:30:00.000-05:00";
                    } else if (text.endsWith(DATE_TIME_FORMAT_DATE)) {
                        return "2020-12-26";
                    }
                }
            default:
                break;
        }
        return null;
    }
}
