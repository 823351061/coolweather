package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author hyh
 * @date 2018/9/26 0026
 * @describe:
 */
public class Now {
    @SerializedName("tmp")
    public String temprature;

    @SerializedName("cond")
    public  More more;

    public class More{
        @SerializedName("txt")
        public  String info;
    }
}
