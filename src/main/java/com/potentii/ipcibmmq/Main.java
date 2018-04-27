package com.potentii.ipcibmmq;

import com.potentii.ipc.worker.api.IPCWorker;
import com.potentii.ipcibmmq.operation.Connection;
import com.potentii.ipcibmmq.operation.Message;
import com.potentii.ipcibmmq.operation.SendMessageOperation;

public class Main {
    public static void main(String[] args){
        IPCWorker worker = new IPCWorker(System.in, System.out);

        worker.listen((req, res) -> {
            String operation = req.queryString("operation");
            switch(operation){
                case "send-message":
                    final SendMessageOperation sendMessageOperation = req.json(SendMessageOperation.class);
                    final Connection connection = sendMessageOperation.getConnection();
                    final Message message = sendMessageOperation.getMessage();

                    try{
                        if(connection == null)
                            throw new Exception("The \"connection\" information must be set");
                        if(message == null)
                            throw new Exception("The \"message\" information must be set");

                        new MessageSender().send(connection, message);
                    } catch(Exception e){
                        res.error(e);
                    }
                    break;
                case "register-connection":

                    break;
                default:
                    res.error(new Exception("Invalid operation \"" + operation + "\""));
                    break;
            }

        });
    }
}
