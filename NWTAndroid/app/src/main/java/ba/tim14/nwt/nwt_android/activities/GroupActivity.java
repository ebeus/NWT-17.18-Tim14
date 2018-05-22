package ba.tim14.nwt.nwt_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.adapters.CustomUserAdapter;
import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.User;
import ba.tim14.nwt.nwt_android.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = GroupActivity.class.getSimpleName();

    ListView listViewUsers;
    ArrayList<Korisnik> users = new ArrayList<>();
    CustomUserAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getKorisnike();
        users = Utils.getPopulatedListWithUsers();
        setAdapter();
    }

    private void setAdapter() {
        listAdapter = new CustomUserAdapter(GroupActivity.this, users);
        listViewUsers = findViewById(R.id.user_list);
        listViewUsers.setAdapter(listAdapter);
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> performOnListClick(position));
    }

    private void performOnListClick(int position) {
        Log.i(TAG,"You Clicked at " + users.get(position).getUserName());
    }

//TODO Kopirao api poziv ovdje da se može adapter refreshat
    public void getKorisnike() {
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
        users.addAll(korisnici);
        listAdapter.notifyDataSetChanged();//TODO adapter refresh
    }

}
