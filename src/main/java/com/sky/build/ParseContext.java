package com.sky.build;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import lombok.Builder;
import lombok.Data;

/**
 * @author gangyf
 * @since 2020/12/20 10:18 AM
 */
@Data
@Builder
public class ParseContext {

    private Project project;

    private PsiClass psiClass;

    private PsiMethod psiMethod;

    private Stack<Map<String, PsiType>> stack;

    public Map<String, PsiType> resolvedGeneralTypes() {
        if (stack == null) {
            return new HashMap<>();
        }

        return stack.peek();
    }

    public void addResolvedGeneralTypes(Map<String, PsiType> resolvedGeneralTypes) {
        if (stack == null) {
            stack = new Stack<>();
        }

        stack.add(resolvedGeneralTypes);
    }

    public void popResolvedGeneralTypes() {
        if (stack == null) {
            return;
        }

        stack.pop();
    }

}
