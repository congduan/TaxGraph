package com.dc.tax;

public enum CityType {
    CITY_BEIJING("北京", new TaxCalculator()),
    CITY_SHANGHAI("上海", new TaxCalculator()),
    CITY_GUANGZHOU("广州", new TaxCalculator()),
    CITY_SHENZHEN("深圳", new TaxCalculator());

    public String name;
    public Calculable calculable;

    CityType(String name, Calculable calculable) {
        this.name = name;
        this.calculable = calculable;
    }
}
