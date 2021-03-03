package com.sky.util;

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.compiled.ClsEnumConstantImpl;
import com.intellij.psi.impl.source.PsiEnumConstantImpl;
import com.intellij.psi.impl.source.javadoc.PsiDocParamRef;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.sky.build.util.NormalTypes;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 描述工具, 用于获取各种注释
 */
public class DesUtil {

    public final static Pattern SEE_OR_LINK_TAG = Pattern.compile("([a-zA-Z_$][a-zA-Z\\d_$]*)#?"
            + "([a-zA-Z_$][a-zA-Z\\d_$]*)?");
    public static final String PARAM_TAG = "param";

    /**
     * 去除字符串首尾出现的某个字符.
     *
     * @param source 源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    public static String trimFirstAndLastChar(String source, char element) {
        boolean beginIndexFlag;
        boolean endIndexFlag;
        do {
            if (Strings.isNullOrEmpty(source.trim()) || source.equals(String.valueOf(element))) {
                source = "";
                break;
            }
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex =
                    source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }


    /**
     * 获得描述
     *
     * @param psiMethodTarget the psi method target
     * @return the description
     */
    public static String getDescription(PsiMethod psiMethodTarget) {
        if (psiMethodTarget.getDocComment() != null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if (psiDocTag.getText().contains("@description") || psiDocTag.getText().contains("@Description")
                        || psiDocTag.getText().toLowerCase().contains("description")) {
                    return trimFirstAndLastChar(
                            psiDocTag.getText().replace("@description", "").replace("@Description", "")
                                    .replace("Description", "").replace("<br>", "").replace(":", "").replace("*", "")
                                    .replace("\n", " "), ' ');
                }
            }
            return trimFirstAndLastChar(
                    psiMethodTarget.getDocComment().getText().split("@")[0].replace("@description", "")
                            .replace("@Description", "").replace("Description", "").replace("<br>", "").replace(":", "")
                            .replace("*", "").replace("/", "").replace("\n", " "), ' ');
        }
        return null;
    }

    /**
     * 通过paramName 获得描述.
     *
     * @param psiMethodTarget the psi method target
     * @param paramName the param name
     * @return the param desc
     */
    public static String getParamDesc(PsiMethod psiMethodTarget, String paramName) {
        if (psiMethodTarget.getDocComment() != null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if ((psiDocTag.getText().contains("@param") || psiDocTag.getText().contains("@Param")) && (!psiDocTag
                        .getText().contains("[")) && psiDocTag.getText().contains(paramName)) {
                    return trimFirstAndLastChar(
                            psiDocTag.getText().replace("@param", "").replace("@Param", "").replace(paramName, "")
                                    .replace(":", "").replace("*", "").replace("\n", " "), ' ');
                }
            }
        }
        return "";
    }

    /**
     * 获得引用url
     *
     * @param text the text
     * @return the url re ference r desc
     */
    public static String getUrlReFerenceRDesc(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return text;
        }
        if (!text.contains("*/")) {
            return null;
        }
        return DesUtil.trimFirstAndLastChar(
                text.split("\\*/")[0].replace("@description", "").replace("@Description", "").split("@")[0]
                        .replace(":", "").replace("*", "").replace("/", "").replace("\n", " "), ' ');
    }

    /**
     * 获得菜单
     *
     * @param text the text
     * @return the menu
     */
    public static String getMenu(String text) {
        if (Strings.isNullOrEmpty(text) || !text.contains("*/")) {
            return null;
        }
        String[] menuList = text.split("\\*/")[0].split("@menu");
        if (menuList.length > 1) {
            return DesUtil.trimFirstAndLastChar(
                    menuList[1].split("\\*")[0].replace("*", "").replace("/", "").replace("\n", " "), ' ');
        } else {
            return null;
        }
    }

    /**
     * 获得link 备注
     *
     * @param project the project
     * @param field the field
     * @return the link remark
     */
    public static String getLinkOrSeeRemark(Project project, PsiField field) {
        if (Objects.isNull(field.getDocComment())) {
            return getEnumDesc(project, field, field.getType().getCanonicalText());
        }

        String desc = getDesc(field);

        // 尝试获得@link 的常量定义
        String[] strings = parseLinkOrSee(field);

        if (strings[0] != null) {
            String string = getEnumDesc(project, field, strings[0]);
            if (Strings.isNullOrEmpty(desc)) {
                return string;
            } else {
                return desc + "," + string;
            }
        }

        return desc;
    }

    private static String getEnumDesc(Project project, PsiField field, String linkAddress) {
        PsiClass psiClassLink = getPsiClass(project, field, linkAddress);

        if (Objects.nonNull(psiClassLink)) {

            // 不是Enum直接返回类注释
            if (!psiClassLink.isEnum()) {
                return getDesc(psiClassLink);
            }

            return getEnumDesc(psiClassLink);
        }
        return "";
    }

    @NotNull
    public static String getEnumDesc(PsiClass psiClassLink) {
        //说明获得了link 的class
        List<PsiField> linkFields = Arrays.stream(psiClassLink.getFields()).filter(
                psiField -> psiField instanceof PsiEnumConstant).collect(Collectors.toList());

        String result = "[";

        AtomicInteger ordinal = new AtomicInteger();
        result = result + linkFields.stream().map(psiField -> {

            StringBuilder sb = new StringBuilder();
            sb.append(psiField.getName()).append("(");
            PsiExpressionList argumentList;
            if (psiField instanceof ClsEnumConstantImpl) {

                ClsEnumConstantImpl clsEnumConstant = (ClsEnumConstantImpl) psiField;
                argumentList = clsEnumConstant.getArgumentList();
            } else {

                argumentList = ((PsiEnumConstantImpl) psiField).getArgumentList();
            }
            boolean hasArguments = argumentList != null;
            if (hasArguments) {
                PsiExpression[] expressions = argumentList.getExpressions();
                // 先获得名称
                sb.append(Arrays.stream(expressions).map(PsiExpression::getText).collect(Collectors.joining(", ")));
            } else {
                if (psiField.getDocComment() != null
                        && Strings.isNullOrEmpty(psiField.getDocComment().getText())) {
                    // 使用enum的注释, 或者直接使用名字
                    sb.append(psiField.getDocComment().getText());
                } else {
                    // 没有注释, 使用ordinal
                    sb.append(ordinal.get());
                    ordinal.getAndIncrement();
                }
            }
            return sb.append(")").toString();
        }).collect(Collectors.joining(", "));

        result += "]";
        return result;
    }

    /**
     * Class desc string.
     *
     * @param project the project
     * @param psiType the psi type
     * @return the string
     */
    public static String getDesc(Project project, PsiType psiType) {

        if (psiType == null) {
            return "";
        }

        PsiClass psiClass = getPsiClass(project, psiType.getCanonicalText());

        if (psiClass.isEnum()) {
            return getEnumDesc(psiClass);
        }

        return getDesc(psiClass);
    }

    /**
     * Gets desc.
     *
     * @param psiType the psi type
     * @return the desc
     */
    public static Optional<String> getDesc(PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);

        String desc = getDesc(psiClass);
        if (Strings.isNullOrEmpty(desc)) {
            return Optional.empty();
        }
        return Optional.of(desc);
    }

    /**
     * Class desc string.
     *
     * @param psiClass the psi class
     * @return the string
     */
    public static String getDesc(PsiClass psiClass) {

        if (psiClass == null) {
            return "";
        }

        return getDesc(psiClass.getDocComment());
    }

    /**
     * Class desc string.
     *
     * @param psiField the psi class
     * @return the string
     */
    public static String getDesc(PsiField psiField) {
        if (psiField == null) {
            return "";
        }

        String desc = getDesc(psiField.getDocComment());

        if (Strings.isNullOrEmpty(desc)) {
            // parse swagger ApiModelProperty
            PsiAnnotation annotation = PsiAnnotationSearchUtil
                    .findAnnotation(psiField, "io.swagger.annotations.ApiModelProperty");

            if (annotation == null) {
                return desc;
            }

            PsiAnnotationMemberValue memberValue = annotation.findAttributeValue("value");

            if (memberValue != null) {
                return memberValue.getText();
            }

        }
        return desc;
    }

    /**
     * Gets desc.
     *
     * @param psiMethod the psi method
     * @return the desc
     */
    public static String getDesc(PsiMethod psiMethod) {

        PsiDocComment docComment = psiMethod.getDocComment();
        if (docComment == null) {
            return "";
        }

        return getDesc(docComment);

    }

    /**
     * 获得属性注释
     *
     * @param psiDocComment the psi doc comment
     * @return the filed desc
     */
    public static String getDesc(PsiDocComment psiDocComment) {

        if (psiDocComment == null) {
            return "";
        }

        return Arrays.stream(psiDocComment.getDescriptionElements()).map(doc -> doc.getText().trim())
                .filter(doc -> !Strings.isNullOrEmpty(doc)).collect(Collectors.joining(", "));
    }

    @Nullable
    private static PsiClass getPsiClass(Project project, PsiField field, String linkAddress) {
        PsiClass psiClassLink = JavaPsiFacade.getInstance(project).findClass(
                linkAddress, GlobalSearchScope.allScope(project));
        if (Objects.isNull(psiClassLink)) {
            //可能没有获得全路径，尝试获得全路径
            String[] importPaths =
                    field.getParent().getContext() != null ? field.getParent().getContext().getText().split("import")
                            : new String[0];
            if (importPaths.length > 1) {
                for (String importPath : importPaths) {
                    if (importPath.contains(linkAddress.split("\\.")[0])) {
                        linkAddress = importPath.split(linkAddress.split("\\.")[0])[0] + linkAddress;
                        psiClassLink = JavaPsiFacade.getInstance(project)
                                .findClass(linkAddress.trim(), GlobalSearchScope.allScope(
                                        project));
                        break;
                    }
                }
            }
            //如果小于等于一为不存在import，不做处理
        }
        return psiClassLink;
    }

    /**
     * Enum type string.
     *
     * @param project the project
     * @param field the field
     * @return the string
     */
    public static String enumType(Project project, PsiField field) {

        PsiDocComment docComment = field.getDocComment();
        if (docComment == null) {
            return "enum";
        }

        String type = "enum";
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(
                field.getType().getCanonicalText(), GlobalSearchScope.allScope(project));

        if (psiClass == null) {
            return type;
        }

        String[] strings = parseLinkOrSee(field);

        if (strings[1] == null) {
            return type;
        }

        // 方法
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (method.getName().equals(strings[1])) {
                if (method.getReturnType() != null) {
                    type = method.getReturnType().getPresentableText();
                }
                break;
            }
        }

        for (PsiField psiField : psiClass.getFields()) {
            if (!(psiField instanceof PsiEnumConstant) && psiField.getName().equals(strings[1])) {
                type = psiField.getType().getPresentableText();
                break;
            }

        }

        return NormalTypes.javaTypeToJsType(type);
    }

    private static String[] parseLinkOrSee(PsiField psiField) {
        String[] result = new String[2];
        Optional<PsiDocTag> psiDocTag = linkOrSeeTag(psiField);

        if (!psiDocTag.isPresent()) {
            return result;
        }

        PsiDocTagValue tagValue = psiDocTag.get().getValueElement();

        if (tagValue == null) {
            return result;
        }

        Matcher matcher = SEE_OR_LINK_TAG.matcher(tagValue.getText());

        if (!matcher.find()) {
            return result;
        }

        for (int i = 0; i < matcher.groupCount(); i++) {
            result[i] = matcher.group(i + 1);
        }

        return result;
    }

    /**
     * Link or see tag optional.
     *
     * @param psiField the psi field
     * @return the optional
     */
    public static Optional<PsiDocTag> linkOrSeeTag(PsiField psiField) {
        PsiDocComment docComment = psiField.getDocComment();
        if (docComment == null) {
            return Optional.empty();
        }
        return Arrays.stream(docComment.getTags())
                .filter(doc -> doc.getName().equals("see") || doc.getName().equals("link")).findFirst();
    }

    /**
     * 通过包名前缀获取PsiClass
     *
     * @param project the project
     * @param className the class name
     * @return the psi class
     */
    private static PsiClass getPsiClass(Project project, String className) {
        return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project));
    }

    /**
     * 获取方法参数的描述信息
     * 1. 方法没有注释, 返回PsiType的PresentableText做为描述
     * 2. 存在注释, 使用param信息作为描述
     *
     * @param psiMethod the psi method
     * @param psiParameter the psi parameter
     * @return the string
     */
    public static String paramDesc(PsiMethod psiMethod, PsiParameter psiParameter) {

        if (psiMethod.getDocComment() == null) {
            return NormalTypes.javaTypeToJsType(psiParameter.getType().getPresentableText());
        }

        String paramName = psiParameter.getName();

        StringBuilder desc = new StringBuilder();
        Arrays.stream(psiMethod.getDocComment().getTags())
                .filter(tag -> tag.getName().equals(PARAM_TAG))
                .filter(tag -> Objects.nonNull(tag.getValueElement()))
                .filter(tag -> tag.getValueElement().getText().equals(paramName))
                .findFirst()
                .ifPresent(tag -> desc.append(Arrays.stream(tag.getDataElements())
                        // first element is refer to param name, ignore
                        .filter(element -> !(element instanceof PsiDocParamRef))
                        .map(PsiElement::getText)
                        .filter(s -> !Strings.isNullOrEmpty(s.trim()))
                        .collect(Collectors.joining(", "))));

        return desc.toString();
    }
}
