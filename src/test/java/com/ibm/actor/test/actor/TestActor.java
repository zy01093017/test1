package com.ibm.actor.test.actor;

import com.ibm.actor.Actor;
import com.ibm.actor.message.DefaultMessage;
import com.ibm.actor.message.Message;
import com.ibm.actor.test.DefaultActorTest;

import java.util.Date;

/**
 * An actor that sends messages while counting down a send count.
 *
 * @author BFEIGENB
 */
public class TestActor extends TestableActor {

    @Override
    public void activate() {
        logger.trace("TestActor activate: {}", this);
        super.activate();
    }

    @Override
    public void deactivate() {
        logger.trace("TestActor deactivate: {}", this);
        super.deactivate();
    }

    @Override
    protected void runBody() {
        // logger.trace("TestActor:%s runBody: %s", getName(), this);
        DefaultActorTest.sleeper(1);
        DefaultMessage m = new DefaultMessage("init", 8);
        getManager().send(m, null, this);
    }

    @Override
    protected void loopBody(Message m) {
        // logger.trace("TestActor:%s loopBody %s: %s", getName(), m, this);
        DefaultActorTest.sleeper(1);
        String subject = m.getSubject();
        if ("repeat".equals(subject)) {
            int count = (Integer) m.getData();
            logger.trace("TestActor:{} repeat({}) {}: {}", getName(), count, m,
                         this);
            if (count > 0) {
                m = new DefaultMessage("repeat", count - 1);
                // logger.trace("TestActor loopBody send %s: %s", m, this);
                String toName = "actor"
                        + DefaultActorTest
                        .nextInt(DefaultActorTest.TEST_ACTOR_COUNT);
                Actor to = actorTest.getTestActors().get(toName);
                if (to != null) {
                    getManager().send(m, this, to);
                } else {
                    logger.warn("repeat:{} to is null: {}", getName(),
                                toName);
                }
            }
        } else if ("init".equals(subject)) {
            int count = (Integer) m.getData();
            count = DefaultActorTest.nextInt(count) + 1;
            logger.trace("TestActor:{} init({}): {}", getName(), count, this);
            for (int i = 0; i < count; i++) {
                DefaultActorTest.sleeper(1);
                m = new DefaultMessage("repeat", count);
                // logger.trace("TestActor runBody send %s: %s", m, this);
                String toName = "actor"
                        + DefaultActorTest
                        .nextInt(DefaultActorTest.TEST_ACTOR_COUNT);
                Actor to = actorTest.getTestActors().get(toName);
                if (to != null) {
                    getManager().send(m, this, to);
                } else {
                    logger.warn("init:{} to is null: {}", getName(), toName);
                }
                DefaultMessage dm = new DefaultMessage("repeat", count);
                dm.setDelayUntil(new Date().getTime()
                                         + (DefaultActorTest.nextInt(5) + 1) * 1000);
                getManager().send(dm, this, this.getClass().getSimpleName());
            }
        } else {
            logger.warn("TestActor:{} loopBody unknown subject: {}", getName(), subject);
        }
    }

}