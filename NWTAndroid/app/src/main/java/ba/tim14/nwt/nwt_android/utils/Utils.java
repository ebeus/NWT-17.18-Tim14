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
import ba.tim14.nwt.nwt_android.classes.Trip;
import ba.tim14.nwt.nwt_android.classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";

    public static final String URL="http://192.168.0.18:8080";

    public static ArrayList<Trip> tripList;
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



    public static ArrayList<User> getPopulatedListWithUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("User 1", new LatLng(43.856259, 18.413076)));
        users.add(new User("User 2", new  LatLng(43.857657, 18.416134)));
        users.add(new User("User 3", new  LatLng(43.859143, 18.412014)));
        users.add(new User("User 4", new  LatLng(43.855859, 18.396404)));
        users.add(new User("User 5", new  LatLng(43.847683, 18.387619)));
        users.add(new User("User 6", new  LatLng(43.859342, 18.423951)));
        users.add(new User("User 7", new  LatLng(43.861988, 18.412428)));
        users.add(new User("User 8", new  LatLng(43.845642, 18.361547)));

        return users;
    }



    public static void getKorisnike() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Korisnik>> korisniciDobijeni = locatorService.listaKorisnika();

        korisniciDobijeni.enqueue(new Callback<List<Korisnik>>() {
            @Override
            public void onResponse(Call<List<Korisnik>> call, Response<List<Korisnik>> response) {
                List<Korisnik> korisnici = response.body();
                Log.i("AAAA", "Korisnici "+ korisnici);
            }
            @Override
            public void onFailure(Call<List<Korisnik>> call, Throwable t) {
                Log.i("AAa", "Nesto nije okej:  " + t.toString());

            }
        });
    }


    public static void setFont(Activity activity) {
        customFont = Typeface.createFromAsset(activity.getAssets(), "font/alex_brush.ttf");
    }

    public static Typeface getFont() {        return customFont;    }

}
