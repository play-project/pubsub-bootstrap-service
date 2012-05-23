/**
 * 
 */
package eu.playproject.platform.service.bootstrap.api;

import fr.inria.eventcloud.webservices.api.EventCloudManagementWsApi;

/**
 * @author chamerling
 * 
 */
public interface EventCloudClientFactory {

	/**
	 * Get a client for the given endpoint
	 * 
	 * @param endpoint
	 * @return
	 */
	EventCloudManagementWsApi getClient(String endpoint);

}
