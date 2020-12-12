package com.sky.build;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiMethod;
import com.sky.dto.YapiApiDTO;

/**
 *
 * @author gangyf
 * @since 2020/12/11 9:02 PM
 */
public interface JsonApiParser {

    /**
     * Request path yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    YapiApiDTO requestPath(PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Request method yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    YapiApiDTO requestMethod(PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Request desc yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    YapiApiDTO requestDesc(PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Parse request yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    YapiApiDTO parseRequest(PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

    /**
     * Parse response yapi api dto.
     *
     * @param psiMethod the psi method
     * @param yapiApiDTO the yapi api dto
     * @return the yapi api dto
     */
    YapiApiDTO parseResponse(PsiMethod psiMethod, YapiApiDTO yapiApiDTO);

}
