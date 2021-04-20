package com.sky;

import static com.sky.build.AbstractJsonApiParser.PARSE_CONTEXT_THREAD_LOCAL;

import com.intellij.mock.MockProjectEx;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.testFramework.LightJavaCodeInsightTestCase;
import com.sky.build.ParseContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTestCase extends LightJavaCodeInsightTestCase {

    protected Map<String, PsiClass> psiClassMap;


    @Override
    public void setUp() throws Exception {
        Disposable testRootDisposable = getTestRootDisposable();
        Project project = new MockProjectEx(testRootDisposable);
        PARSE_CONTEXT_THREAD_LOCAL.set(ParseContext.builder().project(project).build());

        super.setUp();
        psiClassMap = Stream
                .of("UserController.java", "UserControllerForVariable.java", "UserResponse.java", "Response.java",
                        "UserRequest.java", "GenderEnum.java", "MediaType.java").map(fileName -> {
                    InputStream resourceAsStream = this.getClass().getResourceAsStream("/" + fileName);
                    try {
                        String content = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
                        return getPsiClass(fileName, content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(psiClass -> Objects.requireNonNull(psiClass.getName()).replace(".java", ""),
                        Function.identity()));
    }

    @Override
    protected Sdk getProjectJDK() {
        return JavaSdk.getInstance().createJdk("java 1.8", "lib/mockJDK-1.8", false);
    }


    /**
     * 通过文件名及内容, 创建PisClass
     *
     * @param fileName the file name
     * @param content the content
     * @return the psi class
     */
    protected PsiClass getPsiClass(String fileName, String content) {
        PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) createFile(fileName.replace("txt", "java"), content);
        return psiJavaFile.getClasses()[0];
    }

    /**
     * Gets method by name.
     *
     * @param psiClass the psi class
     * @param methodName the method name
     * @return the method by name
     */
    protected PsiMethod getMethodByName(@NotNull PsiClass psiClass, String methodName) {

        return Arrays.stream(psiClass.getAllMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst().get();
    }

    /**
     * Gets field by name.
     *
     * @param psiClass the psi class
     * @param fieldName the field name
     * @return the field by name
     */
    protected Optional<PsiField> getFieldByName(@NotNull PsiClass psiClass, @NotNull String fieldName) {
        return Arrays.stream(psiClass.getAllFields())
                .filter(field -> field.getName().equals(fieldName))
                .findFirst();
    }

}
