package com.example.administrator.coolweather.db;

import org.litepal.crud.DataSupport;


/**
 * 市
 * */
public class City extends DataSupport {
    private  int id;
    private  int cityCode;//市代号
    private  String  cityName;
    private  int  provinceId;//市所属省

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
