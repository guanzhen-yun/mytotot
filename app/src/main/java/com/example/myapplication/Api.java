package com.example.myapplication;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
  @GET("citys")
  Observable<AllCity> getAllCity(@Query("key") String key);
}
