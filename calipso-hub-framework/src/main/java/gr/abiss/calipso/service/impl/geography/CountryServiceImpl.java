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
package gr.abiss.calipso.service.impl.geography;

import gr.abiss.calipso.model.geography.Country;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.repository.geography.CountryRepository;
import gr.abiss.calipso.service.geography.CountryService;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;


@Named("countryService")
@Transactional(readOnly = true)
public class CountryServiceImpl extends AbstractModelServiceImpl<Country, String, CountryRepository> implements CountryService {

}