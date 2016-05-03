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
package gr.abiss.calipso.service.impl;

import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.repository.RoleRepository;
import gr.abiss.calipso.service.RoleService;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;


@Named("roleService")
@Transactional(readOnly = true)
public class RoleServiceImpl extends AbstractModelServiceImpl<Role, String, RoleRepository> implements RoleService {

	@Override
	public Role findByIdOrName(String idOrName) {
		Role role = this.repository.findOne(idOrName);
		if (role == null) {
			role = this.repository.findByName(idOrName);
		}
		return role;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMember(String roleId, String userId) {
		Role role = this.repository.findByIdOrName(roleId);
		User member = userRepository.findOne(userId);
		member.removeRole(role);
		userRepository.save(member);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveMember(String roleId, User user) {
		Role role = this.repository.findByIdOrName(roleId);
		User member = userRepository.findOne(user.getId());
		member.addRole(role);
		userRepository.save(member);
	}
}