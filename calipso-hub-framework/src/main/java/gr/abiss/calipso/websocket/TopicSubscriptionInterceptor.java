package gr.abiss.calipso.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import gr.abiss.calipso.model.User;

//TODO. setup for secirity in WebSocketConfig
public class TopicSubscriptionInterceptor extends ChannelInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopicSubscriptionInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())
        		//&& headerAccessor.getHeader("simpUser") !=null &&  headerAccessor.getHeader("simpUser") instanceof UsernamePasswordAuthenticationToken
        		) {
            UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) headerAccessor.getHeader("simpUser");

        	LOGGER.debug("preSend, userToken: {}", userToken);
            if(!validateSubscription((User)userToken.getPrincipal(), headerAccessor.getDestination()))
            {
                throw new IllegalArgumentException("No permission for this topic");
            }
        }
        return message;
    }

    private boolean validateSubscription(User principal, String topicDestination)
    {
    	LOGGER.debug("Validate subscription for {} to topic {}",principal.getUsername(),topicDestination);
        //Validation logic coming here
        return true;
    }
}