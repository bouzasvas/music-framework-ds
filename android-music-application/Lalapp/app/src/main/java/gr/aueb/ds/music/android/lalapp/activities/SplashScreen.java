package gr.aueb.ds.music.android.lalapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;

public class SplashScreen extends AppCompatActivity {

    private static final int TIMEOUT_SECS = 3000;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove Title Bar
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        setContentView(R.layout.activity_splash_screen);
        AppFileOperations.deleteChunks(this);
        startTimerForMainActivity();
    }

    private void startTimerForMainActivity() {
        // Go To main activity after X Seconds
        new Handler().postDelayed(this::goToMainActivity, TIMEOUT_SECS);
    }

    private void goToMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainNavigationActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}
