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
package eu.playproject.platform.service.bootstrap.api;

import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
public interface TopicManager {

	/**
	 * Subscribe to a notification producer on behalf of the subscriber
	 * 
	 * @param producer
	 *            to send the subscribe to
	 * @param topic
	 *            the topic to subscribe to
	 * @param subscriber
	 *            who is subscribing ie who will receive notifications?
	 * 
	 * @return the subscription ID
	 * 
	 * @throws BootstrapFault
	 */
	String subscribe(String producer, QName topic, String subscriber)
			throws BootstrapFault;

}
