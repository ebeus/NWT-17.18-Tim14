package ba.tim14.nwt.nwt_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.adapters.TripItemAdapter;
import ba.tim14.nwt.nwt_android.classes.Trip;

public class TripHistoryActivity extends AppCompatActivity {

    ListView listViewTripHistory;

    ArrayList<Trip> tripList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        fillTripListWithData();
        setAdapter();

    }

    private void setAdapter() {
        TripItemAdapter listAdapter = new TripItemAdapter(TripHistoryActivity.this, tripList);
        listViewTripHistory = findViewById(R.id.trip_history_list);
        listViewTripHistory.setAdapter(listAdapter);
        listViewTripHistory.setOnItemClickListener((parent, view, position, id) -> performOnListClick(position));
    }

    private void performOnListClick(int position) {
        Log.i("TAG","You Clicked at " + tripList.get(position).getDate());
    }

    private void fillTripListWithData() {
        // TODO: 14.05.2018. fake data for now
        tripList.add(new Trip("4.05.2018", "1h 30min", "4"));
        tripList.add(new Trip("9.05.2018", "45min", "2"));
        tripList.add(new Trip("10.05.2018", "30min", "0.7"));
        tripList.add(new Trip("14.05.2018", "1h", "2.1"));
    }

}
