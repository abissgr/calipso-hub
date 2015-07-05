package gr.abiss.calipso.jpasearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.CascadeType;

import gr.abiss.calipso.model.Role;

/**
 * Used to enforce a criterion based on the current session userDetails, i.e. the loggedin in user
 * @author manos
 *
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserDetailsCriterion {
	
    /**
     * (Optional) The names of roles to exclude from the 
     * current principal criterion
     *
     * <p> By default no roles are excluded.
     */
    String[] excludeRoles() default {};

}
