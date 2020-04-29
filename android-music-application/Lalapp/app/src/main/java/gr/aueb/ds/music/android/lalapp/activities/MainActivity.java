package gr.aueb.ds.music.android.lalapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Optional;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.helpers.LogHelper;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void searchForArtists(View view) {
        // Get ArtistName from InputText
        EditText artistNameInput = findViewById(R.id.artist_name_input);

        try {
            String artistName = artistNameInput.getText().toString();

            LogHelper.logInfo(getClass(), "Searching for Artist {}", artistName);
            NotificationsHelper.showToastNotification(getApplicationContext(), getString(R.string.search_artist_toast), artistName);
        }
        catch (Exception ex) {
            LogHelper.logError(getClass(), "Could not Retrieve input value from artist_name_input EditText");
            NotificationsHelper.showToastNotification(getApplicationContext(), "test");
        }
    }
}
