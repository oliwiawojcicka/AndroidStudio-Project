package com.salle.grup17.api;

import com.salle.grup17.models.ApiResponse;
import com.salle.grup17.models.Character;
import com.salle.grup17.models.Episode;
import com.salle.grup17.models.Location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RickAndMortyApi {

    // character

    @GET("api/character")
    Call<ApiResponse<Character>> getCharacters(@Query("page") int page);

    @GET("api/character")
    Call<ApiResponse<Character>> searchCharacters(
            @Query("name") String name,
            @Query("page") int page
    );

    @GET("api/character/{id}")
    Call<Character> getCharacterById(@Path("id") int id);


    // episodes

    @GET("api/episode")
    Call<ApiResponse<Episode>> getEpisodes(@Query("page") int page);

    @GET("api/episode")
    Call<ApiResponse<Episode>> searchEpisodes(
            @Query("name") String name,
            @Query("page") int page
    );

    @GET("api/episode/{id}")
    Call<Episode> getEpisodeById(@Path("id") int id);


    // locations

    @GET("api/location")
    Call<ApiResponse<Location>> getLocations(@Query("page") int page);

    @GET("api/location")
    Call<ApiResponse<Location>> searchLocations(
            @Query("name") String name,
            @Query("page") int page
    );

    @GET("api/location/{id}")
    Call<Location> getLocationById(@Path("id") int id);
}