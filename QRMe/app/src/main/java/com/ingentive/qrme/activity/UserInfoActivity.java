package com.ingentive.qrme.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.activeandroid.query.Select;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.UserInfoTable;

/**
 * Created by PC on 11-04-2016.
 */
public class UserInfoActivity extends AppCompatActivity {

    private Button btnRecording, btnSave;
    private EditText etFacebook, etEmail, etTwitter, etLinkdin, etPhone, etName;
    private Toolbar mToolbar;
    private UserInfoTable userInfoTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        userInfoTable=new UserInfoTable();
        userInfoTable=new Select().from(UserInfoTable.class).executeSingle();
        if (userInfoTable == null) {
            initialize();
        } else {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    protected void initialize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_userinfo);
        setSupportActionBar(mToolbar);
        btnSave = (Button) findViewById(R.id.btn_save);
        etEmail = (EditText) findViewById(R.id.et_email);
        etFacebook = (EditText) findViewById(R.id.et_facebook);
        etTwitter = (EditText) findViewById(R.id.et_twitter);
        etLinkdin = (EditText) findViewById(R.id.et_linkdin);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etName = (EditText) findViewById(R.id.et_name);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty()) {
                    etName.setError("You can't leave this empty.");
                    etName.requestFocus();
                }else if (etEmail.getText().toString().isEmpty()) {
                    etEmail.setError("You can't leave this empty.");
                    etEmail.requestFocus();
                }
                if (!etName.getText().toString().trim().isEmpty() && !etEmail.getText().toString().trim().isEmpty()) {
                    UserInfoTable model=new UserInfoTable();
                    model.userName=etName.getText().toString().trim();
                    model.userFacebook=etFacebook.getText().toString().trim();
                    model.userTwitter=etTwitter.getText().toString().trim();
                    model.userEmail=etEmail.getText().toString().trim();
                    model.userLinkedin=etLinkdin.getText().toString().trim();
                    model.userPhone=etPhone.getText().toString().trim();
                    model.save();
                    Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
