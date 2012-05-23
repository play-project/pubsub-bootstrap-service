/**
 * 
 */
package eu.playproject.platform.service.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import eu.playproject.commons.utils.StreamHelper;
import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.BootstrapService;
import eu.playproject.platform.service.bootstrap.api.EventCloudClientFactory;
import eu.playproject.platform.service.bootstrap.api.KeyValueBean;
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

	private static Logger logger = Logger.getLogger(DSBSubscribesToECBootstrapServiceImpl.class
			.getName());

	private TopicManager topicManager;

	private EventCloudClientFactory eventCloudClientFactory;

	@Override
	public List<KeyValueBean> bootstrap(String topicEndpoint,
			String eventCloudEndpoint, String subscriberEndpoint)
			throws BootstrapFault {
		List<KeyValueBean> result = new ArrayList<KeyValueBean>();

		// get the topics from the runtime engine (DSB for now...)
		if (topicEndpoint == null) {
			throw new BootstrapFault(
					"Can not find any topics endpoint, please check the settings");
		}

		if (eventCloudEndpoint == null) {
			throw new BootstrapFault(
					"Can not find any EventCloud endpoint, please check the settings");
		}

		if (subscriberEndpoint == null) {
			throw new BootstrapFault(
					"Can not find any subscriber endpoint, please check the settings");
		}

		List<QName> topics = topicManager.getTopics(topicEndpoint);

		if (topics == null || topics.size() == 0) {
			throw new BootstrapFault("Can not get any topic");
		}

		for (QName topic : topics) {
			KeyValueBean bean = createResources(topic, eventCloudEndpoint, subscriberEndpoint);
			if (bean != null) {
				result.add(bean);
			}
		}

		return result;
	}

	protected KeyValueBean createResources(QName topic,
			String eventCloudEndpoint, String subscriberEndpoint)
			throws BootstrapFault {
		KeyValueBean result = new KeyValueBean();
		result.setKey(topic.toString());

		logger.info("Let's do it for topic " + topic);

		EventCloudManagementWsApi client = 
				eventCloudClientFactory.getClient(eventCloudEndpoint);

		// creates an eventcloud...
		String streamName = StreamHelper.getStreamName(topic);

		boolean created = client.createEventCloud(streamName);

		// The create operation returns true if the eventcloud has been created,
		// if false it means that it is already created and that we do not need
		// to do it anymore...
		if (created) {
			// creates a default proxy for each interface: pub/sub and put/get
			String subscribeEndpoint = client.createSubscribeProxy(streamName);
			client.createPublishProxy(streamName);
			client.createPutGetProxy(streamName);

			if (needsToSubscribe(topic)) {
				// let's subscribe on behalf of the DSB if it is a topic for
				// complex events
				// FIXME: We can have N...
				topicManager.subscribe(subscribeEndpoint, topic, subscriberEndpoint);
			}

			result.setValue("true");
		} else {
			// looks like it is already created
			result.setValue("false");
		}

		return result;
	}

	private boolean needsToSubscribe(QName topic) {
		// FIXME: The filters needs to be set by configuration or by setup
		// TODO : Use the metadata service
		return (topic != null && topic.getLocalPart().toLowerCase().endsWith("cepresults"));
	}

	public void setTopicManager(TopicManager topicManager) {
		this.topicManager = topicManager;
	}

	public void setEventCloudClientFactory(
			EventCloudClientFactory eventCloudClientFactory) {
		this.eventCloudClientFactory = eventCloudClientFactory;
	}

}
