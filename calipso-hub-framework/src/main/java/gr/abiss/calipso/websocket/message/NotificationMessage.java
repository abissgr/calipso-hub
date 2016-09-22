package gr.abiss.calipso.websocket.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Generic interface for "subject > predicate > object"-style messages. 
 * The semantics are generic enough to denote events like user > posted > comment or user > status > active
 *
 * @param <P> the predicate type
 * @see AbstractNotificationMessage
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.MINIMAL_CLASS,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "@class")
public interface NotificationMessage<S extends MessageResource<?>, P extends Enum<P>, O extends MessageResource<?>> {

	S getSubject();

	void setSubject(S subject);

	P getPredicate();

	void setPredicate(P predicate);

	O getObject();

	void setObject(O object);

}