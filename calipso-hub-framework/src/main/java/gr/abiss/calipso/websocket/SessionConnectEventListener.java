package gr.abiss.calipso.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class SessionConnectEventListener 
        implements ApplicationListener<SessionConnectEvent> {
  
    public void onApplicationEvent(SessionConnectEvent event) {
    }
}