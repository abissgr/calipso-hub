package gr.abiss.calipso.jpasearch.annotation;

public @interface FieldFormFieldAsJasonConfig {
	String create() default "'Text'";

	String update() default "'Text'";

	String search() default "'Text'";
}
