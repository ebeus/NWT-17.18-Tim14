package ba.tim14.nwt.nwt_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.Lokacija;
import ba.tim14.nwt.nwt_android.dialogs.SettingsDialog;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends Activity implements View.OnClickListener{

    private static final String TAG = MenuActivity.class.getSimpleName();

    private ArrayList<Lokacija> lokacije;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lokacije = new ArrayList<>();
        getKorisnike();
        if (!SharedPreferencesManager.instance().isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else {
            setContentView(R.layout.activity_menu);
            if(getIntent().getIntExtra(Constants.STEP,0) != 0){
                manageSettingsClick();
            }
            initViews();
        }
    }

    private void initViews() {
        ((TextView)findViewById(R.id.textView_title_start_trip)).setTypeface(Utils.getFont());
        ((TextView)findViewById(R.id.textView_title_my_group)).setTypeface(Utils.getFont());
        ((TextView)findViewById(R.id.textView_title_my_trips)).setTypeface(Utils.getFont());

        findViewById(R.id.layout_button_trip).setOnClickListener(this);
        findViewById(R.id.layout_button_trip_list).setOnClickListener(this);
        RelativeLayout buttonGroup = findViewById(R.id.layout_button_group);
        if(SharedPreferencesManager.instance().getUserGroupId() == 0L){
            buttonGroup.setVisibility(View.GONE);
        }
        else {
            buttonGroup.setVisibility(View.VISIBLE);
            findViewById(R.id.layout_button_group).setOnClickListener(this);
        }
        findViewById(R.id.btn_settings).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_button_trip:
                startTrip();
                break;
            case R.id.layout_button_trip_list:
                startActivity(new Intent(this, TripHistoryActivity.class));
                break;
            case R.id.layout_button_group:
                Intent startMyGroup = new Intent(this, GroupActivity.class);
                startActivity(startMyGroup);
                break;
            case R.id.btn_settings:
                SettingsDialog settingsDialog=new SettingsDialog(this);
                settingsDialog.show();
                break;
        }
    }

    private void startTrip() {
        Intent startTrip = new Intent(new Intent(this, TripActivity.class));
        startTrip.putExtra(Constants.STEP, Constants.MY_TRIP);
        startActivity(startTrip);
    }

    private void startNewActivityForResult(Class c, int step){
        Intent intent = new Intent(this, c).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.STEP, step);
        startActivityForResult(intent, step);
    }

    private void manageSettingsClick() {
        switch (getIntent().getIntExtra(Constants.STEP,0)){
            case Constants.SETTINGS_CHANGE:
                startNewActivityForResult(LoginActivity.class, Constants.SETTINGS_CHANGE);
                break;
            case Constants.SETTINGS_SIGN_OUT:
                SharedPreferencesManager.instance().setLoggedIn(false);
                startNewActivityForResult(MainActivity.class, Constants.LOGIN);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.SETTINGS_CHANGE:
                if(resultCode == Constants.VALID){
                    finish();
                    Intent finishIntent = getIntent();
                    finishIntent.removeExtra(Constants.STEP);
                    startActivity(finishIntent);
                }
                break;
        }
    }

    public void getKorisnike() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Korisnik>> korisniciDobijeni = locatorService.getAllUsersFromGroup(Utils.tokenType + Utils.token, SharedPreferencesManager.instance().getUserGroupId());

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

        System.out.println("Korisnici iz poziva: " + korisnici);
        Utils.users.clear();
        Utils.users.addAll(korisnici);

        System.out.println("Korisnici iz u utils: " + Utils.users);

        getLastKnownUserLocation();
    }

    public void getLastKnownUserLocation() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Lokacija> returnedLocation = null;
        for (int i = 0; i < Utils.users.size(); i++){
            returnedLocation = locatorService.getLastLocationByUser(Utils.tokenType + Utils.token, Utils.users.get(i).getId());

            int finalI = i;
            returnedLocation.enqueue(new Callback<Lokacija>() {
                @Override
                public void onResponse(Call<Lokacija> call, Response<Lokacija> response) {
                    Lokacija lastLocation = response.body();
                    Log.i(TAG, "Lokacija "+ lastLocation);
                    lokacije.add(lastLocation);
                    if(Utils.users.size() == finalI + 1){
                        Utils.usersLoc.addAll(lokacije);
                        Utils.usersSet();
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
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

}
