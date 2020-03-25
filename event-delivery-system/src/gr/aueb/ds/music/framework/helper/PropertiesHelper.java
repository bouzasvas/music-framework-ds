package gr.aueb.ds.music.framework.helper;

import gr.aueb.ds.music.framework.commons.SystemExitCodes;

import java.io.IOException;
import java.util.Properties;

public class PropertiesHelper {

    private static Properties props;

    public static String getProperty(String key) {
        if (props == null) loadProperties();
        return props.getProperty(key);
    }

    private static void loadProperties() {
        props = new Properties();
        try {
            props.load(ClassLoader.getSystemResourceAsStream("app.properties"));
        }
        catch (IOException ex) {
            System.err.println("Could not load Properties file app.properties");
            System.exit(SystemExitCodes.INIT_APP_ERROR.getCode());
        }
    }
}
