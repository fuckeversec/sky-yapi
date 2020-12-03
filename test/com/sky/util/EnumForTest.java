package com.sky.util;

/**
 * @author gangyf
 * @since 2020/12/2 7:42 PM
 */
public enum EnumForTest {
    /**
     * TEST
     */
    TEST(0, "TEST"),

    /**
     * PRODUCT
     */
    PRODUCT(1, "PRODUCT"),
    ;

    EnumForTest(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private final int value;
    private final String desc;

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "EnumForTest{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}
