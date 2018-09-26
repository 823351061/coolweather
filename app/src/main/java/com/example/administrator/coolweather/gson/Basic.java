package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author hyh
 * @date 2018/9/26 0026
 * @describe:
 */
public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public  Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
