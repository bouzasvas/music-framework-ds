package gr.aueb.ds.music.framework.commons;

public enum SystemExitCodes {
    USER_REQUEST(1),
    INIT_APP_ERROR(-1),
    INIT_BROKER_ERROR(-2),
    INIT_PUBLISHER_ERROR(-3),
    INIT_CONSUMER_ERROR(-4),
    MASTER_NOT_FOUND_ERROR(-100);


    private int code;

    SystemExitCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
