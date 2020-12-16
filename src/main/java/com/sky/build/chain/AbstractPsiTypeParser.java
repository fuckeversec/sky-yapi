package com.sky.build.chain;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.sky.build.KV;

/**
 * psi type解析器, 解析结果放入KV结构
 *
 * @author gangyf
 * @since 2020 /12/14 8:11 AM
 */
public abstract class AbstractPsiTypeParser {

    /**
     * 下一个解析器
     */
    protected AbstractPsiTypeParser nextParser;

    /**
     * 所有解析器, 解析到复杂对象时, 调用第一个解析器进行复杂属性
     */
    protected AbstractPsiTypeParser firstPsiTypeParser;

    /**
     * 解析类型
     *
     * @param psiType the psi type
     * @param kv the kv
     */
    abstract void parser(PsiType psiType, KV<String, Object> kv);

    /**
     * 解析复杂类型field属性
     *
     * @param psiField the psi field
     * @param kv the kv
     */
    abstract void parser(PsiField psiField, KV<String, Object> kv);

    public void setNextParser(AbstractPsiTypeParser nextParser) {
        this.nextParser = nextParser;
    }

    public void setFirstPsiTypeParser(AbstractPsiTypeParser firstPsiTypeParser) {
        this.firstPsiTypeParser = firstPsiTypeParser;
    }
}
