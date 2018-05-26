package ba.tim14.nwt.nwt_android.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.activities.MenuActivity;
import ba.tim14.nwt.nwt_android.activities.TripActivity;
import ba.tim14.nwt.nwt_android.utils.Constants;

public class TripNameDialog extends Dialog implements android.view.View.OnClickListener {

    private Activity activity;
    EditText tripName;

    public TripNameDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_trip_name);

        tripName = findViewById(R.id.editText_trip_name);
        findViewById(R.id.button_exit).setOnClickListener(this);
        findViewById(R.id.button_start_trip).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_exit:
                Intent intentChange = new Intent(activity, TripActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intentChange);
                dismiss();
                break;
            case R.id.button_start_trip:
                Intent startTrip = new Intent(activity, TripActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startTrip.putExtra(Constants.TRIP_NAME, tripName.getText().toString());
                startTrip.putExtra(Constants.STEP, Constants.START_TRIP);
                activity.startActivity(startTrip);
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

}