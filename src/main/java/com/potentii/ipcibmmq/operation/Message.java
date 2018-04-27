package com.potentii.ipcibmmq.operation;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Message implements Serializable {

    public static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String queueName;
    @Getter
    @Setter
    private String correlationId;
    @Getter
    @Setter
    private String body;
}
