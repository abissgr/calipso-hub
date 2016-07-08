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
package gr.abiss.calipso.service.impl.acl;

import java.util.Date;

import gr.abiss.calipso.model.acl.AclClass;
import gr.abiss.calipso.model.dto.ReportDataSet;
import gr.abiss.calipso.model.types.AggregateFunction;
import gr.abiss.calipso.model.types.TimeUnit;
import gr.abiss.calipso.repository.acl.AclClassRepository;
import gr.abiss.calipso.service.acl.AclClassService;
import gr.abiss.calipso.tiers.service.impl.AbstractAclAwareServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named("aclClassService")
@Transactional(readOnly = true)
public class AclClassServiceImpl extends
		AbstractAclAwareServiceImpl<AclClass, Long, AclClassRepository> implements
		AclClassService {

	@Override
	public AclClass findByClassName(String name) {
		return this.repository.findByClassName(name);
	}


}

