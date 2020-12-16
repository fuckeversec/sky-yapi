package com.sky.build;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.sky.build.chain.PsiTypeParserChain;
import com.sky.constant.SpringMVCConstant;
import com.sky.dto.YapiApiDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @author gangyf
 * @since 2020/12/13 10:00 AM
 */
public abstract class AbstractJsonApiParser implements JsonApiParser {

    public final static ThreadLocal<ParseContext> PARSE_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    protected final static String HTTP_METHOD_KEY = "method";
    protected final static Map<String, String> MAPPING_TO_HTTP_METHOD;
    protected final static String HTTP_REQUEST_PATH_PATH = "path";
    protected final static String HTTP_REQUEST_PATH_VALUE = "value";

    protected final static Map<String, String> REQUEST_METHOD;

    static {
        MAPPING_TO_HTTP_METHOD = new HashMap<>(16);

        MAPPING_TO_HTTP_METHOD.put("GetMapping", "GET");
        MAPPING_TO_HTTP_METHOD.put("PostMapping", "POST");
        MAPPING_TO_HTTP_METHOD.put("PutMapping", "PUT");
        MAPPING_TO_HTTP_METHOD.put("PatchMapping", "PATCH");
        MAPPING_TO_HTTP_METHOD.put("DeleteMapping", "DELETE");
        MAPPING_TO_HTTP_METHOD.put(SpringMVCConstant.GetMapping, "GET");
        MAPPING_TO_HTTP_METHOD.put(SpringMVCConstant.PostMapping, "POST");
        MAPPING_TO_HTTP_METHOD.put(SpringMVCConstant.PutMapping, "PUT");
        MAPPING_TO_HTTP_METHOD.put(SpringMVCConstant.DeleteMapping, "DELETE");
        MAPPING_TO_HTTP_METHOD.put(SpringMVCConstant.PatchMapping, "PATCH");

        REQUEST_METHOD = new HashMap<>();
        REQUEST_METHOD.put("GET", "GET");
        REQUEST_METHOD.put("HEAD", "HEAD");
        REQUEST_METHOD.put("POST", "POST");
        REQUEST_METHOD.put("PUT", "PUT");
        REQUEST_METHOD.put("DELETE", "DELETE");
        REQUEST_METHOD.put("TRACE", "TRACE");
        REQUEST_METHOD.put("OPTIONS", "OPTIONS");
        REQUEST_METHOD.put("RequestMethod.GET", "GET");
        REQUEST_METHOD.put("RequestMethod.HEAD", "HEAD");
        REQUEST_METHOD.put("RequestMethod.POST", "POST");
        REQUEST_METHOD.put("RequestMethod.PUT", "PUT");
        REQUEST_METHOD.put("RequestMethod.DELETE", "DELETE");
        REQUEST_METHOD.put("RequestMethod.TRACE", "TRACE");
        REQUEST_METHOD.put("RequestMethod.OPTIONS", "OPTIONS");
    }

    @Override
    public YapiApiDTO parseRequestMethod(Project project, PsiMethod psiMethod) {

        YapiApiDTO yapiApiDTO = new YapiApiDTO();

        PARSE_CONTEXT_THREAD_LOCAL.set(ParseContext.builder()
                .project(project)
                .psiMethod(psiMethod)
                .psiClass(psiMethod.getContainingClass())
                .build());

        requestDesc(psiMethod, yapiApiDTO);

        requestTitle(psiMethod, yapiApiDTO);

        psiMethod.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement psiElement) {
                super.visitElement(psiElement);

                if (psiElement instanceof PsiModifierList) {

                    requestPath((PsiModifierList) psiElement, yapiApiDTO);

                    requestMethod((PsiModifierList) psiElement, yapiApiDTO);
                    return;
                }

                if (psiElement instanceof PsiParameterList) {

                    parseRequest((PsiParameterList) psiElement, yapiApiDTO);
                }
            }

        });

        parseResponse(project, psiMethod, yapiApiDTO);

        PARSE_CONTEXT_THREAD_LOCAL.remove();
        return yapiApiDTO;
    }

    /**
     * 生成form类型请求参数描述信息
     *
     * @param psiType the psi type
     * @return the list
     */
    public List<Map<String, Object>> parseRequestForm(PsiType psiType) {
        PsiTypeParserChain psiTypeParserChain = new PsiTypeParserChain();
        KV<String, Object> response = psiTypeParserChain.parse(psiType);
        List<Map<String, Object>> result = new ArrayList<>();
        if (NormalTypes.isNormalType(psiType)) {
            result.add(response);
        } else {
            @SuppressWarnings("unchecked")
            KV<String, KV<String, Object>> properties =
                    (KV<String, KV<String, Object>>) response.get("properties");
            result.addAll(properties.values());
        }
        return result;
    }


    @Data
    @Builder
    public static class ParseContext {

        private Project project;

        private PsiClass psiClass;

        private PsiMethod psiMethod;
    }
}
