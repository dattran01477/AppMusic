package com.tranthanhdat.mucsicplayergroup2.service;

import com.tranthanhdat.mucsicplayergroup2.Config.Config;
import com.tranthanhdat.mucsicplayergroup2.model.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SCService {

    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getRecentTracks(@Query("created_at") String date);
}