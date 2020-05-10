package gr.aueb.ds.music.android.lalapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Locale;
import java.util.Map;

import gr.aueb.ds.music.android.lalapp.R;

public class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initDefaultSettingsValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Go to "Settings" Activity

                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initDefaultSettingsValues() {
        setApplicationLanguage(getApplicationLanguage());
        setMasterIp(getMasterIp());
        setMasterPort(getMasterPort());
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    private SharedPreferences getApplicationSettings() {
        return getSharedPreferences(getString(R.string.app_shared_prefs), MODE_PRIVATE);
    }

    protected void setApplicationLanguage(String language) {
        // TODO - Change Language at Runtime
        //updateResources(this, new Locale("el-GR").getLanguage());
        SharedPreferences.Editor editor = getApplicationSettings().edit();

        editor.putString("app_language", language);
        editor.apply();
    }

    protected void setMasterIp(String masterIp) {
        SharedPreferences.Editor editor = getApplicationSettings().edit();

        editor.putString("master_ip", masterIp);
        editor.apply();
    }

    protected void setMasterPort(String masterPort) {
        SharedPreferences.Editor editor = getApplicationSettings().edit();

        editor.putString("master_port", masterPort);
        editor.apply();
    }


    // Get Application Settings
    protected Map<String, ?> getAllSettings() {
        return getApplicationSettings().getAll();
    }

    protected String getApplicationLanguage() {
        SharedPreferences applicationSettings = getApplicationSettings();

        return applicationSettings.getString("app_language", "English");
    }

    protected String getMasterIp() {
        SharedPreferences applicationSettings = getApplicationSettings();

        return applicationSettings.getString("master_ip", "10.0.2.2");
    }

    protected String getMasterPort() {
        SharedPreferences applicationSettings = getApplicationSettings();

        return applicationSettings.getString("master_port", "8080");
    }
}
