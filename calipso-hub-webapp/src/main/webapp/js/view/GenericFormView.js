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
		initialize: function(options){
			console.log("GenericFormView#initialize, model: "+this.model.getTypeName());
			Marionette.ItemView.prototype.initialize.apply(this, arguments);

			// set schema action/key
			if(this.options.formSchemaKey){
				this.formSchemaKey = options.formSchemaKey;
			}
			else if(this.model){
				this.formSchemaKey = this.model.isNew() ? "create" : "view";
			}
			else{
				this.formSchemaKey = "search";
			}
			
			// ensure we can use some sort of unique form id
			if(!this.model.get("id")){
				this.model.set("id", this.formSchemaKey);
			}

			// grab a handle for the search results collection if any
			if(this.model.wrappedCollection){
				this.searchResultsCollection = this.model.wrappedCollection;
			}
			else if(this.options.searchResultsCollection){
				this.searchResultsCollection = options.searchResultsCollection;
			}

			this.formTemplate = this.options.formTemplate? this.options.formTemplate : BackboneForm.template;

			console.log("GenericFormView.initialize, this.formSchemaKey: " + this.formSchemaKey + 
					",  searchResultsCollection: " + 
					" of type " + this.model.getTypeName());
			
			// console.log("GenericFormView#onShow, formSchemaKey:
			// "+formSchemaKey+", model:
			// "+this.model.constructor.name+this.model.constructor);
			
	  },
		formSchemaKey: "view",
		events : {
			"click .submit" : "commit"
		},
		commit : function(){
			// runs schema and model validation
			var errors = this.form.commit({ validate: true });
			var _this = this;
			// persist entity?
			if(this.formSchemaKey == "create" || this.formSchemaKey == "update"){
				// persist changes
				this.model.save();
			}
			else if(this.formSchemaKey == "search"){
				this.searchResultsCollection.bind('refresh', function(){alert("refreshed")});
				this.searchResultsCollection.fetch({
					reset : true, 
					data: this.form.toJson(),
					success: function(){
						//console.log("GenericFormView#commit search, success");

						_this.trigger('search:retreivedResults', _this.searchResultsCollection);
						
					},

					// Generic error, show an alert.
					error: function(model, response){
						alert("Failed retreiving search results");
					}

				})
			}
			// search entities?
		},
		onShow : function() {
			var _self = this;
			// get appropriate schema
//			console.log("GenericFormView.onShow, this.formSchemaKey: "+this.formSchemaKey);
			
			var selector = '#generic-form-' + this.model.get("id");
			var schemaForAction = this.model.getFormSchema(this.formSchemaKey);
			
			console.log("GenericFormView#onShow, selector: " + selector + 
					 ", formSchemaKey: " + this.formSchemaKey + 
					 ", model id: " + this.model.get("id") + 
					 ", schema: " + schemaForAction);
			
			// render form
			var JsonableForm = Backbone.Form.extend({
				toJson: function(){
					return _.reduce(this.$el.serializeArray(), function (hash, pair) {
						if(pair.value){
							hash[pair.name] = pair.value;
						}
						return hash;
					}, {});
				},
			});
			
			//console.log("GenericformView#onShow: " + schemaForAction.toSource());
			this.form = new JsonableForm({
				model : _self.model,
				schema : schemaForAction,
				template : _self.formTemplate
			}).render();
			$(selector).append(this.form.el);
//			$(selector + ' textarea[data-provide="markdown"]').each(function() {
//				var $this = $(this);
//
//				if ($this.data('markdown')) {
//					$this.data('markdown').showEditor()
//				} else {
//					$this.markdown($this.data())
//				}
//
//			});
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
		getTypeName: function(){return "GenericFormView"}
	});
	return GenericFormView;
});
