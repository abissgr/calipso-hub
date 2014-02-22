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
package gr.abiss.calipso;

import gr.abiss.calipso.model.Host;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.cms.Text;
import gr.abiss.calipso.service.HostService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.service.cms.TextService;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.resthub.common.util.PostInitialize;

@Named("sampleInitializer")
public class AppInitializer {

	@Inject
	@Named("userService")
	private UserService userService;

	@Inject
	@Named("hostService")
	private HostService hostService;
	
	@Inject
	@Named("textService")
	private TextService textService;

	@PostInitialize
	public void init() {
		Date now = new Date();

		Host h1 = new Host("www.abiss.gr");
		h1.addAlias("abiss.gr");
		hostService.create(h1);
		Host h2 = new Host("dev.abiss.gr");
		hostService.create(h2);
		Host h3 = new Host("calipso.abiss.gr");
		hostService.create(h3);

		Text t1 = new Text();
		t1.setHost(h2);
		t1.setPath("test2");
		t1.setSource("test2");
		t1.setSourceContentType(Text.MIME_MARKDOWN);
		textService.create(t1);
		Text t2 = new Text();
		t2.setHost(h1);
		t2.setPath("test2");
		t2.setSource("test2");
		t2.setSourceContentType(Text.MIME_MARKDOWN);
		textService.create(t2);
		Text t3 = new Text();
		t3.setHost(h1);
		t3.setPath("test3");
		t3.setSource("test3");
		t3.setSourceContentType(Text.MIME_MARKDOWN);
		textService.create(t3);
		
		User u0 = new User("info@abiss.gr");
		u0.setFirstName("admin");
		u0.setLastName("user");
		u0.setUserName("admin");
		u0.setUserPassword("admin");
		u0.setLastVisit(now);
		u0 = userService.create(u0);

		User u1 = new User("manosi@abiss.gr");
		u1.setFirstName("Manos");
		u1.setLastName("Batsis");
		u1.setUserName("manos");
		u1.setUserPassword("manos");
		u1.setLastVisit(now);
		u1 = userService.create(u1);
		
		for(int i = 0; i < 30; i++){
			User u = new User("user"+i+"@abiss.gr");
			u.setFirstName("First"+i);
			u.setLastName("Last"+i);
			u.setUserName("user"+i);
			u.setUserPassword("user"+i);
			u.setLastVisit(now);
			u = userService.create(u);
		}
	}
}
