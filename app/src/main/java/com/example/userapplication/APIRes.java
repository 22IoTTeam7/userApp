package com.example.userapplication;

import com.google.gson.annotations.SerializedName;

/**
 *
 * */
public class APIRes {
    @SerializedName("result")
    private String result;
    @SerializedName("predict")
    private int predict;

    public  String getResult(){
        return result;
    }
    public int getPredict() { return predict; }
}
