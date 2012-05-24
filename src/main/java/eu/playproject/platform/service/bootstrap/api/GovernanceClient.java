/**
 * 
 */
package eu.playproject.platform.service.bootstrap.api;

import eu.playproject.governance.api.EventGovernance;
import eu.playproject.governance.api.TopicMetadataService;

/**
 * @author chamerling
 * 
 */
public interface GovernanceClient extends TopicMetadataService, EventGovernance {

	public static String TOPIC_ENDPOINT = "endpoint.topic";
	public static String META_ENDPOINT = "endpoint.metadata";

}
