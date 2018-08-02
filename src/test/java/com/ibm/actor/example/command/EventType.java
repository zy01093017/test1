package com.ibm.actor.example.command;

/**
 * Author: ZhangXiao
 * Created: 2016/9/19
 */
public enum EventType {

    STARTING, STARTED, RUNNING, ENDING, ENDED;


    public static class StartingMessageListener implements MessageListener {

        @Override
        public void onMessage(MessageEvent event) {
            if (event.getMessage().getData() == EventType.STARTING) {
                System.out.println("StartingMessageListener : Status : " + event.getMessage().getData());
            }
        }
    }

    public static class StartedMessageListener implements MessageListener {

        @Override
        public void onMessage(MessageEvent event) {
            if (event.getMessage().getData() == EventType.STARTED) {
                System.out.println("StartedMessageListener : Status : " + event.getMessage().getData());
            }
        }
    }

    public static class RunningMessageListener implements MessageListener {

        @Override
        public void onMessage(MessageEvent event) {
            if (event.getMessage().getData() == EventType.RUNNING) {
                System.out.println("RunningMessageListener : Status : " + event.getMessage().getData());
            }
        }
    }

    public static class EndingMessageListener implements MessageListener {

        @Override
        public void onMessage(MessageEvent event) {
            if (event.getMessage().getData() == EventType.ENDING) {
                System.out.println("EndingMessageListener : Status : " + event.getMessage().getData());
            }
        }
    }

    public static class EndedMessageListener implements MessageListener {

        @Override
        public void onMessage(MessageEvent event) {
            if (event.getMessage().getData() == EventType.ENDED) {
                event.setConsumed(true);
                System.out.println("EndedMessageListener : Status : " + event.getMessage().getData());
            }
        }
    }
}
