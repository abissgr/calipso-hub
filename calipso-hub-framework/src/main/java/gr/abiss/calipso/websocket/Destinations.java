package gr.abiss.calipso.websocket;

public class Destinations {
	
	/**
	 * Publishes {@link gr.abiss.calipso.websocket.message.IStateUpdateMessage} state updates for entities the user is expected to be interested in, for example friend status
	 */
	public final static String USERQUEUE_UPDATES_STATE = "/queue/stateupdates";

	/**
	 * Publishes {@link gr.abiss.calipso.websocket.message.IActivityNotificationMessage} activity updates of entities the user is expected to be interested in
	 */
	public final static String USERQUEUE_UPDATES_ACTIVITY = "/queue/updates/activity";

	public static final String USERQUEUE_FRIENDSHIPS = "/queue/friendships";

}
