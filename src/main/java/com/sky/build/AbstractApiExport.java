package com.sky.build;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PsiTreeUtil;
import com.sky.build.domain.SelectedContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gangyf
 * @since 2020/12/11 9:18 PM
 */
public abstract class AbstractApiExport implements ApiExport {

    /**
     * 解析选中文本
     *
     * @param anActionEvent the an action event
     * @return the selected context
     */
    protected List<PsiMethod> actionPerform(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getDataContext().getData(CommonDataKeys.PSI_FILE);

        // 选中的文字 （类名、方法名）
        String selectedText = anActionEvent.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel()
                .getSelectedText();

        // 所选的类
        assert psiFile != null;
        assert editor != null;
        PsiClass selectedClass = PsiTreeUtil
                .getContextOfType(psiFile.findElementAt(editor.getCaretModel().getOffset()), PsiClass.class);

        SelectedContext.builder().selectedText(selectedText).selectedPsiClass(selectedClass).build();

        assert selectedClass != null;

        // 选中类, 或者不选中, 解析当前指针所在的类
        if (Objects.equals(selectedClass.getName(), selectedText) || Objects.isNull(selectedText)) {
            return nonPrivateMethods(selectedClass);
        } else {
            // 选择的是方法 则处理此方法
            Optional<PsiMethod> selectedMethod = nonPrivateMethods(selectedClass).stream()
                    .filter(method -> method.getName().equals(selectedText))
                    .findAny();

            if (selectedMethod.isPresent()) {
                return Collections.singletonList(selectedMethod.get());
            }

        }

        return Collections.emptyList();
    }

    /**
     * Non private methods list.
     *
     * @param psiClass the psi class
     * @return the list
     */
    private List<PsiMethod> nonPrivateMethods(PsiClass psiClass) {

        return Arrays.stream(psiClass.getMethods()).filter(method ->
                !method.getModifierList().hasModifierProperty(PsiModifier.PRIVATE)).collect(Collectors.toList());
    }

}
