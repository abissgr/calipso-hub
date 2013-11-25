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
package gr.abiss.calipso.userDetails.controller.form;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
*/
public class PasswordsNotEmptyValidator implements ConstraintValidator<PasswordsNotEmpty, Object> {

	private String validationTriggerFieldName;
	private String passwordFieldName;
	private String passwordVerificationFieldName;

	@Override
	public void initialize(PasswordsNotEmpty constraintAnnotation) {
		validationTriggerFieldName = constraintAnnotation.triggerFieldName();
		passwordFieldName = constraintAnnotation.passwordFieldName();
		passwordVerificationFieldName = constraintAnnotation.passwordVerificationFieldName();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		try {
			Object validationTrigger = ValidatorUtil.getFieldValue(value, validationTriggerFieldName);
			if (validationTrigger == null) {
				return passwordFieldsAreValid(value, context);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Exception occurred during validation", ex);
		}

		return true;
	}

	private boolean passwordFieldsAreValid(Object value, ConstraintValidatorContext context) throws NoSuchFieldException,
			IllegalAccessException {
		boolean passwordWordFieldsAreValid = true;

		String password = (String) ValidatorUtil.getFieldValue(value, passwordFieldName);
		if (isNullOrEmpty(password)) {
			ValidatorUtil.addValidationError(passwordFieldName, context);
			passwordWordFieldsAreValid = false;
		}

		String passwordVerification = (String) ValidatorUtil.getFieldValue(value, passwordVerificationFieldName);
		if (isNullOrEmpty(passwordVerification)) {
			ValidatorUtil.addValidationError(passwordVerificationFieldName, context);
			passwordWordFieldsAreValid = false;
		}

		return passwordWordFieldsAreValid;
	}

	private boolean isNullOrEmpty(String field) {
		return field == null || field.trim().isEmpty();
	}
}