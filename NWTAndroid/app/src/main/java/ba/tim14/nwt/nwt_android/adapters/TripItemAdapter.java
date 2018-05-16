package ba.tim14.nwt.nwt_android.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.classes.Trip;

public class TripItemAdapter extends ArrayAdapter<Trip> {

    private final Activity context;

    public TripItemAdapter(Activity context, ArrayList<Trip> tripList) {
        super(context, R.layout.trip_history_list_item, tripList);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        Trip trip = getItem(position);
        View rowView = inflater.inflate(R.layout.trip_history_list_item, null, true);

        if (trip != null){
            TextView txtDate = rowView.findViewById(R.id.textView_date);
            txtDate.setText(trip.getDate());

            TextView txtDuration = rowView.findViewById(R.id.textView_duration);
            txtDuration.setText(trip.getDuration());

            TextView txtKilometers = rowView.findViewById(R.id.textView_kilometers);
            txtKilometers.setText(trip.getKilometers());
        }

        return rowView;
    }

}