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
package gr.abiss.calipso.service.cms.impl;

import gr.abiss.calipso.model.cms.BinaryFile;
import gr.abiss.calipso.repository.cms.BinaryFileRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.service.cms.BinaryFileService;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Named("binaryFileService")
public class BinaryFileServiceImpl extends AbstractModelServiceImpl<BinaryFile, String, BinaryFileRepository> 
	implements BinaryFileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryFileServiceImpl.class);

	@Override
	@Inject
	public void setRepository(BinaryFileRepository binaryFileRepository) {
		super.setRepository(binaryFileRepository);
	}

	@Transactional(readOnly = false)
	@Override
	public void delete(BinaryFile resource) {
		// TODO Auto-generated method stub
		this.repository.delete(resource);
	}
	
	

}