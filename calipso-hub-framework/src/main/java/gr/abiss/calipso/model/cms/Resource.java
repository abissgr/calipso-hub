/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.model.cms;

import static org.apache.commons.lang.CharEncoding.UTF_8;

import gr.abiss.calipso.model.Host;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.types.ResourceProtocol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
@Entity
@Table(name = "content_resource")
public class Resource extends AbstractSystemUuidPersistable {

	private static final long serialVersionUID = -7942906897981646998L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

	/**
	 * The last HTTP URL segment of the resource, excluding the protocol prefix, host, port and extension suffix.
	 * Starts with a slash. 
	 */
	@Column(name = "name", nullable = false)
	private String name;
	
	/**
	 * The last HTTP URL segment of the resource, excluding the protocol prefix, host, port and extension suffix.
	 * Starts with a slash. 
	 */
	@Column(name = "path_name", nullable = false)
	private String pathName;

	/**
	 * The HTTP domain of the resource. 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "host", referencedColumnName = "id", nullable = true)
	private Host host;

	/**
	 * The HTTP domain of the resource. 
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ResourceProtocol protocol = ResourceProtocol.HTTP;

	

	public Resource() {
		super();
	}
	public Resource(String name) {
		this();
		this.name = name;
		try {
			this.pathName = URLEncoder.encode(name, UTF_8);
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn("Could not URL encode given name '"+name+"'", e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Resource)) {
			return false;
		}
		Resource that = (Resource) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public ResourceProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(ResourceProtocol protocol) {
		this.protocol = protocol;
	}




}