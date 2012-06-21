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

import java.net.URL;
import java.util.List;
import java.util.Properties;

import eu.playproject.platform.service.bootstrap.api.BootstrapFault;
import eu.playproject.platform.service.bootstrap.api.BootstrapService;
import eu.playproject.platform.service.bootstrap.api.InitService;
import eu.playproject.platform.service.bootstrap.api.KeyValueBean;

/**
 * Main init service which gets default values from configuration and create all
 * the subscriptions in the platform.
 * 
 * @author chamerling
 * 
 */
public class InitServiceImpl implements InitService {

	private BootstrapService dsbSubscribesBootstrapService;

	private BootstrapService ecSubscribesBootstrapService;

	String dsbsubscribeconfig = "https://raw.github.com/play-project/play-configfiles/master/platformservices/pubsubbootstrap/dsbsubscribestoec.properties";

	String ecsubscribeconfig = "https://raw.github.com/play-project/play-configfiles/master/platformservices/pubsubbootstrap/ecsubscribestodsb.properties";

	@Override
	public boolean go() {
		System.out.println("### DSB subscribes to EC...");
		dsbSubscribes2EC();

		System.out.println("### EC subscribes to DSB...");
		ecSubscribes2DSB();

		return true;
	}

	protected boolean dsbSubscribes2EC() {
		// get the properties from remote...
		Properties props = new Properties();
		try {
			URL url = new URL(dsbsubscribeconfig);
			props.load(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		String eventCloudEndpoint = props.getProperty("endpoint.eventcloud");
		String subscriberEndpoint = props.getProperty("endpoint.subscriber");

		System.out.printf("Initializing with ec %s and subscriber %s",
				eventCloudEndpoint, subscriberEndpoint);

		try {
			List<KeyValueBean> result = dsbSubscribesBootstrapService
					.bootstrap(eventCloudEndpoint, subscriberEndpoint);

			for (KeyValueBean bean : result) {
				System.out.println(bean);
			}

		} catch (BootstrapFault e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	protected boolean ecSubscribes2DSB() {
		// get the properties from remote...
		Properties props = new Properties();
		try {
			URL url = new URL(ecsubscribeconfig);
			props.load(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		String eventCloudEndpoint = props.getProperty("endpoint.eventcloud");
		String dsbEndpoint = props.getProperty("endpoint.dsb");

		System.out.printf("Initializing with ec %s and dsb %s",
				eventCloudEndpoint, dsbEndpoint);

		try {
			List<KeyValueBean> result = ecSubscribesBootstrapService.bootstrap(
					dsbEndpoint, eventCloudEndpoint);

			for (KeyValueBean bean : result) {
				System.out.println(bean);
			}

		} catch (BootstrapFault e) {
			e.printStackTrace();
			return false;
		}

		return true;
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
