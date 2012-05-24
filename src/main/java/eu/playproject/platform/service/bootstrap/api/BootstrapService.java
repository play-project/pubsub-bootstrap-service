/**
 * 
 */
package eu.playproject.platform.service.bootstrap.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Bootstrap API between notification consumers and providers.
 * 
 * @author chamerling
 * 
 */
@Path("/bootstrap/")
@WebService
public interface BootstrapService {

	/**
	 * Subscribes to a topic on the provider endpoint on behalf of the
	 * subscriber. It means that when the subscription is done, the subscriber
	 * will be notified when new messages are published on the topic by the
	 * provider.
	 * 
	 * @return
	 */
	@WebMethod
	@GET
	@Path("/boot")
	List<KeyValueBean> bootstrap(String providerEndpoint,
			String subscriberEndpoint) throws BootstrapFault;

}
