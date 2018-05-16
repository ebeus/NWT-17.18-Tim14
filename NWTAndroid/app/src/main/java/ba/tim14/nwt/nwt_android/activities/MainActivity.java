package ba.tim14.nwt.nwt_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.utils.Constants;

public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(() -> {
            SharedPreferencesManager.initialise(this);
           // SharedPreferencesManager.instance().setUsername("");
           // SharedPreferencesManager.instance().setUserPass("");
            SharedPreferencesManager.instance().setLoggedIn(false);
            Log.i("TAG ", "username " + SharedPreferencesManager.instance().getUsername());
            Log.i("TAG ", "pass " + SharedPreferencesManager.instance().getUserPass());

            if(!SharedPreferencesManager.instance().isLoggedIn() && !SharedPreferencesManager.instance().getUsername().equals("")) {
                //Login
                startNewActivityForResult(LoginActivity.class, Constants.LOGIN);
            }
            else {
                //Register
                startNewActivityForResult(LoginActivity.class, Constants.REGISTER);
            }
        }, SPLASH_DISPLAY_LENGTH);

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
    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
