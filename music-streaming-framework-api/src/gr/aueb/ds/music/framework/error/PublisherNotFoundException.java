package gr.aueb.ds.music.framework.error;

public class PublisherNotFoundException extends Exception {

    private static final String PUBLISHER_NOT_FOUND_MSG = "Could not find Publisher that serves the Artist \"%s\"";

    public PublisherNotFoundException() {
        this("Unknown");
    }

    public PublisherNotFoundException(String artist) {
        super(String.format(PUBLISHER_NOT_FOUND_MSG, artist));
    }
}
