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
import com.sky.build.util.RequiredPropertyParse;
import com.sky.util.DesUtil;
import java.util.ArrayList;
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
public class ObjectPsiTypeParse extends AbstractPsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {

        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);

        if (psiClass == null) {
            return;
        }

        List<PsiField> collect = getPsiFields(psiClass);
        KV<String, KV<String, Object>> properties = KV.create();
        Map<String, PsiType> resolvedGeneralTypes = resolveGenericTypes(psiType, psiClass);

        List<String> required = new ArrayList<>();

        for (PsiField psiField : collect) {
            KV<String, Object> fieldProperty = KV.create();
            properties.set(psiField.getName(), fieldProperty);

            // general type, replace to actual type
            if (genericTypeeHasActualType(resolvedGeneralTypes, psiField)) {

                PsiType fieldType = resolvedGeneralTypes.get(psiField.getType().getPresentableText());
                firstPsiTypeParser.parser(fieldType, fieldProperty);
                fieldProperty.set("description", DesUtil.getDesc(psiField));
            } else {
                firstPsiTypeParser.parser(psiField, fieldProperty);
            }

            // 存在非空注解, 添加到required列表
            if (RequiredPropertyParse.required(psiField)) {
                required.add(psiField.getName());
            }
        }

        kv.set("type", "object");
        kv.set("required", required);
        kv.set("properties", properties);
        kv.set("description", DesUtil.getDesc(psiType).orElse(""));
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
    private Map<String, PsiType> resolveGenericTypes(PsiType psiType, PsiClass psiClass) {

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

    /**
     * Generic typee has actual type boolean.
     *
     * @param resolvedGeneralTypes the resolved general types
     * @param psiField the psi field
     * @return the boolean
     */
    private boolean genericTypeeHasActualType(Map<String, PsiType> resolvedGeneralTypes, PsiField psiField) {
        return NormalTypes.isGenericType(psiField.getType()) &&
                resolvedGeneralTypes.containsKey(psiField.getType().getPresentableText());
    }

}
