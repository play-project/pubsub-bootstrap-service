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

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.petalslink.dsb.cxf.CXFHelper;

import eu.playproject.governance.api.EventGovernance;
import eu.playproject.governance.api.GovernanceExeption;
import eu.playproject.governance.api.TopicMetadataService;
import eu.playproject.governance.api.bean.Metadata;
import eu.playproject.governance.api.bean.Topic;
import eu.playproject.platform.service.bootstrap.api.GovernanceClient;

/**
 * Governance client used to retrieve topics and metadata
 * 
 * @author chamerling
 * 
 */
public class GovernanceClientImpl implements GovernanceClient {

	private static final String config = "https://raw.github.com/play-project/play-configfiles/master/platformservices/pubsubbootstrap/config.properties";

	private TopicMetadataService metaClient = null;

	private EventGovernance eventClient = null;

	private Properties props;

	private TopicMetadataService getMetaClient() {

		if (metaClient == null) {
			metaClient = CXFHelper.getClientFromFinalURL(getConfig()
					.getProperty(META_ENDPOINT), TopicMetadataService.class);
		}
		return metaClient;
	}

	private EventGovernance getEventClient() {

		if (eventClient == null) {
			eventClient = CXFHelper.getClientFromFinalURL(getConfig()
					.getProperty(TOPIC_ENDPOINT), EventGovernance.class);
		}
		return eventClient;
	}

	/**
	 * To be overrided if needed...
	 * 
	 * @return
	 */
	protected Properties getConfig() {
		if (props == null) {
			try {
				props = new Properties();
				URL url = new URL(config);
				props.load(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return props;
	}

	@Override
	public void createTopic(Topic topic) throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public List<W3CEndpointReference> findEventProducersByElements(
			List<QName> arg0) throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public List<W3CEndpointReference> findEventProducersByTopics(
			List<QName> arg0) throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public List<QName> findTopicsByElement(QName arg0)
			throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public List<Topic> getTopics() {
		return getEventClient().getTopics();
	}

	@Override
	public void loadResources(InputStream arg0) throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public void addMetadata(Topic arg0, Metadata arg1)
			throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public boolean deleteMetaData(Topic arg0) throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public List<Metadata> getMetaData(Topic topic) throws GovernanceExeption {
		return getMetaClient().getMetaData(topic);
	}

	@Override
	public Metadata getMetadataValue(Topic arg0, String arg1)
			throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}

	@Override
	public List<Topic> getTopicsWithMeta(List<Metadata> list)
			throws GovernanceExeption {
		return getMetaClient().getTopicsWithMeta(list);
	}

	@Override
	public void removeMetadata(Topic arg0, Metadata arg1)
			throws GovernanceExeption {
		throw new GovernanceExeption(
				"This method is not implemented in the client");
	}
}
