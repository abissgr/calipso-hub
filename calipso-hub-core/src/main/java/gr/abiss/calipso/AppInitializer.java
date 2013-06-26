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
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso;

import gr.abiss.calipso.model.Sample;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.repository.SampleRepository;
import gr.abiss.calipso.service.UserService;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.resthub.common.util.PostInitialize;

@Named("sampleInitializer")
public class AppInitializer {

	@Inject
	@Named("sampleRepository")
	private SampleRepository sampleRepository;

	@Inject
	@Named("userService")
	private UserService userService;

	@PostInitialize
	public void init() {
		sampleRepository.save(new Sample("testSample1"));
		sampleRepository.save(new Sample("testSample2"));
		sampleRepository.save(new Sample("testSample3"));

		Date now = new Date();

		User u0 = new User("info@ab1ss.gr");
		u0.setFirstName("admin");
		u0.setLastName("user");
		u0.setUserName("admin");
		u0.setUserPassword("admin");
		u0.setLastVisit(now);
		u0 = userService.create(u0);

		User u1 = new User("manos @ab1ss.gr");
		u1.setFirstName("Manos");
		u1.setLastName("Batsis");
		u1.setUserName("manos");
		u1.setUserPassword("manos");
		u1.setLastVisit(now);
		u1 = userService.create(u1);
	}
}
