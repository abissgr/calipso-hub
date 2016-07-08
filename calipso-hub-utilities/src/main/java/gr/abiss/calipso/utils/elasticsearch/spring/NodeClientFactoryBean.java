/**
 * calipso-hub-utilities - A full stack, high level framework for lazy application hackers.
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
package gr.abiss.calipso.utils.elasticsearch.spring;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public abstract class NodeClientFactoryBean extends AbstractFactoryBean<Client> {

	private Client client;

	protected abstract Client createClient();
	
	/**
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
	 */
	@Override
	public Client createInstance() throws Exception {
		client = createClient();
		return client;
	}

	/**
	 * Destroy the singleton instance, if any.
	 * 
	 * @see #destroyInstance(Object)
	 */
	@Override
	public final void destroy() throws Exception {
		client.close();
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public final Class<?> getObjectType() {
		return Client.class;
	}
}