/*
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
define(function(require) {
	var Backbone = require('backbone'), Marionette = require('marionette'), BackboneForm = require('backbone-forms'), tmpl = require('hbs!template/GenericFormView');

	var GenericFormView = Marionette.ItemView.extend({
		// Define view template
		tagName : 'div',
		template : tmpl,
		events : {
			"click .submit" : "save"
		},
		save : function(){
			console.log("GenericFormView#save");
			// runs schema and model validation
			if(this.form && !this.form.commit({ validate: true })){
				// persist changes
				this.model.save();
			}
		},
		onShow : function() {
			// get appropriate schema
			if (!this.formSchemaKey) {
				this.formSchemaKey = this.model.isNew() ? "create" : "view";
			}
			// console.log("GenericFormView#onShow, formSchemaKey:
			// "+formSchemaKey+", model:
			// "+this.model.constructor.name+this.model.constructor);
			var selector = '#generic-form-' + this.model.get("id");
			// console.log("GenericFormView#onShow, selector: "+selector+",
			// formSchemaKey: "+formSchemaKey+", schema:
			// "+this.model.schemaForAction(formSchemaKey));
			// render form
			this.form = new Backbone.Form({
				model : this.model,
				schema : this.model.schemaForAction(this.formSchemaKey)
			}).render();
			$(selector).append(this.form.el);
			$(selector + ' textarea[data-provide="markdown"]').each(function() {
				var $this = $(this);

				if ($this.data('markdown')) {
					$this.data('markdown').showEditor()
				} else {
					$this.markdown($this.data())
				}

			});
		}
	},
	// static members
	{
		className : "GenericFormView",
	});
	return GenericFormView;
});
