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

import gr.abiss.calipso.utils.elasticsearch.NodeServlet;

import javax.servlet.ServletContext;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Spring FactoryBean that creates a local node and makes accessible via the
 * servlet context as "elasticsearchNode". Loads elasticsearch/elasticsearch.yml
 * from the classpath
 */
public class LocalNodeFactoryBean extends AbstractFactoryBean<Node> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LocalNodeFactoryBean.class);

	private Node node;

	@Autowired
	private ServletContext servletContext;

	/**
	 * Creates a local node and makes accessible via the servlet context as
	 * "elasticsearchNode". Loads elasticsearch/elasticsearch.yml from classpath
	 * 
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
	 */
	@Override
	protected Node createInstance() throws Exception {
		// load settings from classpathn
		Builder builder = ImmutableSettings.settingsBuilder()
				.loadFromClasspath("elasticsearch/elasticsearch.yml");

		// create node according to settings
		this.node = NodeBuilder.nodeBuilder().settings(builder).node();
		// make the node accessible via the servlet context as
		// "elasticsearchNode"
		this.servletContext.setAttribute(
				NodeServlet.NODE_KEY, this.node);
		return this.node;
	}

	/**
	 * @see #destroyInstance(Object)
	 */
	@Override
	public void destroy() throws Exception {
		this.node.close();
		servletContext.removeAttribute(NodeServlet.NODE_KEY);
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return Node.class;
	}
}