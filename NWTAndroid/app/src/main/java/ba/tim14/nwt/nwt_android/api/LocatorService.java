package ba.tim14.nwt.nwt_android.api;

import java.util.List;

import ba.tim14.nwt.nwt_android.classes.Korisnik;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by elvedin on 20/05/2018.
 *
 */

public interface LocatorService {
    @GET("/users/")
    Call<List<Korisnik>> listaKorisnika();


    @POST("adduserandroid")
    Call<Void> add(@Body Korisnik korisnik);

}
