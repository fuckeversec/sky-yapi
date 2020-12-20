package com.sky.build.chain;

import static com.sky.build.AbstractJsonApiParser.PARSE_CONTEXT_THREAD_LOCAL;

import com.google.common.base.Strings;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.sky.build.KV;
import com.sky.build.util.NormalTypes;
import com.sky.util.DesUtil;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gangyf
 * @since 2020/12/14 3:53 PM
 */
public class EnumPsiTypeParser extends AbstractPsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {
        if (!isEnum(psiType)) {
            nextParser.parser(psiType, kv);
            return;
        }

        kv.set("description", DesUtil.getEnumDesc(Objects.requireNonNull(PsiUtil.resolveClassInType(psiType))));
        Optional<String> descOptional = DesUtil.getDesc(psiType);
        descOptional.ifPresent(desc -> kv.set("description", desc + kv.get("description")));
    }

    @Override
    void parser(PsiField psiField, KV<String, Object> kv) {
        if (!isEnum(psiField.getType())) {
            nextParser.parser(psiField, kv);
            return;
        }

        kv.set("type", DesUtil.enumType(PARSE_CONTEXT_THREAD_LOCAL.get().getProject(), psiField));
        parser(psiField.getType(), kv);
        // 拼接字段注释和Enum类注释
        String description = Stream.of(DesUtil.getDesc(psiField), kv.get("description").toString())
                .filter(desc -> !Strings.isNullOrEmpty(desc))
                .collect(Collectors.joining(","));
        kv.set("description", description);

        if (NormalTypes.NORMAL_TYPES.containsKey(kv.get("type").toString())) {
            kv.set("default", NormalTypes.NORMAL_TYPES.get(kv.get("type").toString()));
        }

    }

    /**
     * Is enum boolean.
     *
     * @param psiType the psi type
     * @return the boolean
     */
    private boolean isEnum(PsiType psiType) {

        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        return Objects.requireNonNull(psiClass).isEnum();
    }
}
