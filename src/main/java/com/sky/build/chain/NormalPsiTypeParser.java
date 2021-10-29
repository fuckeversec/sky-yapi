package com.sky.build.chain;

import static com.sky.build.AbstractJsonApiParser.PARSE_CONTEXT_THREAD_LOCAL;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.sky.build.KV;
import com.sky.build.util.LengthPropertyParse;
import com.sky.build.util.NormalTypes;
import com.sky.util.DesUtil;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * 简单类型
 *
 * @author gangyf
 * @since 2020/12/14 8:21 AM
 */
public class NormalPsiTypeParser extends AbstractPsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {
        if (Objects.isNull(PsiUtil.resolveClassInType(psiType))) {
            return;
        }

        if (!NormalTypes.isNormalType(psiType)) {
            nextParser.parser(psiType, kv);
            return;
        }

        kv.set("type", NormalTypes.javaTypeToJsType(psiType.getPresentableText()));
        kv.set("default", NormalTypes.NORMAL_TYPES.get(psiType.getPresentableText()));
    }

    @Override
    void parser(PsiField psiField, KV<String, Object> kv) {

        if (Objects.isNull(PsiUtil.resolveClassInType(psiField.getType()))) {
            return;
        }

        if (!NormalTypes.isNormalType(psiField.getType())) {
            nextParser.parser(psiField, kv);
            return;
        }

        kv.set("description", Optional.ofNullable(DesUtil.getDesc(psiField)).filter(s -> !StringUtils.isEmpty(s))
                .orElse(DesUtil.getLinkOrSeeRemark(PARSE_CONTEXT_THREAD_LOCAL.get().getProject(), psiField)));

        if (NormalTypes.isString(psiField.getType())) {
            LengthPropertyParse.maxLength(psiField).ifPresent(length -> kv.set("maxLength", length));

            LengthPropertyParse.minLength(psiField).ifPresent(length -> kv.set("minLength", length));
        }

        parser(psiField.getType(), kv);
    }

}
