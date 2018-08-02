package com.ibm.actor;

import com.ibm.actor.message.DefaultMessage;
import com.ibm.actor.message.Message;

/**
 * An actor with exception trapping.
 *
 * @author BFEIGENB
 */
abstract public class SafeActor extends AbstractActor {

    @Override
    protected void loopBody(Message m) {
        try {
            logger.trace("SafeActor loopBody: {}", m);
            doBody((DefaultMessage) m);
        } catch (Exception e) {
            logger.error("SafeActor: exception", e);
        }
    }

    @Override
    protected void runBody() {
        // by default, nothing to do
    }

    /**
     * Override to define message reception behavior.
     *
     * @param m
     * @throws Exception
     */
    abstract void doBody(DefaultMessage m) throws Exception;

}