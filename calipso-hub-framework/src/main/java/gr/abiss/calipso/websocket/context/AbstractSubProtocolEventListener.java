package gr.abiss.calipso.websocket.context;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

import gr.abiss.calipso.websocket.service.StompSessionService;

/**
 * Sets the authentication context while obtaining a {@link StompHeaderAccessor}
 */
public abstract class AbstractSubProtocolEventListener<E extends AbstractSubProtocolEvent> implements ApplicationListener<E>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSubProtocolEventListener.class);
	
	protected StompSessionService stompSessionService;

	@Inject
	@Qualifier("stompSessionService")
	public void setStompSessionService(StompSessionService stompSessionService) {
		this.stompSessionService = stompSessionService;
	}
	

	protected void setAuthentication(E event) {
        // manually set user
        SecurityContextHolder.getContext().setAuthentication((Authentication) event.getUser());
	}
	
	protected void clearAuthentication() {
        // manually set user
        SecurityContextHolder.clearContext();
	}
	
	
}