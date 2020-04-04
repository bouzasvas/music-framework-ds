package gr.aueb.ds.music.framework.error;

import gr.aueb.ds.music.framework.helper.PropertiesHelper;

public class FileChunksProcessingException extends Exception {

    public FileChunksProcessingException(String message) {
        super(PropertiesHelper.getProperty(message));
    }

}
