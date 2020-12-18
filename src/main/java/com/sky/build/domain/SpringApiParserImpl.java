package com.sky.build.domain;

import static com.sky.constant.JavaConstant.HttpServletRequest;
import static com.sky.constant.JavaConstant.HttpServletResponse;

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.sky.build.AbstractJsonApiParser;
import com.sky.build.KV;
import com.sky.build.chain.PsiTypeParserChain;
import com.sky.build.util.SpringMvcAnnotationUtil;
import com.sky.constant.SpringMVCConstant;
import com.sky.dto.ValueWrapper;
import com.sky.dto.YapiApiDTO;
import com.sky.util.DesUtil;
import com.sky.util.PsiAnnotationSearchUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * @author gangyf
 * @since 2020/12/11 10:33 PM
 */
public class SpringApiParserImpl extends AbstractJsonApiParser {

    @Override
    public void requestPath(PsiModifierList psiModifierList, YapiApiDTO yapiApiDTO) {

        StringBuilder path = new StringBuilder();

        classRequestPath(path, PsiAnnotationSearchUtil
                .findAnnotation(PARSE_CONTEXT_THREAD_LOCAL.get().getPsiClass(), SpringMVCConstant.RequestMapping));

        Arrays.stream(psiModifierList.getAnnotations())
                .filter(annotation -> MAPPING_TO_HTTP_METHOD.containsKey(annotation.getQualifiedName()))
                .findFirst().ifPresent(psiAnnotation -> {

            PsiAnnotationMemberValue attributeValue = null;
            for (PsiNameValuePair attribute : psiAnnotation.getParameterList().getAttributes()) {

                if (attributeValue != null) {
                    break;
                }

                if (Strings.isNullOrEmpty(attribute.getName())) {
                    attributeValue = attribute.getValue();
                    break;
                }

                switch (attribute.getName()) {
                    case HTTP_REQUEST_PATH_PATH:
                    case HTTP_REQUEST_PATH_VALUE:
                        attributeValue = attribute.getValue();
                        break;
                    default:
                        break;
                }

            }

            if (attributeValue instanceof PsiArrayInitializerMemberValueImpl) {
                String multiPath = Arrays
                        .stream(((PsiArrayInitializerMemberValueImpl) attributeValue).getInitializers())
                        .filter(element -> element instanceof PsiLiteralExpressionImpl)
                        .map(element -> (PsiLiteralExpressionImpl) element)
                        .map(PsiLiteralExpressionImpl::getValue)
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        // append each element path to class path
                        .map(elementPath -> path.toString() + elementPath)
                        .collect(Collectors.joining(", "));

                // reset path value, already has class path prefix
                path.delete(0, path.length());
                path.append(multiPath);
            } else if (attributeValue instanceof PsiLiteralExpressionImpl) {
                path.append(((PsiLiteralExpressionImpl) attributeValue).getValue());
            }

            // replace multi(/) to one(/)
            yapiApiDTO.setPath(path.toString().replaceAll("(?<!https?:)/{2,}", "/"));
        });
    }

    @Override
    public void requestMethod(PsiModifierList psiModifierList, YapiApiDTO yapiApiDTO) {

        for (PsiAnnotation psiAnnotation : psiModifierList.getAnnotations()) {

            String method = null;
            switch (Objects.requireNonNull(psiAnnotation.getQualifiedName())) {
                case "GetMapping":
                case SpringMVCConstant.GetMapping:
                case "PostMapping":
                case SpringMVCConstant.PostMapping:
                case "PutMapping":
                case SpringMVCConstant.PutMapping:
                case "PatchMapping":
                case SpringMVCConstant.PatchMapping:
                case "DeleteMapping":
                case SpringMVCConstant.DeleteMapping:
                    method = MAPPING_TO_HTTP_METHOD.get(psiAnnotation.getQualifiedName());
                    yapiApiDTO.setMethod(method);
                    break;
                case "RequestMapping":
                case SpringMVCConstant.RequestMapping:
                    PsiAnnotationMemberValue attributeValue = psiAnnotation
                            .findAttributeValue(HTTP_METHOD_KEY);

                    if (attributeValue instanceof PsiArrayInitializerMemberValueImpl) {
                        String multiMethod = Arrays.stream(((PsiArrayInitializerMemberValueImpl) attributeValue)
                                .getInitializers())
                                .filter(element -> element instanceof PsiReferenceExpressionImpl)
                                .map(element -> (PsiReferenceExpressionImpl) element)
                                .map(PsiReferenceExpressionImpl::getCanonicalText)
                                .map(REQUEST_METHOD::get)
                                .collect(Collectors.joining(", "));

                        yapiApiDTO.setMethod(multiMethod);
                    } else if (attributeValue instanceof PsiReferenceExpressionImpl) {
                        yapiApiDTO.setMethod(REQUEST_METHOD
                                .get(((PsiReferenceExpressionImpl) attributeValue).getCanonicalText()));
                    }
                default:
                    break;
            }

            if (Strings.isNullOrEmpty(method)) {
                return;
            }
        }

    }

