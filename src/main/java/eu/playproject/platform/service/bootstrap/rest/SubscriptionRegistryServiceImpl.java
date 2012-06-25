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
package eu.playproject.platform.service.bootstrap.rest;

import javax.ws.rs.core.Response;

import eu.playproject.platform.service.bootstrap.api.SubscriptionRegistry;
import eu.playproject.platform.service.bootstrap.api.rest.SubscriptionRegistryService;
import eu.playproject.platform.service.bootstrap.api.rest.beans.Subscriptions;

/**
 * @author chamerling
 * 
 */
public class SubscriptionRegistryServiceImpl implements
		SubscriptionRegistryService {

	SubscriptionRegistry subscriptionRegistry;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.playproject.platform.service.bootstrap.api.rest.
	 * SubscriptionRegistryService#all()
	 */
	@Override
	public Response all() {
		return Response.ok(
				new Subscriptions(subscriptionRegistry.getSubscriptions()))
				.build();
	}

	public void setSubscriptionRegistry(
			SubscriptionRegistry subscriptionRegistry) {
		this.subscriptionRegistry = subscriptionRegistry;
	}

}
