package com.ingentive.qrme.activity;

/*
    http://www.tejaprakash.com/2013/05/how-to-create-facebook-hash-key-for.html
     */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareButton;
import com.google.zxing.BarcodeFormat;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.UserInfoTable;
import com.ingentive.qrme.common.Contents;
import com.ingentive.qrme.common.QRCodeEncoder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class UploadActivity extends Activity {

    private TextView messageText;
    private Button uploadButton, btnselectvideo;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    private String upLoadServerUri = null;
    private String filepath = null;
    private int FLAG = 0;
    private String result = "";
    private ImageButton ibtnTwitter;
    private ShareLinkContent shareContent;
    private ShareButton shareButton;
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";
    public static final int WEBVIEW_REQUEST_CODE = 100;
    private ProgressDialog pDialog;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
    private EditText mShareEditText;
    private TextView userName;
    private View loginLayout;
    private View shareLayout;
    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    private Bitmap shareImage;
    private UserInfoTable userInfoTable;
    private String fb, tw, em, ld;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initTwitterConfigs();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_upload);
        upLoadServerUri = "http://yourbrand.pk/wasaocr/services/meter_reading_video_api";
        uploadButton = (Button) findViewById(R.id.uploadButton);
        messageText = (TextView) findViewById(R.id.messageText);
        btnselectvideo = (Button) findViewById(R.id.button_selectvideo);
        shareButton = (ShareButton) findViewById(R.id.shareButton);
        ibtnTwitter = (ImageButton) findViewById(R.id.btnTwitterAuthorize);

        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);
        shareLayout = (LinearLayout) findViewById(R.id.share_layout);
        mShareEditText = (EditText) findViewById(R.id.share_text);
        userName = (TextView) findViewById(R.id.user_name);

        loginLayout.setVisibility(View.GONE);
        shareLayout.setVisibility(View.GONE);

        ibtnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSharedPreferences = getSharedPreferences(PREF_NAME, 0);

                boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
                /*  if already logged in, then hide login layout and show share layout */
                if (isLoggedIn) {
                    loginLayout.setVisibility(View.GONE);
                    shareLayout.setVisibility(View.VISIBLE);
                    String username = mSharedPreferences.getString(PREF_USER_NAME, "");
                    userName.setText(getResources().getString(R.string.hello)
                            + username);
                } else {
                    loginLayout.setVisibility(View.VISIBLE);
                    shareLayout.setVisibility(View.GONE);
                    Uri uri = getIntent().getData();
                    if (uri != null && uri.toString().startsWith(callbackUrl)) {
                        String verifier = uri.getQueryParameter(oAuthVerifier);
                        try {
                            /* Getting oAuth authentication token */
                            twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                            /* Getting user id form access token */
                            long userID = accessToken.getUserId();
                            final User user = twitter.showUser(userID);
                            final String username = user.getName();
                            /* save updated token */
                            saveTwitterInfo(accessToken);
                            loginLayout.setVisibility(View.GONE);
                            shareLayout.setVisibility(View.VISIBLE);
                            userName.setText(getString(R.string.hello) + username);

                        } catch (Exception e) {
                            // Log.e("Failed to login Twitter!!", e.getMessage());
                        }
                    }

                }
            }
        });
        findViewById(R.id.btnLinkedin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                Uri screenshotUri = Uri.parse("android.resource://comexample.sairamkrishna.myapplication/*");
//
//                try {
//                    InputStream stream = getContentResolver().openInputStream(screenshotUri);
//                }
//
//                catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                sharingIntent.setType("image/jpeg");
//                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//                startActivity(Intent.createChooser(sharingIntent, "Share image using"));


                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.linkdin);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
