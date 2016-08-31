package com.ingentive.qrme.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.activeandroid.query.Select;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.zxing.BarcodeFormat;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.QRHistoryTable;
import com.ingentive.qrme.activeandroid.UserInfoTable;
import com.ingentive.qrme.common.Config;
import com.ingentive.qrme.common.Contents;
import com.ingentive.qrme.common.NetworkChangeReceiver;
import com.ingentive.qrme.common.QRCodeEncoder;
import com.ingentive.qrme.file.FileUtils;
import com.ingentive.qrme.video.MediaController;
import com.ingentive.qrme.videocompress.configuration.PredefinedCaptureConfigurations;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MakeVideoActivity extends Activity implements View.OnClickListener{

    private final String KEY_STATUSMESSAGE    = "com.ingentive.statusmessage";
    private final String KEY_FILENAME         = "com.ingentive.outputfilename";
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private int serverResponseCode = 0;
    private String statusMessage = null;
    private String filename      = null;
    private String result = "";
    private ImageView thumbnailIv;
    private TextView statusTv;
    private ProgressDialog pDialog;
    private ImageView ivBack;
    private Toolbar mToolbar;
    //private Button btnUpload;
    private Button captureBtn,retryBtn,uplaodBtn;
    private LinearLayout bottom_buttons_layout;
    private UserInfoTable userInfoTable;
    private String userName,fb, tw, em, ld,phone;
    private VideoView myVideoView;
    public static File fileSaveImage=null;
    private String videoPath=null;
    private int position = 0;
    private android.widget.MediaController mediaControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_video);

        pDialog = new ProgressDialog(MakeVideoActivity.this);
        pDialog.setTitle("Uploading file...");
        pDialog.setMessage("Please wait.");
        pDialog.setCancelable(false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_make_video);
        ivBack=(ImageView)mToolbar.findViewById(R.id.iv_back_make_video);
        //btnUpload=(Button)findViewById(R.id.btn_upload);
        bottom_buttons_layout=(LinearLayout)findViewById(R.id.bottom_buttons_layout);
        myVideoView=(VideoView)findViewById(R.id.video_view);
        retryBtn=(Button)findViewById(R.id.btn_retry);
        uplaodBtn=(Button)findViewById(R.id.btn_upload);
        captureBtn = (Button) findViewById(R.id.btn_capturevideo);
        thumbnailIv = (ImageView) findViewById(R.id.iv_thumbnail);
        statusTv = (TextView) findViewById(R.id.tv_status);
        thumbnailIv.setOnClickListener(this);
        captureBtn.setOnClickListener(this);
        retryBtn.setOnClickListener(this);
        uplaodBtn.setOnClickListener(this);


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

        if (savedInstanceState != null) {
            statusMessage = savedInstanceState.getString(KEY_STATUSMESSAGE);
            filename = savedInstanceState.getString(KEY_FILENAME);
        }

        updateStatusAndThumbnail();
        initializeSpinners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_STATUSMESSAGE, statusMessage);
        outState.putString(KEY_FILENAME, filename);
        outState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_capturevideo:
                startVideoCaptureActivity();
                break;
