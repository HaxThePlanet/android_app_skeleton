package com.example.skeleton;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WelcomeScreen extends Activity {
    private static final String PREFS_NAME = "prefs";
    private static final String LOG_TAG = "skelly_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button finishWelcome = (Button) findViewById(R.id.finishWelcome);
        finishWelcome.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.finishWelcome:
                    setNotFirstRun();
                    finish();
                    break;
            }

        }
    };

    private void setNotFirstRun() {
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstrun", false);
            editor.commit();
        } catch (Exception e) {
            Log.i(LOG_TAG, "setNotFirstRun error " + e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
