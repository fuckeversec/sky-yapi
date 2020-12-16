package com.sky.build;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NonNls;

/**
 * 基本类
 *
 * @author gangyf
 * @since 2019/1/30 9:58 AM
 */
public class NormalTypes {

    public final static List<String> collectionClasses = new ArrayList<>();

    static {
        collectionClasses.add("Iterable");
        collectionClasses.add("java.lang.Iterable");
        collectionClasses.add("Collection");
        collectionClasses.add("java.util.Collection");
        collectionClasses.add("List");
        collectionClasses.add("ArrayList");
        collectionClasses.add("java.util.ArrayList");
        collectionClasses.add("java.util.LinkedList");
        collectionClasses.add("Set");
        collectionClasses.add("HashSet");
        collectionClasses.add("java.util.TreeSet");
        collectionClasses.add("java.util.SortedSet");
        collectionClasses.add("java.util.Queue");
        collectionClasses.add("java.util.Deque");
        collectionClasses.add("ArrayBlockingQueue");
        collectionClasses.add("java.util.Stack");
        collectionClasses.add("com.sun.jmx.remote.internal.ArrayQueue");
    }

    @NonNls
    public final static Map<String, String> NORMAL_TYPES = new HashMap<>();

    public final static Map<String, Object> COLLECT_TYPES = new HashMap<>();

    public final static Map<String, PsiType> PRIMITIVE_TYPES = new HashMap<>();

    public final static Map<String, String> WRAPPER_TO_PRIMITIVE = new HashMap<>();

    public final static Map<String, Object> NORMAL_TYPES_PACKAGES = new HashMap<>();

    public final static Map<String, Object> COLLECT_TYPES_PACKAGES = new HashMap<>();


    /**
     * 泛型列表
     */
    public static final List<String> GENERIC_LIST = new ArrayList<>();

    static {
        GENERIC_LIST.add("T");
        GENERIC_LIST.add("E");
        GENERIC_LIST.add("A");
        GENERIC_LIST.add("B");
        GENERIC_LIST.add("K");
        GENERIC_LIST.add("V");
    }

    static {
        NORMAL_TYPES.put("boolean", "false");
        NORMAL_TYPES.put("Boolean", "false");
        NORMAL_TYPES.put("void", "null");
        NORMAL_TYPES.put("Void", "null");
        NORMAL_TYPES.put("char", "'a'");
        NORMAL_TYPES.put("Character", "'a'");
        NORMAL_TYPES.put("null", "null");
        NORMAL_TYPES.put("byte", "1");
        NORMAL_TYPES.put("Byte", "0");
        NORMAL_TYPES.put("short", "1");
        NORMAL_TYPES.put("Short", "0");
        NORMAL_TYPES.put("int", "1");
        NORMAL_TYPES.put("Integer", "0");
        NORMAL_TYPES.put("long", "1");
        NORMAL_TYPES.put("Long", "0");
        NORMAL_TYPES.put("float", "0.0F");
        NORMAL_TYPES.put("Float", "0.0F");
        NORMAL_TYPES.put("double", "0.0");
        NORMAL_TYPES.put("Double", "0.0");
        NORMAL_TYPES.put("String", "");
        NORMAL_TYPES.put("BigDecimal", "0.0");
        NORMAL_TYPES.put("class", "null");
        NORMAL_TYPES.put("Class", "null");
        NORMAL_TYPES.put("java.lang.Boolean", "false");
        NORMAL_TYPES.put("java.lang.Object", "{}");
        NORMAL_TYPES.put("java.lang.Character", "'a'");
        NORMAL_TYPES.put("java.lang.Void", "null");
        NORMAL_TYPES.put("java.lang.Byte", "0");
        NORMAL_TYPES.put("java.lang.Short", "0");
        NORMAL_TYPES.put("java.lang.Integer", "0");
        NORMAL_TYPES.put("java.lang.Long", "0L");
        NORMAL_TYPES.put("java.lang.Float", "0.0F");
        NORMAL_TYPES.put("java.lang.Double", "0.0");
        NORMAL_TYPES.put("java.lang.String", "");
        NORMAL_TYPES.put("java.math.BigDecimal", "0.0");
        NORMAL_TYPES.put("java.lang.Class", "null");
        NORMAL_TYPES.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());


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
        NORMAL_TYPES_PACKAGES.put("java.time.LocalDate", "2020-10-11");
        NORMAL_TYPES_PACKAGES.put("java.time.LocalTime", "12:42:59");
        NORMAL_TYPES_PACKAGES.put("java.time.LocalDateTime", "2020-10-11 12:42:59");
        NORMAL_TYPES_PACKAGES.put("java.sql.Timestamp", new Timestamp(System.currentTimeMillis()));
        NORMAL_TYPES_PACKAGES.put("java.util.Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        NORMAL_TYPES_PACKAGES.put("java.lang.String", "String");
        NORMAL_TYPES_PACKAGES.put("java.math.BigDecimal", 1);

    }

