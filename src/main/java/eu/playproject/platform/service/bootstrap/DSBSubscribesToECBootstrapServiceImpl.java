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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import eu.playproject.commons.utils.StreamHelper;
import eu.playproject.governance.api.GovernanceExeption;
import eu.playproject.governance.api.bean.Metadata;
import eu.playproject.governance.api.bean.Topic;
import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.BootstrapService;
import eu.playproject.platform.service.bootstrap.api.EventCloudClientFactory;
import eu.playproject.platform.service.bootstrap.api.GovernanceClient;
import eu.playproject.platform.service.bootstrap.api.LogService;
import eu.playproject.platform.service.bootstrap.api.Subscription;
import eu.playproject.platform.service.bootstrap.api.SubscriptionRegistry;
import eu.playproject.platform.service.bootstrap.api.TopicManager;
import fr.inria.eventcloud.webservices.api.EventCloudManagementWsApi;

/**
 * Creates all the subscriptions between the DSB and the EventCloud. The DSB
 * subscribes to all complex 'topics' provided by the EventCloud so it receives
 * WSN notifications when new messages are published in the EventCloud by CEP
 * for example.
 * 
 * @author chamerling
 * @author lpellegr
 */
public class DSBSubscribesToECBootstrapServiceImpl implements BootstrapService {

	private static Logger logger = Logger
			.getLogger(DSBSubscribesToECBootstrapServiceImpl.class.getName());

	private TopicManager topicManager;

	private EventCloudClientFactory eventCloudClientFactory;

	private GovernanceClient governanceClient;

	private SubscriptionRegistry subscriptionRegistry;

	@Override
	public List<Subscription> bootstrap(String eventCloudEndpoint,
			String subscriberEndpoint) throws BootstrapFault {
		List<Subscription> result = new ArrayList<Subscription>();

		if (eventCloudEndpoint == null) {
			throw new BootstrapFault(
					"Can not find any EventCloud endpoint, please check the settings");
		}

		if (subscriberEndpoint == null) {
			throw new BootstrapFault(
					"Can not find any subscriber endpoint, please check the settings");
		}

		List<Topic> topics = governanceClient.getTopics();

		if (topics == null || topics.size() == 0) {
			throw new BootstrapFault(
					"Can not get any topic from the governance");
		}

		for (Topic topic : topics) {
			try {
				Subscription subscription = createResources(topic,
						eventCloudEndpoint, subscriberEndpoint);
				if (subscription != null) {
					result.add(subscription);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	protected Subscription createResources(Topic topic,
			String eventCloudEndpoint, String subscriberEndpoint)
			throws BootstrapFault {
		Subscription result = null;

		LogService log = MemoryLogServiceImpl.get();
		QName topicName = new QName(topic.getNs(), topic.getName(),
				topic.getPrefix());

		logger.info("Let's do it for topic " + topic);

		EventCloudManagementWsApi client = eventCloudClientFactory
				.getClient(eventCloudEndpoint);

		// creates an eventcloud...
		String streamName = StreamHelper.getStreamName(topicName);

		boolean created = client.createEventCloud(streamName);

		// The create operation returns true if the eventcloud has been created,
		// if false it means that it is already created and that we do not need
		// to do it anymore...
		if (created) {
			// creates a default proxy for each interface: pub/sub and put/get
			String subscribeEndpoint = client.createSubscribeProxy(streamName);
			client.createPublishProxy(streamName);
			client.createPutGetProxy(streamName);
			log.log("EventCloud has been created for stream %s", streamName);
		} else {
			log.log("EventCloud has been already created for stream %s",
					streamName);
		}

		// check if it is necessary to subscribe to the eventcloud...
		if (needsToSubscribe(topic)) {
		    // check if we already subscribed...
	        if (alreadySubscribed(topic, eventCloudEndpoint, subscriberEndpoint)) {
	            log.log("DSB already subscribed to EC for topic %s", topic);
	            logger.info(String.format("Already subscribed to topic %s", topic));
	            return result;
	        } else {
	            // let's subscribe on behalf of the DSB if it is a topic for
	            // complex events
	            // let's get the subscribe proxy endpoint
	            List<String> endpoints = client
	                    .getSubscribeProxyEndpointUrls(streamName);
	            if (endpoints == null || endpoints.size() == 0) {
	                log.log("Can not find any subscribe endpoint in the EC for stream %s",
	                        streamName);
	            } else {
	                log.log("Let's subscribe to eventcloud for stream %s",
	                        streamName);

	                result = topicManager.subscribe(endpoints.get(0), topicName,
	                        subscriberEndpoint);
	                log.log("DSB subscribed to EC : " + result);
	            }
	        }
		} else {
			log.log("Do not need to subscribe to eventcloud for stream %s",
					streamName);
		}

		return result;
	}

	/**
	 * Have a look to the current subscriptions if someone already exists... For
	 * the DSB we just check that we have a subscription where the topic and the
	 * subscriber (dsb) are already in the list.
	 * 
	 * @param topic
	 * @param eventCloudEndpoint
	 * @param subscriberEndpoint
	 * @return
	 */
	protected boolean alreadySubscribed(Topic topic, String eventCloudEndpoint,
			String subscriberEndpoint) {

		List<Subscription> subscriptions = this.subscriptionRegistry
				.getSubscriptions();

		Iterator<Subscription> iter = subscriptions.iterator();
		boolean found = false;
		while (iter.hasNext() && !found) {
			Subscription subscription = iter.next();
			found = subscription.getTopic().equals(topic)
					&& subscription.getSubscriber().equals(subscriberEndpoint);
		}

		return found;
	}

	/**
	 * Query the metadata service to see of we need to subscribe
	 * 
	 * @param topic
	 * @return
	 */
	protected boolean needsToSubscribe(Topic topic) {
		// DSB subscribes only to topics where the metadata for
		// dsbneedstosubscribe is set to true...
		List<Metadata> filter = new ArrayList<Metadata>();
		Metadata meta = new Metadata();
		meta.setName("dsbneedstosubscribe");
		meta.setValue("true");
		filter.add(meta);

		List<Topic> topics = null;
		try {
			topics = this.governanceClient.getTopicsWithMeta(filter);
		} catch (GovernanceExeption e) {
			e.printStackTrace();
			return false;
		}

		return topics != null && topics.contains(topic);
	}

	public void setTopicManager(TopicManager topicManager) {
		this.topicManager = topicManager;
	}

	public void setEventCloudClientFactory(
			EventCloudClientFactory eventCloudClientFactory) {
		this.eventCloudClientFactory = eventCloudClientFactory;
	}

	public void setGovernanceClient(GovernanceClient governanceClient) {
		this.governanceClient = governanceClient;
	}

	public void setSubscriptionRegistry(
			SubscriptionRegistry subscriptionRegistry) {
		this.subscriptionRegistry = subscriptionRegistry;
	}

}
