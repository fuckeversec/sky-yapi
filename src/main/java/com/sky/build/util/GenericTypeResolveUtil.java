package com.sky.build.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.util.PsiUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections.MapUtils;

/**
 * 泛型
 *
 * @author gangyf
 * @since 2021/7/24 5:13 PM
 */
public class GenericTypeResolveUtil {

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
    public static Map<String, PsiType> resolveGenericTypes(PsiType psiType, PsiClass psiClass) {

        Map<String, PsiType> result = new HashMap<>(8);
        PsiTypeParameter[] typeParameters = psiClass.getTypeParameters();

        PsiType[] parameters = ((PsiClassType) psiType).getParameters();

        for (int i = 0; i < typeParameters.length; i++) {
            PsiType actualType = null;
            if (parameters.length > i) {
                actualType = parameters[i];
            }
            result.put(typeParameters[i].getName(), actualType);
            if (MapUtils.isNotEmpty(result)) {
                return result;
            }
        }

        for (PsiClassType classType : psiClass.getImplementsListTypes()) {
            resolveGenericTypes(classType, Objects.requireNonNull(PsiUtil.resolveClassInType(classType)))
                    .forEach((key1, value) -> result.computeIfAbsent(key1, key -> value));
        }

        for (PsiClassType classType : psiClass.getExtendsListTypes()) {
            resolveGenericTypes(classType, Objects.requireNonNull(PsiUtil.resolveClassInType(classType)))
                    .forEach((key1, value) -> result.computeIfAbsent(key1, key -> value));
        }

        return result;
    }

}
