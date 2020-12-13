package com.sky.build.util;

import com.intellij.psi.PsiType;
import com.sky.build.NormalTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author gangyf
 * @since 2020/12/13 2:56 PM
 */
public class ExampleValueUtil {

    /**
     * 通过PsiType, 获取常见类型数据的example
     *
     * @param psiType the psi type
     * @return the string
     */
    public static String psiTypeToExample(@NotNull PsiType psiType) {

        return NormalTypes.NORMAL_TYPES.get(psiType.getPresentableText());
    }
}
