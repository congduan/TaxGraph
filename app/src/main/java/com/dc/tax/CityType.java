package com.dc.tax;

public enum CityType {
    CITY_BEIJING("北京"),
    CITY_SHANGHAI("上海"),
    CITY_GUANGZHOU("广州"),
    CITY_SHENZHEN("深圳");

    public String name;
    CityType(String name) {
        this.name = name;
    }
}
