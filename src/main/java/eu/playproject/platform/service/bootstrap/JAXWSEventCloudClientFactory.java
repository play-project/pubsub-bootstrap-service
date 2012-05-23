/**
 * 
 */
package eu.playproject.platform.service.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.petalslink.dsb.cxf.CXFHelper;

import eu.playproject.platform.service.bootstrap.api.EventCloudClientFactory;
import fr.inria.eventcloud.webservices.api.EventCloudManagementWsApi;

/**
 * @author chamerling
 * 
 */
public class JAXWSEventCloudClientFactory implements EventCloudClientFactory {

	static Map<String, EventCloudManagementWsApi> cache = new HashMap<String, EventCloudManagementWsApi>();

	@Override
	public synchronized EventCloudManagementWsApi getClient(String endpoint) {
		if (cache.get(endpoint) == null) {
			EventCloudManagementWsApi client = CXFHelper.getClientFromFinalURL(
					endpoint, EventCloudManagementWsApi.class);
			cache.put(endpoint, client);
		}
		return cache.get(endpoint);
	}
}
