package com.sky.build.domain;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.sky.build.JsonApiParser;
import com.sky.constant.SpringMVCConstant;
import com.sky.dto.YapiApiDTO;
import com.sky.util.PsiAnnotationSearchUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gangyf
 * @since 2020/12/11 10:33 PM
 */
public class JsonApiParserImpl implements JsonApiParser {

    public static final String HTTP_METHOD_KEY = "method";
    private final static Map<String, String> SPECIFY_MAPPING;
    private final static List<String> MAPPING_TO_HTTP_METHOD;
    public static final String HTTP_REQUEST_PATH_KEY = "value";

    public static final Map<String, String> REQUEST_METHOD;

    static {
        SPECIFY_MAPPING = new HashMap<>();
        MAPPING_TO_HTTP_METHOD = new ArrayList<>();

        MAPPING_TO_HTTP_METHOD.add(SpringMVCConstant.GetMapping);
        MAPPING_TO_HTTP_METHOD.add(SpringMVCConstant.PostMapping);
        MAPPING_TO_HTTP_METHOD.add(SpringMVCConstant.PutMapping);
        MAPPING_TO_HTTP_METHOD.add(SpringMVCConstant.PatchMapping);
        MAPPING_TO_HTTP_METHOD.add(SpringMVCConstant.DeleteMapping);
        MAPPING_TO_HTTP_METHOD.add(SpringMVCConstant.RequestMapping);

        SPECIFY_MAPPING.put(SpringMVCConstant.GetMapping, "GET");
        SPECIFY_MAPPING.put(SpringMVCConstant.PostMapping, "POST");
        SPECIFY_MAPPING.put(SpringMVCConstant.PutMapping, "PUT");
        SPECIFY_MAPPING.put(SpringMVCConstant.DeleteMapping, "DELETE");
        SPECIFY_MAPPING.put(SpringMVCConstant.PatchMapping, "PATCH");

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
    public YapiApiDTO requestPath(PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {

        StringBuilder path = new StringBuilder();

        PsiAnnotation psiAnnotation = PsiAnnotationSearchUtil
                .findAnnotation(Objects.requireNonNull(psiMethod.getContainingClass()),
                        SpringMVCConstant.RequestMapping);

        classRequestPath(path, psiAnnotation);

        MAPPING_TO_HTTP_METHOD.stream()
                .map(mapping -> PsiAnnotationSearchUtil.findAnnotation(psiMethod, mapping))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(annotation -> {

                    PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(HTTP_REQUEST_PATH_KEY);

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
                });

        // replace multi(/) to one(/)
        yapiApiDTO.setPath(path.toString().replaceAll("(?<!https?:)/{2,}", "/"));

        return yapiApiDTO;
    }

    @Override
    public YapiApiDTO requestMethod(PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {
        Optional<PsiAnnotation> specifyMapping = SPECIFY_MAPPING.keySet().stream()
                .map(mapping -> PsiAnnotationSearchUtil.findAnnotation(psiMethod, mapping))
                .filter(Objects::nonNull)
                .findFirst();

        // use GetMapping or PostMapping...
        if (specifyMapping.isPresent()) {
            yapiApiDTO.setMethod(specifyMapping.get().getQualifiedName());
            return yapiApiDTO;
        }

        PsiAnnotation annotation = PsiAnnotationSearchUtil
                .findAnnotation(psiMethod, SpringMVCConstant.RequestMapping);

        if (annotation != null) {
            PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(HTTP_METHOD_KEY);

            if (attributeValue instanceof PsiArrayInitializerMemberValueImpl) {
                String multiMethod =
                        Arrays.stream(((PsiArrayInitializerMemberValueImpl) attributeValue).getInitializers())
                                .filter(element -> element instanceof PsiReferenceExpressionImpl)
                                .map(element -> (PsiReferenceExpressionImpl) element)
                                .map(PsiReferenceExpressionImpl::getCanonicalText)
                                .map(REQUEST_METHOD::get)
                                .collect(Collectors.joining(", "));

                yapiApiDTO.setMethod(multiMethod);
            } else if (attributeValue instanceof PsiReferenceExpressionImpl) {
                yapiApiDTO.setMethod(
                        REQUEST_METHOD.get(((PsiReferenceExpressionImpl) attributeValue).getCanonicalText()));
            }
        }

        if (yapiApiDTO.getMethod() == null) {
            yapiApiDTO.setMethod("");
        }

        return yapiApiDTO;
    }

    @Override
    public YapiApiDTO requestDesc(PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {
        return null;
    }

    private void classRequestPath(StringBuilder path, PsiAnnotation psiAnnotation) {
        if (psiAnnotation != null) {
            PsiNameValuePair[] psiNameValuePairs = psiAnnotation.getParameterList().getAttributes();
            Arrays.stream(psiNameValuePairs).forEach(psiNameValuePair -> {
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

    @Override
    public YapiApiDTO parseRequest(PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {
        return null;
    }

    @Override
    public YapiApiDTO parseResponse(PsiMethod psiMethod, YapiApiDTO yapiApiDTO) {
        return null;
    }
}
