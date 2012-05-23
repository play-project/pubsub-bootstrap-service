/**
 * 
 */
package eu.playproject.platform.service.bootstrap.api;

/**
 * @author chamerling
 *
 */
public class BootstrapFault extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public BootstrapFault() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public BootstrapFault(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public BootstrapFault(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BootstrapFault(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