//                try {
//                    f.createNewFile();
//                    FileOutputStream fo = new FileOutputStream(f);
//                    fo.write(bytes.toByteArray());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                share.putExtra(Intent.EXTRA_STREAM, b);
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });


        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToTwitter();
            }
        });
        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String status = mShareEditText.getText().toString();

                if (status.trim().length() > 0) {
                    new updateTwitterStatus().execute(status);
                } else {
                    Toast.makeText(getApplication(), "Message is empty!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
            Toast.makeText(this, "Twitter key and secret not configured",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        btnselectvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = 1;
                Intent intent = new Intent();
                intent.setType("video/*");
                // intent.setType("audio/*");
                // intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"), 1);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FLAG == 0) {
                    Toast.makeText(
                            UploadActivity.this,
                            "Please select atleast one image or audio or video !!!",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (filepath != null) {
                        dialog = ProgressDialog.show(UploadActivity.this, "",
                                "Uploading file...", true);
                        messageText.setText("uploading started.....");
                        new Thread(new Runnable() {
                            public void run() {
                                uploadFile(filepath);
                            }
                        }).start();
                    } else {
                        Toast.makeText(UploadActivity.this, "Please try again !!!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    /**
     * Saving user information, after user is authenticated for the first time.
     * You don't need to show user to login, until user has a valid access toen
     */
    private void saveTwitterInfo(twitter4j.auth.AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = twitter.showUser(userID);

            String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_USER_NAME, username);
            e.commit();

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    private void loginToTwitter() {
        boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        if (!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);

                /**
                 *  Loading twitter login page on webview for authorization
                 *  Once authorized, results are received at onActivityResult
                 *  */
                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {

            loginLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            filepath = getPath(selectedImageUri);
            messageText.setText("Uploading file path:" + filepath);
            uploadButton.setVisibility(View.VISIBLE);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == WEBVIEW_REQUEST_CODE) {
            String verifier = data.getExtras().getString(oAuthVerifier);
            try {
                twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                long userID = accessToken.getUserId();
                final User user = twitter.showUser(userID);
                String username = user.getName();

                saveTwitterInfo(accessToken);

                loginLayout.setVisibility(View.GONE);
                shareLayout.setVisibility(View.VISIBLE);
                userName.setText(UploadActivity.this.getResources().getString(
                        R.string.hello) + username);

            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :" + filepath);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :" + filepath);
                }
            });

            return 0;

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(upLoadServerUri);
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
                                Toast.makeText(UploadActivity.this, "Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                uploadButton.setVisibility(View.GONE);
                                userInfoTable = new UserInfoTable();
                                userInfoTable = new Select().from(UserInfoTable.class).executeSingle();
                                if (userInfoTable != null) {
                                    fb = userInfoTable.getUserFacebook();
                                    tw = userInfoTable.getUserTwitter();
                                    em = userInfoTable.getUserEmail();
                                    ld = userInfoTable.getUserLinkedin();
                                }

                                shareContent = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(fb)).build();

                                String qrInputText = result + "," + fb + "," + tw + "," + em + "," + ld;
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
                                messageText.setText(msg);
                            }
                        });
                    }

                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText
                                .setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(UploadActivity.this,
                                "MalformedURLException", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(UploadActivity.this,
                                "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
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
        File file = new File(root.getAbsolutePath() + "/VideoCompressor/" + "QR" + timeStamp + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            shareImage = image;
            ibtnTwitter.setVisibility(View.VISIBLE);
            shareButton.setVisibility(View.VISIBLE);
            SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
            SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
            shareButton.setShareContent(content);

            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(UploadActivity.this);
            pDialog.setMessage("Posting to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {

            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                twitter4j.auth.AccessToken accessToken = new twitter4j.auth.AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(status);
//                InputStream is = getResources().openRawResource(R.drawable.twitter_);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                shareImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream is = new ByteArrayInputStream(stream.toByteArray());

//                shareImage
                statusUpdate.setMedia("test.jpg", is);

                twitter4j.Status response = twitter.updateStatus(statusUpdate);

                Log.d("Status", response.getText());

            } catch (TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
			/* Dismiss the progress dialog after sharing */
            pDialog.dismiss();
            Toast.makeText(UploadActivity.this, "Posted to Twitter!", Toast.LENGTH_SHORT).show();
            // Clearing EditText field
            mShareEditText.setText("");
        }

    }
}