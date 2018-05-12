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
        PCFMessageAgent pcfAgent = new PCFMessageAgent(connection.getHost(), connection.getPort(), connection.getChannel());
        PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_NAMES);

        request.addParameter(CMQC.MQCA_Q_NAME, "*");
        request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);

        PCFMessage[] responses = pcfAgent.send(request);
        String[] names = (String[]) responses[0].getParameterValue(CMQCFC.MQCACF_Q_NAMES);

        return Arrays.stream(names).map(String::trim).collect(Collectors.toList());
    }



}
