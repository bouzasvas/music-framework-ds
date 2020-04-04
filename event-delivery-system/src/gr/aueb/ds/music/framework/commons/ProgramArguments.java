package gr.aueb.ds.music.framework.commons;

import java.util.HashMap;
import java.util.Map;

public class ProgramArguments {

    private static final Map<String, Object> PROGRAM_ARGUMENTS = new HashMap<>();

    public static void loadProgramArguments(String... args) {
        for (String arg : args) {
            PROGRAM_ARGUMENTS.put(arg, true);
        }
    }

    public static Object getArgument(String arg) {
        return PROGRAM_ARGUMENTS.get(arg);
    }
}
