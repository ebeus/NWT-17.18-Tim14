package ba.tim14.nwt.nwt_android.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertNotNull;

public class LoginActivity extends Activity {

    private static String TAG = LoginActivity.class.getSimpleName();

    private TextInputLayout firstNameInputLayout;
    private EditText editTextFirstName;
    private TextInputLayout lastNameInputLayout;
    private EditText editTextLastName;
    private TextInputLayout nameInputLayout;
    private EditText editTextName;
    private TextInputLayout emailInputLayout;
    private EditText editTextEmail;
    private TextInputLayout passInputLayout;
    private EditText editTextPass;
    private TextInputLayout passAgainInputLayout;
    private EditText editTextPassAgain;

    Korisnik korisnikProvjera = new Korisnik();

    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            result = getIntent().getIntExtra(Constants.STEP, 0);
            if(result == Constants.LOGIN && !SharedPreferencesManager.instance().isLoggedIn()){
                setContentView(R.layout.activity_login);
                ((TextView)findViewById(R.id.textView_title)).setTypeface(Utils.getFont());

                setUsernameAndPassLogin();
                setListenersUsernameAndPass();
                findViewById(R.id.sign_in_button).setOnClickListener(view -> login());
            }
            else {
                if(result == Constants.REGISTER) {
                    setContentView(R.layout.layout_register);
                    ((TextView)findViewById(R.id.textView_title)).setTypeface(Utils.getFont());

                    setRegisterAndChangeViews();
                    setListenersUsernameAndPass();
                    setListenersRegisterAndChange();
                    findViewById(R.id.action_button_register).setOnClickListener(view -> register());
                }
                else if (result == Constants.SETTINGS_CHANGE){
                    setContentView(R.layout.layout_change_user);
                    ((TextView)findViewById(R.id.textView_title)).setTypeface(Utils.getFont());

                    setRegisterAndChangeViews();
                    setParamsChange();
                    setListenersUsernameAndPass();
                    setListenersRegisterAndChange();
                    findViewById(R.id.action_button_change).setOnClickListener(view -> change());
                }
            }
        }
    }

    private void setParamsChange() {
        editTextFirstName.setText(SharedPreferencesManager.instance().getUserFirstName());
        editTextLastName.setText(SharedPreferencesManager.instance().getUserLastName());
        editTextName.setText(SharedPreferencesManager.instance().getUsername());
        editTextEmail.setText(SharedPreferencesManager.instance().getUserEmail());
        editTextPass.setText(SharedPreferencesManager.instance().getUserPass());
        editTextPassAgain.setText(SharedPreferencesManager.instance().getUserPass());
    }

    private void setRegisterAndChangeViews() {
        firstNameInputLayout = findViewById(R.id.til_first_name);
        editTextFirstName = findViewById(R.id.first_name);
        lastNameInputLayout = findViewById(R.id.til_last_name);
        editTextLastName = findViewById(R.id.last_name);
        nameInputLayout = findViewById(R.id.til_username);
        editTextName = findViewById(R.id.username);
        passInputLayout = findViewById(R.id.til_pass);
        editTextPass = findViewById(R.id.password);
        passAgainInputLayout = findViewById(R.id.til_pass_again);
        editTextPassAgain = findViewById(R.id.password_again);
        emailInputLayout = findViewById(R.id.til_email);
        editTextEmail = findViewById(R.id.email);
    }

    void setUsernameAndPassLogin(){
        nameInputLayout = findViewById(R.id.til_email);
        editTextName = findViewById(R.id.email_or_username);
        passInputLayout = findViewById(R.id.til_pass);
        editTextPass = findViewById(R.id.password);
    }

    private void login() {
        if(validLogin()) {
            SharedPreferencesManager.instance().setLoggedIn(true);
            Intent returnIntent = new Intent();
            setResult(Constants.VALID, returnIntent);
            finish();
        }
    }

    private void register() {
        if(valid()) {
            addRegisterAndChangeParams();
            saveUserInDB();
        }
    }

    private void change() {
        if(valid()) {
            addRegisterAndChangeParams();
            updateUserInDB();

        }
    }

    private void addRegisterAndChangeParams() {
        SharedPreferencesManager.instance().setFirstName(editTextFirstName.getText().toString());
        SharedPreferencesManager.instance().setLastName(editTextLastName.getText().toString());
        SharedPreferencesManager.instance().setUsername(editTextName.getText().toString());
        SharedPreferencesManager.instance().setUserEmail(editTextEmail.getText().toString());
        SharedPreferencesManager.instance().setUserPass(editTextPass.getText().toString());
        SharedPreferencesManager.instance().setUserGroupId(0L);
        SharedPreferencesManager.instance().setUserTypeId(1L);
    }

    private void setListenersUsernameAndPass() {
        editTextName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputLayout.setErrorEnabled(false);
            }
        });
        editTextPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passInputLayout.setErrorEnabled(false);
            }
        });
    }

    private void setListenersRegisterAndChange() {
        editTextFirstName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstNameInputLayout.setErrorEnabled(false);
            }
        });
        editTextLastName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastNameInputLayout.setErrorEnabled(false);
            }
        });
        editTextEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailInputLayout.setErrorEnabled(false);
            }
        });
        editTextPassAgain.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passAgainInputLayout.setErrorEnabled(false);
            }
        });
    }

    private boolean validLogin() {
        boolean valid = true;
        if (!validNameLogin())
            valid = false;
        if (!validPassLogin())
            valid = false;
        return valid;
    }

    private boolean valid() {
        boolean valid = true;
        if (!validFirstName())
            valid = false;
        if (!validLastName())
            valid = false;
        if (!validUserName())
            valid = false;
        if (!validPass())
            valid = false;
        if (!validEmail())
            valid = false;
        if(!validPassAgain())
            valid = false;
        return valid;
    }

    @SuppressLint("StaticFieldLeak")
    private boolean validNameLogin() {
        try {
            String nameOrEmailLogin = editTextName.getText().toString();
            if (nameOrEmailLogin.length() > 2 && nameOrEmailLogin.length()< 20 ) {
                final boolean[] check = {false};
//                new Thread( () -> {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Log.i(TAG, "name: " + nameOrEmailLogin);
                        korisnikProvjera = getUserWithUserName(nameOrEmailLogin);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (nameOrEmailLogin.equals(korisnikProvjera.getUserName())) {
                            //if (nameOrEmail.equals(SharedPreferencesManager.instance().getUsername())) {
                            //SharedPreferencesManager.instance().setId(korisnikProvjera.getId());
                            //SharedPreferencesManager.instance().setUsername(nameOrEmailLogin);
                            //SharedPreferencesManager.instance().setParamsForUser(korisnikProvjera);
                            SharedPreferencesManager.instance().setParamsForUser(korisnikProvjera);

                            Log.i(TAG, "name: " + nameOrEmailLogin);
                            check[0] = true;
                        }
                        return null;
                    }
                }.execute();

                Thread.sleep(1000);
//                });
                if(check[0]){
                    Log.i(TAG, "validUsername: " + check[0] + " " + korisnikProvjera.getUserName());
                    SharedPreferencesManager.instance().setParamsForUser(korisnikProvjera);
                }

                return check[0];

            } else{
                nameInputLayout.setError(getText(R.string.error_email_or_username_short));
                return false;
            }
           /* nameOrEmailLogin = editTextName.getText().toString();


            korisnikProvjera = getUserWithUserName(nameOrEmailLogin);
            if (nameOrEmailLogin.equals(korisnikProvjera.getUserName())) {
                //if (nameOrEmail.equals(SharedPreferencesManager.instance().getUsername())) {
                //SharedPreferencesManager.instance().setId(korisnikProvjera.getId());
                //SharedPreferencesManager.instance().setUsername(nameOrEmailLogin);
                //SharedPreferencesManager.instance().setParamsForUser(korisnikProvjera);
                SharedPreferencesManager.instance().setParamsForUser(korisnikProvjera);

                Log.i(TAG, "name: " + nameOrEmailLogin);
                return true;
            }
            else if(nameOrEmailLogin.equals(korisnikProvjera.getEmail())){
                //SharedPreferencesManager.instance().setUserEmail(nameOrEmailLogin);
                Log.i(TAG, "email: " + nameOrEmailLogin);
                SharedPreferencesManager.instance().setParamsForUser(korisnikProvjera);
                return true;
            }
            else{
                nameInputLayout.setError(getText(R.string.error_username_login));
                return false;
            }*/
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    private boolean validPassLogin() {
        try {
            String pass = editTextPass.getText().toString();
            if (pass.equals(korisnikProvjera.getPassword())) {
            //if (pass.equals(SharedPreferencesManager.instance().getUserPass())) {
                Log.i(TAG, "pass: " + pass);
                SharedPreferencesManager.instance().setUserPass(pass);
                return true;
            } else{
                passInputLayout.setError(getText(R.string.error_pass_login));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    private boolean validFirstName() {
        try {
            String firstName = editTextFirstName.getText().toString();
            Pattern pattern = Pattern.compile(Utils.NAME_REGEX);
            if (firstName.length() > 2 && firstName.length()< 51 && pattern.matcher(firstName).find()) {
                SharedPreferencesManager.instance().setFirstName(firstName);
                Log.i(TAG, "firstName: " + firstName);
                return true;
            } else{
                firstNameInputLayout.setError(getText(R.string.error_first_name));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }
    private boolean validLastName() {
        try {
            String lastName = editTextLastName.getText().toString();
            Pattern pattern = Pattern.compile(Utils.NAME_REGEX);
            if (lastName.length() > 2 && lastName.length()< 51 && pattern.matcher(lastName).find()) {
                SharedPreferencesManager.instance().setLastName(lastName);
                Log.i(TAG, "lastName: " + lastName);
                return true;
            } else{
                lastNameInputLayout.setError(getText(R.string.error_last_name));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private boolean validUserName() {
        try {
            String userName = editTextName.getText().toString();
            if (userName.length() > 2 && userName.length()< 20 ) {
                final boolean[] a = {false};
//                new Thread( () -> {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Log.i(TAG, "name: " + userName);
                        boolean doesItExist=doesUserWithUserNameExist(userName);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(!doesItExist){
                            Log.i(TAG, "userExists: false" );
                            SharedPreferencesManager.instance().setUsername(userName);
                            a[0] =true;
                        }
                        else {
                            Log.i(TAG, "userExists: true" );
                            runOnUiThread(() -> nameInputLayout.setError(getText(R.string.error_username_exists)));
                            a[0]=false;
                        }
                        return null;
                    }
                }.execute();

                Thread.sleep(1000);
//                });
                if(a[0]){
                    Log.i(TAG, "validUsername: " + a[0] + userName);
                    SharedPreferencesManager.instance().setUsername(userName);
                }

                return a[0];

            } else{
                nameInputLayout.setError(getText(R.string.error_email_or_username_short));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    public boolean doesUserWithUserNameExist(String userName) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Boolean userExists = false;
        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithUsernameExist(userName);

        try {
            Response<ResponseBody> response = dobijeniKorisnik.execute();
            userExists = Boolean.valueOf(response.body().string());
            Log.i( TAG, "doesUserWithUserNameExist - Exists:  "+ userExists);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    private boolean validPass() {
        try {
            String pass = editTextPass.getText().toString();
            if (pass.length() >= 5) {
                Log.i(TAG, "pass: " + pass);
                SharedPreferencesManager.instance().setUserPass(pass);
                return true;
            } else{
                passInputLayout.setError(getText(R.string.error_invalid_password));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    private boolean validEmail() {
        try {
            String email = editTextEmail.getText().toString();
            Pattern pattern = Pattern.compile(Utils.EMAIL_REGEX);
            if (pattern.matcher(email).find()) {
                Log.i(TAG, "email: " + email);
                SharedPreferencesManager.instance().setUserEmail(email);
                return true;
            } else{
                emailInputLayout.setError(getText(R.string.error_invalid_email));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    private boolean validPassAgain() {
        try {
            String passAgain = editTextPassAgain.getText().toString();
            if (passAgain.equals(editTextPass.getText().toString())) {
                Log.i(TAG, "passAgain: " + passAgain);
                return true;
            } else{
                passAgainInputLayout.setError(getText(R.string.error_invalid_password_again));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    public void saveUserInDB() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        LocatorService locatorService = retrofit.create(LocatorService.class);
        Korisnik korisnik = SharedPreferencesManager.instance().getParamsForUser();
//        korisnik.setFirstName(SharedPreferencesManager.instance().getUserFirstName());
//        korisnik.setLastName(SharedPreferencesManager.instance().getUserLastName());
//        korisnik.setUserName(SharedPreferencesManager.instance().getUsername());
//        korisnik.setPassword(SharedPreferencesManager.instance().getUserPass());
//        korisnik.setEmail(SharedPreferencesManager.instance().getUserEmail());
//        korisnik.setUserTypeId(1L);
//        korisnik.setUserGroupId(0L);

        String abc=SharedPreferencesManager.instance().getUsername();
        System.out.println("ABC: -------------: " + abc);
        korisnik.setUserName(abc);

        Call<ResponseBody> addedUser=locatorService.add(korisnik.getFirstName(),korisnik.getLastName(),korisnik.getUserName(),korisnik.getPassword(),korisnik.getEmail(),korisnik.getUserTypeId(),korisnik.getUserGroupId());

        Log.i(TAG, "ADDing user" + korisnik);
        addedUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()) {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        String message=jsonObject.get("message").toString();
                        Log.i(TAG, "addedUser - Response string: " + message);
                        finishRegisterUser();
                    }
                    else {

                        Log.i(TAG, "error response" + response);
                        Log.i(TAG, "error response body" + response.errorBody());
                        String errorResponse = response.errorBody().string();
                        System.out.println("asdughasuidgasuidg" + errorResponse);
                        Log.i(TAG, errorResponse);
//                        JSONObject jsonObject = new JSONObject(errorResponse);
//                        String message=jsonObject.get("message").toString();
//                        Log.i(TAG, "addedUser - Response string: " + message);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG,"IO/JSOn Exception: ",e);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "addedUser - Nesto nije okej:  " + t.toString());
            }
        });
    }

    private void finishRegisterUser() {
        Intent returnIntent = new Intent();
        setResult(Constants.VALID, returnIntent);
        finish();
    }


    public void updateUserInDB() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Korisnik korisnik = SharedPreferencesManager.instance().getParamsForUser();

//        korisnik.setFirstName(SharedPreferencesManager.instance().getUserFirstName());
//        korisnik.setLastName(SharedPreferencesManager.instance().getUserLastName());
//        korisnik.setUserName(SharedPreferencesManager.instance().getUsername());
//        korisnik.setPassword(SharedPreferencesManager.instance().getUserPass());
//        korisnik.setEmail(SharedPreferencesManager.instance().getUserEmail());
//        korisnik.setUserTypeId(1L);
//        korisnik.setUserGroupId(0L);
        Call<ResponseBody> updateUser = locatorService.update(korisnik.getId(),korisnik.getFirstName(),korisnik.getLastName(),korisnik.getUserName(),korisnik.getPassword(),korisnik.getEmail(),korisnik.getUserTypeId(),korisnik.getUserGroupId());
        updateUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()) {
                        String responseString = response.body().string();
                        JSONObject jsonObject=new JSONObject(responseString);
                        String message=jsonObject.get("message").toString();
                        Log.i(TAG, "updateUserInDB - Response string: " + message);
                        finishUpdateUser();
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        JSONObject jsonObject=new JSONObject(errorResponse);
                        String message=jsonObject.get("message").toString();
                        Log.i(TAG, "updateUserInDB - Response string: " + message);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG,"IO/JSOn Exception: ",e);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "updateUserInDB - Nesto nije okej:  " + t.toString());
            }
        });
    }

    private void finishUpdateUser() {
        Intent returnIntent = new Intent();
        setResult(Constants.VALID, returnIntent);
        finish();
    }


    /**
     * onBackPressed returns to other activity
     */
    @Override
    public void onBackPressed() {
        if(result == Constants.SETTINGS_CHANGE) {
            Intent goBack = new Intent(this, MenuActivity.class);
            goBack.putExtra(Constants.STEP,0);
            startActivity(goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else{
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    public Korisnik getUserWithUserName(String nameOrEmailLogin) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Korisnik> dobijeniKorisnik = locatorService.getUserWithUserName(nameOrEmailLogin);

        try {
            Response<Korisnik> response = dobijeniKorisnik.execute();
            korisnikProvjera = response.body();
            if(korisnikProvjera != null){
                Log.i( TAG, "doesUserWithUserNameExist - Provjera:  "+ korisnikProvjera.getUserName());
            }
            else
                Log.i( TAG, "doesUserWithUserNameExist - Provjera:  Nema korisnika"+ nameOrEmailLogin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return korisnikProvjera;
    }

}
