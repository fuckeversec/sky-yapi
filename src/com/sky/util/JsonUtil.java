package com.sky.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gangyf
 * @since 2020/11/30 10:01 PM
 */
public class JsonUtil {

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);


    /**
     * 通过PsiType获取到类对应的json schema
     *
     * @return json schema
     * @throws JsonProcessingException
     */
    public static String toJsonSchema(Project project, PsiType psiType)
            throws JsonProcessingException, ClassNotFoundException {
        JavaType javaType;

        TypeFactory typeFactory = OBJECT_MAPPER.getTypeFactory();

        IgnoreURNSchemaFactoryWrapper visitor = new IgnoreURNSchemaFactoryWrapper();

        List<String> types = Arrays.stream(psiType.getCanonicalText().split("<")).map(type -> type.replace(">", ""))
                .collect(Collectors.toList());

        if (types.size() > 1) {

            List<String> classFileLocations = types.stream().map(className -> JavaPsiFacade.getInstance(project)
                    .findClass(className, GlobalSearchScope.allScope(project)))
                    .map(psiClass -> {
                        String path = Objects.requireNonNull(JavaPsiFacade.getInstance(project).findClass(types.get(0),
                                GlobalSearchScope.allScope(project)))
                                .getContainingFile().getVirtualFile().getPath();
                        return path;
                    }).collect(Collectors.toList());

            System.out.println(classFileLocations);
            // javaType = typeFactory.constructParametricType(classes.get(0),
            //         classes.subList(1, classes.size()).toArray(new Class<?>[0]));
            //
            // OBJECT_MAPPER.acceptJsonFormatVisitor(javaType, visitor);
        } else {
            OBJECT_MAPPER.acceptJsonFormatVisitor(Class.forName(types.get(0)), visitor);
        }

        JsonSchema schema = visitor.finalSchema();

        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
    }

    public static String writeValueAsString(Object obj, boolean pretty) {
        try {
            if (pretty) {
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return OBJECT_MAPPER.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    /**
     * 忽略id中的urn字段
     */
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    protected static class IgnoreURNSchemaFactoryWrapper extends SchemaFactoryWrapper {

        public IgnoreURNSchemaFactoryWrapper() {
            this(null, new WrapperFactory());
        }

        public IgnoreURNSchemaFactoryWrapper(SerializerProvider p) {
            this(p, new WrapperFactory());
        }

        protected IgnoreURNSchemaFactoryWrapper(WrapperFactory wrapperFactory) {
            this(null, wrapperFactory);
        }

        public IgnoreURNSchemaFactoryWrapper(SerializerProvider p, WrapperFactory wrapperFactory) {
            super(p, wrapperFactory);
            visitorContext = new VisitorContext() {
                @Override
                public String javaTypeToUrn(JavaType jt) {
                    return jt.toCanonical();
                }
            };
        }
    }

}
