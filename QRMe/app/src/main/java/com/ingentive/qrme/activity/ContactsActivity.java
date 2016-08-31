package com.ingentive.qrme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.UserInfoTable;
import com.ingentive.qrme.activeandroid.UsersTable;
import com.ingentive.qrme.adapter.UserIfoAdapter;
import com.ingentive.qrme.adapter.UsersExpListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends Activity {

    private Toolbar mToolbar;
    private ImageView ivBack;
    private ExpandableListView expandUsers;
    private List<UsersTable> usersTableList;
    private List<UsersTable> usersChildList;
    private List<UsersTable> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        expandUsers = (ExpandableListView) findViewById(R.id.expand_list_users);

        usersTableList = new ArrayList<UsersTable>();
        usersTableList = new Select().from(UsersTable.class).execute();
        usersList = new ArrayList<UsersTable>();
        for (int i = 0; i < usersTableList.size(); i++) {
            UsersTable usersTable = new UsersTable();
            usersTable = usersTableList.get(i);
            usersChildList = new ArrayList<UsersTable>();
            usersChildList.add(usersTable);
            usersTable.setUsersChildList(usersChildList);
            usersList.add(usersTable);
        }

        UsersExpListAdapter listAdapter = new UsersExpListAdapter(
                ContactsActivity.this, usersList);
        // setting list adapter
        expandUsers.setAdapter(listAdapter);

//                for (int i = 0; i < listAdapter.getGroupCount(); i++)
//                    expandUsers.expandGroup(i);

        expandUsers.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // expandUsers.expandGroup(groupPosition);
                return false;
            }
        });


        mToolbar = (Toolbar) findViewById(R.id.toolbar_contacts);
        ivBack = (ImageView) mToolbar.findViewById(R.id.iv_back_contacts);

        ivBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ivBack.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        ivBack.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        ivBack.getDrawable().clearColorFilter();
                        ivBack.invalidate();
                        finish();
                        break;
                    }
                }
                return true;
            }
        });
    }
}
