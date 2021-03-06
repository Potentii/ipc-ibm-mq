package com.potentii.ipcibmmq.operation;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class SendMessageOperation implements Serializable {

    public static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private Message message;
    @Getter
    @Setter
    private Connection connection;

}
