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
import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.cms.Text;
import gr.abiss.calipso.notification.model.BaseNotification;
import gr.abiss.calipso.notification.model.NotificationType;
import gr.abiss.calipso.notification.service.BaseNotificationsService;
import gr.abiss.calipso.service.HostService;
import gr.abiss.calipso.service.RoleService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.service.cms.TextService;
import gr.abiss.calipso.utils.ConfigurationFactory;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.configuration.Configuration;
import org.resthub.common.util.PostInitialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//@Named("sampleInitializer")
public class AppInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);
			
	@Inject
	@Named("userService")
	private UserService userService;

	@Inject
	@Named("hostService")
	private HostService hostService;
	
	@Inject
	@Named("textService")
	private TextService textService;

	@Inject
	@Named("roleService")
	private RoleService roleService;
	
	@Inject
	@Named("baseNotificationService")
	private BaseNotificationsService baseNotificationService;

	@PostInitialize
	public void init() {
		Configuration config = ConfigurationFactory.getConfiguration();
		boolean initData = config.getBoolean(ConfigurationFactory.INIT_DATA, true);
		if(initData){
			

			Role adminRole = new Role(Role.ROLE_ADMIN, "System Administrator.");
			adminRole = roleService.create(adminRole);
			Role siteAdminRole = new Role(Role.ROLE_SITE_OPERATOR, "Site Operator.");
			siteAdminRole = roleService.create(siteAdminRole);
			// this is added to users by user service, just creating it
			Role userRole = new Role(Role.ROLE_USER, "Logged in user");
			userRole = roleService.create(userRole);

			Date now = new Date();

			Host h1 = new Host("www.abiss.gr");
			h1.addAlias("abiss.gr");
			h1 = hostService.create(h1);
			Host h2 = new Host("dev.abiss.gr");
			h2 = hostService.create(h2);
			Host h3 = new Host("calipso.abiss.gr");
			h3 = hostService.create(h3);

			Text t1 = new Text("test2");
			t1.setHost(h2);
			t1.setSource("test2");
			t1.setSourceContentType(Text.MIME_MARKDOWN);
			textService.create(t1);
			Text t2 = new Text("test2");
			t2.setHost(h1);
			t2.setSource("test2");
			t2.setSourceContentType(Text.MIME_MARKDOWN);
			textService.create(t2);
			Text t3 = new Text("test3");
			t3.setHost(h1);
			t3.setSource("test3");
			t3.setSourceContentType(Text.MIME_MARKDOWN);
			textService.create(t3);
			
			User u0 = new User("info@abiss.gr");
			u0.setFirstName("admin");
			u0.setLastName("user");
			u0.setUsername("admin");
			u0.setPassword("admin");
			u0.setLastVisit(now);
			u0.addRole(adminRole);
			u0 = userService.createActive(u0);

			for(int i = 0; i < 10; i++){
				User u = new User("user"+i+"@abiss.gr");
				u.setFirstName("First"+i);
				u.setLastName("Last"+i);
				u.setUsername("user"+i);
				u.setPassword("user"+i);
				u.setLastVisit(now);
				u = userService.createActive(u);

				// notify the admin for each user creation to test notifications
				baseNotificationService.create(new BaseNotification(u, u0, null, now, (i % 2 == 0) ? true : false));
			}
			
			LOGGER.info("Admin has " + this.baseNotificationService.countUnseen(u0) + " notifications");
			
		
		}
		
	}
}
