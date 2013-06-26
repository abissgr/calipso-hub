package gr.abiss.calipso.service;

import gr.abiss.calipso.model.LoggedInUserDetails;

import org.resthub.common.service.CrudService;


public interface LoggedInUserDetailsService extends CrudService<LoggedInUserDetails, String> {

	LoggedInUserDetails create(String confirmationToken);

	LoggedInUserDetails resetPasswordAndLogin(String userNameOrEmail, String token, String newPassword);

	void sendPasswordResetToken(String userNameOrEmail);
}