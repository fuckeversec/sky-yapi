package com.sky.build.chain;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;
import com.sky.build.KV;
import com.sky.build.NormalTypes;
import com.sky.util.DesUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * pojo对象解析
 *
 * @author gangyf
 * @since 2020/12/14 3:24 PM
 */
public class ObjectPsiTypeParse extends PsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {

        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);

        if (psiClass == null) {
            return;
        }

        List<PsiField> collect = getPsiFields(psiClass);
        KV<String, KV<String, Object>> properties = KV.create();
        Map<String, PsiType> resolveGeneralTypes = resolveGeneralTypes(psiType, psiClass);

        for (PsiField psiField : collect) {
            KV<String, Object> fieldProperty = KV.create();
            properties.set(psiField.getName(), fieldProperty);
            // general type, replace to actual type
            String fieldTypeName = psiField.getType().getPresentableText();
            if (NormalTypes.GENERIC_LIST.contains(fieldTypeName)
                    && resolveGeneralTypes.containsKey(fieldTypeName)) {

                PsiType fieldType = resolveGeneralTypes.get(fieldTypeName);
                firstPsiTypeParser.parser(fieldType, fieldProperty);
                fieldProperty.set("description", DesUtil.getDesc(psiField));
            } else {
                firstPsiTypeParser.parser(psiField, fieldProperty);
            }
        }
        kv.set("type", "object");
        kv.set("description", DesUtil.getDesc(psiType).orElse(""));
        kv.set("properties", properties);
    }

    @Override
    void parser(PsiField psiField, KV<String, Object> kv) {
        KV<String, Object> properties = KV.create();
        parser(psiField.getType(), properties);
        kv.putAll(properties);
        kv.set("description", DesUtil.getDesc(psiField));
    }

    /**
     * Gets psi fields.
     *
     * @param psiClass the psi class
     * @return the psi fields
     */
    @NotNull
    private List<PsiField> getPsiFields(PsiClass psiClass) {

        return Arrays.stream(psiClass.getAllFields())
                .filter(psiField -> !psiField.hasModifierProperty(PsiModifier.FINAL)).collect(Collectors.toList());
    }

    /**
     * Resolve general types map.
     * Map<String, PsiType>
     * key: General Type
     * value: Actual Type (might be null, when doesn't use diamond to specify type)
     *
     * @param psiType the psi type
     * @param psiClass the psi class
     * @return the map
     */
    private Map<String, PsiType> resolveGeneralTypes(PsiType psiType, PsiClass psiClass) {

        Map<String, PsiType> result = new HashMap<>(8);
        PsiTypeParameter[] typeParameters = psiClass.getTypeParameters();
        if (typeParameters.length == 0) {
            return result;
        }

        PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();

        for (int i = 0; i < typeParameters.length; i++) {
            PsiType actualType = null;
            if (parameters.length > i) {
                actualType = parameters[i];
            }
            result.put(typeParameters[i].getName(), actualType);
        }

        return result;
    }

}
