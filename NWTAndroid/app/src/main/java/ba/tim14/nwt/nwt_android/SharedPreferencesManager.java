package ba.tim14.nwt.nwt_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import ba.tim14.nwt.nwt_android.classes.Korisnik;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesManager {

    private static String TAG = SharedPreferencesManager.class.getSimpleName();

    private static final String PREF_NAME = "ba.tim14.nwt.prefs";
    private static final int PREF_MODE = MODE_PRIVATE;

    private static final String ID_KEY = "id_key";
    private static final String FIRST_NAME_KEY = "first_name_key";
    private static final String LAST_NAME_KEY = "last_name_key";
    private static final String USERNAME_KEY = "username_key";
    private static final String EMAIL_KEY = "email_key";
    private static final String USER_PASS_KEY = "user_pass_key";
    private static final String LOGGED_IN_KEY = "logged_in_key";
    private static final String USER_TYPE_ID_KEY = "user_type_id";
    private static final String USER_GROUP_ID_KEY = "user_group_id";

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

    public void setParamsForUser(Korisnik user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(ID_KEY, user.getId());
        editor.putString(FIRST_NAME_KEY, user.getFirstName());
        editor.putString(LAST_NAME_KEY, user.getLastName());
        editor.putString(USERNAME_KEY, user.getUserName());
        editor.putString(EMAIL_KEY, user.getEmail());
        editor.putString(USER_PASS_KEY, user.getPassword());
        editor.putLong(USER_GROUP_ID_KEY, user.getUserGroupId());
        editor.putLong(USER_TYPE_ID_KEY, user.getUserTypeId());
        editor.apply();
    }

    public void setId(long id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(ID_KEY, id);
        editor.apply();
    }

    public void setFirstName(String firstName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIRST_NAME_KEY, firstName);
        editor.apply();
    }

    public void setLastName(String lastName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_NAME_KEY, lastName);
        editor.apply();
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        System.out.println("SHARED usernameeee" + username);
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

    public void setUserGroupId(long userGroupId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(USER_GROUP_ID_KEY, userGroupId);
        editor.apply();
    }

    public void setUserTypeId(long userTypeId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(USER_TYPE_ID_KEY, userTypeId);
        editor.apply();
    }

    /// Getters


    public Korisnik getParamsForUser() {
        Korisnik korisnik = new Korisnik();
        korisnik.setId(getId());
        korisnik.setFirstName(getUserFirstName());
        korisnik.setLastName(getUserLastName());
        korisnik.setEmail(getUserEmail());
        korisnik.setPassword(getUserPass());
        korisnik.setUserGroupId(getUserGroupId());
        korisnik.setUserTypeId(getUserTypeId());
        return korisnik;
    }

    public long getId() {
        return sharedPreferences.getLong(ID_KEY, 0L);
    }

    public String getUserFirstName() {
        if(!sharedPreferences.getString(FIRST_NAME_KEY, "").equals("")){
            return sharedPreferences.getString(FIRST_NAME_KEY, "");
        }
        return "";
    }

    public String getUserLastName() {
        if(!sharedPreferences.getString(LAST_NAME_KEY, "").equals("")){
            return sharedPreferences.getString(LAST_NAME_KEY, "");
        }
        return "";
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

    public long getUserGroupId() {
        return sharedPreferences.getLong(USER_GROUP_ID_KEY, 0L);
    }

    public long getUserTypeId() {
        return sharedPreferences.getLong(USER_TYPE_ID_KEY, 0L);
    }

    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_IN_KEY, loggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() { return sharedPreferences.getBoolean(LOGGED_IN_KEY,false); }

}
