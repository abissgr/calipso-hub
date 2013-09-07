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
package gr.abiss.calipso.jpasearch.service;

import java.io.Serializable;

import org.resthub.common.service.CrudService;
import org.springframework.data.domain.Persistable;

/**
 * CRUD Service interface.
 * 
 * @param <T>
 *            Your resource POJO to manage, maybe an entity or DTO class
 * @param <ID>
 *            Resource id type, usually Long or String
 */
public interface GenericService<T extends Persistable<ID>, ID extends Serializable>
		extends
		CrudService<T, ID> {

	/**
	 * Get the entity Class corresponding to the generic T
	 * 
	 * @return the corresponding entity Class
	 */
	public Class<T> getDomainClass();
}
