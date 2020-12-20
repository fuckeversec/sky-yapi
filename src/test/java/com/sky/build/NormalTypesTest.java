package com.sky.build;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.sky.AbstractTestCase;
import com.sky.build.util.NormalTypes;

public class NormalTypesTest extends AbstractTestCase {

    public void testIsCollection() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test11");

        PsiType psiType = psiMethod.getReturnType();

        assertThat(psiType).isNotNull();
        assertThat(NormalTypes.isCollection(psiType)).isTrue();
    }

    public void testCustomizeListIsCollection() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test12");

        PsiType psiType = psiMethod.getReturnType();

        assertThat(psiType).isNotNull();
        assertThat(NormalTypes.isCollection(psiType)).isFalse();
    }
}