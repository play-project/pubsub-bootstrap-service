/**
 * 
 */
package eu.playproject.platform.service.bootstrap.api;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
public interface TopicManager {

	/**
	 * Get all the available topics
	 * 
	 * @return
	 */
	List<QName> getTopics(String endpoint) throws BootstrapFault;

	/**
	 * Subscribe to a notification producer on behalf of the subscriber
	 * 
	 * @param producer
	 *            to send the subscribe to
	 * @param topic
	 *            the topic to subscribe to
	 * @param subscriber
	 *            who is subscribing ie who will receive notifications?
	 * 
	 * @return the subscription ID
	 * 
	 * @throws BootstrapFault
	 */
	String subscribe(String producer, QName topic, String subscriber)
			throws BootstrapFault;

}
