/**
 * Copyright 2014 Jeroen Mols
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ingentive.qrme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ingentive.qrme.activeandroid.UserInfoTable;
import com.ingentive.qrme.activeandroid.UsersTable;
import com.ingentive.qrme.common.NetworkChangeReceiver;
import com.ingentive.qrme.fragment.CaptureDemoFragment;
import com.ingentive.qrme.R;
import com.ingentive.qrme.fragment.FragmentDrawer;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    private FragmentDrawer drawerFragment;
    private Toolbar mToolbar;
    private ImageButton ibtnScanQr, ibtnMakeVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetworkChangeReceiver.getConnectivityStatus(MainActivity.this);
        initialize();
    }

    protected void initialize() {
        ibtnScanQr = (ImageButton) findViewById(R.id.ibtn_scan_qr);
        ibtnMakeVideo = (ImageButton) findViewById(R.id.ibtn_make_video);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        ibtnScanQr.setOnClickListener(this);
        ibtnMakeVideo.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(getApplication(), ContactsActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(getApplication(), QRHistoryActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(getApplication(), ProfileActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(getApplication(), SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibtn_scan_qr:
//                intent = new Intent(getApplication(), ScanQRAactivity.class);
//                startActivity(intent);
                if (NetworkChangeReceiver.isConnected) {
                    scanBarCode();
                } else {
                    Toast.makeText(MainActivity.this, "Please make sure, your network connection is ON ", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ibtn_make_video:

//                if (savedInstanceState == null) {
//                    getSupportFragmentManager().beginTransaction().add(R.id.container, new CaptureDemoFragment()).commit();
//                }
//                intent = new Intent(getApplication(), ShareActivity.class);
//                startActivity(intent);

                intent = new Intent(getApplication(), MakeVideoActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void scanBarCode() {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setScanningRectangle(1000, 600);
        integrator.setPrompt("Scan code");
        integrator.setResultDisplayDuration(0);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            /*
          http://yourbrand.pk/wasaocr/app/webroot/files/videos/VIDEO_20160822_121636.mp4,
          Azhar Abbas,
          azhar_1492@yahoo.com,
          ,
          azharabbas1105@gmail.com,
          azharabbas193@yahoo.com,
          03466969193
             */
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            if (scanContent != null && scanFormat != null) {

                Log.d("scanningResult.... ", "  " + scanningResult);
                String[] separated = scanContent.split(",");
                String videoPath = "";
                String userName = "";
                String fb = "";
                String tw = "";
                String email = "";
                String ld = "";
                String phone = "";
                if (separated.length >= 7) {
                    videoPath = separated[0];
                    userName = separated[1];
                    fb = separated[2];
                    tw = separated[3];
                    email = separated[4];
                    ld = separated[5];
                    phone = separated[6];

                    UserInfoTable userInfoTable = new Select().from(UserInfoTable.class).executeSingle();
                    if (userInfoTable != null && userInfoTable.getUserEmail().equals(email)) {
                        Intent i = new Intent(MainActivity.this, VideoViewActivity.class);
                        i.putExtra("url", videoPath);
                        i.putExtra("intent_", "scan_activity");
                        startActivity(i);
                        finish();
                    } else {
                        UsersTable usersTable = new Select().from(UsersTable.class).where("user_email=?", email).executeSingle();
                        if (usersTable == null) {
                            UsersTable model = new UsersTable();
                            model.userName = userName;
                            model.userFacebook = fb;
                            model.userTwitter = tw;
                            model.userEmail = email;
                            model.userLinkedin = ld;
                            model.userPhone = phone;
                            model.save();

                            Intent i = new Intent(MainActivity.this, VideoViewActivity.class);
                            i.putExtra("url", videoPath);
                            startActivity(i);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
        }
    }
}
