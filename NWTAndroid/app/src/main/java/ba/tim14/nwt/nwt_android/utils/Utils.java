package ba.tim14.nwt.nwt_android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.Lokacija;
import ba.tim14.nwt.nwt_android.classes.Putovanje;
import ba.tim14.nwt.nwt_android.classes.Trip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    public static final String NAME_REGEX = "^[a-zA-ZČĆŠĐŽčćšđž]+$";

    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";

    public static final String URLPutovanja = "http://192.168.0.18:8081";
    public static final String URLKorisnici="http://192.168.0.18:8080";
    public static final String URLOAuth = "http://192.168.0.18:8090";
//    public static final String URLPutovanja = "http://80.80.40.105:8081";
//    public static final String URLKorisnici="http://80.80.40.105:8080";

    public static ArrayList<Korisnik> users = new ArrayList<>();
    public static ArrayList<Lokacija> usersLoc = new ArrayList<>();
    public static ArrayList<Putovanje> putovanjaKorisnika = new ArrayList<>();

    public static String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVSUQiOjIsIlVHcm91cCI6IkdydXBhMSIsInVzZXJfbmFtZSI6ImNvQnJpYW4iLCJzY29wZSI6WyJtb2JpbGUiXSwiVVR5cGUiOiJVU0VSIiwiZXhwIjoxNTI4MjgyMzQwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiNmM3OGIyZTktYTlmNy00MTIwLTk0ZDAtODQ3MWZiMDUyYTFlIiwiY2xpZW50X2lkIjoiY2xpZW50In0.EXw3YC9eg1ooXiMJ7DhwdRUh9Y8A46H2khQ-brJj468";
    public static String tokenType="Bearer ";


    private static Typeface customFont;

    public static BitmapDescriptor getBitmapDescriptor(Context applicationContext, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) applicationContext.getDrawable(id);

            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    public static void getKorisnike() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

               LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Korisnik>> korisniciDobijeni = locatorService.getAllUsers(Utils.tokenType + Utils.token);

        korisniciDobijeni.enqueue(new Callback<List<Korisnik>>() {
            @Override
            public void onResponse(Call<List<Korisnik>> call, Response<List<Korisnik>> response) {
                List<Korisnik> korisnici = response.body();
                Log.i(TAG, "Korisnici "+ korisnici);
                setListOfUsers(korisnici);
            }
            @Override
            public void onFailure(Call<List<Korisnik>> call, Throwable t) {
                Log.i(TAG, "Nesto nije okej:  " + t.toString());
            }
        });
    }

    private static void setListOfUsers(List<Korisnik> korisnici) {
        users.clear();
        users.addAll(korisnici);
    }


    public static void setFont(Activity activity) {
        customFont = Typeface.createFromAsset(activity.getAssets(), "font/alex_brush.ttf");
    }

    public static Typeface getFont() {        return customFont;    }

    public static void usersSet() {
        boolean usersSet = true;
    }

}

