package gr.aueb.ds.music.framework.commons;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;

import java.util.*;
import java.util.stream.Stream;

public class ProgramArguments {

    private static String NODE;

   public enum NodeType {
       BROKER_ARG("--broker"),
       PUBLISHER_ARG("--publisher"),
       CONSUMER_ARG("--consumer");

       private String node;

       NodeType(String node) {
           this.node = node;
       }

       public String getNode() {
           return node;
       }

       public static String mapNodeTypeToEnum(String node) {
           return Stream
                   .of(NodeType.values())
                   .map(NodeType::getNode)
                   .filter(type -> type.equals(node))
                   .findFirst().orElse(null);
       }
   }

    private static final Map<String, List<String>> PROGRAM_ARGUMENTS = new HashMap<>();

    public static void loadProgramArguments(String... args) {
        for (String arg : args) {
            if (isNodeArgument(arg)) {
                NODE = NodeType.mapNodeTypeToEnum(arg);

                if (ProgramArguments.containsNodeConfiguration()) {
                    LogHelper.error(PropertiesHelper.getProperty("main.with.args.multiple.hosts.init.not.allowed"));
                    System.exit(SystemExitCodes.INIT_APP_ERROR.getCode());
                }

                PROGRAM_ARGUMENTS.put(NODE, new ArrayList<>());
            } else if (isConfigurationArgument(arg)) {
                PROGRAM_ARGUMENTS.put(arg, Collections.emptyList());
            } else {
                // Node Properties
                List<String> nodeProperties = PROGRAM_ARGUMENTS.get(NODE);
                nodeProperties.add(arg);
            }
        }
    }

    public static List<String> getArgument(String arg) {
        return PROGRAM_ARGUMENTS.getOrDefault(arg, Collections.emptyList());
    }

    public static boolean containsNodeConfiguration() {
        return PROGRAM_ARGUMENTS.containsKey(NodeType.BROKER_ARG.getNode())
                || PROGRAM_ARGUMENTS.containsKey(NodeType.PUBLISHER_ARG.getNode())
                || PROGRAM_ARGUMENTS.containsKey(NodeType.CONSUMER_ARG.getNode());
    }

    public static boolean containsArg(String arg) {
        return PROGRAM_ARGUMENTS.containsKey(arg);
    }

    private static boolean isNodeArgument(String arg) {
        return arg.equals(NodeType.BROKER_ARG.getNode()) || arg.equals(NodeType.PUBLISHER_ARG.getNode()) || arg.equals(NodeType.CONSUMER_ARG.getNode());
    }

    private static boolean isConfigurationArgument(String arg) {
        return arg.startsWith("--");
    }
}
