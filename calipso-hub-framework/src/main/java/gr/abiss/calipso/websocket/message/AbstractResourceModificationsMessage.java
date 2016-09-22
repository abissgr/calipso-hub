package gr.abiss.calipso.websocket.message;

/**
 * Convenient generic base type for "subject > predicate > object"-style messages. 
 * The semantics are generic enough to denote events like user > posted > comment or user > status > active
 *
 * @param <P> the predicate type
 */
public class AbstractResourceModificationsMessage<S extends MessageResource<?>, P extends Enum<P>, O extends MessageResource<?>>
		implements NotificationMessage<S, P, O> {

	private S subject;

	private P predicate;

	private O object;

	public AbstractResourceModificationsMessage() {
		super();
	}

	@Override
	public S getSubject() {
		return subject;
	}

	@Override
	public void setSubject(S subject) {
		this.subject = subject;
	}

	@Override
	public P getPredicate() {
		return predicate;
	}

	@Override
	public void setPredicate(P predicate) {
		this.predicate = predicate;
	}

	@Override
	public O getObject() {
		return object;
	}

	@Override
	public void setObject(O object) {
		this.object = object;
	}
}
