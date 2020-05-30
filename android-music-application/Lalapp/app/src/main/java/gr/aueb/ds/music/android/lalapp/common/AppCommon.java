package gr.aueb.ds.music.android.lalapp.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Optional;

public class AppCommon {

    private static ProgressDialog progressDialog;

    public static void showDialog(Context context, String msg) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public static void dismissDialog() {
        progressDialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> T readDataFromIntent(Intent intent, String param) {
        return (T) Optional
                .ofNullable(intent.getExtras())
                .map(bundle -> bundle.get(param))
                .orElse(null);
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
