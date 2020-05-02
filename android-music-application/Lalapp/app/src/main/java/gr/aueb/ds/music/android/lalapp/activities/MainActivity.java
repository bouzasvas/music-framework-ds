package gr.aueb.ds.music.android.lalapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener;
import gr.aueb.ds.music.android.lalapp.RecyclerViewAdapter;
import gr.aueb.ds.music.android.lalapp.helpers.LogHelper;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.request.async.AsyncTaskProgress;
import gr.aueb.ds.music.android.lalapp.request.async.BrokerAsyncRequest;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class MainActivity extends AppCompatActivity
        implements RecyclerItemClickListener.OnRecyclerClickListener{

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private Switch switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener(this, recyclerView, this));
//        ArrayList<MusicFile> list = new ArrayList<>();
//        MusicFile mf1 = new MusicFile();
//        MusicFile mf2 = new MusicFile();
//        MusicFile mf3 = new MusicFile();
//        mf1.setTrackName("Kostas");
//        mf2.setTrackName("Tass");
//        mf3.setTrackName("Vouzas");
//        list.add(mf1);
//        list.add(mf2);
//        list.add(mf3);
//        adapter = new RecyclerViewAdapter(list);
//        recyclerView.setAdapter(adapter);

        switchButton = findViewById(R.id.offline_switch);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    public void searchForArtists(View view) {
        // Get ArtistName from InputText
        EditText artistNameInput = findViewById(R.id.artist_name_input);

        try {
            String artistName = artistNameInput.getText().toString();
            ArtistName artistNameReq = new ArtistName(artistName, true);

            LogHelper.logInfo(getClass(), "Searching for Artist {}", artistName);
            NotificationsHelper.showToastNotification(getApplicationContext(), getString(R.string.search_artist_toast), artistName);

            // Send Request to AsyncTask
            new BrokerAsyncRequest(this.asyncTaskProgress).execute(artistNameReq);
        }
        catch (Exception ex) {
            LogHelper.logError(getClass(), "Could not Retrieve input value from artist_name_input EditText");
            NotificationsHelper.showToastNotification(getApplicationContext(), "test");
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("OnItemClick", "Tap at position" + position);
        Toast.makeText(this, "Tap at position" + position, Toast.LENGTH_SHORT).show();
    }

    // AsyncTaskProgress instance
    private AsyncTaskProgress asyncTaskProgress = new AsyncTaskProgress() {
        @Override
        public void onSuccessfulRequest(List<MusicFile> musicFiles) {
            adapter = new RecyclerViewAdapter(musicFiles);
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onFailedRequest() {
//            NotificationsHelper.showToastNotification(this.context, this.error.getErrorMessage(), this.masterHostIP, String.valueOf(this.masterPort));
            NotificationsHelper.showToastNotification(getApplicationContext(), "Connection failed!");
        }
    };
}
