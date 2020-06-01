package gr.aueb.ds.music.android.lalapp.player;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;

import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;

public class DataSourceProducer {


    public static DataSource createDataSource() {
        return DataSourceProducer.createDataSource(false, null);
    }

    public static MediaSource createMediaSource(Context context, String fileName) {
        File mfStorage = AppFileOperations.getMusicFileFromName(context, fileName);
        Uri fileUri = Uri.fromFile(mfStorage);

        return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(context, "Lalapp"))
                .createMediaSource(fileUri);
    }

    public static DataSource createDataSource(byte[] data) {
        return DataSourceProducer.createDataSource(false, data);
    }

    public static DataSource createDataSource(boolean onlineMode, byte[] data) {
//        return onlineMode ? new ChunksDataSourceImplementation() : new ByteArrayDataSource(data);
        return onlineMode ? new ByteArrayDataSource(data)  : new ByteArrayDataSource(data);
    }

}
