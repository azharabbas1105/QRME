package com.ingentive.qrme.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ingentive.qrme.R;

public class QRListActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private Button btnRecording,btnQRGenerate,btnVideoUpload,btnScanQR;
    private ListView mListView;
    private ProgressDialog pDialog;
    private static final int SELECT_VIDEO = 3;

    private String selectedPath;
    String[] values = new String[]{"Android List View",
            "Adapter implementation",
            "Simple List View In Android",
            "Create List View Android",
            "Android Example",
            "List View Source Code",
            "List View Array Adapter",
            "Android Example List View"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlist);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_qr_list);
        setSupportActionBar(mToolbar);
        mListView = (ListView) findViewById(R.id.lv_qr);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = mListView.getItemAtPosition(position).toString();

                Intent intent = new Intent(getApplication(), QRDetail.class);
                intent.putExtra("STRING_I_NEED", itemValue);
                startActivity(intent);


                // Show Alert
                Toast.makeText(getApplicationContext(), "" + itemValue, Toast.LENGTH_LONG)
                        .show();
            }
        });

        btnRecording = (Button) findViewById(R.id.btn_recoding);
        btnQRGenerate = (Button) findViewById(R.id.btn_create_qr);
        btnVideoUpload = (Button) findViewById(R.id.btn_upload_vidio);
        btnScanQR = (Button) findViewById(R.id.btn_scan_qr);

        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRListActivity.this, ScanQRAactivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnVideoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRListActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        btnRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
//        btnQRGenerate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(QRListActivity.this, GenerateQRCodeActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}

