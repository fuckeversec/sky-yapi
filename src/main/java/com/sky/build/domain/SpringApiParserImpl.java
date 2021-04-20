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
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.sky.build.AbstractJsonApiParser;
import com.sky.build.KV;
import com.sky.build.chain.PsiTypeParserChain;
import com.sky.build.util.ExampleValueUtil;
import com.sky.build.util.NormalTypes;
import com.sky.build.util.SpringMvcAnnotationUtil;
import com.sky.constant.SpringMVCConstant;
import com.sky.dto.ValueWrapper;
import com.sky.dto.YapiApiDTO;
import com.sky.util.DesUtil;
import com.sky.util.PsiAnnotationSearchUtil;
import com.sky.util.SpringPsiUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * @author gangyf
 * @since 2020/12/11 10:33 PM
 */
public class SpringApiParserImpl extends AbstractJsonApiParser {

    @Override
    public void requestPath(PsiModifierList psiModifierList, YapiApiDTO yapiApiDTO) {

        String urlPathForClass = classRequestPath(PsiAnnotationSearchUtil
                .findAnnotation(PARSE_CONTEXT_THREAD_LOCAL.get().getPsiClass(), SpringMVCConstant.RequestMapping));

        Arrays.stream(psiModifierList.getAnnotations())
                .filter(annotation -> ALL_MAPPING.contains(annotation.getQualifiedName()))
                .findFirst().ifPresent(psiAnnotation -> {

            String mayBeMultiPath = Arrays
                    .stream(SpringPsiUtil.extractPath(SpringPsiUtil.findPathMemberValue(psiAnnotation)).split(","))
                    .map(subPath -> urlPathForClass + subPath).collect(Collectors.joining(", "));

            // replace multi(/) to one(/)
            yapiApiDTO.setPath(mayBeMultiPath.replaceAll("(?<!https?:)/{2,}", "/"));
        });
    }

    @Override
    public void requestMethod(PsiModifierList psiModifierList, YapiApiDTO yapiApiDTO) {

        for (PsiAnnotation psiAnnotation : psiModifierList.getAnnotations()) {

            String method;
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
                    return;
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
                    return;
                default:
                    break;
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

                            if (parseFormData(annotations, yapiApiDTO, psiParameter)) {
                                return;
                            }

                            AtomicReference<ValueWrapper> valueWrapper = new AtomicReference<>(new ValueWrapper());
                            valueWrapper.get().setExample(ExampleValueUtil.psiTypeToExample(psiParameter.getType()));

                            valueWrapper.get().setDesc(DesUtil.paramDesc(psiMethod, psiParameter));

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
                                        SpringMvcAnnotationUtil.parseRequestParam(annotation, psiParameter,
                                                valueWrapper.get());
                                        yapiQueryDTOList.add(valueWrapper.get());
                                        break;
                                    case "PathVariable":
                                    case SpringMVCConstant.PathVariable:
                                        SpringMvcAnnotationUtil.parseRequestParam(annotation, psiParameter,
                                                valueWrapper.get());
                                        yapiPathVariableDTOList.add(valueWrapper.get());
                                        break;
                                    case "RequestAttribute":
                                    case SpringMVCConstant.RequestAttribute:
                                        break;
                                    case "RequestHeader":
                                    case SpringMVCConstant.RequestHeader:
                                        break;
                                    default:
                                        SpringMvcAnnotationUtil.anotherAnnotationParse(annotation, psiParameter,
                                                valueWrapper.get());
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

    private String classRequestPath(PsiAnnotation psiAnnotation) {
        if (psiAnnotation == null) {
            return "";
        }

        return "/" + SpringPsiUtil.extractPath(SpringPsiUtil.findPathMemberValue(psiAnnotation));
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
            response.set("type", "text");
            result.add(response);
        } else {
            @SuppressWarnings("unchecked")
            KV<String, KV<String, Object>> properties =
                    (KV<String, KV<String, Object>>) response.get("properties");
            properties.values().forEach(kv -> kv.set("type", "text"));
            result.addAll(properties.values());
        }
        return result;
    }

    /**
     * 解析form-data类型请求参数, 此类型不能嵌套复杂对象
     *
     * @param annotations the annotations
     * @param yapiApiDTO the yapi api dto
     * @param psiParameter the psi parameter
     * @return the boolean
     */
    private boolean parseFormData(PsiAnnotation[] annotations, YapiApiDTO yapiApiDTO, PsiParameter psiParameter) {
        if (annotations.length == 0) {
            // 支持实体对象接收
            if (yapiApiDTO.getReqBodyForm() == null) {
                yapiApiDTO.setReqBodyType("form");
                yapiApiDTO.setReqBodyForm(new ArrayList<>());
            }

            yapiApiDTO.getReqBodyForm().addAll(parseRequestForm(psiParameter.getType()));
            return true;
        }
        return false;
    }
}
