package com.sky.util;

import static com.sky.util.JsonUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.sky.config.ConfigEntity;
import com.sky.util.JsonUtil.IgnoreURNSchemaFactoryWrapper;
import org.junit.Test;

public class JsonUtilTest {

    @Test
    public void testJsonSchema() throws JsonProcessingException {
        TypeFactory typeFactory = OBJECT_MAPPER.getTypeFactory();
        IgnoreURNSchemaFactoryWrapper visitor = new IgnoreURNSchemaFactoryWrapper();

        OBJECT_MAPPER.acceptJsonFormatVisitor(typeFactory.constructType(new TypeReference<ConfigEntity>() {}), visitor);
        JsonSchema schema = visitor.finalSchema();

        System.out.println(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
    }

    @Test
    public void enumJsonSchema() throws JsonProcessingException {
        TypeFactory typeFactory = OBJECT_MAPPER.getTypeFactory();
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

        IgnoreURNSchemaFactoryWrapper visitor = new IgnoreURNSchemaFactoryWrapper();

        OBJECT_MAPPER.acceptJsonFormatVisitor(typeFactory.constructType(new TypeReference<EnumForTest>() {}), visitor);
        JsonSchema schema = visitor.finalSchema();

        System.out.println(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
    }

}