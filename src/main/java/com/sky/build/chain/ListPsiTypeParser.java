package com.sky.build.chain;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.sky.build.KV;
import com.sky.build.util.NormalTypes;
import com.sky.util.DesUtil;

/**
 * 解析collection, json(array)类型数据
 *
 * @author gangyf
 * @since 2020/12/14 8:44 AM
 */
public class ListPsiTypeParser extends AbstractPsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {
        if (!NormalTypes.isCollection(psiType)) {
            nextParser.parser(psiType, kv);
            return;
        }

        kv.set("type", "array");

        // get general types
        PsiType[] typeParameters = ((PsiClassReferenceType) psiType).getReference().getTypeParameters();

        if (typeParameters.length == 0) {
            // no general type
            kv.set("items", KV.by("type", "object"));
            return;
        }

        for (PsiType typeParameter : typeParameters) {

            KV<String, Object> items = KV.create();
            kv.set("items", items);
            firstPsiTypeParser.parser(typeParameter, items);
        }
    }

    @Override
    void parser(PsiField psiField, KV<String, Object> kv) {

        if (!NormalTypes.isCollection(psiField.getType())) {
            nextParser.parser(psiField, kv);
            return;
        }

        parser(psiField.getType(), kv);
        kv.set("description", DesUtil.getDesc(psiField));
    }

}
