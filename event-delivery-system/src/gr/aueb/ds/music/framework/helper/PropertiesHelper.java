package gr.aueb.ds.music.framework.helper;

import gr.aueb.ds.music.framework.commons.ProgramArguments;
import gr.aueb.ds.music.framework.commons.SystemExitCodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertiesHelper {

    private static Properties props;

    public static String getProperty(String key) {
        if (props == null) loadProperties();
        return props.getProperty(key);
    }

    private static void loadProperties() {
        boolean sameLocationAsJar = false;

        props = new Properties();
        try {
            sameLocationAsJar = !ProgramArguments.getArgument("--property-file-jar-dir").isEmpty();

            final String classPathProperties = ProgramArguments.getArgument("--property-file-dir").get(0);
            PropertiesHelper.loadProperties(classPathProperties, sameLocationAsJar);
        }
        catch (IndexOutOfBoundsException ex) {
            PropertiesHelper.loadProperties(null, sameLocationAsJar);
        }

    }

    private static void loadProperties(String dir, boolean sameLocationAsJar) {
        try {
            if (sameLocationAsJar) {
                dir = new File("").getAbsolutePath();
            }

            if (dir == null) {
                props.load(ClassLoader.getSystemResourceAsStream("app.properties"));
            }
            else {
                System.out.println(dir);
                props.load(new FileInputStream(dir + "\\app.properties"));
            }
        }
        catch (IOException ex) {
            System.err.println("Could not load Properties file app.properties");
            System.exit(SystemExitCodes.INIT_APP_ERROR.getCode());
        }
    }
}
