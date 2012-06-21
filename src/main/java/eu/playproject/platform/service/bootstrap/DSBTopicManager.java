/**
 *
 * Copyright (c) 2012, PetalsLink
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package eu.playproject.platform.service.bootstrap;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.client.http.simple.HTTPProducerClient;
import org.petalslink.dsb.notification.commons.NotificationException;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

import eu.playproject.governance.api.bean.Topic;
import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.Subscription;
import eu.playproject.platform.service.bootstrap.api.SubscriptionManager;
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
	
	private SubscriptionManager subscriptionManager;

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
			
			Subscription subscription = new Subscription();
			subscription.setDate(System.currentTimeMillis());
			subscription.setId(id);
			subscription.setProvider(producer);
			subscription.setSubscriber(subscriber);
			Topic t = new Topic();
			t.setName(topic.getLocalPart());
			t.setNs(topic.getNamespaceURI());
			t.setPrefix(topic.getPrefix());
			subscription.setTopic(t);
			
			this.subscriptionManager.addSubscription(subscription);
			
		} catch (NotificationException e) {
			logger.log(Level.SEVERE, "Problem while subscribing", e);
			throw new BootstrapFault(e);
		}
		return id;
	}
	
	public void setSubscriptionManager(SubscriptionManager subscriptionManager) {
		this.subscriptionManager = subscriptionManager;
	}
}
