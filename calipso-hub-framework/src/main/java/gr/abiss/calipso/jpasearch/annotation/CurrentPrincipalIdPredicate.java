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
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentPrincipalIdPredicate {

    /**
     * (Mantatory) The names of roles to exclude from the 
     * current principal criterion
     *
     * <p> By default no roles are excluded.
     */
    String path() default "";
    
    /**
     * (Optional) The names of roles to exclude from the 
     * current principal criterion
     *
     * <p> By default no roles are excluded.
     */
    String[] ignoreforRoles() default {};

}
