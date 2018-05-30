package ba.tim14.nwt.nwt_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.adapters.TripItemAdapter;
import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Putovanje;
import ba.tim14.nwt.nwt_android.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertNotNull;

public class TripHistoryActivity extends AppCompatActivity {

    private static final String TAG = TripHistoryActivity.class.getSimpleName();

    ListView listViewTripHistory;
    TextView textViewInfo;

    List<Putovanje> putovanja = new ArrayList<>();
    ArrayList<Putovanje> putovanjaKorisnika = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        textViewInfo = findViewById(R.id.textView_info);
        getPutovanja();
    }

    private void getPutovanja() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Putovanje>> returnedTrips = locatorService.geAllTripsByUser(SharedPreferencesManager.instance().getId());

        returnedTrips.enqueue(new Callback<List<Putovanje>>() {
            @Override
            public void onResponse(Call<List<Putovanje>> call, Response<List<Putovanje>> response) {
                putovanja = response.body();
                Log.i(TAG, "trips "+ putovanja);
                if (putovanja == null){
                    textViewInfo.setVisibility(View.VISIBLE);
                    textViewInfo.setTypeface(Utils.getFont());
                }
                else {
                    putovanjaKorisnika.addAll(putovanja);
                    setAdapter();
                }
            }
            @Override
            public void onFailure(Call<List<Putovanje>> call, Throwable t) {
                Log.i(TAG, "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        TripItemAdapter listAdapter = new TripItemAdapter(TripHistoryActivity.this, putovanjaKorisnika);
        listViewTripHistory = findViewById(R.id.trip_history_list);
        listViewTripHistory.setAdapter(listAdapter);
    }

}
