package gr.aueb.ds.music.android.lalapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;

public class SettingsFragment extends ParentFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.activity_settings, container, false);
        root.findViewById(R.id.settings_save_button).setOnClickListener(this);
        root.findViewById(R.id.settings_cancel_button).setVisibility(View.GONE);

        initLanguagesSpinner(root);
        initDefaultValues(root);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_save_button:
                this.saveSettings(getView());
                break;
            case R.id.settings_cancel_button:
                this.returnToMainFragment();
                break;
            default:
                break;
        }
    }

    private void initDefaultValues(View root) {
        String language = getApplicationLanguage();
        String masterIp = getMasterIp();
        String masterPort = getMasterPort();

        // Set Values
        ((Spinner) root.findViewById(R.id.settings_languages_spinner)).setSelection(language.equals("English") ? 0 : 1);
        ((EditText) root.findViewById(R.id.settings_master_ip_editText)).setText(masterIp);
        ((EditText) root.findViewById(R.id.settings_master_port_editText)).setText(masterPort);
    }

    private void initLanguagesSpinner(View root) {
        Spinner languagesSpinner =  (Spinner) root.findViewById(R.id.settings_languages_spinner);

        // Init Available Languages Adapter Data
        List<String> languagesAdapterData = Arrays.asList(getString(R.string.settiings_lang_eng), getString(R.string.settings_lang_gr));
        ArrayAdapter<String> languagesSpinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, languagesAdapterData);

        languagesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languagesSpinner.setAdapter(languagesSpinnerAdapter);
    }

    public void saveSettings(View view) {
        String language     = ((Spinner) Objects.requireNonNull(getView()).findViewById(R.id.settings_languages_spinner)).getSelectedItem().toString();
        String masterIp     = ((EditText) Objects.requireNonNull(getView()).findViewById(R.id.settings_master_ip_editText)).getText().toString();
        String masterPort   = ((EditText) Objects.requireNonNull(getView()).findViewById(R.id.settings_master_port_editText)).getText().toString();

        setApplicationLanguage(language);
        setMasterIp(masterIp);
        setMasterPort(masterPort);

        NotificationsHelper.showToastNotification(getContext(), getString(R.string.save_sucess));
    }

    private void returnToMainFragment() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment mainFragment = new MainFragment();

        fragmentTransaction.replace(R.id.drawer_layout, mainFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
