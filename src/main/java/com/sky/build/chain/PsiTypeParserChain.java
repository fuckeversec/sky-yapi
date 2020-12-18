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

    private static AbstractPsiTypeParser firstPsiTypeParser;

    static {

        AbstractPsiTypeParser previousPsiTypeParser = null;

        List<AbstractPsiTypeParser> psiTypeParsers = Arrays
                .asList(new NormalPsiTypeParser(),
                        new TimePsiTypeParse(),
                        new EnumPsiTypeParser(),
                        new ListPsiTypeParser(),
                        new ArrayPsiTypeParser(),
                        new ObjectPsiTypeParse());

        firstPsiTypeParser = psiTypeParsers.get(0);

        for (AbstractPsiTypeParser psiTypeParser : psiTypeParsers) {
            psiTypeParser.setFirstPsiTypeParser(firstPsiTypeParser);
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
        firstPsiTypeParser.parser(psiType, kv);
        return kv;
    }

}
