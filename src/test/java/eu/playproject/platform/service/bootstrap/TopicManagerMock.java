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

import javax.xml.namespace.QName;

import eu.playproject.governance.api.bean.Topic;
import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.Subscription;
import eu.playproject.platform.service.bootstrap.api.TopicManager;

public class TopicManagerMock implements TopicManager {

	@Override
	public Subscription subscribe(String producer, QName topic,
			String subscriber) throws BootstrapFault {
		System.out.println("Subscribe to topic " + topic + " on " + producer
				+ " for subscriber " + subscriber);

		Topic t = new Topic();
		t.setName(topic.getLocalPart());
		t.setNs(topic.getNamespaceURI());
		t.setPrefix(topic.getPrefix());
		
		return new Subscription("1234565689", subscriber, producer, t,
				System.currentTimeMillis());
	}
	
	@Override
	public void unsubscribe(Subscription subscription) throws BootstrapFault {
		// TODO Auto-generated method stub
		
	}

}
