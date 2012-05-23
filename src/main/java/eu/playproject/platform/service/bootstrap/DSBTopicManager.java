/**
 * 
 */
package eu.playproject.platform.service.bootstrap;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.client.http.simple.HTTPProducerClient;
import org.petalslink.dsb.notification.client.http.simple.HTTPProducerRPClient;
import org.petalslink.dsb.notification.commons.NotificationException;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.TopicManager;

/**
 * Get the topics from a DSB instance. A better version will be to get them from
 * the governance directly.
 * 
 * @author chamerling
 * 
 */
public class DSBTopicManager implements TopicManager {

	static Logger logger = Logger.getLogger(DSBTopicManager.class.getName());

	static {
		// WTF?
		logger.info("Creating WSN factories...");
		Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
				new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
				new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
				new WsnbModelFactoryImpl());
	}

	@Override
	public List<QName> getTopics(String endpoint) throws BootstrapFault {
		logger.info("Getting topic from " + endpoint);
		HTTPProducerRPClient client = new HTTPProducerRPClient(endpoint);
		try {
			return client.getTopics();
		} catch (NotificationException e) {
			logger.log(Level.SEVERE, "Problem while getting topics", e);
			throw new BootstrapFault(e);
		}
	}

	@Override
	public String subscribe(String producer, QName topic, String subscriber)
			throws BootstrapFault {
		String id = null;

		logger.info("Subscribe to topic '" + topic + "' on producer '"
				+ producer + "' for subscriber '" + subscriber + "'");

		HTTPProducerClient client = new HTTPProducerClient(producer);
		try {
			id = client.subscribe(topic, subscriber);
			logger.info("Subscribed to topic " + topic + " and ID is " + id);
		} catch (NotificationException e) {
			logger.log(Level.SEVERE, "Problem while subscribing", e);
			throw new BootstrapFault(e);
		}
		return id;
	}

}
