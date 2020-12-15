package com.sky.build.chain;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.sky.AbstractTestCase;
import com.sky.build.KV;
import java.util.Objects;

public class EnumPsiTypeParserTest extends AbstractTestCase {

    private EnumPsiTypeParser enumPsiTypeParser;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        enumPsiTypeParser = new EnumPsiTypeParser();
    }

    public void testEnum() {
        PsiClass userResponse = psiClassMap.get("UserResponse");

        PsiField psiField = userResponse.findFieldByName("jdbcType", false);

        KV<String, Object> kv = KV.create();
        enumPsiTypeParser.parser(Objects.requireNonNull(psiField).getType(), kv);
        assertThat(kv.get("type")).isEqualTo("array");

    }
}