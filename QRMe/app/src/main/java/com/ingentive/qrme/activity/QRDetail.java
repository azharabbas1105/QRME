package com.ingentive.qrme.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.ingentive.qrme.R;

public class QRDetail extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvQRDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdetail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_qr_detail);
        setSupportActionBar(mToolbar);

        tvQRDetail = (TextView) findViewById(R.id.tv_qr_detail);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
        } else {
            tvQRDetail.setText(extras.getString("STRING_I_NEED"));
        }
    }
}
