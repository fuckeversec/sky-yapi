package com.sky.build;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameterList;
import com.sky.dto.YapiApiDTO;

/**
 * The interface Json api parser.
 *
 * @author gangyf
 * @since 2020 /12/11 9:02 PM
 */
public interface JsonApiParser {

    /**
     * 模板方法, 调用实现类方法, 填充YapiApiDTO
     *
     * @param project the project
     * @param psiMethod the psi method
     * @return the yapi api dto
     */
    YapiApiDTO parseRequestMethod(Project project, PsiMethod psiMethod);

    /**
     * Request path yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    void requestPath(PsiModifierList psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Request method yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    void requestMethod(PsiModifierList psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Request desc yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    void requestDesc(PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Request title.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     */
    void requestTitle(PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Parse request yapi api dto.
     *
     * @param parameterList the parameter list
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    void parseRequest(PsiParameterList parameterList, YapiApiDTO yapiApiDTO);

    /**
     * Parse response yapi api dto.
     *
     * @param project the project
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    void parseResponse(Project project, PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

}
