package gr.aueb.ds.music.android.lalapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import gr.aueb.ds.music.android.lalapp.R;

public class SettingsActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initLanguagesSpinner();
        initDefaultValues();
    }

    private void initDefaultValues() {
        String language = getApplicationLanguage();
        String masterIp = getMasterIp();
        String masterPort = getMasterPort();

        // Set Values
        ((Spinner) findViewById(R.id.settings_languages_spinner)).setSelection(language.equals("English") ? 0 : 1);
        ((EditText) findViewById(R.id.settings_master_ip_editText)).setText(masterIp);
        ((EditText) findViewById(R.id.settings_master_port_editText)).setText(masterPort);
    }

    private void initLanguagesSpinner() {
        Spinner languagesSpinner =  (Spinner) findViewById(R.id.settings_languages_spinner);

        // Init Available Languages Adapter Data
        List<String> languagesAdapterData = Arrays.asList(getString(R.string.settiings_lang_eng), getString(R.string.settings_lang_gr));
        ArrayAdapter<String> languagesSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languagesAdapterData);

        languagesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languagesSpinner.setAdapter(languagesSpinnerAdapter);
    }

    public void returnToMainActivity(View view) {
        finish();
    }

    public void saveSettings(View view) {
        String language     = ((Spinner) findViewById(R.id.settings_languages_spinner)).getSelectedItem().toString();
        String masterIp     = ((EditText) findViewById(R.id.settings_master_ip_editText)).getText().toString();
        String masterPort   = ((EditText) findViewById(R.id.settings_master_port_editText)).getText().toString();

        setApplicationLanguage(language);
        setMasterIp(masterIp);
        setMasterPort(masterPort);

        returnToMainActivity(view);
    }
}
