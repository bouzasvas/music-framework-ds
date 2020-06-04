package gr.aueb.ds.music.android.lalapp.player;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;

public class DataSourceProducer {


    public static DataSource createDataSource() {
        return DataSourceProducer.createDataSource(false, null);
    }

    public static MediaSource createMediaSource(Context context, String fileName) {
        File mfStorage = AppFileOperations.getMusicFileFromName(context, fileName);
        Uri fileUri = Uri.fromFile(mfStorage);

        // Create a data source factory.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(context, Util.getUserAgent(context, "app-name"));

        DefaultExtractorsFactory extractorsFactory =
                new DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true).setMp3ExtractorFlags(Mp3Extractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING);
// Create a progressive media source pointing to a stream uri.
//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
//                .createMediaSource(fileUri);

        DefaultDataSourceFactory factory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "app-name"));

        MediaSource mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(fileUri);

//        return new ProgressiveMediaSource.Factory(
//                new ResolvingDataSource.Factory(
//                        new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "app-name")),
//                        // Provide just-in-time URI resolution logic.
//                        (DataSpec dataSpec)-> dataSpec.withUri(fileUri)).createDataSource();

        return mediaSource;
    }

    public static DataSource createDataSource(byte[] data) {
        return DataSourceProducer.createDataSource(false, data);
    }

    public static DataSource createDataSource(boolean onlineMode, byte[] data) {
//        return onlineMode ? new ChunksDataSourceImplementation() : new ByteArrayDataSource(data);
        return onlineMode ? new ByteArrayDataSource(data)  : new ByteArrayDataSource(data);
    }

}
