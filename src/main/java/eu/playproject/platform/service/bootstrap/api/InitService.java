/**
 * 
 */
package eu.playproject.platform.service.bootstrap.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author chamerling
 *
 */
@Path("/init/")
public interface InitService {
	
	@GET
	boolean go();

}
