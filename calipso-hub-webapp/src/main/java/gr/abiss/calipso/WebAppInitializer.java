package gr.abiss.calipso;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.h2.server.web.WebServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * This class replaces the "old" web.xml and is automatically scanned at the application startup
 */
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
		// Load config properties
		CompositeConfiguration config = new CompositeConfiguration();
		try {
			config.addConfiguration(new PropertiesConfiguration(
					"calipso.properties"));
			config.addConfiguration(new PropertiesConfiguration(
					"calipso.defaults.properties"));
		} catch (ConfigurationException e) {
			throw new RuntimeException("Failed to load configuration", e);
		}

		// Configure cookie security
		boolean secureCookies = config.getBoolean("calipso.cookies.secure",
				false);
		boolean httpOnly = config.getBoolean("calipso.cookies.httpOnly", false);
		servletContext.getSessionCookieConfig().setHttpOnly(httpOnly);
		servletContext.getSessionCookieConfig().setSecure(secureCookies);
		// LOGGER.info("Using secure cookies: " + secureCookies +
		// ", HTTP only: "
		// + httpOnly);

		// utf-8
		FilterRegistration.Dynamic encodingFilter = servletContext.addFilter(
				"CharacterEncodingFilter", CharacterEncodingFilter.class);
		encodingFilter.setInitParameter("encoding", "UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(
				(EnumSet.of(DispatcherType.REQUEST)), true, "/*");

		// // only using URL rewriting in manager/* for now
		// FilterRegistration.Dynamic urlrewriteFilter =
		// servletContext.addFilter(
		// "UrlRewriteFilter", UrlRewriteFilter.class);
		// urlrewriteFilter.addMappingForUrlPatterns(
		// (EnumSet.of(DispatcherType.REQUEST)), true, "/*");
		// // urlrewriteFilter.setInitParameter("logLevel", "DEBUG");
		//
		// // Convert JSONP requests (i.e. HTTP GETs) to proper REST methods
		// FilterRegistration.Dynamic jsonpToRestFilter = servletContext
		// .addFilter("JSONP View Filter", RestToJsonpFilter.class);
		// jsonpToRestFilter.addMappingForUrlPatterns(
		// (EnumSet.of(DispatcherType.REQUEST)), true, "/api/*");
		// jsonpToRestFilter.addMappingForUrlPatterns(
		// (EnumSet.of(DispatcherType.REQUEST)), true, "/apiauth/*");

		// security filter for normal session based users
		FilterRegistration.Dynamic springSecurityFilterChain = servletContext
				.addFilter("springSecurityFilterChain",
						DelegatingFilterProxy.class);
		springSecurityFilterChain.addMappingForUrlPatterns(
				(EnumSet.of(DispatcherType.REQUEST)), true, "/api/*");

        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.getEnvironment().setActiveProfiles("resthub-jpa", "resthub-web-server");
        String[] locations = { "classpath*:resthubContext.xml", "classpath*:applicationContext.xml" };
        appContext.setConfigLocations(locations);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");

        servletContext.addListener(new ContextLoaderListener(appContext));

        //Database Console for managing the app's database (TODO : profile)
        ServletRegistration.Dynamic h2Servlet = servletContext.addServlet("h2console", WebServlet.class);
        h2Servlet.setLoadOnStartup(2);
        h2Servlet.addMapping("/console/database/*");
    }
}
