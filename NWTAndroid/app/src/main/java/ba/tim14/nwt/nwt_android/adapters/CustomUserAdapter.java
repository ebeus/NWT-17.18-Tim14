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
import ba.tim14.nwt.nwt_android.activities.GroupActivity;
import ba.tim14.nwt.nwt_android.activities.TripActivity;
import ba.tim14.nwt.nwt_android.classes.User;
import ba.tim14.nwt.nwt_android.utils.Constants;

public class CustomUserAdapter  extends ArrayAdapter<User> {

    private final Activity context;

    public CustomUserAdapter(Activity context, ArrayList<User> userList) {
        super(context, R.layout.user_list_item, userList);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        User user = getItem(position);
        View rowView = inflater.inflate(R.layout.user_list_item, null, true);

        if (user != null){
            TextView txtUsername = rowView.findViewById(R.id.textView_username);
            txtUsername.setText(user.getUsername());
            ImageButton imageViewPin = rowView.findViewById(R.id.imageViewPin);
            imageViewPin.setOnClickListener(view1 -> {
                Intent startUserTrip = new Intent(new Intent(context, TripActivity.class));
                startUserTrip.putExtra("user", position);
                startUserTrip.putExtra(Constants.STEP, Constants.USERS);
                context.startActivity(startUserTrip);
            });
        }
        return rowView;
    }

}
