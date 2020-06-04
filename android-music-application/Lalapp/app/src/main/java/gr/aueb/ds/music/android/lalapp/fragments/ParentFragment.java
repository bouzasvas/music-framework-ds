package gr.aueb.ds.music.android.lalapp.fragments;

import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import java.util.Map;
import java.util.Objects;

import gr.aueb.ds.music.android.lalapp.R;

import static android.content.Context.MODE_PRIVATE;

public abstract class ParentFragment extends Fragment {

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

    private SharedPreferences getApplicationSettings() {
        return Objects.requireNonNull(this.getActivity()).getSharedPreferences(getString(R.string.app_shared_prefs), MODE_PRIVATE);
    }

    protected Map<String, ?> getAllSettings() {
        return getApplicationSettings().getAll();
    }
}
