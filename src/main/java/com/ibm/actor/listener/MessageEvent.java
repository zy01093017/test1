package com.ibm.actor.listener;

import com.ibm.actor.message.Message;

import java.util.EventObject;

/**
 * Sent when a message is received.
 *
 * @author BFEIGENB
 */
public class MessageEvent extends EventObject {

    /**
     * Possible message events.
     *
     * @author BFEIGENB
     */
    public enum MessageStatus {
        SENT, DELIVERED, COMPLETED, FAILED
    }
    
    private MessageStatus status;

    protected Message message;

    public MessageStatus getStatus() {
        return status;
    }

    public MessageEvent(Object source, Message m, MessageStatus status) {
        super(source);
        this.message = m;
        this.status = status;
    }
}
