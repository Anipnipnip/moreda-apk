package com.example.cd

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

data class LoginResponse(
    val message: String,
    val success: Boolean,
    val data: LoginData?
)

data class LoginData(
    val user: UserDetails,
    val token: String
)

data class UserDetails(
    val userId: Int,
    val username: String,
    val email: String
)

data class SignupResponse(
    val message: String,
    val success: Boolean
)

data class Movie(
    val movieId: Int,
    val title: String,
    val genres: String
)

data class MovieResponse(
    val judul: List<Movie>
)

data class MovieResponseML(
    val data: List<Movie>
)


interface ApiService {
    @FormUrlEncoded
    @POST("/auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("/auth/register")
    fun signup(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignupResponse>

    @POST("/movies/search/title")
    @FormUrlEncoded
    fun searchMoviesByTitle(
        @Header("Authorization") token: String,
        @Field("title") title: String
    ): Call<MovieResponse>


    @POST("/wishlist/user/add")
    @FormUrlEncoded
    fun addWishlist(
        @Header("Authorization") token: String,
        @Field("movieId") movieId: Int
    ): Call<Void>

    @GET("/wishlist/user/get")
    fun getWishlist(
        @Header("Authorization") token: String
    ): Call<MovieResponseML>


}