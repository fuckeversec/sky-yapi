package com.sky.build.util;

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

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter,
                valueWrapper);

        assertThat(valueWrapper.getName()).isEqualTo("id");
        assertThat(valueWrapper.getRequired()).isEqualTo("1");
    }

    public void testParseRequestParam_withIncomplete() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test8");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter,
                valueWrapper);

        assertThat(valueWrapper.getName()).isEqualTo("fake_name");
    }

    public void testParseRequestParam_specifyNameOrValue() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test6");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(
                        PsiAnnotationSearchUtil.findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter,
                valueWrapper);

        assertThat(valueWrapper.getName()).isEqualTo("fake_value");
        assertThat(valueWrapper.getRequired()).isEqualTo("1");

        psiMethod = getMethodByName(userController, "test7");

        parameter = psiMethod.getParameterList().getParameters()[0];

        SpringMvcAnnotationUtil.parseRequestParam(Objects.requireNonNull(PsiAnnotationSearchUtil
                .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, valueWrapper);

        assertThat(valueWrapper.getName()).isEqualTo("fake_name");
        assertThat(valueWrapper.getRequired()).isEqualTo("1");
    }

    public void testParaDesc() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test6");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter,
                valueWrapper);

        assertThat(valueWrapper.getDesc()).isEqualTo("the id");

        psiMethod = getMethodByName(userController, "test7");

        parameter = psiMethod.getParameterList().getParameters()[1];

        SpringMvcAnnotationUtil.parseRequestParam(Objects.requireNonNull(PsiAnnotationSearchUtil
                .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter, valueWrapper);

        assertThat(valueWrapper.getDesc()).isEqualTo("the gender");
    }

    public void testParaDesc_withoutParamDoc() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test5");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.RequestParam)), parameter,
                valueWrapper);

        assertThat(valueWrapper.getDesc()).isEqualTo("Long");
    }

    public void testParsePathVariable() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test9");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.PathVariable)), parameter,
                valueWrapper);

        assertThat(valueWrapper.getName()).isEqualTo("id");
    }

    public void testParsePathVariable_specifyName() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test10");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.parseRequestParam(
                Objects.requireNonNull(PsiAnnotationSearchUtil
                        .findAnnotation(parameter, SpringMVCConstant.PathVariable)), parameter,
                valueWrapper);

        assertThat(valueWrapper.getName()).isEqualTo("id");
    }

    public void testDateTimeFormat_format() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test15");

        PsiParameter parameter = psiMethod.getParameterList().getParameters()[0];

        ValueWrapper valueWrapper = new ValueWrapper();
        SpringMvcAnnotationUtil.anotherAnnotationParse(
                Objects.requireNonNull(PsiAnnotationSearchUtil.findAnnotation(parameter, "org.springframework.format"
                        + ".annotation.DateTimeFormat")), parameter, valueWrapper);

        String example = valueWrapper.getExample();
        assertThat(example.matches("\\d{4}-\\d{2}-\\d{2}")).isTrue();
    }

}