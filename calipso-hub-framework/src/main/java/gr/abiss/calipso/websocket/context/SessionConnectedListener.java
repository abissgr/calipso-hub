package gr.abiss.calipso.websocket.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import gr.abiss.calipso.websocket.model.StompSession;

/**
 * Creates and persists a {@link StompSession}} instance upon a {@linkplain SessionConnectedEvent}
 */
@Component("calipsoSessionConnectedListener")
public class SessionConnectedListener extends AbstractSubProtocolEventListener<SessionConnectedEvent>{
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionConnectedListener.class);
	
    public void onApplicationEvent(SessionConnectedEvent event) {

    	// init temp auth context
		setAuthentication(event);
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        // persist STOMP session
        StompSession stompSession = this.stompSessionService.create(new StompSession.Builder().id(sha.getSessionId()).build());
        LOGGER.debug("Persisted STOMP session: {}", stompSession);

    	// clear temp auth context
        clearAuthentication();
        
        
    }

}