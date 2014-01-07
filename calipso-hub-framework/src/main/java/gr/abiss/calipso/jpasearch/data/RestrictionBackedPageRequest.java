/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package gr.abiss.calipso.jpasearch.data;

import gr.abiss.calipso.jpasearch.model.structuredquery.Restriction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
/**
 * An implementation of PageRequest that holds all request parameters as a Map
 * provided by 
 * {@link http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/web/context/request/WebRequest.html#getParameterMap()}
 */
@Deprecated
public class RestrictionBackedPageRequest extends PageRequest {

	private static final long serialVersionUID = -752023352147402776L;

	private static final Logger LOGGER = LoggerFactory.getLogger(RestrictionBackedPageRequest.class);

	private Restriction restriction = null;

	/**
	 * 
	 * @param parameterMap
	 * @param page
	 * @param size
	 */
	public RestrictionBackedPageRequest(Restriction restriction) {
		super(1, 10);
		this.restriction = restriction;
	}

	/**
	 * 
	 * @param parameterMap
	 * @param page
	 * @param size
	 * @param sort
	 */
	public RestrictionBackedPageRequest(Restriction restriction, int page,
			int size, Sort sort) {
		super(page, size, sort);
		this.restriction = restriction;
	}

	/**
	 * 
	 * @param parameterMap
	 * @param page
	 * @param size
	 * @param direction
	 * @param properties
	 */
	public RestrictionBackedPageRequest(Restriction restriction, int page,
			int size, Direction direction, String... properties) {
		super(page, size, direction, properties);
		this.restriction = restriction;
	}

	public Restriction getRestriction() {
		return restriction;
	}

	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;
	}
}
