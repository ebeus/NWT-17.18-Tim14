package ba.tim14.nwt.nwt_android.api;

import java.util.List;

import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.Lokacija;
import ba.tim14.nwt.nwt_android.classes.Trip;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by elvedin on 20/05/2018.
 *
 */

public interface LocatorService {
    @GET("/users/")
    Call<List<Korisnik>> getAllUsers();

    @GET("/users/group/{userGroupId}")
    Call<List<Korisnik>> getAllUsersFromGroup(@Path("userGroupId") Long userGroupId);

    @FormUrlEncoded
    @POST("/users/add")
    Call<ResponseBody> add(@Field("firstName")   String firstName,
                       @Field("lastName")    String lastName,
                       @Field("userName")    String userName,
                       @Field("password")    String password,
                       @Field("email")       String email,
                       @Field("userTypeId")  Long userTypeId,
                       @Field("userGroupId") Long userGroupId);

    @FormUrlEncoded
    @POST("/trip/start")
    Call<ResponseBody> start(@Field("naziv") String naziv,
                             @Field("start_time") Long start_time,
                             @Field("korisnikId") Long korisnikId);

    @FormUrlEncoded
    @POST("/trip/stop")
    Call<ResponseBody> stop(@Field("id") Long id,
                            @Field("end_time") Long end_time);

    @FormUrlEncoded
    @POST("/trip/locations/add")
    Call<ResponseBody> addLocation(@Field("id_putovanja") Long id_putovanja,
                                   @Field("time") Long time,
                                   @Field("lat") Double lat,
                                   @Field("lng") Double lng);

    @GET("/trip/by-user/{userId}")
    Call<List<Trip>> geAllTripsByUser(@Path("userId") Long userId);

    @GET("/trip/locations/{tripId}")
    Call<List<Lokacija>> getAllLocationsInATrip(@Path("tripId") Long tripId);
}

