package com.potentii.ipcibmmq;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;
import com.potentii.ipcibmmq.operation.Connection;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ibm.mq.constants.CMQCFC.MQGACF_Q_STATISTICS_DATA;
import static com.ibm.mq.constants.CMQCFC.MQIACF_OPEN_OPTIONS;

public class QueuesBrowser {


    public List<String> listQueues(@NotNull final Connection connection) throws MQException, IOException {
        if(connection.getHost() == null)
            throw new IllegalArgumentException("The connection host must be set");
        if(connection.getPort() == 0)
            throw new IllegalArgumentException("The connection host port must be set");

        PCFMessageAgent pcfAgent = new PCFMessageAgent(connection.getHost(), connection.getPort(), connection.getChannel() == null ? "" : connection.getChannel());
        PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_NAMES);

        request.addParameter(CMQC.MQCA_Q_NAME, "*");
        request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);

        PCFMessage[] responses = pcfAgent.send(request);
        String[] names = (String[]) responses[0].getParameterValue(CMQCFC.MQCACF_Q_NAMES);

        return Arrays.stream(names).map(String::trim).collect(Collectors.toList());
    }


    public Object listQueueMessages(@NotNull final Connection connection, @NotNull final String queueName) throws MQException, IOException {
        PCFMessageAgent pcfAgent = new PCFMessageAgent(connection.getHost(), connection.getPort(), connection.getChannel());
        PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_STATUS);

        request.addParameter(CMQC.MQCA_Q_NAME, queueName);
        request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);

        request.addParameter(CMQCFC.MQIACF_Q_STATUS_TYPE, CMQCFC.MQIACF_Q_STATUS);
        request.addParameter(CMQCFC.MQIACF_Q_STATUS_ATTRS, new int[] {
                CMQC.MQCA_Q_NAME, CMQC.MQIA_CURRENT_Q_DEPTH,
                CMQCFC.MQCACF_LAST_GET_DATE, CMQCFC.MQCACF_LAST_GET_TIME,
                CMQCFC.MQCACF_LAST_PUT_DATE, CMQCFC.MQCACF_LAST_PUT_TIME,
                CMQCFC.MQIACF_OLDEST_MSG_AGE, CMQC.MQIA_OPEN_INPUT_COUNT,
                CMQC.MQIA_OPEN_OUTPUT_COUNT, CMQCFC.MQIACF_UNCOMMITTED_MSGS,
                MQIACF_OPEN_OPTIONS});

        PCFMessage[] responses = pcfAgent.send(request);
        Object res = responses[0].getParameterValue(MQGACF_Q_STATISTICS_DATA);

        return res;
    }


    public void browse(@NotNull final Connection connection, @NotNull final String queueName) throws MQException {
        List<String> messages = new ArrayList<>();
        MQQueueManager QMgr = new MQQueueManager(connection.getQueueManager());
        int openOptions = CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INPUT_SHARED | CMQC.MQOO_BROWSE;

        MQQueue queue = QMgr.accessQueue(queueName, openOptions);

        MQMessage theMessage = new MQMessage();
        MQGetMessageOptions gmo = new MQGetMessageOptions();
        gmo.options=CMQC.MQGMO_WAIT | CMQC.MQGMO_BROWSE_FIRST;
        gmo.matchOptions=CMQC.MQMO_NONE;
        gmo.waitInterval=5000;

        boolean thereAreMessages=true;
        while(thereAreMessages){
            try{
                //read the message
                queue.get(theMessage,gmo);
                //print the text
                String msgText = theMessage.readString(theMessage.getMessageLength());
                messages.add(msgText);

                //move cursor to the next message
                gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_BROWSE_NEXT;

            } catch(MQException e){
//                if(e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE)
//                    System.out.println("no more message available or retrived");

                thereAreMessages = false;
            } catch (IOException e){
                System.out.println("ERROR: "+e.getMessage());
            }
        }

        messages.forEach(System.out::println);
    }




}
