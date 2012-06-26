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

import junit.framework.TestCase;
import eu.playproject.governance.api.bean.Topic;
import eu.playproject.platform.service.bootstrap.api.Subscription;
import eu.playproject.platform.service.bootstrap.api.SubscriptionRegistry;

/**
 * @author chamerling
 * 
 */
public class DSBSubscribesToECBootstrapTest extends TestCase {

	public void testAlreadySubscribed() throws Exception {
		DSBSubscribesToECBootstrapServiceImpl bootstrap = new DSBSubscribesToECBootstrapServiceImpl();
		SubscriptionRegistry registry = new SubscriptionRegistryServiceImpl();
		bootstrap.setSubscriptionRegistry(registry);

		Topic t = new Topic();
		t.setName("name");
		t.setNs("http://foo");
		t.setPrefix("pre");

		String ecEndpoint = "http://ec";
		String subscriber = "http://dsb";

		assertFalse(bootstrap.alreadySubscribed(t, ecEndpoint, subscriber));

		Subscription subscription = new Subscription("12345", subscriber,
				ecEndpoint, t, System.currentTimeMillis());
		registry.addSubscription(subscription);
		
		assertTrue(bootstrap.alreadySubscribed(t, ecEndpoint, subscriber));

	}

}
