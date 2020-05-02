import gr.aueb.ds.music.framework.commons.ProgramArguments;
import gr.aueb.ds.music.framework.commons.SystemExitCodes;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.nodes.impl.BrokerImplementation;
import gr.aueb.ds.music.framework.nodes.impl.ConsumerImplementation;
import gr.aueb.ds.music.framework.nodes.impl.PublisherImplementation;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String EMPTY_STRING = "";

    private final static Scanner userInput = new Scanner(System.in);

    /**
     *
     *  The main function that Runs the Event Delivery System Application
     *
     * @param args The Program Arguments passed by user when running the App:
     *
     *             --no-colors: Show Menu Items & System out Logs without Colors
     *             --online: Run the app using Internet IP Addresses instead of Local
     *             --broker [broker_name] [broker_port]
     *             --publisher [publisher_name] [publisher_port] [from_artist_range] [to__artist_range]
     *             --consumer [consumer_name]
     */
    public static void main(String... args) {
        ProgramArguments.loadProgramArguments(args);

        if (ProgramArguments.containsNodeConfiguration()) {
            initAppFromArguments();
        }
        else {
            startMenu();
        }
    }

    private static void initAppFromArguments() {
        List<String> brokerConfigurationValues = ProgramArguments.getArgument(ProgramArguments.NodeType.BROKER_ARG.getNode());
        List<String> publisherConfigurationValues = ProgramArguments.getArgument(ProgramArguments.NodeType.PUBLISHER_ARG.getNode());
        List<String> consumerConfigurationValues = ProgramArguments.getArgument(ProgramArguments.NodeType.CONSUMER_ARG.getNode());

        if (!brokerConfigurationValues.isEmpty()) initBrokerFromArgs(brokerConfigurationValues);
        if (!publisherConfigurationValues.isEmpty()) initPublisherFromArgs(publisherConfigurationValues);
        if (!consumerConfigurationValues.isEmpty()) initConsumerFromArgs(consumerConfigurationValues);
    }

    private static void startMenu() {
        String userChoice = EMPTY_STRING;
        while (!userChoice.equals(PropertiesHelper.getProperty("main.menu.exit.command"))) {
            showMenu();
            userChoice = userInput.nextLine();

            switch (userChoice.toUpperCase()) {
                case "B":
                    initBroker();
                    break;
                case "C":
                    initConsumer();
                    break;
                case "P":
                    initPublisher();
                    break;
                case "E":
                    LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.exit.message"), true);
                    System.exit(SystemExitCodes.USER_REQUEST.getCode());
                    break;
                default:
                    LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.not.available.option"), true);
                    LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.empty.line"), true);
                    break;
            }
        }
    }

    private static void showMenu() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(String.format(PropertiesHelper.getProperty("main.menu.header"), PropertiesHelper.getProperty("main.menu.exit.command")))
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)
                .append(PropertiesHelper.getProperty("main.menu.broker"))
                    .append(LINE_SEPARATOR)
                .append(PropertiesHelper.getProperty("main.menu.consumer"))
                    .append(LINE_SEPARATOR)
                .append(PropertiesHelper.getProperty("main.menu.publisher"))
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);

        LogHelper.printMenuItem(sb.toString(), true);
        LogHelper.printMenuItem((PropertiesHelper.getProperty("main.menu.user.choice")), false);
    }

    private static void initBroker() {
        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.broker.name"), true);
        String brokerName = userInput.nextLine();

        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.broker.port"), true);
        int port = userInput.nextInt();

        Broker broker = null;
        try {
            broker = new BrokerImplementation(brokerName, port);
            broker.init();
        }
        catch (IOException ex) {
            System.err.println(String.format(PropertiesHelper.getProperty("main.init.broker.error"), port));
            System.exit(SystemExitCodes.INIT_BROKER_ERROR.getCode());
        }
    }

    private static void initBrokerFromArgs(List<String> brokerArgs) {
        String brokerName;
        String brokerPort = "";
        try {
            brokerName = brokerArgs.get(0);
            brokerPort = brokerArgs.get(1);

            Broker broker = new BrokerImplementation(brokerName, Integer.parseInt(brokerPort));
            broker.init();
        } catch (IndexOutOfBoundsException e) {
            LogHelper.error(PropertiesHelper.getProperty("main.with.args.broker.wrong.args"));
            System.exit(SystemExitCodes.INIT_APP_ERROR.getCode());
        } catch (IOException e) {
            LogHelper.error(String.format(PropertiesHelper.getProperty("main.init.broker.error"), brokerPort));
            System.exit(SystemExitCodes.INIT_BROKER_ERROR.getCode());
        }
    }

    private static void initConsumer() {
        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.consumer.name"), false);
        String consumerName = userInput.nextLine();

        Consumer consumer;
        try {
            consumer = new ConsumerImplementation(consumerName);
            consumer.init();
        } catch (IOException ex){
            System.err.println(PropertiesHelper.getProperty("main.init.consumer.error"));
            System.exit(SystemExitCodes.INIT_CONSUMER_ERROR.getCode());
        }
    }

    private static void initConsumerFromArgs(List<String> consumerArgs) {
        try {
            String name = consumerArgs.get(0);

            Consumer consumer = new ConsumerImplementation(name);
            consumer.init();
        } catch (IndexOutOfBoundsException ex) {
            LogHelper.error(PropertiesHelper.getProperty("main.with.args.consumer.wrong.args"));
            System.exit(SystemExitCodes.INIT_APP_ERROR.getCode());
        } catch (IOException ex){
            LogHelper.error(PropertiesHelper.getProperty("main.init.consumer.error"));
            System.exit(SystemExitCodes.INIT_CONSUMER_ERROR.getCode());
        }
    }

    private static void initPublisher() {
        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.publisher.name"), true);
        String publisherName = userInput.nextLine();

        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.publisher.port"), true);
        int port = userInput.nextInt();

        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.publisher.artist.start"), false);
        String artistStart = userInput.next();

        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.publisher.artist.end"), false);
        String artistEnd = userInput.next();

        Publisher publisher;
        try {
            publisher = new PublisherImplementation(publisherName, port, artistStart, artistEnd);
            publisher.init();
        }
        catch (IOException ex) {
            System.err.println(String.format(PropertiesHelper.getProperty("main.init.publisher.error"), port));
            System.exit(SystemExitCodes.INIT_PUBLISHER_ERROR.getCode());
        }
    }

    private static void initPublisherFromArgs(List<String> publisherArgs) {
        String name;
        String port = "";
        String artistStart;
        String artistEnd;
        try {
            name = publisherArgs.get(0);
            port = publisherArgs.get(1);
            artistStart = publisherArgs.get(2);
            artistEnd = publisherArgs.get(3);

            Publisher publisher = new PublisherImplementation(name, Integer.parseInt(port), artistStart, artistEnd);
            publisher.init();
        } catch (IndexOutOfBoundsException ex) {
            LogHelper.error(PropertiesHelper.getProperty("main.with.args.publisher.wrong.args"));
            System.exit(SystemExitCodes.INIT_APP_ERROR.getCode());
        }
        catch (IOException ex) {
            System.err.println(String.format(PropertiesHelper.getProperty("main.init.publisher.error"), port));
            System.exit(SystemExitCodes.INIT_PUBLISHER_ERROR.getCode());
        }
    }
}
