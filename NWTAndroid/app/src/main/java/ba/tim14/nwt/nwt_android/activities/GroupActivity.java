package ba.tim14.nwt.nwt_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.adapters.CustomUserAdapter;
import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.Lokacija;
import ba.tim14.nwt.nwt_android.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ba.tim14.nwt.nwt_android.utils.Utils.usersLoc;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = GroupActivity.class.getSimpleName();

    ListView listViewUsers;
    CustomUserAdapter listAdapter;
    private ArrayList<Lokacija> lokacije;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        lokacije = new ArrayList<>();
        setAdapter();
//        getKorisnike();
    }

    private void performOnListClick(int position) {
        Log.i(TAG,"You Clicked at " + Utils.users.get(position).getUserName());
    }

    private void setAdapter() {
        Log.i(TAG, "KORISNICII: " + Utils.users);
        listAdapter = new CustomUserAdapter(GroupActivity.this, Utils.users);
        listViewUsers = findViewById(R.id.user_list);
        listViewUsers.setAdapter(listAdapter);
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> performOnListClick(position));
        listAdapter.notifyDataSetChanged();
    }

}
