package gr.abiss.calipso.service.impl;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.service.UserService;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

@Named("userService")
public class UserServiceImpl extends AbstractServiceImpl<User, String, UserRepository> implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	private final StringKeyGenerator generator = KeyGenerators.string();
	private EmailService emailService;


	@Inject
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Override
	@Inject
	public void setRepository(UserRepository userRepository) {
		super.setRepository(userRepository);
	}


	@Override
	public User findByCredentials(String userNameOrEmail, String password) {
		return this.repository.findByCredentials(userNameOrEmail, password);
	}



}