package gr.aueb.ds.music.android.lalapp.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import java.util.List;
import java.util.Objects;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener;
import gr.aueb.ds.music.android.lalapp.RecyclerViewAdapter;
import gr.aueb.ds.music.android.lalapp.common.AppCommon;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.helpers.LogHelper;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.request.async.AsyncTaskError;
import gr.aueb.ds.music.android.lalapp.request.async.AsyncTaskProgress;
import gr.aueb.ds.music.android.lalapp.request.async.BrokerAsyncRequest;
import gr.aueb.ds.music.android.lalapp.request.async.OfflineTrackAsyncRequest;
import gr.aueb.ds.music.android.lalapp.request.async.TrackAsyncRequest;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class MainFragment extends ParentFragment implements RecyclerItemClickListener.OnRecyclerClickListener, View.OnClickListener {

    private Consumer consumer;
    private List<MusicFile> retrievedMusicFiles;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private Switch switchButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.activity_main, container, false);
        initListeners(root);

        retrieveDownloadedFiles(switchButton.isChecked());
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();

        retrieveDownloadedFiles(switchButton.isChecked());
        initOnLayoutTouchListener(Objects.requireNonNull(getView()));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.search_artist_btn:
                this.searchForArtists(getView());
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void retrieveDownloadedFiles(boolean onlineMode) {
        // When in offline mode retrieve Music Files from Device Storage
        if (!onlineMode) {
            try {
                this.retrievedMusicFiles = AppFileOperations.getDownloadedFiles(Objects.requireNonNull(getContext()));
                adapter = new RecyclerViewAdapter(this.retrievedMusicFiles);
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(Objects.requireNonNull(getContext())), DividerItemDecoration.VERTICAL));
            } catch (Exception e) {
                NotificationsHelper.showToastNotification(Objects.requireNonNull(getContext()), getString(R.string.loading_local_files_failed));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initListeners(View root) {
        initSearchButtonListener(root);

        initRecyclerViewListener(root);
        initSwitchListener(root);
        initOnLayoutTouchListener(root);
    }

    public void searchForArtists(View view) {
        AppCommon.hideKeyboard(Objects.requireNonNull(getActivity()), view);
        recyclerView.setForeground(null);
        // Get ArtistName from InputText
        EditText artistNameInput = view.findViewById(R.id.artist_name_input);

        try {
            String artistName = artistNameInput.getText().toString();
            ArtistName artistNameReq = new ArtistName(artistName, true);

            LogHelper.logInfo(getClass(), "Searching for Artist {}", artistName);
            NotificationsHelper.showToastNotification(Objects.requireNonNull(getContext()), getString(R.string.search_artist_toast), artistName);

            // Send Request to AsyncTask
            BrokerAsyncRequest brokerAsyncRequest = new BrokerAsyncRequest(Objects.requireNonNull(getContext()), this.asyncTaskProgress, this.getAllSettings());
            brokerAsyncRequest.execute(artistNameReq);

            this.consumer = brokerAsyncRequest.getConsumer();
        }
        catch (Exception ex) {
            LogHelper.logError(getClass(), "Could not Retrieve input value from artist_name_input EditText");
            NotificationsHelper.showToastNotification(Objects.requireNonNull(getContext()), "test");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(View view, int position) {
        boolean onlineMode = switchButton.isChecked();
        MusicFile selectedTrack = this.retrievedMusicFiles.get(position);

        if (AppFileOperations.musicFileExists(Objects.requireNonNull(getContext()), selectedTrack)) {
            OfflineTrackAsyncRequest offlineTrackAsyncRequest = new OfflineTrackAsyncRequest(Objects.requireNonNull(getContext()));
            offlineTrackAsyncRequest.execute(selectedTrack);
        }
        else {
            TrackAsyncRequest trackAsyncRequest = new TrackAsyncRequest(Objects.requireNonNull(getContext()), this.consumer, onlineMode);
            trackAsyncRequest.execute(selectedTrack);
        }
    }

    // Listeners
    private void initSearchButtonListener(View root) {
        root.findViewById(R.id.search_artist_btn).setOnClickListener(this);
    }

    private void initRecyclerViewListener(View root) {
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getContext())));

        recyclerView.addOnItemTouchListener(new gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener(Objects.requireNonNull(getContext()), recyclerView, this));
    }

    private void initSwitchListener(View root) {
        switchButton = root.findViewById(R.id.offline_switch);
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) ->
                switchButton.setText(switchButton.isChecked() ? getString(R.string.switch_online) : getString(R.string.switch_offline)));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOnLayoutTouchListener(View fragmentView) {
        ConstraintLayout layout = fragmentView.findViewById(R.id.main_activity_layout);
        layout.setOnTouchListener((view, event) -> {
            AppCommon.hideKeyboard(Objects.requireNonNull(getActivity()), view);
            return false;
        });
    }

    // AsyncTaskProgress instance
    private AsyncTaskProgress asyncTaskProgress = new AsyncTaskProgress() {
        @Override
        public void onSuccessfulRequest(List<MusicFile> musicFiles) {
            MainFragment.this.retrievedMusicFiles = musicFiles;
            adapter = new RecyclerViewAdapter(musicFiles);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        }

        @Override
        public void onFailedRequest(AsyncTaskError asyncTaskError) {
//            NotificationsHelper.showToastNotification(this.context, this.error.getErrorMessage(), this.masterHostIP, String.valueOf(this.masterPort));
            NotificationsHelper.showToastNotification(Objects.requireNonNull(getContext()), asyncTaskError.getErrorMessage());
        }
    };
}
