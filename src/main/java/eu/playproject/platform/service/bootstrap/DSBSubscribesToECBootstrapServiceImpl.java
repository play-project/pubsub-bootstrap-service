/**
 * 
 */
package eu.playproject.platform.service.bootstrap;

import java.util.ArrayList;
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

	private static Logger logger = Logger
			.getLogger(DSBSubscribesToECBootstrapServiceImpl.class.getName());

	private TopicManager topicManager;

	private EventCloudClientFactory eventCloudClientFactory;

	private GovernanceClient governanceClient;

	@Override
	public List<KeyValueBean> bootstrap(String eventCloudEndpoint,
			String subscriberEndpoint) throws BootstrapFault {
		List<KeyValueBean> result = new ArrayList<KeyValueBean>();

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
			KeyValueBean bean = createResources(topic, eventCloudEndpoint,
					subscriberEndpoint);
			if (bean != null) {
				result.add(bean);
			}
		}

		return result;
	}

	protected KeyValueBean createResources(Topic topic,
			String eventCloudEndpoint, String subscriberEndpoint)
			throws BootstrapFault {
		KeyValueBean result = new KeyValueBean();
		result.setKey(topic.toString());
		StringBuffer sb = new StringBuffer();

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

			sb.append("EventCloudCreated");
		} else {
			// looks like it is already created
			sb.append("EventCloudAlreadyCreated");
		}

		// in all the cases, subscribe to the eventcloud...
		if (needsToSubscribe(topic)) {
			// let's subscribe on behalf of the DSB if it is a topic for
			// complex events
			// let's get the subscribe proxy endpoint
			List<String> endpoints = client
					.getSubscribeProxyEndpointUrls(streamName);
			if (endpoints == null || endpoints.size() == 0) {
				sb.append(";NoSubscribeProxyAvailable");
			} else {
				topicManager.subscribe(endpoints.get(0), topicName,
						subscriberEndpoint);
				sb.append(";Subscribed");
			}
		} else {
			sb.append(";NotSubscribed");
		}

		result.setValue(sb.toString());

		return result;
	}

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

}
