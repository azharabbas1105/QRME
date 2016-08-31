package com.ingentive.qrme.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ingentive.qrme.R;
import com.ingentive.qrme.video.MediaController;

public class ScanQRAactivity extends Activity {

    private Button scanBtn;
    private TextView formatTxt, contentTxt;
    private Toolbar mToolbar;
    private ImageView ivBack;

    VideoView video_player_view;
    DisplayMetrics dm;
    SurfaceView sur_View;
    MediaController media_Controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_qr_scan);
        //setSupportActionBar(mToolbar);
        scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);
        ivBack=(ImageView)mToolbar.findViewById(R.id.iv_back_qr_scan);

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
        scanBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentIntegrator integrator = new IntentIntegrator(ScanQRAactivity.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setScanningRectangle(1000, 600);
                        integrator.setPrompt("Scan code");
                        integrator.setResultDisplayDuration(0);
                        //integrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
                        integrator.setCameraId(0);  // Use a specific camera of the device
                        integrator.initiateScan();
                    }
                }
        );
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//        if (scanningResult != null) {
//            String scanContent = scanningResult.getContents();
//            String scanFormat = scanningResult.getFormatName();
//            if (scanContent != null && scanFormat != null) {
//
//                String[] separated = scanContent.split(",");
//                String a= separated[0];
//
//                formatTxt.setText("FORMAT: " + scanFormat);
//                contentTxt.setText("CONTENT: " + a);
//
//                Intent i = new Intent(ScanQRAactivity.this,VideoViewActivity.class);
//                i.putExtra("url",a);
//                startActivity(i);
//                finish();
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
//        }
//    }
}
