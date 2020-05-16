package gr.aueb.ds.music.android.lalapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import java.util.List;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener;
import gr.aueb.ds.music.android.lalapp.RecyclerViewAdapter;
import gr.aueb.ds.music.android.lalapp.common.AppCommon;
import gr.aueb.ds.music.android.lalapp.helpers.LogHelper;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.request.async.AsyncTaskError;
import gr.aueb.ds.music.android.lalapp.request.async.AsyncTaskProgress;
import gr.aueb.ds.music.android.lalapp.request.async.BrokerAsyncRequest;
import gr.aueb.ds.music.android.lalapp.request.async.TrackAsyncRequest;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class MainActivity extends ParentActivity
        implements RecyclerItemClickListener.OnRecyclerClickListener{

    private Consumer consumer;
    private List<MusicFile> retrievedMusicFiles;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private Switch switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initOnLayoutTouchListener();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener(this, recyclerView, this));

        switchButton = findViewById(R.id.offline_switch);
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) ->
                switchButton.setText(switchButton.isChecked() ? getString(R.string.switch_online) : getString(R.string.switch_offline)));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOnLayoutTouchListener() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_activity_layout);
        layout.setOnTouchListener((view, event) -> {
            AppCommon.hideKeyboard(this, view);
            return false;
        });
    }

    public void searchForArtists(View view) {
        AppCommon.hideKeyboard(this, view);
        recyclerView.setForeground(null);
        // Get ArtistName from InputText
        EditText artistNameInput = findViewById(R.id.artist_name_input);

        try {
            String artistName = artistNameInput.getText().toString();
            ArtistName artistNameReq = new ArtistName(artistName, true);

            LogHelper.logInfo(getClass(), "Searching for Artist {}", artistName);
            NotificationsHelper.showToastNotification(getApplicationContext(), getString(R.string.search_artist_toast), artistName);

            // Send Request to AsyncTask
            BrokerAsyncRequest brokerAsyncRequest = new BrokerAsyncRequest(MainActivity.this, this.asyncTaskProgress, this.getAllSettings());
            brokerAsyncRequest.execute(artistNameReq);

            this.consumer = brokerAsyncRequest.getConsumer();
        }
        catch (Exception ex) {
            LogHelper.logError(getClass(), "Could not Retrieve input value from artist_name_input EditText");
            NotificationsHelper.showToastNotification(getApplicationContext(), "test");
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        boolean onlineMode = switchButton.isChecked();

        MusicFile selectedTrack = this.retrievedMusicFiles.get(position);

        TrackAsyncRequest trackAsyncRequest = new TrackAsyncRequest(MainActivity.this, this.consumer, onlineMode);
        trackAsyncRequest.execute(selectedTrack);
    }

    // AsyncTaskProgress instance
    private AsyncTaskProgress asyncTaskProgress = new AsyncTaskProgress() {
        @Override
        public void onSuccessfulRequest(List<MusicFile> musicFiles) {
            MainActivity.this.retrievedMusicFiles = musicFiles;
            adapter = new RecyclerViewAdapter(musicFiles);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        }

        @Override
        public void onFailedRequest(AsyncTaskError asyncTaskError) {
//            NotificationsHelper.showToastNotification(this.context, this.error.getErrorMessage(), this.masterHostIP, String.valueOf(this.masterPort));
            NotificationsHelper.showToastNotification(getApplicationContext(), asyncTaskError.getErrorMessage());
        }
    };
}
