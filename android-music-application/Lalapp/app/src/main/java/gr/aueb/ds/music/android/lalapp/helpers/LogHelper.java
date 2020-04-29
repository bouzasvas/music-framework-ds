package gr.aueb.ds.music.android.lalapp.helpers;

import android.util.Log;

import gr.aueb.ds.music.android.lalapp.common.CommonConfiguration;

public class LogHelper {

    public static void logInfo(Class<?> clazz, String message, String... params) {
        message = formatMessage(message, params);
        Log.d(clazz.getSimpleName(), message);
    }

    public static void logError(Class<?> clazz, String message, String... params) {
        message = formatMessage(message, params);
        Log.d(clazz.getSimpleName(), message);
    }

    // Helper Methods
    public static String formatMessage(String message, String... params) {
        if (params == null) return message;

        for (String param : params) {
            message = message.replace(CommonConfiguration.PARAM_DECIMETER, param);
        }

        return message;
    }
}
