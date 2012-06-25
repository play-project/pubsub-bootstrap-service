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
import java.util.List;
import java.util.logging.Logger;

import eu.playproject.platform.service.bootstrap.api.Subscription;

/**
 * Stores the subscriptions. This service does not call the subscription services, it just stores subscriptions.
 * 
 * @author chamerling
 * 
 */
public class SubscriptionRegistryServiceImpl implements
		eu.playproject.platform.service.bootstrap.api.SubscriptionRegistry {

	private static Logger logger = Logger
			.getLogger(SubscriptionRegistryServiceImpl.class.getName());

	List<Subscription> subscriptions;

	public SubscriptionRegistryServiceImpl() {
		this.subscriptions = new ArrayList<Subscription>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.playproject.platform.service.bootstrap.api.SubscriptionRegistry#
	 * addSubscription
	 * (eu.playproject.platform.service.bootstrap.api.Subscription)
	 */
	@Override
	public void addSubscription(Subscription subscription) {
		if (subscription != null) {
			subscriptions.add(subscription);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.playproject.platform.service.bootstrap.api.SubscriptionRegistry#
	 * getSubscriptions()
	 */
	@Override
	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.playproject.platform.service.bootstrap.api.SubscriptionRegistry#unsubscribe
	 * ()
	 */
	@Override
	public List<Subscription> removeAll() {
		List<Subscription> result = new ArrayList<Subscription>();
		for (Subscription subscription : subscriptions) {
			if (this.remove(subscription)) {
				result.add(subscription);
			}
		}
		return result;
	}

	@Override
	public boolean remove(Subscription subscription) {
		logger.info("Remove from " + subscription);
		return subscriptions.remove(subscription);
	}

}
