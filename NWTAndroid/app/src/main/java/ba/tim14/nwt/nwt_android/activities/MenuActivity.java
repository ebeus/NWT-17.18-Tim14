package ba.tim14.nwt.nwt_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.classes.RowItem;
import ba.tim14.nwt.nwt_android.classes.User;
import ba.tim14.nwt.nwt_android.utils.Constants;

public class MenuActivity extends Activity implements View.OnClickListener{

    //LinearLayout dropDownMenuItems;
    public static String[] titles = new String[] { };
    public static final Integer[] images = {R.drawable.ic_user_settings, R.drawable.ic_logout };

    ArrayList<User> users = new ArrayList<>();
    ArrayList<RowItem> rowItems = new ArrayList<>();
    Spinner spinner;

    ImageView btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SharedPreferencesManager.instance().isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else {
            setContentView(R.layout.activity_menu);
            //dropDownMenuItems = findViewById(R.id.layout_settings);

            //setSpinnerItems();
            btnSettings = findViewById(R.id.btn_settings);
            //btnSettings.setOnClickListener(view -> clickOnSpinner());
            btnSettings.setOnClickListener(view -> signOut());

            setButtonActions();
        }
    }

    private void setButtonActions() {
        findViewById(R.id.layout_button_trip).setOnClickListener(this);
        findViewById(R.id.layout_button_trip_list).setOnClickListener(this);
        findViewById(R.id.layout_button_group).setOnClickListener(this);
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
                Intent startMyGroup = new Intent(new Intent(this, GroupActivity.class));
                startActivity(startMyGroup);
                break;
        }
    }

    private void startTrip() {
        //if connected then start trip else tell to turn connection on
        Intent startTrip = new Intent(new Intent(this, TripActivity.class));
        startTrip.putExtra(Constants.STEP, Constants.MY_TRIP);
        startActivity(startTrip);
    }

    private void signOut() {
        SharedPreferencesManager.instance().setLoggedIn(false);
        startNewActivityForResult(MainActivity.class, Constants.LOGIN);
    }

    /*public void verticalDropDownIconMenu() {
       // if (dropDownMenuItems.getVisibility() == View.VISIBLE) {
           // dropDownMenuItems.setVisibility(View.INVISIBLE);
            findViewById(R.id.layout_settings_change).setOnClickListener(null);
            findViewById(R.id.layout_settings_sign_out).setOnClickListener(null);
        //} else {
       //     dropDownMenuItems.setVisibility(View.VISIBLE);
            disableButtons();
            findViewById(R.id.layout_settings_change).setOnClickListener(view -> startNewActivityForResult(LoginActivity.class, Constants.CHANGE));
            findViewById(R.id.layout_settings_sign_out).setOnClickListener(view -> signOut());
        //}
    }*/


    private void startNewActivityForResult(Class c, int step){
        Intent intent = new Intent(this, c).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.STEP, step);
        startActivityForResult(intent, step);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.CHANGE:
                if(resultCode == Constants.VALID){
                    finish();
                    startActivity(getIntent());
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
/*
    private void clickOnSpinner() {
        spinner.setVisibility(View.VISIBLE);
        spinner.performClick();
    }

    private void setSpinnerItems() {
        titles = new String[] {getString(R.string.change_str), getString(R.string.sign_out_str) };
        for(int i = 0; i < titles.length; i++){
            RowItem item = new RowItem(titles[i],images[i]);
            rowItems.add(item);
        }
        spinner = findViewById(R.id.spinner_settings);
        spinner.setVerticalScrollBarEnabled(false);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this,R.layout.spinner_item, R.id.title, rowItems) {};
        spinner.setAdapter(adapter);
    }*/

}
