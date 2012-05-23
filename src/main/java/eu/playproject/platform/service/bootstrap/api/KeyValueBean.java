/**
 * 
 */
package eu.playproject.platform.service.bootstrap.api;

/**
 * @author chamerling
 * 
 */
public class KeyValueBean {

	private String key;

	private String value;

	public KeyValueBean() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "KeyValueBean [key=" + key + ", value=" + value + "]";
	}

}
