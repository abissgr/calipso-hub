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
package gr.abiss.calipso.service.cms.impl;

import gr.abiss.calipso.model.cms.Folder;
import gr.abiss.calipso.repository.cms.FolderRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.service.cms.FolderService;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("folderService")
public class FolderServiceImpl extends AbstractModelServiceImpl<Folder, String, FolderRepository> 
	implements FolderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FolderServiceImpl.class);

	@Override
	@Inject
	public void setRepository(FolderRepository folderRepository) {
		super.setRepository(folderRepository);
	}

}