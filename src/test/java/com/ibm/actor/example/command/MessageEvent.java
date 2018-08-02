package com.ibm.actor.example.command;

import com.ibm.actor.message.Message;

import java.util.EventObject;

/**
 * Author: ZhangXiao
 * Created: 2016/9/19
 */
public class MessageEvent extends EventObject {

    private Message message;

    private boolean consumed = false;
    
    MessageEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }
    
    boolean isConsumed() {
        return consumed;
    }
    
    void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}