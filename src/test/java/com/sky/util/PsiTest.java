package com.sky.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.junit.Test;

/**
 * intellij idea psi usage
 *
 * @author gangyf
 * @since 2020/12/2 12:52 PM
 */
public class PsiTest {

    @Test
    public void psiTypeUsage() {
        Project project = ProjectManager.getInstance().getDefaultProject();
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass("com.sky.config.ConfigEntity",
                GlobalSearchScope.allScope(project));
        System.out.println(psiClass);
    }

}
