/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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