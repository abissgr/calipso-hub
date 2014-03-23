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
	var Backbone = require('backbone'), 
		Marionette = require('marionette'), 
		BackboneForm = require('backbone-forms'), 
		tmpl = require('hbs!template/GenericFormView');

	var GenericFormView = Marionette.ItemView.extend({
		// Define view template
		template : tmpl,
		onShow : function() {
			// get appropriate schema
			var schemaAction = Marionette.getOption(this, "schemaAction");
			if(!schemaAction){
				schemaAction = this.model.isNew() ? "create" : "update";
			}
			//console.log("GenericFormView#onShow, schemaAction: "+schemaAction+", model: "+this.model.constructor.name+this.model.constructor);
			var selector = '#generic-form-'+this.model.get("id");
			console.log("GenericFormView#onShow, selector: "+selector);
			// render form
			var form = new Backbone.Form({
				model :  this.model,
				schema : this.model.schemaForAction(schemaAction)
			}).render();
			$(selector).append(form.el);
			$(selector+' textarea[data-provide="markdown"]').each(function(){
		        var $this = $(this);

		        if ($this.data('markdown')) {
		        	$this.data('markdown').showEditor()
				}
		        else{
		        	$this.markdown($this.data()) 
		        }
	        
		    });
		}
	});
	return GenericFormView;
});
