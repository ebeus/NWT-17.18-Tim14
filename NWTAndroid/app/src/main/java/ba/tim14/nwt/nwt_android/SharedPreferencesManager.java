package ba.tim14.nwt.nwt_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesManager {

    private static String TAG = SharedPreferencesManager.class.getSimpleName();

    private static final String PREF_NAME = "ba.tim14.nwt.prefs";
    private static final int PREF_MODE = MODE_PRIVATE;

    private static final String USERNAME_KEY = "username_key";
    private static final String EMAIL_KEY = "email_key";
    private static final String USER_PASS_KEY = "user_pass_key";
    private static final String LOGGED_IN_KEY = "logged_in_key";

    private static SharedPreferencesManager sharedPreferencesManager;
    private SharedPreferences sharedPreferences = null;

    private SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
    }

    /**
     * Retrieve the singleton instance of SharedPreferencesManager
     * @return singleton instance
     */
    public static SharedPreferencesManager instance() {
        if (null == sharedPreferencesManager) {
            Log.e(TAG, "Morate initialise SharedPreferencesManager da bi koristili instancu.");
        }
        return sharedPreferencesManager;
    }

    public static void initialise(Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }

    public void setUserEmail(String userEmail) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL_KEY, userEmail);
        editor.apply();
    }

    public void setUserPass(String userPass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_PASS_KEY, userPass);
        editor.apply();
    }

    public String getUsername() {
        if(!sharedPreferences.getString(USERNAME_KEY, "").equals("")){
            return sharedPreferences.getString(USERNAME_KEY, "");
        }
        return "";
    }

    public String getUserEmail() {
        if(!sharedPreferences.getString(EMAIL_KEY, "").equals("")){
            return sharedPreferences.getString(EMAIL_KEY, "");
        }
        return "";
    }

    public String getUserPass() {
        if(!sharedPreferences.getString(USER_PASS_KEY, "").equals("")){
            return sharedPreferences.getString(USER_PASS_KEY, "");
        }
        return "";
    }

    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_IN_KEY, loggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() { return sharedPreferences.getBoolean(LOGGED_IN_KEY,false); }

}
