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