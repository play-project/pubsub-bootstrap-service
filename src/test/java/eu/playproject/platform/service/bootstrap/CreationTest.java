package eu.playproject.platform.service.bootstrap;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.playproject.platform.service.bootstrap.api.KeyValueBean;

public class CreationTest {

	@Test
	public void testCreate() throws Exception {

		String topicEndpoint = "http://localhost:4567/TopicService";
		String ecEndpoint = "http://localhost:4568/EventCloudService";
		String subscriberEndpoint = "http://localhost:4569/SubscriberService";

		DSBSubscribesToECBootstrapServiceImpl service = new DSBSubscribesToECBootstrapServiceImpl();
		service.setEventCloudClientFactory(new EventCloudClientFactoryMock(
				ecEndpoint));
		service.setTopicManager(new TopicManagerMock());

		List<KeyValueBean> result = service.bootstrap(topicEndpoint,
				ecEndpoint, subscriberEndpoint);

		Assert.assertTrue(result.size() == 1);
		System.out.println(result);
	}

}
