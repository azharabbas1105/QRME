package com.ingentive.qrme.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.ingentive.qrme.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ShareActivity extends Activity {

    private Toolbar mToolbar;
    private ImageView ivBack, ivTwitter, ivLinkeDin, ivGoogle;
    private String imagePath = null;
    ShareButton shareButton;
    //image
    Bitmap image;

    private View loginLayout;
    private View shareLayout;
    private static SharedPreferences mSharedPreferences;
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private TextView userName;
    public static final int WEBVIEW_REQUEST_CODE = 100;
    private EditText mShareEditText;
    private ProgressDialog pDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        initTwitterConfigs();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_share);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_share);
        ivBack = (ImageView) mToolbar.findViewById(R.id.iv_back_share);
        ivGoogle = (ImageView) findViewById(R.id.iv_google);
        shareButton = (ShareButton) findViewById(R.id.iv_fb);
        ivTwitter = (ImageView) findViewById(R.id.iv_tw);
        ivLinkeDin = (ImageView) findViewById(R.id.iv_ld);
        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);
        shareLayout = (LinearLayout) findViewById(R.id.share_layout);
        userName = (TextView) findViewById(R.id.user_name);
        loginLayout.setVisibility(View.GONE);
        shareLayout.setVisibility(View.GONE);
        mShareEditText = (EditText) findViewById(R.id.share_text);


        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            imagePath = (String) b.get("image_path");
        }

        image = BitmapFactory.decodeFile(imagePath);
        SharePhoto sharePhoto1 = new SharePhoto.Builder()
                .setBitmap(image).build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto1)
                .build();
        shareButton.setShareContent(content);

        ivGoogle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        ivGoogle.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        ivGoogle.invalidate();

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        ivGoogle.getDrawable().clearColorFilter();
                        ivGoogle.invalidate();
                        loginLayout.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);
                        try {
                            File tmpFile = new File(imagePath);
                            final String photoUri = MediaStore.Images.Media.insertImage(
                                    getContentResolver(), tmpFile.getAbsolutePath(), null, null);

                            Intent shareIntent = ShareCompat.IntentBuilder.from(ShareActivity.this)
                                    .setType("image/jpeg")
                                    .setStream(Uri.parse(photoUri))
                                    .getIntent()
                                    .setPackage("com.google.android.apps.plus");
                            startActivityForResult(shareIntent, 0);
                        } catch (Exception e) {

                        }
                        break;
                    }
                }
                return true;
            }
        });

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
                        loginLayout.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);

                        Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
                return true;
            }
        });

        ivTwitter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        ivTwitter.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        ivTwitter.invalidate();

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        ivTwitter.getDrawable().clearColorFilter();
                        ivTwitter.invalidate();
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
                        break;
                    }
                }
                return true;
            }
        });

        ivLinkeDin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        ivLinkeDin.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        ivLinkeDin.invalidate();

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        ivLinkeDin.getDrawable().clearColorFilter();
                        ivLinkeDin.invalidate();
                        loginLayout.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                        Uri screenshotUri = Uri.parse("android.resource://comexample.sairamkrishna.myapplication/*");

                        try {

                            InputStream stream = getContentResolver().openInputStream(screenshotUri);

                        } catch (FileNotFoundException e) {

                            e.printStackTrace();

                        }
                        sharingIntent.setType("image / jpeg");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);

                        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                        break;
                    }
                }
                return true;
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
    }


    protected void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives focus.
        //ivGoogle.initialize(APPURL, REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = intent.getData();
                ContentResolver cr = this.getContentResolver();
                String mime = cr.getType(selectedImage);

                PlusShare.Builder share = new PlusShare.Builder(this);
                //share.setText("hello everyone!");
                share.addStream(selectedImage);
                share.setType(mime);
                startActivityForResult(share.getIntent(), 2);
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == WEBVIEW_REQUEST_CODE) {
            String verifier = intent.getExtras().getString(oAuthVerifier);
            try {
                twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                long userID = accessToken.getUserId();
                final User user = twitter.showUser(userID);
                String username = user.getName();

                saveTwitterInfo(accessToken);

                loginLayout.setVisibility(View.GONE);
                shareLayout.setVisibility(View.VISIBLE);
                userName.setText(ShareActivity.this.getResources().getString(
                        R.string.hello) + username);

            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        }
    }

    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ShareActivity.this);
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
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream is = new ByteArrayInputStream(stream.toByteArray());

//                shareImage
                statusUpdate.setMedia("QRMe.jpg", is);

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
            Toast.makeText(ShareActivity.this, "Posted to Twitter!", Toast.LENGTH_SHORT).show();
            // Clearing EditText field
            mShareEditText.setText("");
            shareLayout.setVisibility(View.GONE);
        }

    }
}
