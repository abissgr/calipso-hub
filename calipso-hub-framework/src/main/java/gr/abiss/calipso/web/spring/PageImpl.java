package gr.abiss.calipso.web.spring;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * 
 * Extends spring's PageImpl to add JsonCreator. Makes it easy for Jackson to use for de-serialization. 
 *
 * @param <T>
 */
public class PageImpl<T> extends org.springframework.data.domain.PageImpl<T>{

	/**
	 * Creates a new {@link PageImpl} with the given content. This will result in the created {@link Page} being identical
	 * to the entire {@link List}.
	 * 
	 * @param content must not be {@literal null}.
	 */
	@JsonCreator
	public PageImpl(@JsonProperty("content") List<T> content) {
		super(content);
	}

}