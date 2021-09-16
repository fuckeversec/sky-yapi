package com.sky.build.chain;

import static com.sky.build.AbstractJsonApiParser.PARSE_CONTEXT_THREAD_LOCAL;

import com.google.common.base.Strings;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.sky.build.KV;
import com.sky.build.util.LengthPropertyParse;
import com.sky.build.util.NormalTypes;
import com.sky.util.DesUtil;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        kv.set("description", Stream.of(DesUtil.getDesc(psiField),
                        DesUtil.getLinkOrSeeRemark(PARSE_CONTEXT_THREAD_LOCAL.get().getProject(), psiField))
                .filter(desc -> !Strings.isNullOrEmpty(desc))
                .collect(Collectors.joining(",")));

        if (NormalTypes.isString(psiField.getType())) {
            LengthPropertyParse.maxLength(psiField).ifPresent(length -> kv.set("maxLength", length));

            LengthPropertyParse.minLength(psiField).ifPresent(length -> kv.set("minLength", length));
        }

        parser(psiField.getType(), kv);
    }

}
