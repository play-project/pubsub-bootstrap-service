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
package eu.playproject.platform.service.bootstrap.client;

import java.util.HashMap;
import java.util.Map;

import org.petalslink.dsb.cxf.CXFHelper;

import eu.playproject.platform.service.bootstrap.api.EventCloudClientFactory;
import fr.inria.eventcloud.webservices.api.EventCloudManagementServiceApi;

/**
 * @author chamerling
 * 
 */
public class JAXWSEventCloudClientFactory implements EventCloudClientFactory {

	/**
	 * Cache client per endpoint URL
	 */
	static Map<String, EventCloudManagementServiceApi> cache = new HashMap<String, EventCloudManagementServiceApi>();

	@Override
	public synchronized EventCloudManagementServiceApi getClient(String endpoint) {
		if (cache.get(endpoint) == null) {
			EventCloudManagementServiceApi client = CXFHelper.getClientFromFinalURL(
					endpoint, EventCloudManagementServiceApi.class);
			cache.put(endpoint, client);
		}
		return cache.get(endpoint);
	}
}
