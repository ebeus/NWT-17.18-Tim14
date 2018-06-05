package ba.tim14.nwt.nwt_android.api;

import java.util.List;

import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.Lokacija;
import ba.tim14.nwt.nwt_android.classes.Putovanje;
import ba.tim14.nwt.nwt_android.classes.Trip;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by elvedin on 20/05/2018.
 *
 */

public interface LocatorService {

//    @GET("/users/")
//    Call<List<Korisnik>> getAllUsers();
//
//    @GET("/users/group/{userGroupId}")
//    Call<List<Korisnik>> getAllUsersFromGroup(@Path("userGroupId") Long userGroupId);
//
//    @GET("/users/{userId}")
//    Call<Korisnik> getUserWithId(@Path("userId") Long userId);
//
//    @GET("/users?")
//    Call<Korisnik> getUserWithUserName(@Query("userName") String userName);
//
//    @GET("/users/exists/{userId}")
//    Call<ResponseBody> doesUserWithIdExist(@Path("userId") Long userId);
//
//    @GET("/users/exists?")
//    Call<ResponseBody> doesUserWithUsernameExist(@Query("userName") String userName);
//
    @FormUrlEncoded
    @POST("/users/add")
    Call<ResponseBody> add(@Field("firstName")   String firstName,
                           @Field("lastName")    String lastName,
                           @Field("userName")    String userName,
                           @Field("password")    String password,
                           @Field("email")       String email,
                           @Field("userTypeId")  Long userTypeId,
                           @Field("userGroupId") Long userGroupId);
//
//    @FormUrlEncoded
//    @POST("/users/update/{userId}")
//    Call<ResponseBody> update(@Path("userId") Long userId,
//                              @Field("firstName")   String firstName,
//                              @Field("lastName")    String lastName,
//                              @Field("userName")    String userName,
//                              @Field("password")    String password,
//                              @Field("email")       String email,
//                              @Field("userTypeId")  Long userTypeId,
//                              @Field("userGroupId") Long userGroupId);
//
//    @FormUrlEncoded
//    @POST("/trip/start")
//    Call<ResponseBody> start(@Field("naziv") String naziv,
//                             @Field("start_time") Long start_time,
//                             @Field("korisnikId") Long korisnikId);
//
//
//    @GET("/trip/by-name/{tripName}")
//    Call<Putovanje> getTripByName(@Path("tripName") String tripName);
//
//    @FormUrlEncoded
//    @POST("/trip/stop")
//    Call<ResponseBody> stop(@Field("id") Long id,
//                            @Field("end_time") Long end_time,
//                            @Field("distance") double distance);
//
//    @FormUrlEncoded
//    @POST("/trip/locations/add")
//    Call<ResponseBody> addLocation(@Field("id_putovanja") Long id_putovanja,
//                                   @Field("time") Long time,
//                                   @Field("lat") Double lat,
//                                   @Field("lng") Double lng);
//
//    @GET("/trip/by-user/{userId}")
//    Call<List<Putovanje>> geAllTripsByUser(@Path("userId") Long userId);
//
//    @GET("/trip/locations/{tripId}")
//    Call<List<Lokacija>> getAllLocationsInATrip(@Path("tripId") Long tripId);
//
//    @GET("/trip/locations/last-trip/{userId}")
//    Call<Lokacija> getLastLocationByUser(@Path("userId") Long userId);


    ////////////////////WITH AUTH/////////////////////////////////////////////

    @FormUrlEncoded
    @POST("/korisnici_ms/oauth/token")
    Call<ResponseBody> getToken(@Header("Authorization") String authorization,
                                @Field("client_id") String client_id,
                                @Field("client_secret") String client_secret,
                                @Field("grant_type") String grant_type,
                                @Field("scope") String scope,
                                @Field("username") String username,
                                @Field("password") String password);


    @GET("/users/")
    Call<List<Korisnik>> getAllUsers(@Header("Authorization") String authorization);

    @GET("/users/group/{userGroupId}")
    Call<List<Korisnik>> getAllUsersFromGroup(@Header("Authorization") String authorization,
                                              @Path("userGroupId") Long userGroupId);

    @GET("/users/{userId}")
    Call<Korisnik> getUserWithId(@Header("Authorization") String authorization,
                                 @Path("userId") Long userId);

    @GET("/users?")
    Call<Korisnik> getUserWithUserName(@Header("Authorization") String authorization,
                                       @Query("userName") String userName);

    @GET("/users/exists/{userId}")
    Call<ResponseBody> doesUserWithIdExist(@Header("Authorization") String authorization,
                                           @Path("userId") Long userId);

    @GET("/users/exists?")
    Call<ResponseBody> doesUserWithUsernameExist(@Header("Authorization") String authorization,
                                                 @Query("userName") String userName);


    @FormUrlEncoded
    @POST("/users/update/{userId}")
    Call<ResponseBody> update(@Header("Authorization") String authorization,
                              @Path("userId") Long userId,
                              @Field("firstName")   String firstName,
                              @Field("lastName")    String lastName,
                              @Field("userName")    String userName,
                              @Field("password")    String password,
                              @Field("email")       String email,
                              @Field("userTypeId")  Long userTypeId,
                              @Field("userGroupId") Long userGroupId);

    @FormUrlEncoded
    @POST("/trip/start")
    Call<ResponseBody> start(@Header("Authorization") String authorization,
                             @Field("naziv") String naziv,
                             @Field("start_time") Long start_time,
                             @Field("korisnikId") Long korisnikId);


    @GET("/trip/by-name/{tripName}")
    Call<Putovanje> getTripByName(@Header("Authorization") String authorization,
                                  @Path("tripName") String tripName);

    @FormUrlEncoded
    @POST("/trip/stop")
    Call<ResponseBody> stop(@Header("Authorization") String authorization,
                            @Field("id") Long id,
                            @Field("end_time") Long end_time,
                            @Field("distance") double distance);

    @FormUrlEncoded
    @POST("/trip/locations/add")
    Call<ResponseBody> addLocation(@Header("Authorization") String authorization,
                                   @Field("id_putovanja") Long id_putovanja,
                                   @Field("time") Long time,
                                   @Field("lat") Double lat,
                                   @Field("lng") Double lng);

    @GET("/trip/by-user/{userId}")
    Call<List<Putovanje>> geAllTripsByUser(@Header("Authorization") String authorization,
                                           @Path("userId") Long userId);

    @GET("/trip/locations/{tripId}")
    Call<List<Lokacija>> getAllLocationsInATrip(@Header("Authorization") String authorization,
                                                @Path("tripId") Long tripId);

    @GET("/trip/locations/last-trip/{userId}")
    Call<Lokacija> getLastLocationByUser(@Header("Authorization") String authorization,
                                         @Path("userId") Long userId);
}

