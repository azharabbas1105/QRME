package com.ingentive.qrme.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.UserInfoTable;
import com.ingentive.qrme.adapter.UserIfoAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {

    private Toolbar mToolbar;
    private ImageView ivBack;
    private ListView mListView;
    private UserInfoTable userInfoTable;
    private UserIfoAdapter mAdapter;
    private List<String> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        ivBack=(ImageView)mToolbar.findViewById(R.id.iv_back_profile);
        mListView = (ListView) findViewById(R.id.lv_profile);
        setmAdapter();

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

    private void setmAdapter() {
        userInfoTable = new UserInfoTable();
        userInfoTable = new Select().from(UserInfoTable.class).executeSingle();
        if (userInfoTable != null) {
            infoList = new ArrayList<>();
            infoList.add(userInfoTable.getUserName());
            infoList.add(userInfoTable.getUserEmail());
            infoList.add(userInfoTable.getUserFacebook());
            infoList.add(userInfoTable.getUserTwitter());
            infoList.add(userInfoTable.getUserLinkedin());
            infoList.add(userInfoTable.getUserPhone());
            mAdapter = new UserIfoAdapter(ProfileActivity.this, R.layout.custom_row_user_info, infoList,userInfoTable);
            mListView.setAdapter(mAdapter);
        }
    }
}
