package com.sky.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.ExceptionUtil;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.testFramework.LightJavaCodeInsightTestCase;
import com.sky.build.NormalTypes;
import com.sky.build.domain.JsonApiParserImpl;
import com.sky.dto.YapiApiDTO;
import com.sky.util.DesUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SneakyThrowsTest extends LightJavaCodeInsightTestCase {

    private Map<String, PsiClass> psiClassMap;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        psiClassMap = Stream
                .of("UserController.txt", "UserResponse.txt", "Response.txt", "UserRequest.txt", "GenderEnum"
                        + ".txt", "MediaType.txt").map(fileName -> {
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

    public void testEnumType() {
        PsiClass psiClass = getPsiClass("EnumTest.java", "package com.sky.api.enums;\n"
                + "\n"
                + "import lombok.AllArgsConstructor;\n"
                + "import lombok.Getter;\n"
                + "import java.lang.Integer;\n"
                + "\n"
                + "/**\n"
                + " * 性别d\n"
                + " * @author gangyf\n"
                + " * @since 2020/11/26 11:51 AM\n"
                + " */\n"
                + "@Getter\n"
                + "@AllArgsConstructor\n"
                + "public enum EnumTest {\n"
                + "\n"
                + "    /**\n"
                + "     * 0, 男\n"
                + "     */\n"
                + "    MALE(0, \"男\"),\n"
                + "\n"
                + "    /**\n"
                + "     * 1, 女\n"
                + "     */\n"
                + "    FEMALE(1, \"女\"),\n"
                + "\n"
                + "    ;\n"
                + "\n"
                + "    private final Integer value;\n"
                + "    private final String desc;\n"
                + "\n"
                + "}\n");

        for (PsiField field : psiClass.getFields()) {
            if (field.getName().equals("value")) {
                assertThat(field.getType().getCanonicalText()).isEqualTo("java.lang.Integer");
                assertThat(NormalTypes.WRAPPER_TO_PRIMITIVE.get(field.getType().getCanonicalText())).isEqualTo("int");
                return;
            }
        }
    }

    public void testRequestPath() {

        JsonApiParserImpl jsonApiParser = new JsonApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        assertThat(jsonApiParser.requestPath(test, yapiApiDTO).getPath()).isEqualTo("/user/test/{id}");

        test = getMethodByName(userController, "test1");

        yapiApiDTO = new YapiApiDTO();
        assertThat(jsonApiParser.requestPath(test, yapiApiDTO).getPath()).isEqualTo("/user/test/1/{id}");

        test = getMethodByName(userController, "test2");

        yapiApiDTO = new YapiApiDTO();
        assertThat(jsonApiParser.requestPath(test, yapiApiDTO).getPath()).isEqualTo("/user/test/2/{id}, "
                + "/user/test/3/{id}");
    }

    public void testRequestPath_multiValue() {

        JsonApiParserImpl jsonApiParser = new JsonApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test2");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        assertThat(jsonApiParser.requestPath(test, yapiApiDTO).getPath())
                .isEqualTo("/user/test/2/{id}, /user/test/3/{id}");
    }

    public void testRequestMethod() {

        JsonApiParserImpl jsonApiParser = new JsonApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        assertThat(jsonApiParser.requestMethod(test, yapiApiDTO).getMethod()).isEqualTo("GET");
    }

    public void testRequestMethod_multi() {

        JsonApiParserImpl jsonApiParser = new JsonApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test2");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        assertThat(jsonApiParser.requestMethod(test, yapiApiDTO).getMethod()).isEqualTo("GET, POST");

        test = getMethodByName(userController, "test4");

        yapiApiDTO = new YapiApiDTO();
        assertThat(jsonApiParser.requestMethod(test, yapiApiDTO).getMethod()).isEqualTo("GET, POST");
    }

    public void testDesc() {
        PsiClass userController = psiClassMap.get("UserController");
        String desc = DesUtil.getDesc(userController);

        assertThat(desc).isEqualTo("UserController");

        PsiClass testClass = getPsiClass("Test.java", "/**"
                + "* test for doc"
                + "*/"
                + "public class Test {}");

        assertThat(DesUtil.getDesc(testClass)).isEqualTo("* test for doc");

        testClass = getPsiClass("Test.java", "/**"
                + "\n* test for doc"
                + "\n*/"
                + "public class Test {}");

        assertThat(DesUtil.getDesc(testClass)).isEqualTo("test for doc");

        testClass = getPsiClass("Test.java", "/**"
                + "\n* test for doc"
                + "\n+1"
                + "\n*/"
                + "public class Test {}");

        assertThat(DesUtil.getDesc(testClass)).isEqualTo("test for doc, +1");

        testClass = getPsiClass("Test.java", "/**"
                + "\n* test for doc"
                + "\n* +1"
                + "\n*/"
                + "public class Test {}");

        assertThat(DesUtil.getDesc(testClass)).isEqualTo("test for doc, +1");

        testClass = getPsiClass("Test.java", "/**"
                + "\n* test for doc"
                + "\n* +1 "
                + "\n*/"
                + "public class Test {}");

        assertThat(DesUtil.getDesc(testClass)).isEqualTo("test for doc, +1");
    }


    /**
     * to avoid catching all exceptions by default by accident
     */
    public void testRegularThrows() {
        PsiMethodCallExpression methodCall = createCall("void foo() {  throwsMyException(); }");
        List<PsiClassType> exceptions = ExceptionUtil.getUnhandledExceptions(methodCall, null);
        assertEquals(1, exceptions.size());
        assertEquals("Test.MyException", exceptions.get(0).getCanonicalText());
    }

    @Override
    protected Sdk getProjectJDK() {
        return JavaSdk.getInstance().createJdk("java 1.8", "lib/mockJDK-1.8", false);
    }

    private PsiMethodCallExpression createCall(@NonNls final String body) {
        final PsiFile file = createTestFile(body);
        PsiMethodCallExpression methodCall = findMethodCall(file);
        assertNotNull(methodCall);
        return methodCall;
    }

    @NotNull
    private PsiFile createTestFile(@NonNls String body) {
        return createFile("test.java", "class Test { " + body +
                "void throwsAnotherException() throws AnotherException {}" +
                "void throwsMyException() throws MyException {}" +
                "void throwsSomeException() throws SomeException {}" +
                "static class MyException extends Exception {}" +
                "static class SomeException extends Exception {}" +
                "static class AnotherException extends Exception {}" +
                "static class Exception{}" +
                "}");
    }

    @Nullable
    private static PsiMethodCallExpression findMethodCall(@NotNull PsiElement element) {
        return findFirstChild(element, PsiMethodCallExpression.class);
    }

    @Nullable
    private static <T extends PsiElement> T findFirstChild(@NotNull PsiElement element, Class<T> aClass) {
        if (aClass.isInstance(element)) {
            return (T) element;
        }

        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            final T call = findFirstChild(child, aClass);
            if (call != null) {
                return call;
            }
        }

        return null;
    }

    /**
     * 通过文件名及内容, 创建PisClass
     *
     * @param fileName the file name
     * @param content the content
     * @return the psi class
     */
    private PsiClass getPsiClass(String fileName, String content) {
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
    private PsiMethod getMethodByName(@NotNull PsiClass psiClass, String methodName) {

        return Arrays.stream(psiClass.getAllMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst().get();
    }
}
