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

import java.util.Map;

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
public class ParameterMapBackedPageRequest extends PageRequest {

	private static final long serialVersionUID = -752023352147402776L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterMapBackedPageRequest.class);

	private Map<String, String[]> parameterMap = null;

	/**
	 * 
	 * @param parameterMap
	 * @param page
	 * @param size
	 */
	public ParameterMapBackedPageRequest(Map<String, String[]> parameterMap, int page, int size) {
		super(page, size);
		this.parameterMap = parameterMap;
	}

	/**
	 * 
	 * @param parameterMap
	 * @param page
	 * @param size
	 * @param sort
	 */
	public ParameterMapBackedPageRequest(Map<String, String[]> parameterMap, int page, int size, Sort sort) {
		super(page, size, sort);
		this.parameterMap = parameterMap;
	}

	/**
	 * 
	 * @param parameterMap
	 * @param page
	 * @param size
	 * @param direction
	 * @param properties
	 */
	public ParameterMapBackedPageRequest(Map<String, String[]> parameterMap, int page, int size, Direction direction, String... properties) {
		super(page, size, direction, properties);
		this.parameterMap = parameterMap;
	}

	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap = parameterMap;
	}
}
