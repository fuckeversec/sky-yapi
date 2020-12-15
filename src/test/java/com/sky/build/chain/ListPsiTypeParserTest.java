package com.sky.build.chain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.sky.AbstractTestCase;
import com.sky.build.KV;
import java.util.Map;
import org.mockito.Mockito;

@SuppressWarnings("unchecked")
public class ListPsiTypeParserTest extends AbstractTestCase {

    private PsiTypeParser firstPsiTypeParser;
    private ListPsiTypeParser listPsiTypeParse;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        firstPsiTypeParser = Mockito.mock(PsiTypeParser.class);
        listPsiTypeParse = new ListPsiTypeParser();
        listPsiTypeParse.setFirstPsiTypeParser(firstPsiTypeParser);
    }

    public void testList_withoutGeneralType() {
        doNothing().when(firstPsiTypeParser).parser(any(PsiType.class), any(KV.class));

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test12");

        PsiType psiType = psiMethod.getReturnType();

        KV<String, Object> kv = KV.create();
        listPsiTypeParse.parser(psiType, kv);
        assertThat(kv.get("type")).isEqualTo("array");
    }

    public void testList_withGeneralType() {
        doNothing().when(firstPsiTypeParser).parser(any(PsiType.class), any(KV.class));

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test13");

        PsiType psiType = psiMethod.getReturnType();

        KV<String, Object> kv = KV.create();
        listPsiTypeParse.parser(psiType, kv);
        assertThat(kv.get("type")).isEqualTo("array");
        assertThat(((Map<String, Object>) kv.get("items")).get("description")).isEqualTo("UserResponse");
    }

    public void testList_withString() {
        listPsiTypeParse.setFirstPsiTypeParser(new NormalPsiTypeParser());

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod psiMethod = getMethodByName(userController, "test14");

        PsiType psiType = psiMethod.getReturnType();

        KV<String, Object> kv = KV.create();
        listPsiTypeParse.parser(psiType, kv);
        assertThat(kv.get("type")).isEqualTo("array");
        assertThat(((Map<String, Object>) kv.get("items")).get("type")).isEqualTo("string");
        assertThat(((Map<String, Object>) kv.get("items")).get("example")).isEqualTo("");
    }
}