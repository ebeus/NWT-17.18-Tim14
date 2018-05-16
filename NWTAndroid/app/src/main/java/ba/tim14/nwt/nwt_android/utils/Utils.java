package ba.tim14.nwt.nwt_android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ba.tim14.nwt.nwt_android.classes.User;

public class Utils {


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

}
