package gr.abiss.calipso.tiers.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.CascadeType;

import gr.abiss.calipso.model.Role;

/**
 * Used to enforce a criterion based on the current session userDetails, 
 * i.e. the loggedin in user. Enforced at controller level. For enforcing 
 * at service/specification level see CurrentPrincipalField.
 * 
 * @see gr.abiss.calipso.tiers.annotation.CurrentPrincipal
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentPrincipalField {

    /**
     * the field to use as a criterion for the current principal
     */
    String value() default "createdBy";
    
    /**
     * (Optional) The names of roles to exclude from the 
     * current principal criterion
     *
     * <p> By default no roles are excluded.
     */
    String[] ignoreforRoles() default {};

}
