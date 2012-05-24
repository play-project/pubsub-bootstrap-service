package eu.playproject.platform.service.bootstrap;

import javax.xml.namespace.QName;

import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.TopicManager;

public class TopicManagerMock implements TopicManager {

	@Override
	public String subscribe(String producer, QName topic, String subscriber)
			throws BootstrapFault {
		System.out.println("Subscribe to topic " + topic + " on " + producer
				+ " for subscriber " + subscriber);
		
		return "1234565689";
	}

}
