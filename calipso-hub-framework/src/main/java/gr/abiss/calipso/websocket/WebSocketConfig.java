package gr.abiss.calipso.websocket;

import javax.inject.Inject;

import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.utils.ConfigurationFactory;

@Configuration
@EnableWebSocketMessageBroker
@ComponentScan(basePackages = "**.calipso.controller")
@EnableScheduling
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer
		implements WebSocketMessageBrokerConfigurer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConfig.class);

	@Inject
	@Qualifier("userDetailsService")
	private UserDetailsService userDetailsService;
	
	/**
	 * Registers the "/ws" endpoint, enabling SockJS fallback options so that alternative 
	 * messaging options may be used if WebSocket is not available. 
	 * 
	 * This endpoint, when prefixed with "/app", is the endpoint that the 
	 * controller methods are mapped to handle.
	 * 
	 * @see org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer#registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry)
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
		org.apache.commons.configuration.Configuration config = ConfigurationFactory.getConfiguration();
		String domain = config.getString(ConfigurationFactory.DOMAIN);
		String originWithPort = new StringBuffer(domain).append(':').append(config.getString(ConfigurationFactory.PORT)).toString();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("registerStompEndpoints");
		}
		stompEndpointRegistry.addEndpoint("/ws")
			.setHandshakeHandler(handshakeHandler())
			.setAllowedOrigins("*"/*domain, originWithPort*/);
	}

	/**  
	 * Configure the message broker with eenableSimpleBroker(), to enablee a simple memory-based message broker 
	 * to carry messages back to the client on destinations prefixed with "/topic". 
	 * 
	 * The "/app" prefix is designated for messages that are bound for @MessageMapping-annotated methods.
	 * 
	 * @see org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer#configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry)
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("configureMessageBroker");
		}
		messageBrokerRegistry.enableSimpleBroker("/topic", "/queue").setTaskScheduler(heartbeatTaskScheduler());
		messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
	}
	


	/**
	 * For Jetty, we need to supply a pre-configured Jetty
	 * WebSocketServerFactory and plug that into Spring’s
	 * DefaultHandshakeHandler through your WebSocket Java config:
	 * 
	 * @return
	 */
	@Bean
	public HandshakeHandler handshakeHandler() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("handshakeHandler");
		}

		WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
		policy.setInputBufferSize(8192);
		policy.setIdleTimeout(600000);

		return new DefaultHandshakeHandler(new JettyRequestUpgradeStrategy(new WebSocketServerFactory(policy)));
	}
	
	@Bean
    ThreadPoolTaskScheduler heartbeatTaskScheduler() {
        return new ThreadPoolTaskScheduler();
	}

}