package ba.tim14.nwt.nwt_android.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;

import static ba.tim14.nwt.nwt_android.utils.Utils.tripList;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String[] permissions = new String[1];

    private static final int PERMISSION_ALL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;

        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(() -> {
            SharedPreferencesManager.initialise(this);

            SharedPreferencesManager.instance().setLoggedIn(false);
            Log.i(TAG, "username " + SharedPreferencesManager.instance().getUsername());
            Log.i(TAG, "pass " + SharedPreferencesManager.instance().getUserPass());

            Utils.setFont(this);
            checkOrRequestPermissions();

        }, SPLASH_DISPLAY_LENGTH);

    }

    private void startApplication() {
        if(!SharedPreferencesManager.instance().isLoggedIn() && SharedPreferencesManager.instance().getUsername().equals("")) {
            //Login
            startNewActivityForResult(LoginActivity.class, Constants.LOGIN);
            tripList = new ArrayList<>();
        }
        else {
            //Register
            tripList = new ArrayList<>();
            startNewActivityForResult(LoginActivity.class, Constants.REGISTER);
        }
    }

    private void checkOrRequestPermissions() {
        if (!hasPermissions(getApplicationContext(), permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        } else {
            startApplication();
        }
    }

    private void startNewActivityForResult(Class c, int step) {
        Intent intent = new Intent(this, c).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.STEP, step);
        startActivityForResult(intent,step);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.LOGIN:
                if(resultCode == Constants.VALID){
                    startActivity(new Intent(this, MenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                break;
            case Constants.REGISTER:
                if(resultCode == Constants.VALID){
                    startNewActivityForResult(LoginActivity.class, Constants.LOGIN);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                if (checkGrantResults(grantResults)) {
                    startApplication();
                } else {
                    // TODO: 11/8/17 change this message!
                    Toast.makeText(this, "Can't proceed without permissions!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkGrantResults(int[] grantResults) {
        if (grantResults.length < 0)
            return false;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
