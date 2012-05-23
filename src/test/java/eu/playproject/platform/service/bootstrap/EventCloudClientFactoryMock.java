/**
 * 
 */
package eu.playproject.platform.service.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.cxf.CXFHelper;

import eu.playproject.platform.service.bootstrap.api.EventCloudClientFactory;
import fr.inria.eventcloud.webservices.api.EventCloudManagementWsApi;

/**
 * @author chamerling
 * 
 */
public class EventCloudClientFactoryMock implements EventCloudClientFactory {

	Map<String, Service> services = new HashMap<String, Service>();

	String eventCloudEndpointPrefix;

	public EventCloudClientFactoryMock(String eventCloudEndpointPrefix) {
		this.eventCloudEndpointPrefix = eventCloudEndpointPrefix;
	}

	@Override
	public EventCloudManagementWsApi getClient(String endpoint) {

		if (services.get(endpoint) == null) {
			createService(endpoint);
		}

		return CXFHelper.getClientFromFinalURL(endpoint,
				EventCloudManagementWsApi.class);
	}

	private final void createService(String endpoint) {
		// create a server, start it and return the associated client

		Service service = CXFHelper.getServiceFromFinalURL(endpoint,
				EventCloudManagementWsApi.class,
				new EventCloudManagementWsApi() {

					@Override
					public boolean createEventCloud(String arg0) {
						System.out.println("Create event cloud operation");
						return true;
					}

					@Override
					public String createPublishProxy(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String createPutGetProxy(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String createSubscribeProxy(String arg0) {
						System.out.println("Create subscribe Proxy operation "
								+ arg0);
						return eventCloudEndpointPrefix + "SubscribePoxy";
					}

					@Override
					public boolean destroyEventCloud(String arg0) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean destroyPublishProxy(String arg0) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean destroyPutGetProxy(String arg0) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean destroySubscribeProxy(String arg0) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public List<String> getEventCloudIds() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List<String> getPublishProxyEndpointUrls(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List<String> getPutgetProxyEndpointUrls(String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getRegistryEndpointUrl() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List<String> getSubscribeProxyEndpointUrls(
							String arg0) {
						// TODO Auto-generated method stub
						return null;
					}

				});

		services.put(endpoint, service);
		service.start();
	}

}
