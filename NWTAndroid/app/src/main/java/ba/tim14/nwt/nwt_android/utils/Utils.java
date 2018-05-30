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

 //   public static final String URLPutovanja = "http://192.168.0.18:8081";
  //  public static final String URLKorisnici="http://192.168.0.18:8080";
    public static final String URLPutovanja = "http://80.80.40.105:8081";
    public static final String URLKorisnici="http://80.80.40.105:8080";

    //public static ArrayList<Trip> tripList;
    public static ArrayList<Putovanje> tripList;
    private static ArrayList<Korisnik> users = new ArrayList<>();
    public static ArrayList<LatLng> usersLoc = new ArrayList<>();

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

    public static ArrayList<Korisnik> getPopulatedListWithUsers() {
        users = new ArrayList<>();
        // TODO: 22.05.2018. Ovo radi za samo 8 razlicitih lokacija, za ostale mi bilo glupo da su jedni
        // na drugima pa ovo treba preko location da Users trazi location za usera ili nesto sl.
        usersLoc.add(new LatLng(43.856259, 18.413076));
        usersLoc.add(new  LatLng(43.857657, 18.416134));
        usersLoc.add(new  LatLng(43.859143, 18.412014));
        usersLoc.add(new  LatLng(43.855859, 18.396404));
        usersLoc.add(new  LatLng(43.847683, 18.387619));
        usersLoc.add(new  LatLng(43.859342, 18.423951));
        usersLoc.add(new  LatLng(43.861988, 18.412428));
        usersLoc.add(new  LatLng(43.845642, 18.361547));
        if(users.size() > 7){
            for (int i = 7; i < users.size(); i++){
                usersLoc.add(new LatLng(0,0));
            }
        }
        return users;
    }

    public static void getKorisnike() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

               LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Korisnik>> korisniciDobijeni = locatorService.getAllUsers();

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
        users.addAll(korisnici);
    }


    public static void setFont(Activity activity) {
        customFont = Typeface.createFromAsset(activity.getAssets(), "font/alex_brush.ttf");
    }

    public static Typeface getFont() {        return customFont;    }

}
