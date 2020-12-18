package com.sky.build.chain;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.sky.build.KV;
import com.sky.build.util.TimeFormatUtil;
import com.sky.util.DesUtil;

/**
 * @author gangyf
 * @since 2020/12/17 10:58 PM
 */
public class TimePsiTypeParse extends AbstractPsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {
        if (!TimeFormatUtil.isTimeType(psiType)){
            nextParser.parser(psiType, kv);
            return;
        }

        kv.set("type", "string");
        kv.set("default", TimeFormatUtil.defaultValue(psiType));
    }

    @Override
    void parser(PsiField psiField, KV<String, Object> kv) {

        if (!TimeFormatUtil.isTimeType(psiField.getType())) {
            nextParser.parser(psiField, kv);
            return;
        }

        parser(psiField.getType(), kv);
        kv.set("name", psiField.getName());
        kv.set("description", DesUtil.getDesc(psiField));
        kv.set("default", TimeFormatUtil.formatValue(psiField));
    }
}
