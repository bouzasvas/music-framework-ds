package gr.aueb.ds.music.android.lalapp.player;

import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;

public class DataSourceProducer {

    public static DataSource createDataSource() {
        return DataSourceProducer.createDataSource(false, null);
    }

    public static DataSource createDataSource(byte[] data) {
        return DataSourceProducer.createDataSource(false, data);
    }

    public static DataSource createDataSource(boolean onlineMode, byte[] data) {
//        return onlineMode ? new ChunksDataSourceImplementation() : new ByteArrayDataSource(data);
        return onlineMode ? new ByteArrayDataSource(data)  : new ByteArrayDataSource(data);
    }

}
