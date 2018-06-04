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
        getKorisnike();
    }

    private void performOnListClick(int position) {
        Log.i(TAG,"You Clicked at " + Utils.users.get(position).getUserName());
    }

    public void getKorisnike() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Korisnik>> korisniciDobijeni = locatorService.getAllUsersFromGroup(SharedPreferencesManager.instance().getUserGroupId());

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

    private void setListOfUsers(List<Korisnik> korisnici) {
        Utils.users.addAll(korisnici);
        getLastKnownUserLocation();
        listAdapter.notifyDataSetChanged();
    }

    public void getLastKnownUserLocation() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Lokacija> returnedLocation = null;
        for (int i = 0; i < Utils.users.size(); i++){
            returnedLocation = locatorService.getLastLocationByUser(Utils.users.get(i).getId());
        }

        returnedLocation.enqueue(new Callback<Lokacija>() {
            @Override
            public void onResponse(Call<Lokacija> call, Response<Lokacija> response) {
                Lokacija lastLocation = response.body();
                Log.i(TAG, "Lokacija "+ lastLocation);
                lokacije.add(lastLocation);
                if(Utils.users.size() == usersLoc.size()){
                    Utils.usersSet();
                    setAdapter();
                }
            }
            @Override
            public void onFailure(Call<Lokacija> call, Throwable t) {
                Log.i(TAG,"Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        listAdapter = new CustomUserAdapter(GroupActivity.this, Utils.users);
        listViewUsers = findViewById(R.id.user_list);
        listViewUsers.setAdapter(listAdapter);
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> performOnListClick(position));
    }

}
