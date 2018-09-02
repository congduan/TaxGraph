package com.dc.tax;

import com.dc.tax.data.TaxRato;

/**
 * 收入计算器
 */
public class TaxCalculator implements Calculable {

    private float moneyBeforeTax;   //税前收入
    private float moneyAfterTax;    //税后收入
    private float moneyAfterShebao; //除去社保公积金之后的收入
    private float moneyShebao;      //社保公积金缴纳数额
    private float moneyTaxBase;     //所得税缴纳基数
    private float taxRatio;         //所得税税率
    private float moneyTax;         //所得税缴纳数额
    private float moneySusuan;      //所得税速算扣除数

    /* 个人需缴纳四金 */
    public static final float ratio_1 = 0.07f; //公积金
    public static final float ratio_2 = 0.02f; //医疗保险
    public static final float ratio_3 = 0.005f;//失业保险
    public static final float ratio_4 = 0.08f; //养老保险

    private String mInfo = "";

    public TaxCalculator() {

    }

    public float getMoneyBeforeTax() {
        return moneyBeforeTax;
    }

    public void setMoneyBeforeTax(float moneyBeforeTax) {
        this.moneyBeforeTax = moneyBeforeTax;
    }

    public float calcTax(float money_x, boolean updateInfo) {
        float ratio_sum = (ratio_1 + ratio_2 + ratio_3 + ratio_4);
        float money_4jin = money_x * (1 - ratio_sum);
        float no_tax = 3500f;
        if (money_4jin < no_tax) {

            if (updateInfo) {
                mInfo = String.format(
                        "税前收入: %.2f\n" +
                                "五险一金: %.2f\n" +
                                "减去五险一金: %.2f\n" +
                                "所得税税率: %.2f\n" +
                                "所得税基数: %.2f\n" +
                                "速算扣除数: %.2f\n" +
                                "个人所得税: %.2f\n" +
                                "税后收入: %.2f",
                        money_x,
                        ratio_sum,
                        money_4jin,
                        0f,
                        0f,
                        0f,
                        0f,
                        money_x);
            }

            return money_x;
        }

        float money_tax_base = money_4jin - no_tax;

        if (updateInfo) {
            mInfo = String.format(
                    "税前收入: %.2f\n" +
                            "五险一金: %.2f\n" +
                            "减去五险一金: %.2f\n" +
                            "所得税税率: %.2f\n" +
                            "所得税基数: %.2f\n" +
                            "速算扣除数: %.2f\n" +
                            "个人所得税: %.2f\n" +
                            "税后收入: %.2f",
                    money_x,
                    ratio_sum,
                    money_4jin,
                    ratio(money_tax_base),
                    money_tax_base,
                    susuan(money_tax_base),
                    (money_tax_base * ratio(money_tax_base) - susuan(money_tax_base)),
                    money_4jin - (money_tax_base * ratio(money_tax_base) - susuan(money_tax_base)));
        }


        return money_4jin - (money_tax_base * ratio(money_tax_base) - susuan(money_tax_base));
    }

    /**
     * 计算速算扣除数
     *
     * @param x
     * @return
     */
    private float susuan(float x) {
        for (TaxRato rato : TaxRato.values()) {
            if (x > rato.min && x < rato.max) {
                return rato.kouchu;
            }
        }
        return 0.0f;
    }

    /**
     * 计算税率
     *
     * @param x
     * @return
     */
    private float ratio(float x) {
        for (TaxRato rato : TaxRato.values()) {
            if (x > rato.min && x < rato.max) {
                return rato.ratio;
            }
        }
        return 0.0f;
    }

    @Override
    public String toString() {
        return mInfo;
    }
}
