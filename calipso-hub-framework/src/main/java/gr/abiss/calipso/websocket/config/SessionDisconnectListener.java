package gr.abiss.calipso.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import gr.abiss.calipso.websocket.model.StompSession;

/**
 * Removes a {@link StompSession}} instance from persistense upon a {@linkplain SessionDisconnectEvent}
 */
//@Component("calipsoSessionDisconnectedListener")
public class SessionDisconnectListener extends AbstractSubProtocolEventListener<SessionDisconnectEvent>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDisconnectListener.class);
	
    public void onApplicationEvent(SessionDisconnectEvent event) {

    	// init temp auth context
		setAuthentication(event);
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        
        // delete STOMP session
        this.stompSessionService.delete(sha.getSessionId());
        LOGGER.debug("Un-persisted STOMP session: {}", sha.getSessionId());

    	// clear temp auth context for this thread
        clearAuthentication();
    }
}