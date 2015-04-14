package gr.abiss.calipso.jpasearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to enforce a criterion based on the current session userDetails, i.e. the loggedin in user
 * @author manos
 *
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserDetailsCriterion {

}
