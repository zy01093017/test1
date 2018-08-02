package com.ibm.actor.listener;

/**
 * Listener for message reception.
 *  
 * @author BFEIGENB
 *
 */
public interface MessageListener {
	/**
	 * Call-back for message reception.
	 * 
	 * @param e event
	 */
	void onMessage(MessageEvent e);
}
