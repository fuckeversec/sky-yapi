package com.sky.build.util;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.sky.AbstractTestCase;
import com.sky.constant.SpringMVCConstant;
import com.sky.dto.ValueWrapper;
import com.sky.util.PsiAnnotationSearchUtil;
import java.util.Objects;

public class SpringMvcAnnotationUtilTest extends AbstractTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testParseRequestParam_withDefaultValue() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test5");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, psiMethod);

        assertThat(valueWrapper.getName()).isEqualTo("id");
        assertThat(valueWrapper.getRequired()).isEqualTo("1");
    }

    public void testParseRequestParam_withIncomplete() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test8");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, psiMethod);

        assertThat(valueWrapper.getName()).isEqualTo("fake_name");
    }

    public void testParseRequestParam_specifyNameOrValue() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test6");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, psiMethod);

        assertThat(valueWrapper.getName()).isEqualTo("fake_value");
        assertThat(valueWrapper.getRequired()).isEqualTo("1");

        psiMethod = getMethodByName(userController, "test7");

        parameter = psiMethod.getParameterList().getParameters()[0];

        valueWrapper = SpringMvcAnnotationUtil.parseRequestParam(Objects.requireNonNull(PsiAnnotationSearchUtil
                .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, psiMethod);

        assertThat(valueWrapper.getName()).isEqualTo("fake_name");
        assertThat(valueWrapper.getRequired()).isEqualTo("1");
    }

    public void testParaDesc() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test6");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, psiMethod);

        assertThat(valueWrapper.getDesc()).isEqualTo("the id");

        psiMethod = getMethodByName(userController, "test7");

        parameter = psiMethod.getParameterList().getParameters()[1];

        valueWrapper = SpringMvcAnnotationUtil.parseRequestParam(Objects.requireNonNull(PsiAnnotationSearchUtil
                .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, psiMethod);

        assertThat(valueWrapper.getDesc()).isEqualTo("the gender");
    }

    public void testParaDesc_withoutParamDoc() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test5");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, psiMethod);

        assertThat(valueWrapper.getDesc()).isEqualTo("Long");
    }


}