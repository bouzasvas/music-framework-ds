package gr.aueb.ds.music.framework.error;

import gr.aueb.ds.music.framework.helper.PropertiesHelper;

public class PublisherNotFoundException extends Exception {

    private static final String PUBLISHER_NOT_FOUND_MSG = PropertiesHelper.getProperty("publisher.not.found.for.artist");

    public PublisherNotFoundException() {
        this("Unknown");
    }

    public PublisherNotFoundException(String artist) {
        super(String.format(PUBLISHER_NOT_FOUND_MSG, artist));
    }
}
