package com.sky.build.domain;

import com.intellij.psi.PsiClass;
import lombok.Builder;
import lombok.Data;

/**
 * @author gangyf
 * @since 2020/12/11 10:08 PM
 */
@Data
@Builder
public class SelectedContext {

    private String selectedText;

    private PsiClass selectedPsiClass;

}
