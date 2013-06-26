package gr.abiss.calipso.controller;

import gr.abiss.calipso.jpasearch.controller.AbstractServiceBasedRestController;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.service.UserService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/api/user", produces = { "application/json", "application/xml" })
public class UserController extends AbstractServiceBasedRestController<User, String, UserService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Override
	@Inject
	public void setService(UserService service) {
		this.service = service;
	}

	@Override
	public User create(@RequestBody User resource) {
		User user = super.create(resource);
		return user;

	}
    
}
