package com.example.administrator.coolweather.gson;

/**
 * @author hyh
 * @date 2018/9/26 0026
 * @describe:
 */
public class AQI {
    public AQICity city;
    public  class AQICity{
        public String aqi;
        public String pm25;
    }
}
