package com.ibm.actor.test.actor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.actor.message.DefaultMessage;
import com.ibm.actor.message.Message;
import com.ibm.actor.test.DefaultActorTest;
import com.ibm.actor.test.actor.TestableActor;

/**
 * An Actor that constructs items. 
 * 
 * @author BFEIGENB
 *
 */
public class ConsumerActor extends TestableActor {

	@Override
	public void activate() {
		logger.trace("ConsumerActor activate:{}", this);
		super.activate();
	}

	@Override
	public void deactivate() {
		logger.trace("ConsumerActor deactivate: {}", this);
		DefaultActorTest.dumpMessages(messages);
		super.deactivate();
	}

	Map<String, Integer> expected = new ConcurrentHashMap<String, Integer>();

	@Override
	protected void loopBody(Message m) {
		// logger.trace("ConsumerActor:%s loopBody %s: %s", getName(), m,
		// this);
		String subject = m.getSubject();
		if ("construct".equals(subject)) {
			String type = (String) m.getData();
			// logger.trace("ConsumerActor:%s construct %s; pending=%d",
			// getName(), type, messages.size());
			logger.trace("ConsumerActor:{} constructing {}", getName(), type);
			delay(type); // takes ~ 1 to N seconds

			DefaultMessage xm = new DefaultMessage("constructionComplete", type);
			// logger.info("ConsumerActor:%s reply to %s", getName(),
			// m.getSource());
			getManager().send(xm, this, m.getSource());
		} else if ("init".equals(subject)) {
			// nothing to do
		} else {
			logger.warn("ConsumerActor:{} loopBody unknown subject: {}", getName(), subject);
		}
	}

	protected void delay(String type) {
		int delay = 1;
		for (int i = 0; i < DefaultActorTest.getItemTypes().length; i++) {
			if (DefaultActorTest.getItemTypes()[i].equals(type)) {
				break;
			}
			delay++;
		}
		DefaultActorTest.sleeper(DefaultActorTest.nextInt(delay) + 1);
		// sleep(100);
	}
}