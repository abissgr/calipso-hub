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
	tmpl = require('hbs!template/GenericFormView'),
	form2js = require('form2js');
	var GenericFormView = Marionette.ItemView.extend({
		// Define view template
		tagName : 'div',
		template : tmpl,
		initialize: function(options){
			if(this.options.formSchemaKey){
				this.formSchemaKey = options.formSchemaKey;
			}

			if(this.options.searchResultsCollection){
				this.searchResultsCollection = options.searchResultsCollection;
			}
	  },
		formSchemaKey: "view",
		events : {
			"click .submit" : "commit"
		},
		commit : function(){
			console.log("GenericFormView#commit, formSchemaKey: "+this.formSchemaKey+", searchResultsCollection: "+this.searchResultsCollection.length);
			// runs schema and model validation
			var errors = this.form.commit({ validate: true });
			
			// persist entity?
			if(this.formSchemaKey == "create" || this.formSchemaKey == "update"){
				// persist changes
				this.model.save();
			}
			else if(this.formSchemaKey == "search"){
				this.searchResultsCollection.bind('refresh', function(){alert("refreshed")});
				this.searchResultsCollection.fetch({data: this.form.toJson()})
			}
			// search entities?
		},
		onShow : function() {
			// get appropriate schema
			console.log("GenericFormView.onShow, this.formSchemaKey: "+this.formSchemaKey);
			if (!this.formSchemaKey) {
				this.formSchemaKey = this.model.isNew() ? "create" : "view";
			}
			console.log("GenericFormView.onShow, this.formSchemaKey: "+this.formSchemaKey);
			// console.log("GenericFormView#onShow, formSchemaKey:
			// "+formSchemaKey+", model:
			// "+this.model.constructor.name+this.model.constructor);
			var selector = '#generic-form-' + this.model.get("id");
			// console.log("GenericFormView#onShow, selector: "+selector+",
			// formSchemaKey: "+formSchemaKey+", schema:
			// "+this.model.schemaForAction(formSchemaKey));
			// render form
			var JsonableForm = Backbone.Form.extend({

				toJson: function(){
					return _.reduce(this.$el.serializeArray(), function (hash, pair) {
						hash[pair.name] = pair.value;
						return hash;
						}, {});
				}
			});
			this.form = new JsonableForm({
				model : this.model,
				schema : this.model.schemaForAction(this.formSchemaKey),
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
		},
		getFormData: function getFormData($form){
		    var unindexed_array = $form.serializeArray();
		    var indexed_array = {};

		    $.map(unindexed_array, function(n, i){
		        indexed_array[n['name']] = n['value'];
		    });

		    return indexed_array;
		}
	},
	// static members
	{
		className : "GenericFormView",
	});
	return GenericFormView;
});
