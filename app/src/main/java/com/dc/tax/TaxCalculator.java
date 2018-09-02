package com.dc.tax;

import com.dc.tax.data.TaxRato;

/**
 * 收入计算器
 */
public class TaxCalculator implements Calculable {

    private float moneyBeforeTax;   //税前收入
    private float moneyAfterTax;    //税后收入
    private float money4Jin;      //社保公积金缴纳数额
    private float moneyAfter4Jin;   //除去社保公积金之后的收入
    private float moneyTaxBase;     //所得税缴纳基数
    private float taxRatio;         //所得税税率
    private float moneyTax;         //所得税缴纳数额
    private float moneySusuan;      //所得税速算扣除数

    /* 个人需缴纳四金 */
    public static final float RATIO_GONGJIJIN = 0.07f; //公积金
    public static final float RATIO_YILIAO = 0.02f; //医疗保险
    public static final float RATIO_SHIYE = 0.005f;//失业保险
    public static final float RATIO_YANGLAO = 0.08f; //养老保险

    public static final float noTaxStandard = 3500f;//个税缴纳基础

    private boolean isIncludeGongjijin = true;// 包含公积金

    private String mInfo = "";

    public TaxCalculator() {

    }

    public float calcTax(float money_x) {
        moneyBeforeTax = money_x;
        float gongjijin = getGongjijin(money_x * RATIO_GONGJIJIN);
        float yiliao = getShebaoBase(money_x) * RATIO_YILIAO;
        float shiye = getShebaoBase(money_x) * RATIO_SHIYE;
        float yanglao = getShebaoBase(money_x) * RATIO_YANGLAO;
        money4Jin = gongjijin + yiliao + shiye + yanglao;
        moneyAfter4Jin = money_x - money4Jin;

        // 扣除4金之后，未达到缴纳基数
        if (moneyAfter4Jin < noTaxStandard) {
            moneyTaxBase = money_x;
            taxRatio = 0;
            moneySusuan = 0;
            moneyTax = 0;
            moneyAfterTax = moneyAfter4Jin;
            return moneyAfterTax;
        }

        // 个税缴纳基数
        moneyTaxBase = moneyAfter4Jin - noTaxStandard;

        // 个税税率
        taxRatio = getTaxRatio(moneyTaxBase);
        moneySusuan = getSusuan(moneyTaxBase);
        moneyTax = moneyTaxBase * taxRatio - moneySusuan;;
        moneyAfterTax = moneyAfter4Jin - moneyTax;
        return moneyAfterTax;
    }

    /**
     * 计算速算扣除数
     *
     * @param x
     * @return
     */
    private float getSusuan(float x) {
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
    private float getTaxRatio(float x) {
        for (TaxRato rato : TaxRato.values()) {
            if (x > rato.min && x < rato.max) {
                return rato.ratio;
            }
        }
        return 0.0f;
    }

    /**
     * 获取社保缴纳基数
     *
     * @param x
     * @return
     */
    private float getShebaoBase(float x) {
        float min = 4279f;
        float max = 21396f;
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }

    /**
     * 获取公积金缴纳金额上下限
     *
     * @param x
     * @return
     */
    private float getGongjijin(float x) {
        float min = 322f;
        float max = 2996f;
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }

    @Override
    public String toString() {
        mInfo = String.format(
                "税前收入: %.2f\n" +
                "税后收入: %.2f\n" +
                "五险一金: %.2f\n" +
                "五险一金后: %.2f\n" +
                "所得税税率: %.2f\n" +
                "所得税基数: %.2f\n" +
                "速算扣除数: %.2f\n" +
                "个人所得税: %.2f",
                moneyBeforeTax,
                moneyAfterTax,
                money4Jin,
                moneyAfter4Jin,
                taxRatio,
                moneyTaxBase,
                moneySusuan,
                moneyTax);
        return mInfo;
    }

    public float getMoneyBeforeTax(){
        return moneyBeforeTax;
    }

    public float getMoney4Jin() {
        return money4Jin;
    }

    public float getMoneyTax() {
        return moneyTax;
    }
}
