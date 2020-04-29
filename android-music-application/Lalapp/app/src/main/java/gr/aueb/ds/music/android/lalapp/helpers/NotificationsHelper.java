package gr.aueb.ds.music.android.lalapp.helpers;

import android.content.Context;
import android.widget.Toast;

public class NotificationsHelper {

    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

    public static void showToastNotification(Context context, String message, String... params) {
        if (params != null) message = LogHelper.formatMessage(message, params);

        Toast toast = Toast.makeText(context, message, TOAST_DURATION);
        toast.show();
    }
}
