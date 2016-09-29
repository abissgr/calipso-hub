package gr.abiss.calipso.websocket.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * A message about modifications of a {@link IMessageResource}
 * 
 * @param <S> the message subject type
 */
public class StateUpdateMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private Serializable id;
	
	@JsonProperty("@class")
	private String resourceClass;
	
	private Map<String, Serializable> modifications;
	
	public StateUpdateMessage(){
		
	}
	
	@Override
	public String toString() {
		return "StateUpdateMessage [id=" + id + ", resourceClass=" + resourceClass + ", modifications=" + modifications + "]";
	}

	public Serializable getId(){
		return this.id;
	}

	public void setId(Serializable id){
		this.id = id;
	}
	
	public String getResourceClass(){
		return this.resourceClass;
	}

	public void setResourceClass(String resourceClass){
		this.resourceClass = resourceClass;
	}

	//@JsonAnyGetter
	public Map<String, Serializable> getModifications(){
		return this.modifications;
	}
	
	public void addModification(String key, Serializable value){
		if(this.modifications == null){
			this.modifications = new HashMap<String, Serializable>();
		}
		this.modifications.put(key, value);
	}
	
	public void setModifications(Map<String, Serializable> modifications){
		this.modifications = modifications;
	}
	
}
