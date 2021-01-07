package com.rockchipme.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

public class DeepLinkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setResult(Activity.RESULT_CANCELED);
        Intent mIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        finish();
        ActivityCompat.finishAffinity(this);
    }
}
