/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.websocket.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.websocket.model.StompSession;

public interface StompSessionService extends ModelService<StompSession, String> {

	public static final String BEAN_ID = "stompSessionService";
	
	public void onSessionConnectEvent(SessionConnectEvent event);

	public void onSessionConnectedEvent(SessionConnectedEvent event);
	
	public void onSessionSubscribeEvent(SessionSubscribeEvent event);

	public void onSessionUnsubscribeEvent(SessionUnsubscribeEvent event);

	public void onSessionDisconnectEvent(SessionDisconnectEvent event);
	
}