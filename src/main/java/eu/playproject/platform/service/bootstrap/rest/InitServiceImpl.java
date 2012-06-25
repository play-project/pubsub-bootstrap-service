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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import eu.playproject.platform.service.bootstrap.MemoryLogServiceImpl;
import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.BootstrapService;
import eu.playproject.platform.service.bootstrap.api.Subscription;
import eu.playproject.platform.service.bootstrap.api.rest.InitService;
import eu.playproject.platform.service.bootstrap.api.rest.beans.Subscriptions;

/**
 * Main init service which gets default values from configuration and create all
 * the subscriptions in the platform.
 * 
 * @author chamerling
 * 
 */
public class InitServiceImpl implements InitService {

	protected static Logger logger = Logger.getLogger(InitServiceImpl.class
			.getName());

	private BootstrapService dsbSubscribesBootstrapService;

	private BootstrapService ecSubscribesBootstrapService;

	String dsbsubscribeconfig = "https://raw.github.com/play-project/play-configfiles/master/platformservices/pubsubbootstrap/dsbsubscribestoec.properties";

	String ecsubscribeconfig = "https://raw.github.com/play-project/play-configfiles/master/platformservices/pubsubbootstrap/ecsubscribestodsb.properties";

	@Override
	public Response go() {
		MemoryLogServiceImpl.get().log("Call to bootstrap service");
		List<Subscription> result = new ArrayList<Subscription>();
		result.addAll(dsbSubscribes2EC());
		result.addAll(ecSubscribes2DSB());
		return Response.ok(new Subscriptions(result)).build();
	}

	@Override
	public Response ecToDSB() {
		return Response.ok(new Subscriptions(ecSubscribes2DSB())).build();
	}

	@Override
	public Response dsbToEC() {
		return Response.ok(new Subscriptions(dsbSubscribes2EC())).build();
	}

	protected List<Subscription> dsbSubscribes2EC() {
		MemoryLogServiceImpl.get().log("DSB subscribes to EC");

		List<Subscription> result = new ArrayList<Subscription>();
		// get the properties from remote...
		Properties props = new Properties();
		try {
			URL url = new URL(dsbsubscribeconfig);
			props.load(url.openStream());
		} catch (Exception e) {
			logger.warning(e.getMessage());
			if (logger.isLoggable(Level.FINE)) {
				e.printStackTrace();
			}
			return result;
		}

		String eventCloudEndpoint = props.getProperty("endpoint.eventcloud");
		String subscriberEndpoint = props.getProperty("endpoint.subscriber");

		logger.info(String.format("Initializing with ec %s and subscriber %s",
				eventCloudEndpoint, subscriberEndpoint));

		try {
			result.addAll(dsbSubscribesBootstrapService.bootstrap(
					eventCloudEndpoint, subscriberEndpoint));

			if (logger.isLoggable(Level.FINE)) {
				for (Subscription s : result) {
					logger.fine("Subscribed : " + s);
				}
			}
		} catch (BootstrapFault e) {
			e.printStackTrace();
		}

		return result;
	}

	protected List<Subscription> ecSubscribes2DSB() {
		MemoryLogServiceImpl.get().log("EC subscribes to DSB");

		List<Subscription> result = new ArrayList<Subscription>();

		// get the properties from remote...
		Properties props = new Properties();
		try {
			URL url = new URL(ecsubscribeconfig);
			props.load(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}

		String eventCloudEndpoint = props.getProperty("endpoint.eventcloud");
		String dsbEndpoint = props.getProperty("endpoint.dsb");

		logger.info(String.format("Initializing with ec %s and dsb %s",
				eventCloudEndpoint, dsbEndpoint));

		try {
			result.addAll(ecSubscribesBootstrapService.bootstrap(dsbEndpoint,
					eventCloudEndpoint));

			if (logger.isLoggable(Level.FINE)) {
				for (Subscription s : result) {
					logger.fine("Subscribed : " + s);
				}
			}

		} catch (BootstrapFault e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setDsbSubscribesBootstrapService(
			BootstrapService dsbSubscribesBootstrapService) {
		this.dsbSubscribesBootstrapService = dsbSubscribesBootstrapService;
	}

	public void setEcSubscribesBootstrapService(
			BootstrapService ecSubscribesBootstrapService) {
		this.ecSubscribesBootstrapService = ecSubscribesBootstrapService;
	}

}
