package ba.tim14.nwt.nwt_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.classes.CustomDialogSettingsClass;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;

public class MenuActivity extends Activity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        findViewById(R.id.layout_button_group).setOnClickListener(this);
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
                CustomDialogSettingsClass settingsDialog=new CustomDialogSettingsClass(this);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

}
