package com.example.skeleton;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.skeleton.billing.IabHelper;
import com.example.skeleton.billing.IabResult;
import com.example.skeleton.billing.Inventory;
import com.example.skeleton.billing.Purchase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREFS_NAME = "prefs";
    private static final String LOG_TAG = "skelly_app";

    //billing
    IabHelper mHelper;
    static final String itemSku = "product_id_here";
    static final String base64key = "key_here";

    TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (isFirstRun()) {
//            showWelcome();
//        } else {
//            showSplash();
//        }

        //showLogin();

        initMain();
        //SetupBilling();

//        Intent intent = getIntent();
//        Uri data = intent.getData();
//
//        if (data != null) {
//            //sendNotif(data.toString());
//            Log.i("CHAD_LOG", data.toString());
//
//
//            if (data.toString().contains("www.example.com/")) {
//                mainText.setText("Blocked " + data.toString());
//                sendNotif(data.toString());
//            } else {
//                PackageManager p = getPackageManager();
//                p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//
//                passToBrowser(data);
//
////                try {
////                    Thread.sleep(500);
////                } catch (Exception e) { }
//
//                p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//
////              this.moveTaskToBack(true);
//            }
//        } else {
//            PackageManager p = getPackageManager();
//            p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        }

        //Log.i("INFO", "");
    }

    private void passToBrowser(Uri uri) {
        Intent mBrowserIntent = new Intent(Intent.ACTION_VIEW);
        mBrowserIntent.setPackage("com.android.chrome");
        mBrowserIntent.setFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        mBrowserIntent.putExtra(Browser.EXTRA_APPLICATION_ID , "com.android.browser");
        mBrowserIntent.setData(uri);
        startActivity(mBrowserIntent);
    }
    private void checkPerms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions((Activity) this, new String[] {Manifest.permission.RECEIVE_SMS}, 333);
            }
        }
    }

//    private void initBrowserListener() {
//        BroadcastReceiver sdcardEjectReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//                    Log.i("INFO", "");
//                }
//            }
//        };
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_VIEW);
//
//        intentFilter.addCategory("android.intent.category.DEFAULT");
//        intentFilter.addCategory("android.intent.category.BROWSABLE");
//
//        intentFilter.addDataScheme("http");
//
//        registerReceiver(sdcardEjectReceiver, intentFilter);
//    }


//    private void writePrefs() {
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putInt("url", 1);
//        editor.commit();
//    }
//
//    private long readPrefs() {
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        long highScore = sharedPref.getInt("url", 0);
//
//        return highScore;
//    }

    private void sendNotif(String theUrl) {
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Phishing attempt prevented!");
        mBuilder.setContentText(theUrl);
        mBuilder.setTicker("Phishing attempt prevented");
        mBuilder.setSmallIcon(R.drawable.ic_menu_send);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2600, mBuilder.build());

    }

    private void initMain() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainText = (TextView) findViewById(R.id.mainText);
    }

    private boolean isFirstRun() {
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            boolean firstRun = settings.getBoolean("firstrun", true);

            if (firstRun) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "isFirstRun error " + e.toString());
            return false;
        }
    }

    private void showSplash() {
        try {
            Intent i = new Intent(MainActivity.this, SplashScreen.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i(LOG_TAG, "showSplash error " + e.toString());
        }
    }

    private void showWelcome() {
        try {
            Intent i = new Intent(MainActivity.this, WelcomeScreen.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i(LOG_TAG, "showWelcome error " + e.toString());
        }
    }

    private void showLogin() {
        try {
            Intent i = new Intent(MainActivity.this, LoginScreen.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i(LOG_TAG, "showLogin error " + e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Log.i(LOG_TAG, "clicked");
        } else if (id == R.id.nav_gallery) {
            Log.i(LOG_TAG, "clicked");
        } else if (id == R.id.nav_slideshow) {
            Log.i(LOG_TAG, "clicked");
        } else if (id == R.id.nav_manage) {
            Log.i(LOG_TAG, "clicked");
        } else if (id == R.id.nav_share) {
            Log.i(LOG_TAG, "clicked");
        } else if (id == R.id.nav_send) {
            Log.i(LOG_TAG, "clicked");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                // Handle errors
                return;
            } else if (purchase.getSku().equals(itemSku)) {
                Log.i(LOG_TAG, "already purchased");
                //buyButton.setEnabled(false);
            }

        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(itemSku), mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                //clickButton.setEnabled(true);
            } else {
                // handle error
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void SetupBilling() {
        String base64EncodedPublicKey = base64key;

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(LOG_TAG, "In-app Billing setup failed: " + result);
                } else {
                    //consumeItem();
                    Log.d(LOG_TAG, "In-app Billing is set up OK");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
