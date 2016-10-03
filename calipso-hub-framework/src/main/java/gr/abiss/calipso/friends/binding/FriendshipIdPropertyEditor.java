package gr.abiss.calipso.friends.binding;

import java.beans.PropertyEditorSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.abiss.calipso.friends.model.FriendshipId;

public class FriendshipIdPropertyEditor extends PropertyEditorSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipIdPropertyEditor.class);
	
    @Override
	public void setAsText(String stringRepresentation) {
        this.setValue(new FriendshipId(stringRepresentation));
	}

    @Override
    public String getAsText() {
    	FriendshipId c = (FriendshipId) this.getValue();
        return c != null ? c.toStringRepresentation() : null;
    }
}
