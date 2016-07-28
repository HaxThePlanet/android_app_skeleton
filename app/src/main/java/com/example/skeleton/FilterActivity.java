package com.example.skeleton;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Browser;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.impl.client.AbstractHttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;

public class FilterActivity extends Activity {
    TextView mainText;
    PackageManager p;
    private static final String chadLog = "CHAD_LOG";
    private static Set<String> values = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMain();
        syncPhishList();

        p = getPackageManager();

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {

            String checkUrl = data.toString().replace("http://", "").replace("https://", "").replace("www.", "");
            Log.i(chadLog, "user visited url = " + checkUrl);

            Log.i("CHAD_LOG", "checking url");
            if (values.contains(checkUrl)) {
                Log.i("CHAD_LOG", "url is phishing, denying browse");
                mainText.setText("Prevented phishing attempt \n" + data.toString());
                sendNotif(data.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            } else {
                Log.i("CHAD_LOG", "url is safe, allowing browse");
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

    private void syncPhishList() {
        Log.i("CHAD_LOG", "starting url def update");

        if (values.size() > 0) return;

        String username = "ws_idg";
        String host = "qa.cyv.idg.io";
        String password = "@:L]P8DCeR@}hsp?.5HD7v~4R%.)!K";

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            HttpClient client = new DefaultHttpClient();

            AuthScope as = new AuthScope(host, 80);
            UsernamePasswordCredentials upc = new UsernamePasswordCredentials(username, password);

            ((AbstractHttpClient) client).getCredentialsProvider().setCredentials(as, upc);

            BasicHttpContext localContext = new BasicHttpContext();

            BasicScheme basicAuth = new BasicScheme();
            localContext.setAttribute("preemptive-auth", basicAuth);

            HttpHost targetHost = new HttpHost(host, 80, "http");

            HttpGet httpget = new HttpGet("http://qa.cyv.idg.io/phishing");
            httpget.setHeader("Content-Type", "application/xml");
            HttpResponse response = client.execute(targetHost, httpget, localContext);
            HttpEntity entity = response.getEntity();
            Object content = EntityUtils.toString(entity);


            //build set of URLS returned from endpoint
            JSONObject json = new JSONObject(content.toString());
            JSONArray arr = json.getJSONArray("phishing_urls");


            for (int i = 0; i < arr.length(); i++) {
                values.add(arr.getString(i));
            }
            //set built

            Log.i("CHAD_LOG", values.size() + " url definitions stored");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CHAD_LOG", "Error: " + e.getMessage());
        }
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
