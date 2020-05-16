package gr.aueb.ds.music.android.lalapp.request.async;

public class AsyncTaskError {
    private String errorMessage;
    private Boolean connectionError;
    private Exception exception;

    public AsyncTaskError() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getConnectionError() {
        return connectionError;
    }

    public void setConnectionError(Boolean connectionError) {
        this.connectionError = connectionError;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
