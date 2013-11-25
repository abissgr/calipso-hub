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
public class PasswordsNotEqualValidator implements ConstraintValidator<PasswordsNotEqual, Object> {

	private String passwordFieldName;

	private String passwordVerificationFieldName;

	@Override
	public void initialize(PasswordsNotEqual constraintAnnotation) {
		this.passwordFieldName = constraintAnnotation.passwordFieldName();
		this.passwordVerificationFieldName = constraintAnnotation.passwordVerificationFieldName();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		try {
			String password = (String) ValidatorUtil.getFieldValue(value, passwordFieldName);
			String passwordVerification = (String) ValidatorUtil.getFieldValue(value, passwordVerificationFieldName);

			if (passwordsAreNotEqual(password, passwordVerification)) {
				ValidatorUtil.addValidationError(passwordFieldName, context);
				ValidatorUtil.addValidationError(passwordVerificationFieldName, context);

				return false;
			}
		} catch (Exception ex) {
			throw new RuntimeException("Exception occurred during validation", ex);
		}

		return true;
	}

	private boolean passwordsAreNotEqual(String password, String passwordVerification) {
		return !(password == null ? passwordVerification == null : password.equals(passwordVerification));
	}
}
