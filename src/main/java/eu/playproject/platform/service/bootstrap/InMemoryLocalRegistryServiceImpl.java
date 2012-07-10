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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;

import org.ow2.play.service.registry.api.Registry;

/**
 * A local registry service, badly implemented, not synchonized, ...
 * 
 * @author chamerling
 * 
 */
public class InMemoryLocalRegistryServiceImpl implements Registry {

	private static Logger logger = Logger
			.getLogger(InMemoryLocalRegistryServiceImpl.class.getName());

	private Map<String, String> map = new HashMap<String, String>();

	private List<String> list = new ArrayList<String>();

	private boolean loaded = false;

	protected synchronized void loadAll() {
		logger.info("Loading data");
		for (String url : list) {
			load(url);
		}
		loaded = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.play.service.registry.api.Registry#clear()
	 */
	@Override
	@WebMethod
	public void clear() {
		map = new HashMap<String, String>();
		loaded = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.play.service.registry.api.Registry#get(java.lang.String)
	 */
	@Override
	@WebMethod
	public String get(String key) {
		if (!loaded) {
			loadAll();
		}
		return map.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.play.service.registry.api.Registry#keys()
	 */
	@Override
	@WebMethod
	public List<String> keys() {
		if (!loaded) {
			loadAll();
		}
		return new ArrayList<String>(map.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.play.service.registry.api.Registry#load(java.lang.String)
	 */
	@Override
	@WebMethod
	public void load(String url) {
		if (url == null) {
			return;
		}
		Properties props = new Properties();

		try {
			URL u = new URL(url);
			props.load(u.openStream());

			for (String key : props.stringPropertyNames()) {
				map.put(key, props.getProperty(key));
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
			if (logger.isLoggable(Level.FINE)) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.play.service.registry.api.Registry#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@WebMethod
	public void put(String key, String value) {
		this.map.put(key, value);
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<String> list) {
		logger.info("Set list " + list);
		this.list = list;
	}

}
