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
	var Backbone = require('backbone'), Marionette = require('marionette'), 
	BackboneForm = require('backbone-forms'), 
	tmpl = require('hbs!template/GenericFormView');
	var GenericView = Marionette.ItemView.extend({
		// Define view template
		tagName : 'div',
		template : tmpl,className : "tab-pane active fade in",
		// dynamically set the id
		initialize: function(options){

			Marionette.ItemView.prototype.initialize.apply(this, arguments);
			this.$el.prop("id", "tab-"+this.model.get("id"));
			
			this.formTemplate = this.options.formTemplate? this.options.formTemplate : BackboneForm.template;
	  },
		formSchemaKey: "view",
	},
	// static members
	{
		className : "GenericView",
	});
	return GenericView;
});