//            case R.id.iv_thumbnail:
//                final Intent videoIntent = new Intent(Intent.ACTION_VIEW);
//                videoIntent.setDataAndType(Uri.parse( MediaController.getInstance().cacheFile.getPath()), "video/*");
//                try {
//                    startActivity(videoIntent);
//                } catch (ActivityNotFoundException e) {
//                    // NOP
//                }
//                playVideo();
//                break;

            case R.id.btn_retry:
                thumbnailIv.setVisibility(View.VISIBLE);
                myVideoView.setVisibility(View.INVISIBLE);
                startVideoCaptureActivity();
                break;

            case R.id.btn_upload:
                if(NetworkChangeReceiver.isConnected){
                    videoPath= MediaController.getInstance().cacheFile.getPath();
                    String str=uplaodBtn.getText().toString();
                    if(str.equals("SHARE")){
                        Intent intent=new Intent(MakeVideoActivity.this,ShareActivity.class);
                        intent.putExtra("image_path",fileSaveImage.getPath()+"");
                        startActivity(intent);
                        finish();
                    }else {
                        Log.d("", "" + videoPath);

                        if (videoPath != null) {
                            initializeSpinners();
                            pDialog.show();
                            statusTv.setText("uploading started.....");
                            updateStatusAndThumbnail();
                            new Thread(new Runnable() {
                                public void run() {
                                    uploadFile(videoPath);
                                }
                            }).start();
                        } else {
                            Toast.makeText(MakeVideoActivity.this, "Please try again !!!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Toast.makeText(MakeVideoActivity.this, "Please make sure, your network connection is ON ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void startVideoCaptureActivity() {
        final Intent intent = new Intent(MakeVideoActivity.this, VideoCaptureActivity.class);
        intent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
        startActivityForResult(intent, 101);
    }
    private PredefinedCaptureConfigurations.CaptureQuality getQuality(int position) {
        final PredefinedCaptureConfigurations.CaptureQuality[] quality = new PredefinedCaptureConfigurations.CaptureQuality[]{PredefinedCaptureConfigurations.CaptureQuality.HIGH, PredefinedCaptureConfigurations.CaptureQuality.MEDIUM,
                PredefinedCaptureConfigurations.CaptureQuality.LOW};
        return quality[position];
    }

    private PredefinedCaptureConfigurations.CaptureResolution getResolution(int position) {
        final PredefinedCaptureConfigurations.CaptureResolution[] resolution = new PredefinedCaptureConfigurations.CaptureResolution[]{PredefinedCaptureConfigurations.CaptureResolution.RES_1080P,
                PredefinedCaptureConfigurations.CaptureResolution.RES_720P, PredefinedCaptureConfigurations.CaptureResolution.RES_480P};
        return resolution[position];
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            filename = data.getStringExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME);
            statusMessage = getString(R.string.status_capturesuccess);
            statusTv.setText(statusMessage);
            FileUtils.createApplicationFolder();
            try {
                new VideoCompressor().execute();
            }catch (Exception e){
                e.printStackTrace();
            }
            captureBtn.setVisibility(View.GONE);
            bottom_buttons_layout.setVisibility(View.VISIBLE);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            filename = null;
            statusMessage = getString(R.string.status_capturecancelled);
            captureBtn.setVisibility(View.VISIBLE);
            bottom_buttons_layout.setVisibility(View.GONE);
        } else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
            filename = null;
            statusMessage = getString(R.string.status_capturefailed);
            captureBtn.setVisibility(View.VISIBLE);
            bottom_buttons_layout.setVisibility(View.GONE);
        }
        updateStatusAndThumbnail();

        //super.onActivityResult(requestCode, resultCode, data);


    }

//    public void playVideo() {
//        if (filename == null) return;
//
//        final Intent videoIntent = new Intent(Intent.ACTION_VIEW);
//        videoIntent.setDataAndType(Uri.parse(filename), "video/*");
//        try {
//            startActivity(videoIntent);
//        } catch (ActivityNotFoundException e) {
//            // NOP
//        }
//    }
    private void initializeSpinners() {

        pDialog = new ProgressDialog(MakeVideoActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
    }
    private void updateStatusAndThumbnail() {
        if (statusMessage == null) {
            statusMessage = getString(R.string.status_nocapture);
        }
        statusTv.setText(statusMessage);

        final Bitmap thumbnail = getThumbnail();

        if (thumbnail != null) {
            thumbnailIv.setImageBitmap(thumbnail);
        } else {
            thumbnailIv.setImageResource(R.drawable.thumbnail_placeholder);
        }
    }
    private Bitmap getThumbnail() {

        if (filename == null) return null;
        return ThumbnailUtils.createVideoThumbnail(filename, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }
    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(MakeVideoActivity.this);
//            pDialog.setTitle("Uploading file...");
//            pDialog.setMessage("Please wait.");
//            pDialog.setCancelable(false);
//            pDialog.show();
            initializeSpinners();
            pDialog.show();
            Log.d("", "Start video compression");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //return MediaController.getInstance().scheduleVideoConvert(filename);
            return MediaController.getInstance().convertVideo(filename);
        }


        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            statusTv.setText(getString(R.string.status_capturesuccess));
            deleteFiles(filename);
            pDialog.dismiss();
            if(compressed){
                Log.d("", "Compression successfully!");

                try {
                    Uri vidFile = Uri.parse(MediaController.getInstance().cacheFile.getPath());
                    // hide image preview
                    thumbnailIv.setVisibility(View.INVISIBLE);
                    myVideoView.setVisibility(View.VISIBLE);

                    if (mediaControls == null) {
                        mediaControls = new android.widget.MediaController(MakeVideoActivity.this);
                    }
                    myVideoView.setMediaController(mediaControls);
                    myVideoView.setVideoURI(vidFile);
                    // start playing
                    myVideoView.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                myVideoView.requestFocus();
                myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
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
    }
    public static void deleteFiles(String path) {

        File file = new File(path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) { }
        }
    }

    public int uploadFile(final String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            pDialog.dismiss();

            Log.e("uploadFile", "Source File not exist :" + sourceFileUri);

            runOnUiThread(new Runnable() {
                public void run() {
                    statusTv.setText("Source File not exist :" + sourceFileUri);
                }
            });

            return 0;

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(Config.upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);


                if (serverResponseCode == 200) {
                    InputStream response = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                    response.close();
                    if (result.contains("fail") || result.equals("fail")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                result = "";
                                Toast.makeText(MakeVideoActivity.this, "Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                //uploadButton.setVisibility(View.GONE);
                                userInfoTable = new UserInfoTable();
                                userInfoTable = new Select().from(UserInfoTable.class).executeSingle();
                                if (userInfoTable != null) {
                                    userName=userInfoTable.getUserName();
                                    fb = userInfoTable.getUserFacebook();
                                    tw = userInfoTable.getUserTwitter();
                                    em = userInfoTable.getUserEmail();
                                    ld = userInfoTable.getUserLinkedin();
                                    phone=userInfoTable.getUserPhone();
                                }


                                String qrInputText = result + "," + userName +"," + fb + "," + tw + "," + em + "," + ld+ "," + phone;
                                //Find screen size
                                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                                Display display = manager.getDefaultDisplay();
                                Point point = new Point();
                                display.getSize(point);
                                int width = point.x;
                                int height = point.y;
                                int smallerDimension = width < height ? width : height;
                                smallerDimension = smallerDimension * 3 / 4;

                                //Encode with a QR Code image
                                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                                        null,
                                        Contents.Type.TEXT,
                                        BarcodeFormat.QR_CODE.toString(),
                                        smallerDimension);
                                try {
                                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                                    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
                                    myImage.setImageBitmap(bitmap);
                                    saveQRImage(bitmap);
                                    //finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String msg = "File Upload Completed.'\n Path : " + result;
                                statusTv.setText(msg);
                                thumbnailIv.setVisibility(View.VISIBLE);
                                myVideoView.setVisibility(View.INVISIBLE);

                                uplaodBtn.setText("SHARE");
                            }
                        });
                    }

                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                pDialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        statusTv
                                .setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(MakeVideoActivity.this,
                                "MalformedURLException", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                pDialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        statusTv.setText("Got Exception : see logcat ");
                        Toast.makeText(MakeVideoActivity.this,
                                "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            pDialog.dismiss();
            return serverResponseCode;
        }
    }
    private void saveQRImage(Bitmap image) {
        String storageDir = Environment.getExternalStorageDirectory() + "/VideoCompressor";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File root = Environment.getExternalStorageDirectory();
        fileSaveImage = new File(root.getAbsolutePath() + "/VideoCompressor/" + "QR" + timeStamp + ".jpg");
        try {
            fileSaveImage.createNewFile();
            FileOutputStream ostream = new FileOutputStream(fileSaveImage);
            image.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            //shareImage = image;
            SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
            SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
            ostream.close();
            QRHistoryTable qrHistoryTable=new QRHistoryTable();
            qrHistoryTable.imagePath=fileSaveImage.getPath();
            qrHistoryTable.videoUrl=result;
            qrHistoryTable.videoPath=videoPath;
            qrHistoryTable.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }
}
