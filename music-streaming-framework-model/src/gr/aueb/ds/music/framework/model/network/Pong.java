package gr.aueb.ds.music.framework.model.network;

public class Pong implements ObjectOverNetwork {
    private static final long serialVersionUID = 3725224259616030204L;

    private String msg = "pong";

    @Override
    public String getMsg() {
        return this.msg;
    }
}
