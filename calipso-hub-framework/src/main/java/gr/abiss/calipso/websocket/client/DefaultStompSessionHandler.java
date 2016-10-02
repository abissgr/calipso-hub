package gr.abiss.calipso.websocket.client;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

public class DefaultStompSessionHandler implements StompSessionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStompSessionHandler.class);
	/**
	 * This implementation is empty.
	 */
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

        LOGGER.info("afterConnected: session: " + session + ". connectedHeaders: " + connectedHeaders);
	}

	/**
	 * This implementation returns String as the expected payload type
	 * for STOMP ERROR frames.
	 */
	@Override
	public Type getPayloadType(StompHeaders headers) {
        LOGGER.info("getPayloadType: headers: " + headers);
		return String.class;
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
        LOGGER.info("handleFrame: headers: " + headers + ", payload: " + payload);
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers,
			byte[] payload, Throwable exception) {

        LOGGER.error("handleException: session: " + session + ", command: " + command + ", headers: " + headers, exception);
        throw new RuntimeException(exception);
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
        LOGGER.error("Transport ERROR for session: " + session.getSessionId(), exception);
        throw new RuntimeException(exception);
	}

}