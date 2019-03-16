package com.example.skeleton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFirstRun()) {
            showWelcome();
        } else {
            showSplash();
        }

        //showLogin();

        initMain();
        SetupBilling();
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
