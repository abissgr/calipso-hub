package gr.abiss.calipso.utils.elasticsearch.spring;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Create a local Node Client for an existing Node
 * 
 * @see gr.abiss.calipso.utils.elasticsearch.spring.LocalNodeFactoryBean
 */
public class LocalNodeClientFactoryBean extends NodeClientFactoryBean {

	@Autowired
	private Node node;

	/**
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(this.node);
	}

	/**
	 * Create a local Node Client
	 * 
	 * @see gr.abiss.calipso.utils.elasticsearch.spring.NodeClientFactoryBean#createClient()
	 */
	@Override
	protected Client createClient() {
		return this.node.client();
	}
}