package com.sky.build.domain;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.sky.AbstractTestCase;
import com.sky.build.JsonApiParser;
import com.sky.dto.YapiApiDTO;
import com.sky.util.JsonUtil;

public class SpringApiParserImplTest extends AbstractTestCase {

    JsonApiParser jsonApiParser = new SpringApiParserImpl();

    public void test_requestBodyIsNull() {
        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test11");

        YapiApiDTO yapiApiDTO = jsonApiParser.parseRequestMethod(project, psiMethod);

        System.out.println(JsonUtil.writeValueAsString(yapiApiDTO));
    }

}