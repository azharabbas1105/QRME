package com.ingentive.qrme.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.activeandroid.query.Select;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.QRHistoryTable;
import com.ingentive.qrme.video.MediaController;

public class VideoViewActivity extends AppCompatActivity {

    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private android.widget.MediaController mediaControls;
    String videoPath;
    private Button btnContact;
    private String imagePath=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        myVideoView = (VideoView) findViewById(R.id.video_view);
        btnContact = (Button) findViewById(R.id.btn_contact);

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (btnContact.getText().toString().equals("SHARE")) {
                    intent = new Intent(VideoViewActivity.this, ShareActivity.class);
                    intent.putExtra("image_path",imagePath);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(VideoViewActivity.this, ContactsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            videoPath = (String) b.get("url");
            if (b.get("intent_").toString() != null&&b.get("intent_").toString().equals("scan_activity") ) {
                btnContact.setText("CONTACT");
            } else {
                btnContact.setText("SHARE");
                QRHistoryTable qrHistoryTable = new Select().from(QRHistoryTable.class).where("video_url=?", videoPath).executeSingle();
                if(qrHistoryTable!=null){
                    imagePath=qrHistoryTable.getImagePath();
                }
            }

            if (mediaControls == null) {
                mediaControls = new android.widget.MediaController(VideoViewActivity.this);
            }

            // Find your VideoView in your video_main.xml layout
            // myVideoView = (VideoView) findViewById(R.id.video_view);

            // Create a progressbar
            progressDialog = new ProgressDialog(VideoViewActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            try {
                myVideoView.setMediaController(mediaControls);
                myVideoView.setVideoURI(Uri.parse(videoPath));

            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            myVideoView.requestFocus();
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    progressDialog.dismiss();
                    myVideoView.seekTo(position);
                    if (position == 0) {
                        myVideoView.start();
                    } else {
                        myVideoView.pause();
                    }
                }
            });
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }
}