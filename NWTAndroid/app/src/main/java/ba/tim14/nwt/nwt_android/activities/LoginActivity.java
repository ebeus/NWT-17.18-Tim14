package ba.tim14.nwt.nwt_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.SharedPreferencesManager;
import ba.tim14.nwt.nwt_android.utils.Constants;

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
                setUsernameAndPassLogin();
                setListenersUsernameAndPass();
                findViewById(R.id.sign_in_button).setOnClickListener(view -> login());
            }
            else {
                if(result == Constants.REGISTER) {
                    setContentView(R.layout.layout_register);
                    setRegisterAndChangeViews();
                    setListenersUsernameAndPass();
                    findViewById(R.id.action_button_register).setOnClickListener(view -> register());
                }
                else if (result == Constants.CHANGE){
                    setContentView(R.layout.layout_change_user);
                    setRegisterAndChangeViews();
                    setParamsChange();
                    setListenersUsernameAndPass();
                    findViewById(R.id.action_button_change).setOnClickListener(view -> change());
                }
            }
        }
    }

    private void setParamsChange() {
        editTextName.setText(SharedPreferencesManager.instance().getUsername());
        editTextPass.setText(SharedPreferencesManager.instance().getUserPass());
        editTextPassAgain.setText(SharedPreferencesManager.instance().getUserPass());
        editTextEmail.setText("");
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
            SharedPreferencesManager.instance().setUserPass(editTextPass.getText().toString());
            Intent returnIntent = new Intent();
            setResult(Constants.VALID, returnIntent);
            finish();
        }
    }

    private void change() {
        if(valid()) {
            SharedPreferencesManager.instance().setUsername(editTextName.getText().toString());
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
                nameInputLayout.setError(null);
                nameInputLayout.setErrorEnabled(false);
            }
        });
        editTextPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passInputLayout.setError(null);
                passInputLayout.setErrorEnabled(false);
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
        return valid;
    }


    private boolean validNameLogin() {
        try {
            String name = editTextName.getText().toString();
            if (name.equals(SharedPreferencesManager.instance().getUsername())) {
                Log.i(TAG, "name: " + name);
                return true;
            } else{
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

    /**
     * onBackPressed returns to main activity
     */
    @Override
    public void onBackPressed() {
        if(result == Constants.CHANGE) {
            startActivity(new Intent(this, MenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else{
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }
}
