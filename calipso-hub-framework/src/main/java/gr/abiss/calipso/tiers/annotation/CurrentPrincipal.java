package gr.abiss.calipso.tiers.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.CascadeType;

import gr.abiss.calipso.model.Role;

/**
 * Used to enforce a criterion based on the current session userDetails, 
 * i.e. the loggedin in user. Enforced at service/specification level. For enforcing 
 * at controller level see CurrentPrincipalField.
 * 
 * @see gr.abiss.calipso.tiers.annotation.CurrentPrincipalField
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentPrincipal {

    /**
     * (Optional) The names of roles to exclude from the 
     * current principal criterion
     *
     * <p> By default no roles are excluded.
     */
    String[] ignoreforRoles() default {};

}
