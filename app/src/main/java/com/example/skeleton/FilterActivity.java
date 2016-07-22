package com.example.skeleton;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

public class FilterActivity extends Activity {
    TextView mainText;
    PackageManager p;
    static final String chadLog = "CHAD_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMain();

        p = getPackageManager();

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            Log.i(chadLog, "URL = " + data.toString());
            if (data.toString().contains("http://my.updatingwellsfargo.com/indez.php")) {
                mainText.setText("Prevented phishing attempt \n" + data.toString());
                sendNotif(data.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
            } else {
                disableApp();
                passToBrowser(data);
                enableApp();
            }
        } else {
            enableApp();
        }
    }

    private void initMain() {
        setContentView(R.layout.filter_layout);
        mainText = (TextView) findViewById(R.id.mainText);
    }

    private void enableApp() {
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void disableApp() {
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
    private void passToBrowser(Uri uri) {
        Intent mBrowserIntent = new Intent(Intent.ACTION_VIEW);
        mBrowserIntent.setPackage("com.android.chrome");
        mBrowserIntent.setFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        mBrowserIntent.putExtra(Browser.EXTRA_APPLICATION_ID , "com.android.browser");
        mBrowserIntent.setData(uri);
        startActivity(mBrowserIntent);
    }

    private void sendNotif(String theUrl) {
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Phishing attempt prevented!");
        mBuilder.setContentText(theUrl);
        mBuilder.setTicker("Phishing attempt prevented");
        mBuilder.setSmallIcon(R.drawable.ic_menu_send);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2600, mBuilder.build());

    }


    @Override
    public void onDestroy() { super.onDestroy(); }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
