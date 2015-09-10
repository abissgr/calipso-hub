package gr.abiss.calipso.model.base;

import java.util.List;

/**
 * Interface for partial updates. Base controllers will pick this up and 
 * load the persisted instance, update the changed properties and save.
 */
public interface PartiallyUpdateable {

	public List<String> getChangedAttributes();
	public void setChangedAttributes(List<String> attrs);
}
