package gr.abiss.calipso.model.entities;

import gr.abiss.calipso.uischema.model.FormSchema;

/**
 * An interface to mark form-schema-aware entities. Form schemas are used to drive 
 * model-driven UI views for e.g. CRUD use cases.
 */
public interface FormSchemaAware {

	public FormSchema getFormSchema();

	public void setFormSchema(FormSchema formSchema);
}
