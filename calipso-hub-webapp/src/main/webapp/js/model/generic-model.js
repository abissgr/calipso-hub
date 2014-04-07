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

define([ 'backbone', 'supermodel', 'backgrid', 'view/GenericFormTabContentView' ], 
/**
 * A module to represent an abstract model, meaning one to inherit from to
 * create your own models. Subclasses of this model should follow the model driven
 * design conventions used in the Calipso backbone stack, note the " REQUIRED" parts 
 * of the example for details.
 * 
 * @exports models/generic-model
 * @example 
 * // Load module
 * require(['models/generic-model'], function(GenericModel) {
 * 	// define our person model subclass
 * 	var PersonModel = GenericModel.extend({
 * 		// add stuff here
 * 	},
 * 	// static members
 * 	{
 * 		// REQUIRED: the model superclass
 * 		parent: GenericModelrties and functions here
 * 	});
 * 
 * 	// REQUIRED: our subclass name
 * 	PersonModel.prototype.getTypeName = function() {
 * 		return "PersonModel";
 * 	}
 * 	// REQUIRED: our subclass URL path fragment, 
 * 	// e.g. "users" for UserModel
 * 	PersonModel.prototype.getPathFragment = function() {
 * 		//...
 * 	}
 * 
 * 	// REQUIRED: our subclass grid schema
 * 	PersonModel.prototype.getGridSchema = function() {
 * 		//...
 * 	}
 * 	// REQUIRED: our subclass form schema
 * 	PersonModel.prototype.getFormSchemas = function() {
 * 		//...
 * 	}
 * 
 * 	// OPTIONAL: our subclass layout view, 
 * 	// defaults to ModelDrivenBrowseLayout 
 * 	PersonModel.prototype.getLayoutViewType = function() {
 * 		//...
 * 	}
 * 	// OPTIONAL: our subclass collection view, 
 * 	// defaults to ModelDrivenCollectionGridView 
 * 	PersonModel.prototype.getCollectionViewType = function() {
 * 		//...
 * 	}
 * 	// OPTIONAL: our subclass item view, 
 * 	// defaults to ModelDrivenFormView
 * 	PersonModel.prototype.getItemViewType = function() {
 * 		//...
 * 	}
 * 	// OPTIONAL: our subclass business key, 
 * 	// used to check if the model has been loaded from the server. 
 * 	// defaults to "name" 
 * 	PersonModel.prototype.GenericModel.prototype.getBusinessKey = function() {
 * 		//...
 * 	}
 * 
 * });         
 */
function(Backbone, Supermodel, Backgrid) {
	/**
	 * @constructor
	 * @requires Backbone
	 * @requires Supermodel
	 * @requires Backgrid
	 * @augments module:Supermodel.Model
	 */
	var GenericModel = Supermodel.Model.extend(
	/**
	 * @lends module:models/generic-model~GenericModel.prototype
	 */
	{
		/**
		 * Prefer the collection URL if any for more specific CRUD, fallback to
		 * own otherwise
		 */
		url : function() {
			var sUrl = this.collection ? _.result(this.collection, 'url') : _.result(this, 'urlRoot') || urlError();
			if (!this.isNew()) {
				sUrl = sUrl + (sUrl.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.get("id"));
			}
			console.log("GenericModel#url: " + sUrl + ", is new: " + this.isNew() + ", id: " + this.get("id"));
			return sUrl;
		},
		
		/**
		 * Get the URL path fragment for this model
		 * @returns the URL path fragment as a string
		 */
		getPathFragment : function() {
			return this.prototype.getPathFragment();
		},
		/**
		 * Get the name of this class
		 * @returns the class name as a string
		 */
		getTypeName : function() {
			return this.prototype.getTypeName();
		},
		/**
		 * Get the layout view for this model. To specify a layout for your model under a static 
		 * or instance context, override {@link GenericModel.prototype.getLayoutViewType} 
		 * or {@link getLayoutViewType} respectively in your subclass. 
		 * 
		 * Layout views defined this way are picked up by controllers.
		 * 
		 * @see {@link GenericModel.prototype.getLayoutViewType}
		 */
		getLayoutViewType : function() {
			return this.prototype.getLayoutViewType();
		},
		/**
		 * Get the collection view type for collections of this model. To specify a collection 
		 * view for your model under a static or instance context, override 
		 * {@link GenericModel.prototype.getCollectionViewType} or 
		 * {@link getCollectionViewType} respectively in your subclass.
		 * 
		 * Collection views defined this way are picked up by layout views..
		 * 
		 * @see {@link GenericModel.prototype.getCollectionViewType}
		 */
		getCollectionViewType : function() {
			return this.prototype.getCollectionViewType();
		},
		/**
		 * Get the item view type for this model. To specify an item view for your model under a static 
		 * or instance context, override {@link GenericModel.prototype.getItemViewType} 
		 * or {@link getItemViewType} respectively in your subclass.
		 * 
		 * Layout views defined this way are picked up by controllers.
		 * 
		 * @see {@link GenericModel.prototype.getItemViewType}
		 */
		getItemViewType : function() {
			return this.prototype.getItemViewType();
		},
		/**
		 * Get the complete set of form schemas. You can also obtain the form schema for 
		 * a specific action like "create", "update" or "search" using 
		 * {@linkcode getFormSchema} instead. 
		 * 
		 * To define form schemas for your subclass under a static or instance context, override 
		 * {@link GenericModel.prototype.getFormSchemas} or {@link getFormSchemas} respectively.
		 * 
		 * Form schemas are picked up by form views like {@linkcode GenericFormView} or layout views
		 * that use such form views in their regions.
		 * 
		 * @see {@link GenericModel.prototype.getFormSchema}
		 */
		getFormSchemas : function() {
			return this.prototype.getFormSchemas();
		},
		/**
		 * Get the form schema for a specific action like "create", "update" or "search". 
		 * 
		 * To define form schemas for your subclass under a static or instance context, override 
		 * {@link GenericModel.prototype.getFormSchemas} or {@link getFormSchemas} respectively.
		 * 
		 * Form schemas are used by form views like {@linkcode GenericFormView} or layout views
		 * that use such form views in their regions.
		 * 
		 * @param {string} actionName for example "create", "update" or "search"
		 * @see {@link getFormSchemas}
		 * @todo implement optional merging of superclass schemas by using the supermodel.parent property
		 */
		getFormSchema : function(actionName) {
			// decide based on model persistence state if no action was given
			console.log("GenericModel.prototype.schema, actionName: " + actionName);
			if (!actionName) {
				console.log("GenericModel.prototype.schema, this: " + this);
				console.log("GenericModel.prototype.schema, caller is " + arguments.callee.caller.toString());
				actionName = this.isNew() ? "create" : "update";
			}
			// the schema to build for the selected action
			var formSchema = {};
			// get the complete schema to filter out from
			var formSchemas = this.formSchemas();
			// console.log("GenericModel#schema actionName: "+actionName+",
			// formSchemas: "+formSchemas);

			// for each property, select the appropriate schema entry for the given
			// action
			var propertySchema;
			var propertySchemaForAction;
			for ( var propertyName in formSchemas) {
				if (formSchemas.hasOwnProperty(propertyName)) {
					propertySchema = formSchemas[propertyName];

					// if a schema exists for the property
					if (propertySchema) {
						// try obtaining a schema for the specific action
						propertySchemaForAction = propertySchema[actionName];
						// support wild card entries
						if (!propertySchemaForAction) {
							propertySchemaForAction = propertySchema["default"];
						}
						if (propertySchemaForAction) {
							formSchema[propertyName] = propertySchemaForAction;
						}
					}
				}

				// reset
				propertySchema = false;
				propertySchemaForAction = false;
			}
			// console.log("GenericModel#schema formSchema:
			// "+formSchema);
			return formSchema;
		},
		initialize : function() {
			Supermodel.Model.prototype.initialize.apply(this, arguments);
			var thisModel = this;
			this.on("change", function(model, options) {
				if (options && options.save === false) {
					return;
				}
			});
		},
	},
	// static members
	{
		typeName : "GenericModel"
	});

	/**
	 * Get the model class URL fragment corresponding to your server 
	 * side controller, e.g. "users" for UserModel. Model subclasses 
	 * are required to implement this method. 
	 * @returns the URL path fragment as a string
	 */
	GenericModel.prototype.getPathFragment = function() {
		throw "Model subclasses must implement GenericModel.prototype.getPathFragment";
	}
	/**
	 * Get the name of this class
	 * @returns the class name as a string
	 */
	GenericModel.prototype.getTypeName = function() {
		return "GenericModel";
	}
	
	/**
	 * Override this to declaratively define 
	 * grid views for your subclass
	 */
	GenericModel.prototype.getGridSchema = function() {
		console.log("GenericModel.prototype.getSchemaForGrid() called, will return undefined");
		return undefined;
	}
	
	/**
	 * Override this in your subclass to declaratively define 
	 * form views for the default or custom actions
	 */
	GenericModel.prototype.getFormSchema = function() {
		console.log("GenericModel.prototype.getFormSchemad() called, will return undefined");
		return undefined;
	}

	/**
	 * Override this to define a default layout view at a static context for your subclass, 
	 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenBrowseLayout}
	 */
	GenericModel.prototype.getLayoutViewType = function() {
		console.log("GenericModel.prototype.getLayoutViewType() called, will return ModelDrivenBrowseLayout");
		return require('view/md-browse-layout');
	}
	
	/**
	 * Override this to define a default collection view like the 
	 * default {@link ModelDrivenCollectionGridView} 
	 * at a static context for your subclass, 
	 *@returns {@link ModelDrivenCollectionGridView}
	 */
	GenericModel.prototype.getCollectionViewType = function() {
		console.log("GenericModel.prototype.getLayoutViewType() called, will return ModelDrivenCollectionGridView");
		return require('view/md-collection-grid-view');
	}

	/**
	 * Override this to define a default layout view at a static context for your subclass, 
	 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenSearchLayout}
	 */
	GenericModel.prototype.getItemViewType = function() {
		console.log("GenericModel.prototype.getItemViewType() called, will return GenericFormViewt");
		return require('view/GenericFormViewt');
	}
	
	/**
	 * Get the name of the model's business key property. The property name is used to 
	 * check whether a model instance has been loaded from the server. The default is "name".
	 * 
	 * @returns the business key if one is defined by the model class, "name" otherwise
	 */
	GenericModel.prototype.getBusinessKey = function() {
		return "name";
	}
	
	return GenericModel;
});