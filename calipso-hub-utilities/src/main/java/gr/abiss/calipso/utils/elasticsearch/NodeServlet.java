package gr.abiss.calipso.utils.elasticsearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.wares.ServletRestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A servlet that can be used to dispatch requests to a local elasticsearch
 * Node.
 * <p>
 * The node stored under the servlet context attribute under
 * <tt>elasticsearchNode</tt> will be either re-used or set if null.
 * <p/>
 * 
 * @see org.elasticsearch.wares.NodeServlet
 */
public class NodeServlet extends org.elasticsearch.wares.NodeServlet {

	private static final long serialVersionUID = 5506898154243675564L;
	private static final Logger LOGGER = LoggerFactory.getLogger(NodeServlet.class);
			
	private boolean preExistingNode = false;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // search only
    	LOGGER.debug("getContextPath: "+req.getContextPath());
    	LOGGER.debug("getPathInfo: "+req.getPathInfo());
    	LOGGER.debug("getPathTranslated: "+req.getPathTranslated());
    	boolean allowed = false;
    	if(req.getMethod().equalsIgnoreCase("GET")){
    		allowed = true;
    	} 
    	else if(req.getMethod().equalsIgnoreCase("POST") && req.getPathInfo().startsWith("/_search")){
    		allowed = true;
    	}
    	
    	if(allowed){
        	super.service(req, resp);
    	}
    	else{
    		resp.sendError(405, "Index is readonly");
    	}
    }
	@Override
	public void init() throws ServletException {
		getServletContext().log(
				"Initializing elasticsearch Node servlet: '" + getServletName()
						+ "'");
		final Object nodeAttribute = getServletContext().getAttribute(NODE_KEY);
		if (nodeAttribute == null || !(nodeAttribute instanceof InternalNode)) {
			if (nodeAttribute != null) {
				getServletContext().log(
						"Warning: overwriting attribute with key \"" + NODE_KEY
								+ "\" and type \""
								+ nodeAttribute.getClass().getName() + "\".");
			}
			else{
				getServletContext().log(
						"Warning: no elasticsearch Node was found in servlet context using attribute key \"" + NODE_KEY
								+ "\", creating new...");
			}
			getServletContext().log(
					"Initializing elasticsearch Node '" + getServletName()
							+ "'");
			ImmutableSettings.Builder settings = ImmutableSettings
					.settingsBuilder();

			InputStream resourceAsStream = getServletContext()
					.getResourceAsStream("/WEB-INF/elasticsearch.json");
			if (resourceAsStream != null) {
				settings.loadFromStream("/WEB-INF/elasticsearch.json",
						resourceAsStream);
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					// ignore
				}
			}

			resourceAsStream = getServletContext().getResourceAsStream(
					"/WEB-INF/elasticsearch.yml");
			if (resourceAsStream != null) {
				settings.loadFromStream("/WEB-INF/elasticsearch.yml",
						resourceAsStream);
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					// ignore
				}
			}

			Enumeration<String> enumeration = getServletContext()
					.getAttributeNames();

			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();

				if (key.startsWith(NAME_PREFIX)) {
					Object attribute = getServletContext().getAttribute(key);

					if (attribute != null)
						attribute = attribute.toString();

					settings.put(key.substring(NAME_PREFIX.length()),
							(String) attribute);
				}
			}

			if (settings.get("http.enabled") == null) {
				settings.put("http.enabled", false);
			}

			node = NodeBuilder.nodeBuilder().settings(settings).node();
			getServletContext().setAttribute(NODE_KEY, node);
		} else {
			getServletContext().log("Using pre-initialized elasticsearch Node");
			this.node = (InternalNode) nodeAttribute;
			preExistingNode = true;
		}
		restController = ((InternalNode) node).injector().getInstance(
				RestController.class);
	}

	/**
	 * If using own Node, close it and remove from servlet context
	 * 
	 * @see org.elasticsearch.wares.NodeServlet#destroy()
	 */
	@Override
	public void destroy() {
		if (node != null && !preExistingNode) {
			getServletContext().removeAttribute(NODE_KEY);
			node.close();
		}
	}

}