package com.sky.build.chain;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.sky.build.KV;
import com.sky.build.util.LengthPropertyParse;
import com.sky.build.util.NormalTypes;
import com.sky.build.util.RequiredPropertyParse;
import com.sky.util.DesUtil;

/**
 * 简单类型
 *
 * @author gangyf
 * @since 2020/12/14 8:21 AM
 */
public class NormalPsiTypeParser extends AbstractPsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {

        if (!NormalTypes.isNormalType(psiType)) {
            nextParser.parser(psiType, kv);
            return;
        }

        kv.set("type", NormalTypes.javaTypeToJsType(psiType.getPresentableText()));
        kv.set("default", NormalTypes.NORMAL_TYPES.get(psiType.getPresentableText()));
    }

    @Override
    void parser(PsiField psiField, KV<String, Object> kv) {

        if (!NormalTypes.isNormalType(psiField.getType())) {
            nextParser.parser(psiField, kv);
            return;
        }

        kv.set("name", psiField.getName());
        kv.set("description", DesUtil.getDesc(psiField));
        kv.set("required", RequiredPropertyParse.required(psiField) ? "1" : "0");

        if (NormalTypes.isString(psiField.getType())) {
            LengthPropertyParse.maxLength(psiField).ifPresent(length -> kv.set("maxLength", length));

            LengthPropertyParse.minLength(psiField).ifPresent(length -> kv.set("minLength", length));
        }

        parser(psiField.getType(), kv);
    }

}
