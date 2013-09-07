/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/lgpl-3.0.txt
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
