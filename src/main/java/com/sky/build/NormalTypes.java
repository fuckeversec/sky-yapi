package com.sky.build;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NonNls;

/**
 * 基本类
 *
 * @author gangyf
 * @since 2019/1/30 9:58 AM
 */
public class NormalTypes {

    public static final Map<String, Object> COLLECT_TYPES = new HashMap<>();

    @NonNls
    public static final Map<String, Object> NORMAL_TYPES = new HashMap<>();

    public static final Map<String, Object> NORMAL_TYPES_PACKAGES = new HashMap<>();

    public static final Map<String, Object> COLLECT_TYPES_PACKAGES = new HashMap<>();

    /**
     * 泛型列表
     */
    public static final List<String> GENERIC_LIST = new ArrayList<>();

    static {
        NORMAL_TYPES.put("int", 1);
        NORMAL_TYPES.put("boolean", false);
        NORMAL_TYPES.put("byte", 1);
        NORMAL_TYPES.put("short", 1);
        NORMAL_TYPES.put("long", 1L);
        NORMAL_TYPES.put("float", 1.0F);
        NORMAL_TYPES.put("double", 1.0D);
        NORMAL_TYPES.put("char", 'a');
        NORMAL_TYPES.put("Boolean", false);
        NORMAL_TYPES.put("Byte", 0);
        NORMAL_TYPES.put("Short", (short) 0);
        NORMAL_TYPES.put("Integer", 0);
        NORMAL_TYPES.put("Long", 0L);
        NORMAL_TYPES.put("Float", 0.0F);
        NORMAL_TYPES.put("Double", 0.0D);
        NORMAL_TYPES.put("String", "String");
        NORMAL_TYPES.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("BigDecimal", 0.111111);
        NORMAL_TYPES.put("Timestamp", new Timestamp(System.currentTimeMillis()));
        COLLECT_TYPES.put("HashMap", "HashMap");
        COLLECT_TYPES.put("Map", "Map");
        COLLECT_TYPES.put("LinkedHashMap", "LinkedHashMap");
        GENERIC_LIST.add("T");
        GENERIC_LIST.add("E");
        GENERIC_LIST.add("A");
        GENERIC_LIST.add("B");
        GENERIC_LIST.add("K");
        GENERIC_LIST.add("V");
    }

    static {
        NORMAL_TYPES_PACKAGES.put("int", 1);
        NORMAL_TYPES_PACKAGES.put("boolean", true);
        NORMAL_TYPES_PACKAGES.put("byte", 1);
        NORMAL_TYPES_PACKAGES.put("short", 1);
        NORMAL_TYPES_PACKAGES.put("long", 1L);
        NORMAL_TYPES_PACKAGES.put("float", 1.0F);
        NORMAL_TYPES_PACKAGES.put("double", 1.0D);
        NORMAL_TYPES_PACKAGES.put("char", 'a');
        NORMAL_TYPES_PACKAGES.put("java.lang.Boolean", false);
        NORMAL_TYPES_PACKAGES.put("java.lang.Byte", 0);
        NORMAL_TYPES_PACKAGES.put("java.lang.Short", (short) 0);
        NORMAL_TYPES_PACKAGES.put("java.lang.Integer", 1);
        NORMAL_TYPES_PACKAGES.put("java.lang.Long", 1L);
        NORMAL_TYPES_PACKAGES.put("java.lang.Float", 1L);
        NORMAL_TYPES_PACKAGES.put("java.lang.Double", 1.0D);
        NORMAL_TYPES_PACKAGES.put("java.sql.Timestamp", new Timestamp(System.currentTimeMillis()));
        NORMAL_TYPES_PACKAGES.put("java.util.Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES_PACKAGES.put("java.lang.String", "String");
        NORMAL_TYPES_PACKAGES.put("java.math.BigDecimal", 1);

        COLLECT_TYPES_PACKAGES.put("java.util.LinkedHashMap", "LinkedHashMap");
        COLLECT_TYPES_PACKAGES.put("java.util.HashMap", "HashMap");
        COLLECT_TYPES_PACKAGES.put("java.util.Map", "Map");
    }


    public static boolean isNormalType(String typeName) {
        return NORMAL_TYPES.containsKey(typeName);
    }
}
