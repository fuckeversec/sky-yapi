package com.sky.build.chain;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.sky.AbstractTestCase;
import com.sky.build.KV;

public class NormalPsiTypeParserTest extends AbstractTestCase {

    private NormalPsiTypeParser normalPsiTypeParser;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        normalPsiTypeParser = new NormalPsiTypeParser();
    }

    public void testPrimitiveType() {
        PsiClass userResponse = psiClassMap.get("UserResponse");

        PsiField psiField = userResponse.findFieldByName("testInt", false);
        assertThat(psiField).isNotNull();

        KV<String, Object> kv = KV.create();
        normalPsiTypeParser.parser(psiField, kv);
        assertThat(kv.get("type")).isEqualTo("integer");
        assertThat(kv.get("description")).isEqualTo("测试基本类型");
    }

}