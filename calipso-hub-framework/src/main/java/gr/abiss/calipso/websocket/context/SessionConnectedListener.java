package gr.abiss.calipso.websocket.context;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;


import gr.abiss.calipso.fs.FilePersistenceService;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.service.FriendshipService;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.websocket.model.StompSession;
import gr.abiss.calipso.websocket.service.StompSessionService;

/**
 * Creates and persists a {@link StompSession}} instance upon a {@linkplain SessionConnectedEvent}
 */
@Component("calipsoSessionConnectedListener")
public class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent>{
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionConnectedListener.class);
	
	StompSessionService stompSessionService;

	@Inject
	@Qualifier("stompSessionService")
	public void setStompSessionService(StompSessionService stompSessionService) {
		this.stompSessionService = stompSessionService;
	}
	
    public void onApplicationEvent(SessionConnectedEvent event) {
    	
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        // get user
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth = 
        		(org.springframework.security.authentication.UsernamePasswordAuthenticationToken)event.getUser();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        
        // persist STOMP session
        StompSession stompSession = this.stompSessionService.create(new StompSession.Builder().id(sha.getSessionId()).user(new User(userDetails.getId())).build());
        LOGGER.debug("Persisted STOMP session: {}", stompSession);
    }
}