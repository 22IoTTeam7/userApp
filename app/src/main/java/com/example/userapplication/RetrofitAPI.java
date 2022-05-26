package com.example.userapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * HTTP 전송 API 정의입니다.
 * */
public interface RetrofitAPI {

    @POST("/location/floor2")
    Call<APIRes> postJsonF2(@Body Floor2JsonModel data);

    @POST("/location/floor4")
    Call<APIRes> postJsonF4(@Body Floor4JsonModel data);

    @POST("/location/floor5")
    Call<APIRes> postJsonF5(@Body Floor5JsonModel data);
}
