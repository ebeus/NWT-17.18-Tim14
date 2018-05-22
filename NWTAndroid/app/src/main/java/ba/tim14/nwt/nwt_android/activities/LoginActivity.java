package ba.tim14.nwt.nwt_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.utils.Constants;
import ba.tim14.nwt.nwt_android.utils.Utils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity {

    private static String TAG = LoginActivity.class.getSimpleName();

    private TextInputLayout nameInputLayout;
    private EditText editTextName;
    private TextInputLayout emailInputLayout;
    private EditText editTextEmail;
    private TextInputLayout passInputLayout;
    private EditText editTextPass;
    private TextInputLayout passAgainInputLayout;
    private EditText editTextPassAgain;

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
                    setListenersEmailAndPassAgain();
                    findViewById(R.id.action_button_register).setOnClickListener(view -> register());
                }
                else if (result == Constants.SETTINGS_CHANGE){
                    setContentView(R.layout.layout_change_user);
                    ((TextView)findViewById(R.id.textView_title)).setTypeface(Utils.getFont());

                    setRegisterAndChangeViews();
                    setParamsChange();
                    setListenersUsernameAndPass();
                    setListenersEmailAndPassAgain();
                    findViewById(R.id.action_button_change).setOnClickListener(view -> change());
                }
            }
        }
    }

    private void setParamsChange() {
        editTextName.setText(SharedPreferencesManager.instance().getUsername());
        editTextEmail.setText(SharedPreferencesManager.instance().getUserEmail());
        editTextPass.setText(SharedPreferencesManager.instance().getUserPass());
        editTextPassAgain.setText(SharedPreferencesManager.instance().getUserPass());
    }

    private void setRegisterAndChangeViews() {
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
            SharedPreferencesManager.instance().setUsername(editTextName.getText().toString());
            SharedPreferencesManager.instance().setUserEmail(editTextEmail.getText().toString());
            SharedPreferencesManager.instance().setUserPass(editTextPass.getText().toString());
            Intent returnIntent = new Intent();
            saveinDBKorisika();
            setResult(Constants.VALID, returnIntent);
            finish();
        }
    }

    private void saveinDBKorisika() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        LocatorService locatorService = retrofit.create(LocatorService.class);
        Korisnik korisnik = new Korisnik();
        korisnik.setUserName(SharedPreferencesManager.instance().getUsername());
        korisnik.setPassword(SharedPreferencesManager.instance().getUserPass());
        korisnik.setEmail(SharedPreferencesManager.instance().getUserEmail());
        locatorService.add(korisnik);
    }

    private void change() {
        if(valid()) {
            SharedPreferencesManager.instance().setUsername(editTextName.getText().toString());
            SharedPreferencesManager.instance().setUserEmail(editTextEmail.getText().toString());
            SharedPreferencesManager.instance().setUserPass(editTextPass.getText().toString());
            Intent returnIntent = new Intent();
            setResult(Constants.VALID, returnIntent);
            finish();
        }
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

    private void setListenersEmailAndPassAgain() {
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
        if (!validName())
            valid = false;
        if (!validPass())
            valid = false;
        if (!validEmail())
            valid = false;
        if(!validPassAgain())
            valid = false;
        return valid;
    }

    private boolean validNameLogin() {
        try {
            String nameOrEmail = editTextName.getText().toString();
            if (nameOrEmail.equals(SharedPreferencesManager.instance().getUsername())) {
                Log.i(TAG, "name: " + nameOrEmail);
                return true;
            }
            else if(nameOrEmail.equals(SharedPreferencesManager.instance().getUserEmail())){
                Log.i(TAG, "email: " + nameOrEmail);
                return true;
            }
            else{
                nameInputLayout.setError(getString(R.string.error_username_login));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    private boolean validPassLogin() {
        try {
            String pass = editTextPass.getText().toString();
            if (pass.equals(SharedPreferencesManager.instance().getUserPass())) {
                Log.i(TAG, "pass: " + pass);
                SharedPreferencesManager.instance().setUserPass(pass);
                return true;
            } else{
                passInputLayout.setError(getString(R.string.error_pass_login));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    private boolean validName() {
        try {
            String name = editTextName.getText().toString();
            if (name.length() > 2 && name.length()< 51) {
                Log.i(TAG, "name: " + name);
                SharedPreferencesManager.instance().setUsername(name);
                return true;
            } else{
                nameInputLayout.setError(getString(R.string.error_email_or_username_short));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
    }

    private boolean validPass() {
        try {
            String pass = editTextPass.getText().toString();
            if (pass.length() >= 5) {
                Log.i(TAG, "pass: " + pass);
                SharedPreferencesManager.instance().setUserPass(pass);
                return true;
            } else{
                passInputLayout.setError(getString(R.string.error_invalid_password));
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
                emailInputLayout.setError(getString(R.string.error_invalid_email));
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
                passAgainInputLayout.setError(getString(R.string.error_invalid_password_again));
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, Constants.EXCEPTION_STRING + e);
            return false;
        }
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
}
