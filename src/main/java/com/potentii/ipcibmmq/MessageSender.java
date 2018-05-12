package com.potentii.ipcibmmq;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.potentii.ipcibmmq.operation.Connection;
import com.potentii.ipcibmmq.operation.Message;
import com.potentii.ipcibmmq.util.CorrelationIdPostProcessor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;

public class MessageSender {


    public void send(@NotNull final Connection connection, @NotNull final Message message) throws JMSException {
        if(message.getQueueName() == null)
            throw new IllegalArgumentException("The message destination must be set");

        CorrelationIdPostProcessor correlationIdProcessor = (message.getCorrelationId() == null)
                ? null
                : new CorrelationIdPostProcessor(message.getCorrelationId());

        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(connection.getHost());
        mqQueueConnectionFactory.setPort(connection.getPort());
        mqQueueConnectionFactory.setQueueManager(connection.getQueueManager());
        mqQueueConnectionFactory.setChannel(connection.getChannel());
        mqQueueConnectionFactory.setTransportType(1);
        mqQueueConnectionFactory.setAppName("ipc-ibm-mq");

        JmsTemplate jmsTemplate = new JmsTemplate(mqQueueConnectionFactory);
        jmsTemplate.setReceiveTimeout(5000L);
        jmsTemplate.setPubSubDomain(false);

        if(correlationIdProcessor == null)
            jmsTemplate.convertAndSend(message.getQueueName(), message.getBody());
        else
            jmsTemplate.convertAndSend(message.getQueueName(), message.getBody(), correlationIdProcessor);
    }


}
