package com.ibm.actor.example.command;

import com.ibm.actor.ActorManager;
import com.ibm.actor.DefaultActorManager;
import com.ibm.actor.message.DefaultMessage;

/**
 * Author: ZhangXiao
 * Created: 2016/9/19
 */
public class CommandPattern {

    public static void main(String[] args) {

        ActorManager manager = DefaultActorManager.getDefaultInstance();

        DelegatingActor actor = (DelegatingActor) manager.createActor(DelegatingActor.class, "Delegating");

        actor.addMessageListener(new EventType.StartingMessageListener());
        actor.addMessageListener(new EventType.StartedMessageListener());
        actor.addMessageListener(new EventType.RunningMessageListener());
        actor.addMessageListener(new EventType.EndingMessageListener());
        actor.addMessageListener(new EventType.EndedMessageListener());

        manager.startActor(actor);
        for (EventType eventType : EventType.values()) {
            manager.broadcast(new DefaultMessage("Command", eventType), actor);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        manager.terminateAndWait();
    }
}
