package com.potentii.ipcibmmq;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.potentii.ipcibmmq.operation.Connection;
import com.potentii.ipcibmmq.operation.Message;
import com.potentii.ipcibmmq.util.CorrelationIdPostProcessor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.core.JmsTemplate;
import javax.jms.JMSException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MessageSender {


    public void send(@NotNull final Connection connection, @NotNull final Message message) throws JMSException {
        if(connection.getHost() == null)
            throw new IllegalArgumentException("The connection host must be set");
        if(connection.getPort() == 0)
            throw new IllegalArgumentException("The connection host port must be set");
        if(connection.getQueueManager() == null)
            throw new IllegalArgumentException("The connection queue manager must be set");

        if(message.getQueueName() == null)
            throw new IllegalArgumentException("The message destination must be set");

        CorrelationIdPostProcessor correlationIdProcessor = (message.getCorrelationId() == null)
                ? null
                : new CorrelationIdPostProcessor(message.getCorrelationId());

        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(connection.getHost());
        mqQueueConnectionFactory.setPort(connection.getPort());
        mqQueueConnectionFactory.setQueueManager(connection.getQueueManager());
        if(connection.getChannel() != null)
            mqQueueConnectionFactory.setChannel(connection.getChannel());
        mqQueueConnectionFactory.setTransportType(1);
        mqQueueConnectionFactory.setAppName("ipc-ibm-mq");
        mqQueueConnectionFactory.setCCSID(819);

        JmsTemplate jmsTemplate = new JmsTemplate(mqQueueConnectionFactory);
        jmsTemplate.setReceiveTimeout(5000L);
        jmsTemplate.setPubSubDomain(false);

        String rawMessage = new String(Base64.getMimeDecoder().decode(message.getBody()), StandardCharsets.UTF_8);

        if(correlationIdProcessor == null)
            jmsTemplate.convertAndSend(message.getQueueName(), rawMessage);
        else
            jmsTemplate.convertAndSend(message.getQueueName(), rawMessage, correlationIdProcessor);

    }


}
