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

import gr.abiss.calipso.model.cms.Text;
import gr.abiss.calipso.repository.cms.PageRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.service.cms.TextService;
import gr.abiss.calipso.service.impl.AbstractServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("pageService")
public class TextServiceImpl extends AbstractServiceImpl<Text, String, PageRepository> 
	implements TextService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TextServiceImpl.class);

	private EmailService emailService;


	@Inject
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Override
	@Inject
	public void setRepository(PageRepository pageRepository) {
		super.setRepository(pageRepository);
	}

}