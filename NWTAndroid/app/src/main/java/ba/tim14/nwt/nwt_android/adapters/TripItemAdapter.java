package ba.tim14.nwt.nwt_android.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.activities.TripActivity;
import ba.tim14.nwt.nwt_android.classes.Trip;
import ba.tim14.nwt.nwt_android.utils.Constants;

public class TripItemAdapter extends ArrayAdapter<Trip> {

    private final Activity context;

    private Trip trip = null;

    public TripItemAdapter(Activity context, ArrayList<Trip> tripList) {
        super(context, R.layout.trip_history_list_item, tripList);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        trip = getItem(position);
        View rowView = inflater.inflate(R.layout.trip_history_list_item, null, true);

        if (trip != null){
            TextView txtDate = rowView.findViewById(R.id.textView_date);
            txtDate.setText(trip.getTripDates());

            TextView txtDuration = rowView.findViewById(R.id.textView_duration);
            txtDuration.setText(getDurationString());

            TextView txtKilometers = rowView.findViewById(R.id.textView_kilometers);
            txtKilometers.setText(trip.getDistance());

            ImageButton imageButtonShowOnMap = rowView.findViewById(R.id.imageButtonShowOnMap);
            imageButtonShowOnMap.setOnClickListener(view1 -> {
                Intent showMyTripOnMap = new Intent(context, TripActivity.class);
                showMyTripOnMap.putExtra(Constants.STEP, Constants.MY_TRIP_HISTORY);
                showMyTripOnMap.putExtra(Constants.MY_TRIP_HISTORY_POSITION, position);
                context.startActivity(showMyTripOnMap);
            });
        }
        return rowView;
    }

    private String getDurationString() {
        StringBuilder resultDurationString = new StringBuilder();
        String durations[] = trip.getDurationPeriod();
        String params[] = new String[]{"days", "h", "min", "sec"};
        int zero = 0;
        for (int i = 0; i < durations.length; i++) {
            if(!"0".equals(durations[i])){
                resultDurationString.append(durations[i]).append(" ").append(params[i]).append(" ");
            }
            else zero++;
            if (zero == 4)  resultDurationString.append(durations[3]).append(" ").append(params[3]).append(" ");
        }

        return resultDurationString.toString();
    }

}