    @Override
    public void requestDesc(PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {
        String classDesc = psiMethod.getText()
                .replace(Objects.nonNull(psiMethod.getBody()) ? psiMethod.getBody().getText() : "", "");

        if (!Strings.isNullOrEmpty(classDesc)) {
            classDesc = classDesc.replace("<", "&lt;").replace(">", "&gt;");
        }

        yapiApiDTO.setDesc("<pre><code>  " + classDesc + "</code> </pre>");

    }

    @Override
    public void requestTitle(PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {

        yapiApiDTO.setTitle(DesUtil.getDesc(psiMethod));
    }

    @Override
    public void parseRequest(PsiParameterList parameterList, YapiApiDTO yapiApiDTO) {

        PsiParameter[] psiParameters = parameterList.getParameters();

        if (psiParameters.length == 0) {
            return;
        }

        List<ValueWrapper> yapiHeaderDTOList = new ArrayList<>();
        ArrayList<ValueWrapper> yapiQueryDTOList = new ArrayList<>();
        List<ValueWrapper> yapiPathVariableDTOList = new ArrayList<>();

        PsiTypeParserChain psiTypeParserChain = new PsiTypeParserChain();
        PsiMethod psiMethod = PARSE_CONTEXT_THREAD_LOCAL.get().getPsiMethod();

        Arrays.stream(psiParameters)
                .filter(psiParameter -> !HttpServletRequest.equals(psiParameter.getType().getCanonicalText()))
                .filter(psiParameter -> !HttpServletResponse.equals(psiParameter.getType().getCanonicalText()))
                .forEach(psiParameter -> {

                    psiParameter.acceptChildren(new PsiElementVisitor() {
                        @Override
                        public void visitElement(@NotNull PsiElement element) {
                            super.visitElement(element);

                            if (!(element instanceof PsiModifierList)) {
                                return;
                            }

                            PsiModifierList modifierList = (PsiModifierList) element;

                            PsiAnnotation[] annotations = modifierList.getAnnotations();

                            if (annotations.length == 0) {
                                // 支持实体对象接收
                                if (yapiApiDTO.getReqBodyForm() == null) {
                                    yapiApiDTO.setReqBodyType("form");
                                    yapiApiDTO.setReqBodyForm(new ArrayList<>());
                                }

                                yapiApiDTO.getReqBodyForm().addAll(parseRequestForm(psiParameter.getType()));
                                return;
                            }

                            Arrays.stream(annotations).forEach(annotation -> {
                                String qualifiedName = annotation.getQualifiedName();

                                switch (Objects.requireNonNull(qualifiedName)) {
                                    case "RequestBody":
                                    case SpringMVCConstant.RequestBody:
                                        KV<String, Object> response = psiTypeParserChain.parse(psiParameter.getType());
                                        yapiApiDTO.setRequestBody(response.toPrettyJson());
                                        yapiApiDTO.setReqBodyType("application/json;charset=UTF-8");
                                        break;
                                    case "RequestParam":
                                    case SpringMVCConstant.RequestParam:
                                        ValueWrapper yapiQueryDTO = SpringMvcAnnotationUtil
                                                .parseRequestParam(annotation, psiParameter, psiMethod);
                                        yapiQueryDTOList.add(yapiQueryDTO);

                                    case "PathVariable":
                                    case SpringMVCConstant.PathVariable:
                                        ValueWrapper yapiPathVariableDTO = SpringMvcAnnotationUtil
                                                .parsePathVariable(annotation, psiParameter, psiMethod);
                                        yapiPathVariableDTOList.add(yapiPathVariableDTO);
                                        break;
                                    case "RequestAttribute":
                                    case SpringMVCConstant.RequestAttribute:
                                        break;
                                    case "RequestHeader":
                                    case SpringMVCConstant.RequestHeader:
                                        break;
                                    default:
                                        break;
                                }
                            });

                        }
                    });

                    yapiApiDTO.setParams(yapiQueryDTOList);
                    yapiApiDTO.setHeader(yapiHeaderDTOList);
                    yapiApiDTO.setReqParams(yapiPathVariableDTOList);

                });
    }

    @Override
    public void parseResponse(Project project, PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {
        PsiTypeParserChain psiTypeParserChain = new PsiTypeParserChain();
        KV<String, Object> response = psiTypeParserChain.parse(psiMethod.getReturnType());
        yapiApiDTO.setResponse(response.toPrettyJson());
    }

    private void classRequestPath(StringBuilder path, PsiAnnotation psiAnnotation) {
        if (psiAnnotation == null) {
            return;
        }
        Arrays.stream(psiAnnotation.getParameterList().getAttributes()).forEach(psiNameValuePair -> {
            // like RequestMapping("path")
            if (psiNameValuePair.getLiteralValue() != null) {
                path.append(psiNameValuePair.getLiteralValue());
            } else {
                // use value and other parameters
                PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findAttributeValue("value");
                if (Objects.nonNull(psiAnnotationMemberValue) && Objects
                        .nonNull(psiAnnotationMemberValue.getReference())) {
                    String[] results = Objects.requireNonNull(psiAnnotationMemberValue.getReference().resolve())
                            .getText().split("=");
                    path.append(results[results.length - 1].split(";")[0].replace("\"", "").trim());
                }
            }
        });
        path.append("/");
    }

}
