package com.potentii.ipcibmmq.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.jms.core.MessagePostProcessor;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.bind.DatatypeConverter;

public class CorrelationIdPostProcessor implements MessagePostProcessor {
    private final String correlationId;

    public CorrelationIdPostProcessor(final String correlationId) {
        this.correlationId = correlationId;
    }

    @NotNull
    @Override
    public Message postProcessMessage(@NotNull final Message msg) throws JMSException {
        byte[] hexCorrelationIdBytes = DatatypeConverter.parseHexBinary(correlationId);
        msg.setJMSCorrelationIDAsBytes(hexCorrelationIdBytes);
        return msg;
    }
}
