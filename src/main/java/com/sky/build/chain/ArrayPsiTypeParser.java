package com.sky.build.chain;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.sky.build.KV;
import com.sky.build.util.LengthPropertyParse;
import com.sky.util.DesUtil;
import java.util.function.Supplier;

/**
 * 解析数组, json(array)类型数据
 *
 * @author gangyf
 * @since 2020/12/14 8:44 AM
 */
public class ArrayPsiTypeParser extends AbstractPsiTypeParser {

    @Override
    void parser(PsiType psiType, KV<String, Object> kv) {
        if (!(psiType instanceof PsiArrayType)){
            nextParser.parser(psiType, kv);
            return;
        }

        kv.set("type", "array");

        // get array type
        psiType = psiType.getDeepComponentType();

        KV<String, Object> items = KV.create();
        kv.set("items", items);
        firstPsiTypeParser.parser(psiType, items);
    }

    @Override
    void parser(PsiField psiField, KV<String, Object> kv) {

        if (!(psiField.getType() instanceof PsiArrayType)){
            nextParser.parser(psiField, kv);
            return;
        }

        parser(psiField.getType(), kv);
        kv.set("description", DesUtil.getDesc(psiField));

        LengthPropertyParse.maxLength(psiField).ifPresent(length -> kv.set("maxLength", length));

        LengthPropertyParse.minLength(psiField).ifPresent(length -> kv.set("minLength", length));

    }

    public static void main(String[] args) {
        Supplier<SupplierForTest> supplier = new Supplier<SupplierForTest>() {

            SupplierForTest supplierForTest;

            @Override
            public SupplierForTest get() {
                if (supplierForTest == null) {
                    supplierForTest = new SupplierForTest();
                }
                return supplierForTest;
            }

        };

        System.out.println(supplier);
        System.out.println(supplier);
        System.out.println(supplier);

    }

    static class SupplierForTest {
    }

}
