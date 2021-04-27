package com.sky.build.chain;

import com.intellij.psi.PsiType;
import com.sky.build.KV;
import java.util.Arrays;
import java.util.List;

/**
 * @author gangyf
 * @since 2020/12/14 7:14 PM
 */
public class PsiTypeParserChain {

    private static final AbstractPsiTypeParser FIRST_PSI_TYPE_PARSER;

    private static final ThreadLocal<String> PARSING_CLASS = ThreadLocal.withInitial(String::new);

    static {

        AbstractPsiTypeParser previousPsiTypeParser = null;

        List<AbstractPsiTypeParser> psiTypeParsers = Arrays
                .asList(new NormalPsiTypeParser(),
                        new TimePsiTypeParse(),
                        new EnumPsiTypeParser(),
                        new ListPsiTypeParser(),
                        new ArrayPsiTypeParser(),
                        new ObjectPsiTypeParse());

        FIRST_PSI_TYPE_PARSER = psiTypeParsers.get(0);

        for (AbstractPsiTypeParser psiTypeParser : psiTypeParsers) {
            psiTypeParser.setFirstPsiTypeParser(FIRST_PSI_TYPE_PARSER);
            if (previousPsiTypeParser != null) {
                previousPsiTypeParser.setNextParser(psiTypeParser);
            }
            previousPsiTypeParser = psiTypeParser;
        }
    }

    /**
     * 解析PsiType
     *
     * @param psiType the psi type
     * @return the kv
     */
    public KV<String, Object> parse(PsiType psiType) {
        KV<String, Object> kv = KV.create();
        FIRST_PSI_TYPE_PARSER.parser(psiType, kv);
        return kv;
    }

    /**
     * 类和类属性, 循环引用
     *
     * @param qualifiedClassName the qualified class name
     * @return the boolean
     */
    public static boolean circularReference(String qualifiedClassName) {
        return qualifiedClassName.equals(PARSING_CLASS.get());
    }

    /**
     * 设置当前解析类, 全限定名
     *
     * @param qualifiedClassName the qualified class name
     */
    public static void setCurrentParsingPasiType(String qualifiedClassName) {
        PARSING_CLASS.set(qualifiedClassName);
    }

}
