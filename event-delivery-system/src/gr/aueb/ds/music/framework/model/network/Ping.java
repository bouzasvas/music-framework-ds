package gr.aueb.ds.music.framework.model.network;

public class Ping implements ObjectOverNetwork {
    private static final long serialVersionUID = -2550234138295605903L;

    private String msg = "ping";

    @Override
    public String getMsg() {
        return this.msg;
    }
}
