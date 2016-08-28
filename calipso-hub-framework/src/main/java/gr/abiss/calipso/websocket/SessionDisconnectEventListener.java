package gr.abiss.calipso.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectEventListener 
        implements ApplicationListener<SessionDisconnectEvent> {
  
    public void onApplicationEvent(SessionDisconnectEvent event) {
    }
}