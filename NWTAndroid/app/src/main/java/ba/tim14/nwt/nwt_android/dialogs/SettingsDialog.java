package ba.tim14.nwt.nwt_android.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.activities.MenuActivity;
import ba.tim14.nwt.nwt_android.utils.Constants;

/**
 *
 * Created by ena on 5/16/18.
 */

public class SettingsDialog extends Dialog implements android.view.View.OnClickListener {

    private Activity activity;

    public SettingsDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_settings);

        findViewById(R.id.layout_settings_change).setOnClickListener(this);
        findViewById(R.id.layout_settings_sign_out).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_settings_change:
                Intent intentChange = new Intent(activity, MenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentChange.putExtra(Constants.STEP, Constants.SETTINGS_CHANGE);
                activity.startActivity(intentChange);
                dismiss();
                break;
            case R.id.layout_settings_sign_out:
                Intent intentSignOut = new Intent(activity, MenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentSignOut.putExtra(Constants.STEP, Constants.SETTINGS_SIGN_OUT);
                activity.startActivity(intentSignOut);
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

}