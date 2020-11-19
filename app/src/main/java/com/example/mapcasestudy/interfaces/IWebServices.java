package com.example.mapcasestudy.interfaces;

import com.example.mapcasestudy.models.BookingResponse;
import com.example.mapcasestudy.models.StationInfoList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IWebServices {

    @GET("stations")
    Call<List<StationInfoList>> getStationInfo();

    @POST("stations/{id}")
    Call<BookingResponse> bookingRequest(@Path("id") String id);
}
