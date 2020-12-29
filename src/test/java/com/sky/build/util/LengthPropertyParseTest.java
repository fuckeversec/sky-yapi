package com.sky.build.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.sky.AbstractTestCase;
import java.util.Optional;

public class LengthPropertyParseTest extends AbstractTestCase {

    private PsiClass userResponse;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        userResponse = psiClassMap.get("UserResponse");
    }

    public void testMaxLength_withAnnotationSize() {

        Optional<PsiField> optional = getFieldByName(userResponse, "surname");

        assertThat(optional.isPresent()).isTrue();
        optional.ifPresent(field -> {
            Optional<Integer> optionalInteger = LengthPropertyParse.maxLength(field);
            assertThat(optionalInteger.isPresent()).isTrue();
            assertThat(optionalInteger.get()).isEqualTo(64);
        });
    }

    public void testMaxLength_withAnnotationLength() {
        PsiClass userResponse = psiClassMap.get("UserResponse");

        Optional<PsiField> optional = getFieldByName(userResponse, "surname");

        assertThat(optional.isPresent()).isTrue();
        optional.ifPresent(field -> {
            Optional<Integer> optionalInteger = LengthPropertyParse.maxLength(field);
            assertThat(optionalInteger.isPresent()).isTrue();
            assertThat(optionalInteger.get()).isEqualTo(64);
        });
    }

    public void testMinLength_withAnnotationSize() {
        PsiClass userResponse = psiClassMap.get("UserResponse");

        Optional<PsiField> optional = getFieldByName(userResponse, "surname");

        assertThat(optional.isPresent()).isTrue();
        optional.ifPresent(field -> {
            Optional<Integer> optionalInteger = LengthPropertyParse.minLength(field);
            assertThat(optionalInteger.isPresent()).isTrue();
            assertThat(optionalInteger.get()).isEqualTo(1);
        });
    }

    public void testMinLength_withAnnotationLength() {
        PsiClass userResponse = psiClassMap.get("UserResponse");

        Optional<PsiField> optional = getFieldByName(userResponse, "surname");

        assertThat(optional.isPresent()).isTrue();
        optional.ifPresent(field -> {
            Optional<Integer> optionalInteger = LengthPropertyParse.minLength(field);
            assertThat(optionalInteger.isPresent()).isTrue();
            assertThat(optionalInteger.get()).isEqualTo(1);
        });
    }

}