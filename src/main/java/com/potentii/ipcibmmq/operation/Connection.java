package com.potentii.ipcibmmq.operation;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Connection implements Serializable {

    public static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String host;
    @Getter
    @Setter
    private int port;
    @Getter
    @Setter
    private String channel;
    @Getter
    @Setter
    private String queueManager;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
}
