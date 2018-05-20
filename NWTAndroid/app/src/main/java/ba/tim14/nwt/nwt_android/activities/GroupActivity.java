package ba.tim14.nwt.nwt_android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.adapters.CustomUserAdapter;
import ba.tim14.nwt.nwt_android.classes.User;
import ba.tim14.nwt.nwt_android.utils.Utils;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = GroupActivity.class.getSimpleName();

    ListView listViewUsers;
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        users = Utils.getPopulatedListWithUsers();
        Utils.getKorisnike();
        setAdapter();
    }

    private void setAdapter() {
        CustomUserAdapter listAdapter = new CustomUserAdapter(GroupActivity.this, users);
        listViewUsers = findViewById(R.id.user_list);
        listViewUsers.setAdapter(listAdapter);
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> performOnListClick(position));
    }

    private void performOnListClick(int position) {
        Log.i(TAG,"You Clicked at " + users.get(position).getUsername());
    }

}
