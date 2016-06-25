package gr.abiss.calipso.uischema.annotation;

public @interface FormSchemaEntry {
	public static final String TYPE_STRING = " { \"type\": \"Text\" }";
	public static final String TYPE_DATE = " { \"type\": \"Date\" }";
	public static final String STATE_DEFAULT = "default";
	String state() default FormSchemaEntry.STATE_DEFAULT;
	String json() default FormSchemaEntry.TYPE_STRING;

}
