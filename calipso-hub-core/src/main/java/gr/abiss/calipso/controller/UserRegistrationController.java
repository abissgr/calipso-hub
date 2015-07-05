/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.controller;

import gr.abiss.calipso.jpasearch.model.structuredquery.Restriction;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.MetadatumDTO;

import java.util.Map;

import org.resthub.common.exception.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wordnik.swagger.annotations.Api;

@Api(value = "Users")
@RequestMapping(value = "/api/rest/userRegistrations", produces = {
		"application/json", "application/xml" })
public class UserRegistrationController extends UserController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserRegistrationController.class);

	@Override
	public Page<User> findPaginated(Integer page, Integer size, String sort,
			String direction) {
		// TODO Auto-generated method stub
		return super.findPaginated(page, size, sort, direction);
	}


	@Override
	public User update(String id, User resource) {
		throw new NotImplementedException("Request is not supported");
	}

	@Override
	public Iterable<User> findAll() {
		throw new NotImplementedException("Request is not supported");
	}

	@Override
	public User findById(String id) {
		throw new NotImplementedException("Request is not supported");
	}

	@Override
	public User getSchemaWrapperInstance() {
		throw new NotImplementedException("Request is not supported");
	}

	@Override
	public void delete(String id) {
		throw new NotImplementedException("Request is not supported");
	}

	@Override
	public Page<User> findPaginatedWithRestrictions(Restriction restriction) {
		throw new NotImplementedException("Request is not supported");
	}

	@Override
	public void addMetadatum(String subjectId, MetadatumDTO dto) {
		throw new NotImplementedException("Request is not supported");
	}

	@Override
	public void removeMetadatum(String subjectId, String predicate) {
		throw new NotImplementedException("Request is not supported");
	}

}
