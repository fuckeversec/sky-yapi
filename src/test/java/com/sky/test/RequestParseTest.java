package com.sky.test;

import static com.sky.build.AbstractJsonApiParser.PARSE_CONTEXT_THREAD_LOCAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.sky.AbstractTestCase;
import com.sky.build.domain.SpringApiParserImpl;
import com.sky.build.util.NormalTypes;
import com.sky.dto.YapiApiDTO;
import com.sky.util.DesUtil;
import org.junit.Test;

public class RequestParseTest extends AbstractTestCase {

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

        SpringApiParserImpl jsonApiParser = new SpringApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");
        PARSE_CONTEXT_THREAD_LOCAL.get().setPsiClass(userController);

        PsiMethod test = getMethodByName(userController, "test");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestPath(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getPath()).isEqualTo("/user/test/{id}");

        test = getMethodByName(userController, "test1");

        yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestPath(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getPath()).isEqualTo("/user/test/1/{id}");

        test = getMethodByName(userController, "test2");

        yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestPath(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getPath()).isEqualTo("/user/test/2/{id}, /user/test/3/{id}");
    }

    public void testRequestPath_multiValue() {

        SpringApiParserImpl jsonApiParser = new SpringApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");
        PARSE_CONTEXT_THREAD_LOCAL.get().setPsiClass(userController);

        PsiMethod test = getMethodByName(userController, "test2");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestPath(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getPath()).isEqualTo("/user/test/2/{id}, /user/test/3/{id}");
    }

    public void testRequestMethod() {

        SpringApiParserImpl jsonApiParser = new SpringApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestMethod(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getMethod()).isEqualTo("GET");
    }

    public void testRequestMethodWithMultiAnnotations() {

        SpringApiParserImpl jsonApiParser = new SpringApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test16");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestMethod(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getMethod()).isEqualTo("POST");
    }

    public void testRequestMethod_multi() {

        SpringApiParserImpl jsonApiParser = new SpringApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test2");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestMethod(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getMethod()).isEqualTo("GET, POST");

        test = getMethodByName(userController, "test4");

        yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestMethod(test.getModifierList(), yapiApiDTO);
        assertThat(yapiApiDTO.getMethod()).isEqualTo("GET, POST");
    }

    @Test
    public void testParseRequest() {
        SpringApiParserImpl jsonApiParser = new SpringApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");
        PsiMethod psiMethod = getMethodByName(userController, "test");
        PARSE_CONTEXT_THREAD_LOCAL.get().setPsiMethod(psiMethod);
        PARSE_CONTEXT_THREAD_LOCAL.get().setPsiClass(userController);

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        jsonApiParser.parseRequest(psiMethod.getParameterList(), yapiApiDTO);
    }

    public void testRequestDesc() {
        SpringApiParserImpl jsonApiParser = new SpringApiParserImpl();

        PsiClass userController = psiClassMap.get("UserController");

        PsiMethod test = getMethodByName(userController, "test");

        YapiApiDTO yapiApiDTO = new YapiApiDTO();
        jsonApiParser.requestDesc(test, yapiApiDTO);
        assertThat(yapiApiDTO.getDesc()).isEqualTo("<pre><code>  @RequestMapping(value = \"/test/{id}\", method = "
                + "RequestMethod.GET)\n"
                + "    public Response&lt;UserResponse&gt; test(@PathVariable Long id) </code> </pre>");
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

}
