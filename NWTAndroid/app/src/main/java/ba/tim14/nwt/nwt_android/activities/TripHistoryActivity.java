package ba.tim14.nwt.nwt_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.adapters.TripItemAdapter;
import ba.tim14.nwt.nwt_android.utils.Utils;

import static ba.tim14.nwt.nwt_android.utils.Utils.tripList;

public class TripHistoryActivity extends AppCompatActivity {

    ListView listViewTripHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        if(tripList.isEmpty()){
            TextView textViewInfo = findViewById(R.id.textView_info);
            textViewInfo.setVisibility(View.VISIBLE);
            textViewInfo.setTypeface(Utils.getFont());
        }
        else {
            setAdapter();
        }
    }

    private void setAdapter() {
        TripItemAdapter listAdapter = new TripItemAdapter(TripHistoryActivity.this, tripList);
        listViewTripHistory = findViewById(R.id.trip_history_list);
        listViewTripHistory.setAdapter(listAdapter);
    }

}
