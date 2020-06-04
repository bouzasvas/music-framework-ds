package gr.aueb.ds.music.android.lalapp.request.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppCommon;

public abstract class AsyncTaskWithDialog<I, P, O> extends AsyncTask<I, P, O> {

    @SuppressLint("StaticFieldLeak")
    protected Context context;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AppCommon.showDialog(this.context, context.getString(R.string.dialog_loading));
    }

    @Override
    protected void onPostExecute(O musicFile) {
        super.onPostExecute(musicFile);
        AppCommon.dismissDialog();
    }

    @Override
    protected void onCancelled(O o) {
        super.onCancelled(o);
        AppCommon.dismissDialog();
    }
}
