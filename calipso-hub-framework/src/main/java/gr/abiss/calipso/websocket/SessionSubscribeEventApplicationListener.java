package gr.abiss.calipso.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

public class SessionSubscribeEventApplicationListener implements ApplicationListener<SessionSubscribeEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionSubscribeEventApplicationListener.class);
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		LOGGER.info("onApplicationEvent: " + event);
	}
	

}