    static {
        COLLECT_TYPES.put("HashMap", "HashMap");
        COLLECT_TYPES.put("Map", "Map");
        COLLECT_TYPES.put("LinkedHashMap", "LinkedHashMap");
        COLLECT_TYPES_PACKAGES.put("java.util.LinkedHashMap", "LinkedHashMap");
        COLLECT_TYPES_PACKAGES.put("java.util.HashMap", "HashMap");
        COLLECT_TYPES_PACKAGES.put("java.util.Map", "Map");
    }

    static {
        WRAPPER_TO_PRIMITIVE.put("java.lang.Boolean", "boolean");
        WRAPPER_TO_PRIMITIVE.put("java.lang.Byte", "int");
        WRAPPER_TO_PRIMITIVE.put("java.lang.Short", "int");
        WRAPPER_TO_PRIMITIVE.put("java.lang.Integer", "int");
        WRAPPER_TO_PRIMITIVE.put("java.lang.Long", "int");
        WRAPPER_TO_PRIMITIVE.put("java.lang.Float", "float");
        WRAPPER_TO_PRIMITIVE.put("java.lang.Double", "double");
        WRAPPER_TO_PRIMITIVE.put("java.lang.String", "string");
        WRAPPER_TO_PRIMITIVE.put("java.math.BigDecimal", "double");
    }

    static {
        PRIMITIVE_TYPES.put(PsiType.INT.getCanonicalText(), PsiType.INT);
        PRIMITIVE_TYPES.put(PsiType.VOID.getCanonicalText(), PsiType.VOID);
        PRIMITIVE_TYPES.put(PsiType.BYTE.getCanonicalText(), PsiType.BYTE);
        PRIMITIVE_TYPES.put(PsiType.CHAR.getCanonicalText(), PsiType.CHAR);
        PRIMITIVE_TYPES.put(PsiType.LONG.getCanonicalText(), PsiType.LONG);
        PRIMITIVE_TYPES.put(PsiType.FLOAT.getCanonicalText(), PsiType.FLOAT);
        PRIMITIVE_TYPES.put(PsiType.SHORT.getCanonicalText(), PsiType.SHORT);
        PRIMITIVE_TYPES.put(PsiType.DOUBLE.getCanonicalText(), PsiType.DOUBLE);
        PRIMITIVE_TYPES.put(PsiType.BOOLEAN.getCanonicalText(), PsiType.BOOLEAN);
    }


    /**
     * Is normal type boolean.
     *
     * @param typeName the type name
     * @return the boolean
     */
    public static boolean isNormalType(String typeName) {
        return NORMAL_TYPES.containsKey(typeName);
    }

    public static boolean isNormalType(PsiType psiType) {
        return NORMAL_TYPES.containsKey(psiType.getPresentableText());
    }

    /**
     * Is primitive boolean.
     *
     * @param typeName the type name
     * @return the boolean
     */
    public static boolean isPrimitive(String typeName) {
        return PRIMITIVE_TYPES.containsKey(typeName);
    }

    /**
     * Is generic type boolean.
     *
     * @param psiType the psi type
     * @return the boolean
     */
    public static boolean isGenericType(PsiType psiType) {
        return GENERIC_LIST.contains(psiType.getPresentableText());
    }

    /**
     * Is collection boolean
     * 判断逻辑:
     * psiType.getCanonicalText以collection中的任何一个为开头
     * java.lang.
     * List<String>
     *
     * @param psiType the psi type
     * @return true, collection类型
     */
    public static boolean isCollection(PsiType psiType) {
        boolean match = collectionClasses.stream()
                .anyMatch(collection -> psiType.getCanonicalText().startsWith(collection));

        if (match) {
            return true;
        }

        // 检测是否继承了collectionClasses

        return isInheritor(psiType);
    }

    /**
     * Is inheritor boolean.
     *
     * @param psiType the psi type
     * @return the boolean
     */
    private static boolean isInheritor(PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);

        if (psiClass == null) {
            return false;
        }

        if (collectionClasses.contains(psiClass.getQualifiedName())) {
            return true;
        }

        return Stream.concat(Arrays.stream(psiClass.getInterfaces()), Arrays.stream(psiClass.getSupers()))
                .anyMatch(superClass -> collectionClasses.contains(superClass.getQualifiedName()));
    }
}
