package com.sky.util;

import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.impl.source.PsiFieldImpl;
import com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl;
import com.intellij.psi.impl.source.tree.java.PsiBinaryExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.sky.build.AbstractJsonApiParser;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * rest api path解析工具类
 *
 * @author gangyf
 * @since 2021/4/20 1:55 PM
 */
public class SpringPsiUtil {

    /**
     * 查找匹配注解中和路径相关的属性
     *
     * @param psiAnnotation the psi annotation
     * @return the psi annotation member value
     */
    public static PsiAnnotationMemberValue findPathMemberValue(PsiAnnotation psiAnnotation) {

        PsiAnnotationMemberValue attributeValue = null;

        for (PsiNameValuePair attribute : psiAnnotation.getParameterList().getAttributes()) {

            if (attributeValue != null) {
                break;
            }

            if (Strings.isNullOrEmpty(attribute.getName())) {
                attributeValue = attribute.getValue();
                break;
            }

            switch (attribute.getName()) {
                case AbstractJsonApiParser.HTTP_REQUEST_PATH_PATH:
                case AbstractJsonApiParser.HTTP_REQUEST_PATH_VALUE:
                    attributeValue = attribute.getValue();
                    break;
                default:
                    break;
            }

        }

        return attributeValue;
    }

    /**
     * 从注解属性中提取路径
     *
     * @param attributeValue
     * @return
     */
    public static String extractPath(PsiAnnotationMemberValue attributeValue) {

        if (attributeValue instanceof PsiArrayInitializerMemberValueImpl) {

            // append each element path to class path
            return Arrays.stream(((PsiArrayInitializerMemberValueImpl) attributeValue).getInitializers())
                    .filter(element -> element instanceof PsiLiteralExpressionImpl)
                    .map(element -> (PsiLiteralExpressionImpl) element)
                    .map(PsiLiteralExpressionImpl::getValue)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        } else if (attributeValue instanceof PsiLiteralExpressionImpl) {
            return Objects.requireNonNull(((PsiLiteralExpressionImpl) attributeValue).getValue()).toString();
        } else if (attributeValue instanceof PsiBinaryExpressionImpl) {
            StringBuilder path = new StringBuilder();

            // 表达式: "test" + "/1"
            for (PsiExpression operand : ((PsiBinaryExpressionImpl) attributeValue).getOperands()) {

                if (operand instanceof PsiReferenceExpressionImpl) {
                    PsiElement resolve = ((PsiReferenceExpressionImpl) operand).resolve();
                    path.append(Objects.requireNonNull(((PsiLiteralExpressionImpl) Objects
                            .requireNonNull(((PsiFieldImpl) Objects.requireNonNull(resolve)).getInitializer()))
                            .getValue()));
                    continue;
                }

                path.append(((PsiLiteralExpressionImpl) operand).getValue());
            }
            return path.toString();
        }

        return "";
    }

}
