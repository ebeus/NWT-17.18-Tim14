package ba.tim14.nwt.nwt_android.api;

import java.util.List;

import ba.tim14.nwt.nwt_android.classes.Korisnik;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by elvedin on 20/05/2018.
 *
 */

public interface LocatorService {
    @GET("/users/")
    Call<List<Korisnik>> listaKorisnika();
}
