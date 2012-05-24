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
 * The EventCloud subscribes to the DSB topics. Just for the simple topics and
 * not the complex ones.
 * 
 * @author chamerling
 * 
 */
public class ECSubscribesToDSBBootstrapServiceImpl implements BootstrapService {

	private static Logger logger = Logger
			.getLogger(ECSubscribesToDSBBootstrapServiceImpl.class.getName());

	private TopicManager topicManager;

	private EventCloudClientFactory eventCloudClientFactory;

	private GovernanceClient governanceClient;

	/*
	 * Provider is the DSB. subscriber is the EC.
	 */
	public List<KeyValueBean> bootstrap(String providerEndpoint,
			String subscriberEndpoint) throws BootstrapFault {
		logger.info("Init all EC subscribes to DSB");

		List<KeyValueBean> result = new ArrayList<KeyValueBean>();

		List<Topic> topics = governanceClient.getTopics();

		if (topics == null || topics.size() == 0) {
			throw new BootstrapFault("Can not get any topic");
		}

		for (Topic topic : topics) {
			logger.info("Create stuff for topic " + topic);

			KeyValueBean bean = createResources(topic, providerEndpoint,
					subscriberEndpoint);
			if (bean != null) {
				result.add(bean);
			}
		}
		return result;
	}

	/**
	 * Provider is the DSB, subscriber is the EC.
	 * 
	 * @param topic
	 * @param providerEndpoint
	 * @param subscriberEndpoint
	 * @return
	 */
	private KeyValueBean createResources(Topic topic, String providerEndpoint,
			String subscriberEndpoint) {
		KeyValueBean kv = new KeyValueBean();
		kv.setKey(topic.toString());

		EventCloudManagementWsApi client = eventCloudClientFactory
				.getClient(subscriberEndpoint);

		QName topicName = new QName(topic.getNs(), topic.getName(),
				topic.getPrefix());

		String stream = StreamHelper.getStreamName(topicName);
		List<String> publishURLs = client.getPublishProxyEndpointUrls(stream);

		logger.info("Got some URLs back from the EC : " + publishURLs);

		String subscriber = (publishURLs != null && publishURLs.size() > 0) ? publishURLs
				.get(0) : null;

		if (subscriber == null) {
			logger.info("Can not get any valid EC endpoint");
			return null;
		}

		logger.info("Let's use the EC endpoint at : " + subscriber);

		if (needsToSubscribe(topic)) {
			// send the subscribe to the event cloud on behalf of the DSB
			try {
				logger.info("Subscribe for topic " + topic);
				String id = topicManager.subscribe(providerEndpoint, topicName,
						subscriber);
				kv.setValue("SubscribeID="+id);

			} catch (BootstrapFault e) {
				e.printStackTrace();
				kv.setValue("Error : " +e.getMessage());
			}
		} else {
			logger.info("No need to subscribe for the topic " + topic);
			kv.setValue("NotSubscribed (governance said no...)");
		}

		return kv;
	}

	protected boolean needsToSubscribe(Topic topic) {
		// only subscribe if this is not a topic the DSB needs to subscribe...

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

		return topics != null && !topics.contains(topic);
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
