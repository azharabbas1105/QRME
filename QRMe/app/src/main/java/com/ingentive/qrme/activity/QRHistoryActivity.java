package com.ingentive.qrme.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.QRHistoryTable;
import com.ingentive.qrme.adapter.QRHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class QRHistoryActivity extends Activity {

    private Toolbar mToolbar;
    private ImageView ivBack;
    private List<QRHistoryTable> qrLit;
    private QRHistoryAdapter qrHistoryAdapter;
    private ListView mListView;
    //public static String videoUrl=null;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrhistory);

        pDialog = new ProgressDialog(QRHistoryActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_qr_history);
        ivBack=(ImageView)mToolbar.findViewById(R.id.iv_back_qr_history);
        mListView=(ListView)findViewById(R.id.listview_qr_history);

        qrLit=new ArrayList<>();
        qrLit=new Select().all().from(QRHistoryTable.class).execute();

        pDialog.dismiss();
        qrHistoryAdapter=new QRHistoryAdapter(QRHistoryActivity.this,R.layout.custom_row_qr_history,qrLit);
        mListView.setAdapter(qrHistoryAdapter);

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                Intent intent = new Intent(QRHistoryActivity.this, VideoViewActivity.class);
//                intent.putExtra("url", qrLit.get(position).getVideoUrl());
//                //videoUrl=qrLit.get(position).getVideoUrl();
//                intent.putExtra("intent_", "qr");
//                startActivity(intent);
//            }
//        });

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
