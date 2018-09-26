package com.potentii.ipcibmmq;

import com.potentii.ipc.worker.api.IPCWorker;
import com.potentii.ipcibmmq.operation.Connection;
import com.potentii.ipcibmmq.operation.ListQueuesOperation;
import com.potentii.ipcibmmq.operation.Message;
import com.potentii.ipcibmmq.operation.SendMessageOperation;
import org.springframework.jms.JmsException;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        IPCWorker worker = new IPCWorker(System.in, System.out, IPCWorker.Charset.UTF8);

        worker.listen((req, res) -> {
            String operation = req.queryString("operation");
            switch(operation){


                case "send-message": {
                    final SendMessageOperation sendMessageOperation = req.json(SendMessageOperation.class);
                    final Connection connection = sendMessageOperation.getConnection();
                    final Message message = sendMessageOperation.getMessage();

                    try {
                        if (connection == null)
                            throw new Exception("The \"connection\" information must be set");
                        if (message == null)
                            throw new Exception("The \"message\" information must be set");

                        new MessageSender().send(connection, message);
                    } catch(JmsException e){
                        res.error(getCleanCause(e));
                    } catch(Exception e){
                        res.error(cleanError(e));
                    }
                    break;
                }


                case "list-queues": {
                    final ListQueuesOperation listQueuesOperation = req.json(ListQueuesOperation.class);
                    final Connection connection = listQueuesOperation.getConnection();

                    try {
                        if (connection == null)
                            throw new Exception("The \"connection\" information must be set");

                        List<String> queueNames = new QueuesBrowser().listQueues(connection);

                        res.json(queueNames);

                    } catch(JmsException e){
                        res.error(getCleanCause(e));
                    } catch(Exception e){
                        res.error(cleanError(e));
                    }
                    break;
                }


                default: {
                    res.error(new UnsupportedOperationException("Invalid operation \"" + operation + "\""));
                    break;
                }


            }

        });
    }


    private static Throwable cleanError(Throwable originalThrowable){
        originalThrowable.setStackTrace(new StackTraceElement[]{});
        return originalThrowable;
    }

    private static Throwable cleanError(JMSException originalThrowable){

        originalThrowable.setStackTrace(new StackTraceElement[]{});
        originalThrowable.setLinkedException(null);
        return originalThrowable;
    }

    private static Throwable getCleanCause(Throwable originalThrowable){
        Throwable t = (originalThrowable.getCause() == null)
                ? originalThrowable
                : originalThrowable.getCause();

        if(t instanceof JMSException){
           return cleanError((JMSException) t);
        } else{
            return cleanError(t);
        }
    }

}
