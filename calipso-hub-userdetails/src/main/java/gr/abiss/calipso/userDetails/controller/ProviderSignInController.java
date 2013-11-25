/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.abiss.calipso.userDetails.controller;

import gr.abiss.calipso.userDetails.controller.form.RegistrationForm;
import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;
import gr.abiss.calipso.userDetails.util.SecurityUtil;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;
import gr.abiss.calipso.userDetails.util.SocialMediaService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;


//@Controller
@SessionAttributes("user")
@RequestMapping("/signin")
public class ProviderSignInController extends org.springframework.social.connect.web.ProviderSignInController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderSignInController.class);

	protected static final String VIEW_NAME_REMEMBER_PAGE = "user/remember";
	protected static final String VIEW_NAME_REGISTRATION_PAGE = "user/registrationForm";

	protected static final String ERROR_CODE_EMAIL_EXIST = "NotExist.user.email";
	protected static final String MODEL_NAME_REGISTRATION_DTO = "user";

	private UserDetailsService userService;

	private UserDetailsConfig userDetailsConfig = new SimpleUserDetailsConfig();

	@Inject
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}

	@Inject
	public void setUserService(UserDetailsService userService) {
		this.userService = userService;
	}

	/**
	 * Creates a new provider sign-in controller.
	 * @param connectionFactoryLocator the locator of {@link ConnectionFactory connection factories} used to support provider sign-in.
	 * Note: this reference should be a serializable proxy to a singleton-scoped target instance.
	 * This is because {@link ProviderSignInAttempt} are session-scoped objects that hold ConnectionFactoryLocator references.
	 * If these references cannot be serialized, NotSerializableExceptions can occur at runtime.
	 * @param usersConnectionRepository the global store for service provider connections across all users.
	 * Note: this reference should be a serializable proxy to a singleton-scoped target instance.
	 * @param signInAdapter handles user sign-in
	 */
	@Inject
	public ProviderSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository,
			SignInAdapter signInAdapter) {
		super(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
	}

	@RequestMapping(value = "/popup/remember", method = RequestMethod.GET)
	public String showRememberPage() {
		LOGGER.debug("Rendering remember page.");
		return VIEW_NAME_REMEMBER_PAGE;
	}

	/**
	* Renders the registration page.
	*/
	@RequestMapping(value = "/popup/register", method = { RequestMethod.GET })
	//@RequestMapping(value = "/popup/register", method = { RequestMethod.GET, RequestMethod.POST })
	public String showRegistrationForm(WebRequest request, Model model) {
		LOGGER.debug("Rendering registration page.");

		Connection<?> connection = ProviderSignInUtils.getConnection(request);

		RegistrationForm registration = createRegistrationDTO(connection);
		LOGGER.debug("Rendering registration form with information: {}", registration);

		model.addAttribute(MODEL_NAME_REGISTRATION_DTO, registration);

		return VIEW_NAME_REGISTRATION_PAGE;
	}

	/**
	* Processes the form submissions of the registration form.
	*/
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerUserAccount(@Valid @ModelAttribute("user") RegistrationForm userAccountData, BindingResult result,
			WebRequest request, HttpServletRequest nativeRquest, HttpServletResponse response) throws DuplicateEmailException {
		LOGGER.debug("registerUserAccount with data: " + userAccountData);
		if (result.hasErrors()) {
			LOGGER.debug("Validation errors found. Rendering form view.");
			return VIEW_NAME_REGISTRATION_PAGE;
		}

		LOGGER.debug("No validation errors found. Continuing registration process.");

		UserDetails registered = createUserAccount(userAccountData, result);

		//If email address was already found from the database, render the form view.
		if (registered == null) {
			LOGGER.debug("An email address was found from the database. Rendering form view.");
			return VIEW_NAME_REGISTRATION_PAGE;
		}

		//Logs the user in.
		SecurityUtil.login(nativeRquest, response, registered, this.userDetailsConfig);
		//If the user is signing in by using a social provider, this method call stores
		//the connection to the UserConnection table. Otherwise, this method does not
		//do anything.
		ProviderSignInUtils.handlePostSignUp(registered.getEmail(), request);

		return "redirect:/signin/popup/remember";
	}

	/**
	* Creates a new user account by calling the service method. If the email address is found
	* from the database, this method adds a field error to the email field of the form object.
	*/
	private UserDetails createUserAccount(RegistrationForm userAccountData, BindingResult result) {
		LOGGER.debug("Creating user account with information: {}", userAccountData);
		UserDetails registered = null;

		try {
			UserDetails user = new UserDetails();
			user.setEmail(userAccountData.getEmail());
			user.setFirstName(userAccountData.getFirstName());
			user.setLastName(userAccountData.getLastName());
			user.setUserName(userAccountData.getUserName());
			user.setUserPassword(userAccountData.getPassword());

			registered = UserDetails.fromUser(userService.createForImplicitSignup(user));
		} catch (DuplicateEmailException ex) {
			LOGGER.debug("An email address: {} exists.", userAccountData.getEmail());
			addFieldError(MODEL_NAME_REGISTRATION_DTO, RegistrationForm.FIELD_NAME_EMAIL, userAccountData.getEmail(),
					ERROR_CODE_EMAIL_EXIST, result);
		}

		return registered;
	}

	private void addFieldError(String objectName, String fieldName, String fieldValue, String errorCode, BindingResult result) {
		LOGGER.debug("Adding field error object's: {} field: {}", objectName, fieldName);
		FieldError error = new FieldError(objectName, fieldName, fieldValue, false, new String[] { errorCode }, new Object[] {}, errorCode);

		result.addError(error);
		LOGGER.debug("Added field error: {} to binding result: {}", error, result);
	}

	/**
	* Creates the form object used in the registration form.
	* @param connection
	* @return If a user is signing in by using a social provider, this method returns a form
	* object populated by the values given by the provider. Otherwise this method returns
	* an empty form object (normal form registration).
	*/
	private RegistrationForm createRegistrationDTO(Connection<?> connection) {
		RegistrationForm dto = new RegistrationForm();

		if (connection != null) {

			LOGGER.debug("createRegistrationDTO connection: " + connection);
			UserProfile socialMediaProfile = connection.fetchUserProfile();
			dto.setEmail(socialMediaProfile.getEmail());
			dto.setFirstName(socialMediaProfile.getFirstName());
			dto.setLastName(socialMediaProfile.getLastName());
			dto.setUserName(socialMediaProfile.getUsername());

			ConnectionKey providerKey = connection.getKey();
			dto.setSignInProvider(SocialMediaService.valueOf(providerKey.getProviderId().toUpperCase()));

			LOGGER.debug("createRegistrationDTO prepopulated form: " + dto);
		} else {
			LOGGER.debug("createRegistrationDTO: no connection was found");
		}

		return dto;
	}

	//SHOULD ALWAYS BE POST, leaving to super class' RequestMapping
	@Override
	@RequestMapping(value = "/{providerId}", method = RequestMethod.POST)
	public RedirectView signIn(@PathVariable String providerId, NativeWebRequest request) {
		LOGGER.info("popupSignIn, providerId: " + providerId);
		//		HttpSession session = ((HttpServletRequest) request.getNativeRequest()).getSession();
		//		if (session == null) {
		//			session = ((HttpServletRequest) request.getNativeRequest()).getSession(true);
		//			LOGGER.info("popupSignIn, created session");
		//
		//		}
		return super.signIn(providerId, request);
	}
}
