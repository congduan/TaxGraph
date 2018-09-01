package com.dc.tax.data;

/**
 * 个人所得税税率+
 */
public enum TaxRato {
    L1(0f, 1500f, 0.03f, 0f),
    L2(1500f, 4500f, 0.10f, 105f),
    L3(4500f, 9000f, 0.20f, 555f),
    L4(9000f, 35000f, 0.25f, 1005f),
    R5(35000f, 55000f, 0.30f, 2755f),
    R6(55000f, 80000f, 0.35f, 5505f),
    R7(80000f, Float.MAX_VALUE, 0.40f, 13505f);

    public float min;
    public float max;
    public float ratio;
    public float kouchu;

    TaxRato(float min, float max, float ratio, float kouchu) {
        this.min = min;
        this.max = max;
        this.ratio = ratio;
        this.kouchu = kouchu;
    }
}
