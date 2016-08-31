package com.ingentive.qrme.activity;

import android.os.Environment;
import android.os.Bundle;

//public class GenerateQRCodeActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_generate_qrcode);
//    }
//}
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.google.zxing.BarcodeFormat;
import com.ingentive.qrme.activeandroid.UserInfoTable;
import com.ingentive.qrme.common.Config;
import com.ingentive.qrme.common.Contents;
import com.ingentive.qrme.common.QRCodeEncoder;
import com.ingentive.qrme.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateQRCodeActivity extends Activity implements OnClickListener {

    private String LOG_TAG = "GenerateQRCode";
    //private String upLoadServerUri = "http://yourbrand.pk/wasaocr/services/meter_reading_video_api";
    private EditText qrInput;
    private Button button1;
    private UserInfoTable userInfoTable;
    private String fb,tw,em,ld;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        qrInput = (EditText) findViewById(R.id.qrInput);

        userInfoTable=new UserInfoTable();
        userInfoTable=new Select().from(UserInfoTable.class).executeSingle();
        if(userInfoTable!=null){
             fb = userInfoTable.getUserFacebook();
             tw =  userInfoTable.getUserTwitter();
             em =  userInfoTable.getUserEmail();
             ld =  userInfoTable.getUserLinkedin();
        }
        qrInput.setText(Config.upLoadServerUri+","+fb+","+tw+","+em+","+ld);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button1:
                String qrInputText = qrInput.getText().toString();
                Log.v(LOG_TAG, qrInputText);
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
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void saveQRImage(Bitmap croppedImage) {
        String storageDir = Environment.getExternalStorageDirectory() + "/VideoCompressor";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        //cropImageView.setDrawingCacheEnabled(true);
        File root = Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/VideoCompressor/" + "QR" + timeStamp + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}