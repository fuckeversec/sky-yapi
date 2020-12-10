package com.sky.util;

import com.google.common.base.Strings;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiEnumConstantImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

/**
 * 描述工具, 用于获取各种注释
 */
public class DesUtil {

    public final static Pattern LINK_CLASS = Pattern.compile("(?<=\\{@link ).*?([a-zA-Z_$][a-zA-Z\\d_$]*)"
            + "(#[a-zA-Z_$][a-zA-Z\\d_$]*)?");
    public final static Pattern SEE_CLASS = Pattern.compile("(?<=@see ).*?([a-zA-Z_$][a-zA-Z\\d_$]*)"
            + "(#[a-zA-Z_$][a-zA-Z\\d_$]*)?");
    private final static List<Function<String, String>> CLASS_EXTRACT_FUNCTIONS;
    private final static List<Function<String, String>> FIELD_EXTRACT_FUNCTIONS;

    static {
        Function<String, String> seeClassExtract = (String text) -> {
            Matcher matcher = SEE_CLASS.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return null;
        };

        Function<String, String> linkClassExtract = (String text) -> {
            Matcher matcher = LINK_CLASS.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return null;
        };

        CLASS_EXTRACT_FUNCTIONS = Arrays.asList(seeClassExtract, linkClassExtract);

        FIELD_EXTRACT_FUNCTIONS = Arrays.asList(
                (String text) -> {
                    Matcher matcher = SEE_CLASS.matcher(text);
                    if (matcher.find()) {
                        return matcher.group(2);
                    }
                    return null;
                },
                (String text) -> {
                    Matcher matcher = SEE_CLASS.matcher(text);
                    if (matcher.find()) {
                        return matcher.group(2);
                    }
                    return null;
                });

    }


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
     * 获得属性注释
     *
     * @param psiDocComment the psi doc comment
     * @return the filed desc
     */
    public static String getFiledDesc(PsiDocComment psiDocComment) {
        if (Objects.nonNull(psiDocComment)) {
            String fileText = psiDocComment.getText();
            if (!Strings.isNullOrEmpty(fileText)) {
                return trimFirstAndLastChar(
                        fileText.replace("*", "").replace("/", "").replace(" ", "").replace("\n", ",")
                                .replace("\t", ""), ',');
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
     * @param remark the remark
     * @param project the project
     * @param field the field
     * @return the link remark
     */
    public static String getLinkOrSeeRemark(String remark, Project project, PsiField field) {
        // 尝试获得@link 的常量定义
        if (Objects.isNull(field.getDocComment())) {
            return remark;
        }

        String docText = field.getDocComment().getText();
        Optional<String> classNameOpt = CLASS_EXTRACT_FUNCTIONS.stream().map(function -> function.apply(docText))
                .filter(Objects::nonNull)
                .findFirst();

        if (classNameOpt.isPresent()) {
            String string = getString(project, field, classNameOpt.get());
            return remark.replaceAll("(\\{@link.*?})|(@see.*?" + classNameOpt.get() + "#?.*)?", "") + string;
        }

        return remark;
    }

    private static String getString(Project project, PsiField field, String linkAddress) {
        String result = "";
        PsiClass psiClassLink = getPsiClass(project, field, linkAddress);

        if (Objects.nonNull(psiClassLink)) {
            //说明获得了link 的class
            List<PsiField> linkFields = Arrays.stream(psiClassLink.getFields()).filter(
                    psiField -> psiField instanceof PsiEnumConstant).collect(Collectors.toList());

            result += psiClassLink.getName() + "[";

            AtomicInteger ordinal = new AtomicInteger();
            result = result + linkFields.stream().map(psiField -> {

                StringBuilder sb = new StringBuilder();
                sb.append(psiField.getName()).append("(");
                PsiExpressionList argumentList = ((PsiEnumConstantImpl) psiField).getArgumentList();
                boolean hasArguments = argumentList != null;
                if (hasArguments) {
                    PsiExpression[] expressions = Objects
                            .requireNonNull(((PsiEnumConstantImpl) psiField).getArgumentList())
                            .getExpressions();
                    // 先获得名称
                    sb.append(Arrays.stream(expressions).map(PsiExpression::getText).collect(Collectors.joining(", ")));
                } else {
                    if (psiField.getDocComment() != null
                            || Strings.isNullOrEmpty(psiField.getDocComment().getText())) {
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
        }
        return result;
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
     * Class desc string.
     *
     * @param psiClass the psi class
     * @return the string
     */
    public static String classDesc(PsiClass psiClass) {
        PsiDocComment docComment = psiClass.getDocComment();
        if (docComment == null) {
            return "";
        }

        return Arrays.stream(docComment.getDescriptionElements()).map(doc -> doc.getText().trim())
                .filter(doc -> !Strings.isNullOrEmpty(doc)).collect(Collectors.joining(", "));
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

        Optional<String> enumField = FIELD_EXTRACT_FUNCTIONS.stream().map(function -> function.apply(docComment.getText()))
                .filter(Objects::nonNull).findFirst();

        if (!enumField.isPresent()) {
            return type;
        }

        for (PsiField psiField : psiClass.getFields()) {
            if (!(psiField instanceof PsiEnumConstant) && psiField.getName().equals(enumField.get().substring(1))){
                return psiField.getType().getCanonicalText();
            }

        }

        return type;
    }
}
