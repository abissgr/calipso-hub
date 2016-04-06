/*
 * Copyright (c) 2007 - 2014 www.Abiss.gr
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

define(['jquery', 'underscore', "lib/calipsolib/util", "lib/calipsolib/form", "lib/calipsolib/uifield", "lib/calipsolib/backgrid", "lib/calipsolib/view", 'handlebars', 'moment'],
		function($, _, Calipso, CalipsoForm, CalipsoField, CalipsoGrid, CalipsoView, Handlebars, moment) {

	//////////////////////////////////////////
	// Models
	//////////////////////////////////////////
	/**
	 * Abstract model implementation to extend through your own models.
	 * Subclasses of this model should follow the model driven
	 * design conventions used in the Calipso backbone stack, note the " REQUIRED" parts
	 * of the example for details. Properly extending this class allows
	 * "model driven" routes, forms, grids and selection of item/collection/layout views.
	 *
	 * @example
	 * // Load module
	 * require(['models/generic-model'], function(GenericModel) {
	 * 	// define our person model subclass
	 * 	var PersonModel = GenericModel.extend({
	 * 		// add stuff here
	 * 	},
	 * 	// static members
	 * 	{	// OPTIONAL: set the form schema cache behavior
	 * 		formSchemaCache : this.FORM_SCHEMA_CACHE_CLIENT
	 * 	});
	 *
	 * 	// REQUIRED: our subclass name
	 * 	PersonModel.getTypeName = function() {
	 * 		return "PersonModel";
	 * 	}
	 * 	// REQUIRED: our subclass URL path fragment,
	 * 	// e.g. "persons" for PersonModel. Used for dynamic MArionette router routes.
	 * 	PersonModel.getPathFragment = function() {
	 * 		return "persons";
	 * 	}
	 *
	 * 	// REQUIRED: our subclass grid schema
	 * 	PersonModel.getGridSchema = function() {
	 * 		//...
	 * 	}
	 * 	// REQUIRED: our subclass form schema
	 * 	PersonModel.getFormSchemas = function() {
	 * 		//...
	 * 	}
	 *
	 * 	// OPTIONAL: our subclass layout view,
	 * 	// defaults to ModelDrivenBrowseLayout
	 * 	PersonModel.getLayoutViewType = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass collection view,
	 * 	// defaults to ModelDrivenCollectionGridView
	 * 	PersonModel.getCollectionViewType = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass item view,
	 * 	// defaults to ModelDrivenFormView
	 * 	PersonModel.getItemViewType = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass item view template,
	 * 	PersonModel.getItemViewTemplate = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass business key,
	 * 	// used to check if the model has been loaded from the server.
	 * 	// defaults to "name"
	 * 	PersonModel.getBusinessKey = function() {
	 * 		//...
	 * 	}
	 *
	 * });
	 * @constructor
	 * @requires Backbone
	 * @requires Backgrid
	 * @augments module:Backbone.Model
	 */
	Calipso.model.GenericModel = Backbone.Model.extend(
	/** @lends Calipso.model.GenericModel.prototype */
	{
		getFormSubmitButton : function() {
			return null;
		},
		getViewTitle : function() {
			var schemaKey = this.getFormSchemaKey();
			var title = "";
			if (schemaKey.indexOf("create") == 0) {
				title += "New ";
			}
			if (schemaKey.indexOf("update") == 0) {
				title += "Edit ";
			}

			if (this.get("name")) {
				title += this.get("name");
			} else if (this.constructor.label) {
				title += this.constructor.label;
			}

			return title;
		},
		skipDefaultSearch : false,
		/**
		 * Returns the URL for this model, giving precedence  to the collection URL if the model belongs to one,
		 * or a URL based on the model path fragment otherwise.
		 */
		url : function() {
			var sUrl = this.collection && _.result(this.collection, 'url') ? _.result(this.collection, 'url') : Calipso.getBaseUrl() + this.getBaseFragment() + this.getPathFragment()/*_.result(this, 'urlRoot')*/|| urlError();
			//console.log("GenericModel#url, sUrl: " + sUrl);
			if (!this.isNew()) {
				sUrl = sUrl + (sUrl.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.get("id"));
			}
			// console.log("GenericModel#url: " + sUrl + ", is new: " + this.isNew() + ", id: " + this.get("id"));
			return sUrl;
		},
		/**
		 * Retusn true if the model is just a search collection wrapper, false otherwise
		 */
		isSearchModel : function() {
			return this.wrappedCollection ? true : false;
		},
		getBaseFragment : function() {
			return this.constructor.getBaseFragment(this);
		},
		getGridSchema : function() {
			return this.constructor.getGridSchema(this);
		},
		/*
		 * Will return <code>search</code> if the model is a search model,
		 * <code>create</code> if the model is new ans not a search model,
		 * <code>update</code> otherwise. The method is used to choose an appropriate
		 * form schema during form generation, see GenericFormView
		 */
		getFormSchemaKey : function() {
			var formSchemaKey = this.get("formSchemaKey");
			if (!formSchemaKey) {
				if (this.isSearchModel()) {
					formSchemaKey = "search";
				} else {
					formSchemaKey = this.isNew() ? "create" : "update";
				}
			}
			return formSchemaKey;
		},
		getFormTemplateKey : function() {
			var schemaKey = this.getFormSchemaKey();
			var formTemplateKey = "vertical";
			if (schemaKey.indexOf("report") == 0) {
				formTemplateKey = "nav";
			}
			return formTemplateKey;
		},
		/**
		 * Get the URL path fragment for this model. Calls the prototype method with the same name.
		 * @returns the URL path fragment as a string
		 */
		getPathFragment : function() {
			return this.constructor.getPathFragment();
		},
		/**
		 * Get the name of this class. Calls the prototype method with the same name.
		 * TODO: switch to named constructors
		 * @returns the class name as a string
		 */
		getTypeName : function() {
			return this.constructor.getTypeName();
		},
		/**
		 *  Check if the model wants search result collections of it's type to be cached.
		 *  Calls the prototype method with the same name.
		 */
		isCollectionCacheable : function() {
			return this.constructor.isCollectionCacheable && this.constructor.isCollectionCacheable();
		},
		/**
		 * Get the layout view for this model. To specify a layout for your model under a static
		 * or instance context, override {@link Calipso.model.GenericModel.getLayoutViewType}
		 * or {@link getLayoutViewType} respectively in your subclass.
		 *
		 * Layout views defined this way are picked up by controllers.
		 *
		 * @see {@link Calipso.model.GenericModel.getLayoutViewType}
		 */
		getLayoutViewType : function() {
			return this.constructor.getLayoutViewType(this);
		},
		getLayoutOptions : function() {
			return this.constructor.getLayoutOptions(this);
		},
		/**
		 * Get the collection view type for collections of this model. To specify a collection
		 * view for your model under a static or instance context, override
		 * {@link Calipso.model.GenericModel.getCollectionViewType} or
		 * {@link getCollectionViewType} respectively in your subclass.
		 *
		 * Collection views defined this way are picked up by layout views..
		 *
		 * @see {@link Calipso.model.GenericModel.getCollectionViewType}
		 */
		getCollectionViewType : function() {
			return this.constructor.getCollectionViewType(this);
		},
		/**
		 * Get the collection view type for collections of this model. To specify a collection
		 * view for your model under a static or instance context, override
		 * {@link Calipso.model.GenericModel.getCollectionViewType} or
		 * {@link getCollectionViewType} respectively in your subclass.
		 *
		 * Collection views defined this way are picked up by layout views..
		 *
		 * @see {@link Calipso.model.GenericModel.getCollectionViewType}
		 */
		getReportCollectionViewType : function() {
			return this.constructor.getReportCollectionViewType(this);
		},
		/**
		 * Get the item view type for this model. To specify an item view for your model under a static
		 * or instance context, override {@link Calipso.model.GenericModel.getItemViewType}
		 * or {@link getItemViewType} respectively in your subclass.
		 *
		 * Layout views defined this way are picked up by controllers.
		 *
		 * @see {@link Calipso.model.GenericModel.getItemViewType}
		 */
		getItemViewType : function() {
			// console.log("GenericModel.getItemViewType() called, will return GenericFormView");
			return this.constructor.getItemViewType(this);
		},
		/**
		 * Get the item view template for this model. the template is picked up and
		 * used by item views like GenericView.  To specify an item view template for
		 * your model under a static or instance context,
		 * override {@link Calipso.model.GenericModel.getItemViewTemplate}
		 * or {@link getItemViewTemplate} respectively in your subclass.
		 *
		 * Layout views defined this way are picked up by controllers.
		 *
		 * @see {@link Calipso.model.GenericModel.getItemViewType}
		 */
		getItemViewTemplate : function() {
			// console.log("GenericModel.getItemViewTemplate() called");
			return this.constructor.getItemViewTemplate(this);
		},
		/**
		 * Get the complete set of form schemas. You can also obtain the form schema for
		 * a specific action like "create", "update" or "search" using
		 * {@linkcode getFormSchema} instead.
		 *
		 * To define form schemas for your subclass under a static or instance context on the client-side, override
		 * {@link Calipso.model.GenericMogetFormSchemasgetFormSchemas} or {@link getFormSchemas} respectively.
		 *
		 * This method will attempt to retrieve the model schema in the following order:
		 * 	<ul><li>Schema set to the model by the server</li>
		 * 	<li>schemas defined by the model's prototype object</li>
		 * 	</ul>
		 *
		 * Form schemas are picked up by form views like {@linkcode GenericFormView} or layout views
		 * that use such form views in their regions.
		 *
		 * @see {@link Calipso.model.GenericModel.getFormSchemas}
		 */
		getFormSchemas : function() {
			return this.constructor.getFormSchemas(this);
		},
		getFormActions : function() {
			return this.constructor.getFormActions(this);
		},
		isRequired : function(schema) {
			var required = schema.required;
			if (!required && schema.validators) {
				required = $.inArray('required', schema.validators) > -1;
			}
			return required;
		},
		getFinalSchema : function(fieldName, fieldSchema, actionName, dontHintRequired) {
			// get i18n labels configuration as defaults,
			// then overwrite those using local settings

			var labelsConfig = Calipso.util.getLabels("models." + this.getPathFragment() + '.' + fieldName + '.' + actionName);
			var labelsDefaultConfig = Calipso.util.getLabels("models." + this.getPathFragment() + '.' + fieldName + '.default');

			var schema = $.extend({}, labelsConfig, fieldSchema);
			//
			// final title
			//
			var title = fieldSchema.titleHTML || fieldSchema.title;
			if (_.isUndefined(title)) {
				// build title from field name
				title = labelsConfig.title || labelsDefaultConfig.title || fieldName.replace(/([A-Z])/g, ' $1').replace(/^./, function(str) {
					return str.toUpperCase();
				});
			}
			if (title) {
				// hint required?
				var hint = "";
				if(!dontHintRequired && this.isRequired(fieldSchema)){
					hint = '<sup class="text-danger"><i class="fa fa-asterisk"></i></sup>';
					title.trim();
					if(title.lastIndexOf(":") == title.length - 1){
						title = title.substring(0, title.length - 1);
						hint += ":";
					}
				}
				fieldSchema.titleHTML = title + hint;
				fieldSchema.title = undefined;
			}
			//
			// final options
			//
			if (labelsConfig.options) {
				var optionListLabels = labelsConfig.options;
				var newOptions = [];

				// listgroup format
				if (optionListLabels["0"] && (optionListLabels["0"].heading || optionListLabels["0"].text)) {
					var i = 0;
					while (optionListLabels[i + ""]) {
						var optionLabels = optionListLabels[i + ""];
						newOptions.push({
							heading : optionLabels.heading,
							text : optionLabels.text
						});
						i++;
					}
				}
				// normal var/label options
				else {
					_.each(optionListLabels, function(value, key, obj) {
						newOptions.push({
							val : key,
							label : value
						});
					});
				}
				fieldSchema.options = newOptions;
			}
			var schema = $.extend({}, labelsConfig, fieldSchema);
			schema.fieldName = fieldName;
			schema.actionName = actionName;
			return schema;
		},
		/**
		 * Get the form schema for a specific action like "create", "update", "search" or "report".
		 *
		 * To define form schemas for your subclass under a static or instance context, override
		 * {@link Calipso.model.GenericModel.getFormSchemas} or {@link getFormSchemas} respectively.
		 *
		 * Form schemas are used by form views like {@linkcode GenericFormView} or layout views
		 * that use such form views in their regions.
		 *
		 * @param {string} actionName for example "create", "update" or "search"
		 * @see {@link getFormSchemas}
		 * @todo implement optional merging of superclass schemas by using the supermodel.parent property
		 */
		getFormSchema : function(actionName) {
			// TODO:only experimenting on usecase-drive for UserModel
			var formSchema = this.getTypeName() == "Calipso.model.UserModel"
				?  this.constructor.getSchema("form")
				: null;
			if(!formSchema){
				formSchema = {};
				// decide based on model persistence state if no action was given
				if (!actionName) {
					actionName = this.getFormSchemaKey();
					//console.log("GenericModel#getFormSchema actionName: "+ actionName);
				}
				// get the complete schema to filter out from
				// console.log("GenericModel#getFormSchema calling : this.getFormSchemas()");
				var formSchemas = this.getFormSchemas();

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
							var partialSchema = propertySchema[actionName];
							// support default fallback
							if (!partialSchema) {
								partialSchema = propertySchema["default"];
							}
							if (partialSchema) {
								propertySchemaForAction = {};
								// extend on top of "extend" if avalable
								if (partialSchema.extend) {
									var extendArr = partialSchema.extend;
									if (!$.isArray(extendArr)) {
										extendArr = [ extendArr ];
									}
									for (var i = 0; i < extendArr.length; i++) {
										var toAdd = extendArr[i];
										// if ref to another action key, resolve it
										if (toAdd instanceof String || typeof toAdd === "string") {
											toAdd = propertySchema[toAdd + ''];
										}
										$.extend(true, propertySchemaForAction, toAdd);
									}
								}
								// add explicit schema for action key
								$.extend(true, propertySchemaForAction, partialSchema);
							}
							// add final schema for field
							if (propertySchemaForAction) {
								formSchema[propertyName] = this.getFinalSchema(propertyName, propertySchemaForAction, actionName);
							}
						} else {
							console.log("WARNING GenericModel#getFormSchema, no " + actionName + "schema found for property: " + propertyName);
						}
					} else {
						console.log("WARNING GenericModel#getFormSchema, no schema found for property: " + actionName);
					}

					// reset
					propertySchema = false;
					propertySchemaForAction = false;
				}
			}
			return formSchema;
		},
		initialize : function() {
			Backbone.Model.prototype.initialize.apply(this, arguments);
			var thisModel = this;
			// make any submit button available to templates
			if (this.getFormSubmitButton()) {
				this.set("calipsoFormSubmitButton", this.getFormSubmitButton());
			}
			this.on("change", function(model, options) {
				if (options && options.save === false) {
					return;
				}
			});
		},
		sync : function() {
			// apply partial update hints
			if (!this.isNew()) {
				var changed = this.changedAttributes();
				//console.log("sync, changed attributes: " + changed);
				if (changed != false) {
					//console.log(_.keys(changed));
					this.set("changedAttributes", _.keys(changed));
				}
			}
			return Backbone.Model.prototype.sync.apply(this, arguments);
		},
	}, {
		// static members
		/** (Default) 0Do not retrieve the form schema from the server */
		FORM_SCHEMA_CACHE_CLIENT : "FORM_SCHEMA_CACHE_CLIENT",
		/** Retrieve the form schema only once for all model instances */
		FORM_SCHEMA_CACHE_STATIC : "FORM_SCHEMA_CACHE_STATIC",
		/** Retrieve the form schema only once per model instance */
		FORM_SCHEMA_CACHE_INSTANCE : "FORM_SCHEMA_CACHE_INSTANCE",
		/** Retrieve the form schema every time it is accessed */
		FORM_SCHEMA_CACHE_NONE : "FORM_SCHEMA_CACHE_NONE",
		formSchemaCacheMode : this.FORM_SCHEMA_CACHE_CLIENT,
		typeName : "Calipso.model.GenericModel",
		label : "GenericModel",
		showInMenu : false,
		public : false,
		businessKey : "name",
		baseFragment : '/api/rest/',
		typeaheadSources : {},
		layoutOptions : null,
		layoutViewType : false,
		collectionViewType : false,
		itemViewType : false,
		reportViewType : false,
		create : function(attrs, options) {
			return new this(attrs, options);
		},
		getLayoutOptions : function() {
			return this.layoutOptions;
		},
		isPublic : function() {
			return this.public;
		},
		isCollectionCacheable : function() {
			return false;
		},
		/**
		 * Get the name of this class
		 * @returns the class name as a string
		 */
		getTypeName : function(instance) {
			return this.typeName;
		},
		getBaseFragment : function() {
			return this.baseFragment;
		},
		/**
		 * Get the path fragment of this class
		 * @returns the class name as a string
		 */
		getPathFragment : function(instance) {
			//console.log("GenericModel.getPathFragment returns: " + this.pathFragment);
			return this.pathFragment;
		},
		// TODO: refactor view to region names to
		// allow multiple views config peer layout
		useCases : {
			create : {
				layout : Calipso.view.ModelDrivenBrowseLayout,
				view : Calipso.view.GenericFormView,
			},
			update : {
				layout : Calipso.view.ModelDrivenBrowseLayout,
				view : Calipso.view.GenericFormView,
			},
			"search" : {
				view : Calipso.view.SearchLayout,
			},

		},
		fieldNames : [],
		getFieldNames : function(){
			var _this = this;
			if(!this.fieldNames){
				_.each(this.fields, function(field, key){
					_this.fieldNames.push(key);
				});
			}
			return this.fieldNames;
		},
		/**
		 * Construct the schema type as an object or, if arrayItemProperty is present, an array
		 */
		getSchema : function(schemaType, arrayItemProperty) {
			var _this = this;
			console.log(_this.getPathFragment() + ".constructor#getSchema, schemaType: " + schemaType + ", arrayItemProperty: " + arrayItemProperty);
			if(this.fields){
				var schema;
				var schemaEntry;
				var baseSchemaEntry;
				var overrideSchemaEntry;

				schema = arrayItemProperty ? [] : {};
				_.each(this.fields, function(field, key){
					console.log("FIELD  "+key);
					console.log(field[schemaType]);
					console.log("DATATYPE: ");
					console.log(Calipso.datatypes[field.datatype][schemaType]);
					baseSchemaEntry = Calipso.datatypes[field.datatype][schemaType];
					overrideSchemaEntry = field[schemaType];
					// if a schema entry exists, add it
					if(baseSchemaEntry || overrideSchemaEntry){
						// merge to new object
						schemaEntry = $.extend({}, baseSchemaEntry, overrideSchemaEntry);

						// if expected schema is of type array, push
						if(arrayItemProperty){
							schemaEntry[arrayItemProperty] = key;
							schema.push(schemaEntry);
						}// if expected schema is of type objet, add
						else{
							schema[key] = schemaEntry;
						}
					}

				});
			}

			console.log(_this.getPathFragment() + ".constructor#getSchema(" + schemaType + "), schema: ");
			console.log(schema);
			return schema;
		},
		/**
		 * Get the default grid schema fro this type.
		 */
		getGridSchema : function(instance) {
			return this.getSchema("backgrid", "name") || this.gridSchema;
		},
		getFormSchemas : function(instance) {
			return this.formSchemas;
		},
		/**
		 * Get the default layout view at a static context for your subclass,
		 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenBrowseLayout}
		 */
		getLayoutViewType : function(instance) {
			var layoutViewType = this.layoutViewType ? this.layoutViewType : Calipso.view.ModelDrivenSearchLayout;
			//console.log("GenericModel.getLayoutViewType, layoutViewType: " + layoutViewType.getTypeName());
			return layoutViewType;
		},
		/**
		 * Get the default collection view like the
		 * default {@link ModelDrivenCollectionGridView}
		 * at a static context for your subclass,
		 *@returns {@link ModelDrivenCollectionGridView}
		 */
		getCollectionViewType : function(instance) {
			return this.collectionViewType ? this.collectionViewType : Calipso.view.ModelDrivenCollectionGridView;
		},
		/**
		 * Override this to define a default report view like the
		 * default {@link ModelDrivenCollectionGridView}
		 * at a static context for your subclass,
		 *@returns {@link Calipso.view.ModelDrivenReportView}
		 */
		getReportCollectionViewType : function(instance) {
			return this.reportViewType ? this.reportViewType : Calipso.view.ModelDrivenReportView;
		},
		/**
		 * Get the default itwem view at a static context for your subclass,
		 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenSearchLayout}
		 */
		getItemViewType : function(instance) {
			var itemViewType = this.itemViewType ? this.itemViewType : Calipso.view.GenericFormView;
			//console.log("GenericModel.getItemViewType, layoutViewType: " + itemViewType.getTypeName());
			return itemViewType;
		},
		getItemViewTemplate : function(instance) {
			var itemViewTemplate = this.itemViewTemplate ? this.itemViewTemplate : Calipso.getTemplate("itemViewTemplate");
			//console.log("GenericModel.getItemViewType, layoutViewType: " + itemViewType.getTypeName());
			return itemViewTemplate;
		},
		/**
		 * Get the name of the model's business key property. The property name is used to
		 * check whether a model instance has been loaded from the server. The default is "name".
		 *
		 * @returns the business key if one is defined by the model class, "name" otherwise
		 */
		getBusinessKey : function(instance) {
			this.businessKey;
		},
		formActions : null,
		getFormActions : function(instance) {
			if(!this.formActions){

					var formSchemas = instance.getFormSchemas();
					var actions = {};
					var actionsArray = [];
					$.each(formSchemas, function(fieldName, fieldSchema) {
						$.each(fieldSchema, function(actionName, actionSchema){
							// add action if missing
							if(_.isUndefined(actions[actionName])){
								actions[actionName] = [];
								actionsArray.push(actions[actionName]);
							}
							actions[actionName].push(instance.getFinalSchema(fieldName, actionSchema, actionName));
						});
					});
					this.formActions = actionsArray;
			}
			return this.formActions;
		},
		getTypeaheadSource : function(options) {
			var _this = this;
			var config = {
				query : "?name=%25wildcard%25",
				wildcard : "wildcard",
				pathFragment : _this.getPathFragment(),
			};
			_.extend(config, options);
			var sourceKey = config.pathFragment + config.wildcard + config.query;
			// if not lready created
			if (!_this.typeaheadSources[sourceKey]) {
				var sourceUrl = Calipso.getBaseUrl() + "/api/rest/" + config.pathFragment + config.query;
				//console.log(_this.getTypeName() + "#getTypeaheadSource creating new source for url " + sourceUrl);
				var bloodhound = new Bloodhound({
					remote : {
						url : sourceUrl,
						wildcard : config.wildcard,
						transform : function(response) {
							//console.log(_this.getTypeName() + ' transform', response.content);
							return response.content;
						}
					},
					identify : function(obj) {
						return obj.id;
					},
					queryTokenizer : Bloodhound.tokenizers.whitespace,
					datumTokenizer : function(d) {
						return Bloodhound.tokenizers.whitespace(d.name);
					},
				});
				bloodhound.initialize();
				_this.typeaheadSources[sourceKey] = bloodhound.ttAdapter();
			}

			return _this.typeaheadSources[sourceKey];
		},
	});

	/**
	 * Encance the extend function to properly merge the
	 * static "fields" and "useCases" hashes
	 */

Calipso.model.GenericModel.extend = function(protoProps, staticProps) {
	var _this = this;
	_.each(["fields", "useCases"], function(mergableStaticProp){

		staticProps[mergableStaticProp] || (staticProps[mergableStaticProp] = {});
		_.each(_.keys(_this[mergableStaticProp]), function(key){
				staticProps[mergableStaticProp][key] || (staticProps[mergableStaticProp][key] = {});
			_.defaults(staticProps[mergableStaticProp][key], _this[mergableStaticProp][key]);
		});
	});
	var ModelType = Backbone.Model.extend.apply(this, arguments);
	console.log("MERGED USECASES " + ModelType.getTypeName());
	console.log(ModelType.useCases);
	return ModelType;
};

		// Role model
		// ---------------------------------------
		Calipso.model.RoleModel = Calipso.model.GenericModel.extend(
		/** @lends Calipso.model.RoleModel.prototype */
		{
			toString : function() {
				return this.get("name");
			}
		//urlRoot : "/api/rest/users"
		}, {
			// static members
			parent : Calipso.model.GenericModel,
			label : "Role",
			pathFragment : "roles",
			typeName : "Calipso.model.RoleModel",
			formSchemaCacheMode : this.FORM_SCHEMA_CACHE_STATIC,
			showInMenu : true,
			formSchemas : {//
				name : {
					"search" : {
						type : 'Text',
					},
					"default" : {
						type : 'Text',
						validators : [ 'required' ]
					}
				},
				description : {
					"search" : {
						type : 'Text',
					},
					"default" : {
						type : 'Text',
						validators : [ 'required' ]
					}
				}
			},
			gridSchema : [ {
				name : "name",
				label : "Name",
				cell : Calipso.components.backgrid.ViewRowCell,
				editable : false
			}, {
				name : "description",
				label : "Description",
				editable : false,
				cell : "string"
			}, {
				name : "edit",
				label : "",
				editable : false,
				cell : Calipso.components.backgrid.EditRowInModalCell,
				headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
			} ],
		});

	Calipso.model.UserModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.UserModel.prototype */
	{
		toString : function() {
			return this.get("username");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		parent : Calipso.model.GenericModel,
		label : "User",
		showInMenu : true,
		pathFragment : "users",
		typeName : "Calipso.model.UserModel",
		getFormSchemasOLD : function(instance) {
			var rolesCollection = new Calipso.collection.AllCollection([], {
				url : function() {
					return Calipso.getBaseUrl() + "/api/rest/" + Calipso.model.RoleModel.getPathFragment();
				},
				model : Calipso.model.RoleModel,
			});
			var text = {
				type : 'Text'
			};
			var textRequired = {
				type : 'Text',
				validators : [ 'required' ]
			};
			return {//
				firstName : {
					"search" : text,
					"default" : textRequired,
				},
				lastName : {
					"search" : text,
					"default" : textRequired,
				},
				username : {
					"search" : text,
					"default" : textRequired,
				},
				email : {
					"search" : {
						type : 'Text',
					},
					"default" : {
						type : 'Text',
						dataType : "email",
						validators : [ 'required', 'email', Calipso.components.backboneform.validators.getUserEmailValidator(instance) ]
					},
					"search" : {
						type : 'Text',
						dataType : "email",
						validators : ['email' ]
					}
				},
				telephone : {
					"default" : {
						type : Calipso.components.backboneform.Tel,
						dataType : "tel",
						validators : [ Calipso.components.backboneform.validators.digitsOnly ]
					}
				},
				cellphone : {
					"default" : {
						type : Calipso.components.backboneform.Tel,
						dataType : "tel",
						validators : [ Calipso.components.backboneform.validators.digitsOnly ]
					},
				},
				active : {
					"base" : {
						type : 'Checkbox',
					},
					"create" : {
						extend : "base",
						help : "Select to skip email confirmation"
					},
					"update" : {
						extend : "base",
					},
				},
				roles : {
					"base" : {
						type : Backbone.Form.editors.ModelSelect2,
						options : rolesCollection,
						multiple : true,
					},
					"search" : {
						type : Backbone.Form.editors.ModelSelect2,
						options : rolesCollection,
						multiple : true,
					},
					"create" : {
						type : Backbone.Form.editors.ModelSelect2,
						options : rolesCollection,
						multiple : true,
						validators : [ 'required' ],
					},
					"update" : {
						type : Backbone.Form.editors.ModelSelect2,
						options : rolesCollection,
						multiple : true,
					},
				}
			};
		},
		fields : {
			username : {
				"datatype" : "String",
				backgrid : {
					cell : Calipso.components.backgrid.ViewRowCell,
				}
			},
			firstName : {
				"datatype" : "String",
			},
			lastName : {
				"datatype" : "String",
			},
			email : {
				"datatype" : "Email",
			},
			telephone : {
				"datatype" : "Tel",
			},
			cellphone : {
				"datatype" : "Tel",
			},
			active : {
				"datatype" : "Boolean",
			},
			roles : {
				"datatype" : "List",
				"form" : {
					"listModel" : Calipso.model.RoleModel
				}
			},
			edit : {
				"datatype" : "Edit",
			},
		},
		useCases : {
			"search" : {
				fieldIncludes : ["username", "firstName", "lastName", "email" ],

			},
		},
		getGridSchemaOLD : function(instance) {
			return [ {
				name : "username",
				label : "Username",
				cell : Calipso.components.backgrid.ViewRowCell,
				editable : false
			}, {
				name : "firstName",
				label : "First Name",
				editable : false,
				cell : "string"
			}, {
				name : "lastName",
				label : "Last Name",
				editable : false,
				cell : "string"
			}, {
				name : "email",
				label : "Email",
				cell : "email",
				editable : false
			}, {
				name : "createdDate",
				label : "Created",
				cell : "date",
				editable : false
			}, {
				name : "edit",
				label : "",
				editable : false,
				cell : Calipso.components.backgrid.EditRowInModalCell,
				headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
			} ];
		},
		getOverviewSchema : function(instance) {
			return [ {
				member : "username",
				label : "username"
			}, {
				fetchUrl : "/api/rest/users",
				// merge in this model if missing:
				// modelType: Foobar,
				member : "mergedAttribute",
				label : "merged attribute",
				viewType : Calipso.view.CollectionMemberGridView
			} ];
		}

	});


	Calipso.model.HostModel = Calipso.model.GenericModel.extend({
	},
	// static members
	{
		parent : Calipso.model.GenericModel,
		label : "Host",
		typeName : "Calipso.model.HostModel",
		fields : {
			"domain" : {
				"datatype" : "String"
			}
		},
		getPathFragment : function(){
			return  "hosts";
		},

	});
	Calipso.model.UserProfileModel = Calipso.model.UserModel.extend(
	/** @lends Calipso.model.UserDetailsModel.prototype */
	{
		skipDefaultSearch : false
	},
	// static members
	{
		parent : Calipso.model.UserModel,
		viewFragment : "userProfile",
		typeName : "Calipso.model.UserProfileModel",
		layoutViewType : Calipso.view.UserProfileLayout,
		itemViewType : Calipso.view.UserProfileView,
	});

	//////////////////////////////////////////////////
	// More models
	//////////////////////////////////////////////////

	// Country model
	// ---------------------------------------
	Calipso.model.CountryModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.RoleModel.prototype */
	{
		initialize : function() {
			Calipso.model.GenericModel.prototype.initialize.apply(this, arguments);
			this.set("translatedName", Calipso.util.getLabels("countries."+this.get("id")));
		},
		toString : function() {
			return this.get("translatedName") || this.get("name");
		},
		text : function() {
			return this.get("translatedName") || this.get("name");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		parent : Calipso.model.GenericModel,
		label : "Role",
		pathFragment : "countries",
		typeName : "Calipso.model.RoleModel",
		formSchemas : {//
			name : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			nativeName : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			callingCode : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			capital : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			currency : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			languages : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
		},
		gridSchema : [ {
			name : "name",
			label : "Name",
			cell : Calipso.components.backgrid.ViewRowCell,
			editable : false
		}, {
			name : "nativeName",
			label : "Native name",
			editable : false,
			cell : "string"
		}, {
			name : "callingCode",
			label : "Calling code",
			editable : false,
			cell : "string"
		}, {
			name : "capital",
			label : "Capital",
			editable : false,
			cell : "string"
		}, {
			name : "currency",
			label : "Currency",
			editable : false,
			cell : "string"
		}, {
			name : "languages",
			label : "Languages",
			editable : false,
			cell : "string"
		}, {
			name : "edit",
			label : "",
			editable : false,
			cell : Calipso.components.backgrid.EditRowInModalCell,
			headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
		} ],
	});

	// Notification Model
	// -----------------------------------------
	Calipso.model.BaseNotificationModel = Calipso.model.GenericModel.extend({},
	// static members
	{
		parent : Calipso.model.GenericModel,
		pathFragment : "baseNotifications",
		typeName : "Calipso.model.BaseNotificationModel",
	});

	Calipso.model.UserDetailsModel = Calipso.model.UserModel.extend(
	/** @lends Calipso.model.UserDetailsModel.prototype */
	{
		isSearchModel : function() {
			return false;
		},
		getViewTitle : function() {
			var schemaKey = this.getFormSchemaKey();
			var title = "";
			if (schemaKey == "create") {
				title += "Login ";
			} else if (schemaKey.indexOf("update") == 0) {
				title += "Change Password ";
			}
			return title;
		},
		toString : function() {
			return this.get("username");
		},
		sync : function(method, model, options) {
			var _this = this;
			options = options || {};
			options.timeout = 30000;
			if (!options.url) {
				options.url = Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/" + _this.getPathFragment();
			}
			// options.dataType = "jsonp"; // JSON is default.
			return Backbone.sync(method, model, options);
		}

	},
	// static members
	{
		parent : Calipso.model.GenericModel,
		pathFragment : "userDetails",
		baseFragment : '/apiauth/',
		typeName : "Calipso.model.UserDetailsModel",
		/**
		* Get the default layout view at a static context for your subclass,
		* like {@link ModelDrivenCrudLayout} or {@link ModelDrivenBrowseLayout}
		*/
		getLayoutViewType : function(instance) {
			//console.log("UserDetailsModel.getLayoutViewType, layoutViewType: " + this.layoutViewType);
			//console.log("UserDetailsModel.getLayoutViewType, modelType: " + this.getTypeName());
			return Calipso.view.UserDetailsLayout;
		},
		getFormSchemas : function(instance) {
			var passwordText = {
				type : 'Password',
				validators : [ 'required' ]
			};
			var passwordConfirm = {
				type : 'Password',
				validators : [ 'required', {
					type : 'match',
					field : 'password',
					message : 'Passwords must match!'
				} ],
			};
			// is a password reset token already present?
			var pwResetTokenPresent = instance && instance.get("resetPasswordToken");

			return {
				id : {
					"update" : {
						type : 'Hidden',
						hidden : true,
					}
				},
				isResetPasswordReguest : {
					"update-createToken" : {
						type : 'Hidden',
						hidden : true,
					},
				},
				email : {
					"create" : {
						type : 'Text',
						label : "Username or Email",
						validators : [ 'required' ],
					},
					"update-createToken" : {
						type : 'Text',
						validators : [ 'required', 'email' ],
					},
				},
				resetPasswordToken : {
					"create-withToken" : {
						type : pwResetTokenPresent ? "Hidden" : 'Text',
						validators : [ 'required' ]
					},

				},
				currentPassword : {
					"update" : {
						type : 'Password',
						validators : [ 'required', function checkPassword(value, formValues) {
							// verify current password
							var userDetails = new Calipso.model.UserDetailsModel({
								email : Calipso.session.userDetails.get("email"),
								password : value
							});
							userDetails.save(null, {
								async : false,
								url : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/verifyPassword",
							});
							var err = {
								type : 'password',
								message : 'Incorrect current password'
							};
							//console.log("checkPassword: ");
							//console.log(userDetails);
							if (!userDetails.get("id"))
								return err;
						} ],//validators
					},
				},
				password : {
					"create" : passwordText,
					"update" : {
						type : 'Password',
						validators : [ 'required' ],
					},
					"create-withToken" : {
						extend : "update",
					},
				},
				passwordConfirmation : {
					"update" : passwordConfirm,
					"create-withToken" : passwordConfirm,
				},
			};
		}
	});

	// User Registration Model
	// -----------------------

	/**
	 * Subclasses UserModel to provide layout, forms etc. configuration
	 * for user registration flows.
	 */
	Calipso.model.UserRegistrationModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.UserRegistrationModel */
	{
		label : "Register",
		getFormSubmitButton : function() {
			return "<i class=\"fa fa-floppy-o\"></i>&nbsp;Register"
		},
		initialize : function() {
			Calipso.model.GenericModel.prototype.initialize.apply(this, arguments);
			this.set("locale", Calipso.util.getLocale());
		},
	//getFormTemplateKey : function(){
	//	return "auth";
	//}
	}, {
		// static members
		parent : Calipso.model.UserModel,
		label : "Register",
		pathFragment : "users",
		typeName : "Calipso.model.UserRegistrationModel",
		getLayoutViewType : function(instance) {
			return Calipso.view.UserRegistrationLayout;
		},
		getFormSchemas : function(instance) {
			//console.log("UserRegistrationModel.getFormSchemas for " + instance.getTypeName());
			var requiredText = {
				type : 'Text',
				validators : [ 'required' ]
			};
			var passwordText = {
				type : 'Password',
				validators : [ 'required' ]
			};
			var passwordConfirm = {
				type : 'Password',
				validators : [ {
					type : 'match',
					field : 'password',
					message : 'Passwords must match!'
				} ]
			};
			return {
				locale : {
					"default" : {type : 'Hidden'},
				},
				firstName : {
					"create" : requiredText,
					"update" : requiredText
				},
				lastName : {
					"create" : requiredText,
					"update" : requiredText
				},
				username : {
					//"create" : requiredText,
					"update" : requiredText
				},
				email : {
					"search" : {
						type : 'Text',
						validators : [ 'email' ]
					},
					"default" : {
						type : 'Text',
						validators : [ 'required', 'email',  Calipso.components.backboneform.validators.getUserEmailValidator(instance)]
					},
				},
				password : {
					//"create" : passwordText,
					"update" : passwordText
				},
				passwordConfirm : {
					//"create" : passwordConfirm,
					"update" : passwordConfirm
				},
			};

		},

	});

/*
	Calipso.model.UserDetailsConfirmationModel = Calipso.model.UserDetailsModel.extend(
	{
		getFormSubmitButton : function() {
			return "<i class=\"fa fa-floppy-o\"></i>&nbsp;Confirm"
		}
	}, {
		label : "Email Confirmation",

		pathFragment : "accountConfirmations",
		typeName : "Calipso.model.UserRegistrationModel",
		layoutViewType : Calipso.view.UserRegistrationLayout,
		getItemViewType : function() {
			return Calipso.view.GenericFormPanelView.extend({
				commit : function(e) {
					Calipso.stopEvent(e);
					if (!this.isFormValid()) {
						return false;
					}
					// if no validation errors,
					// use the email confirmation link route
					else {
						Calipso.navigate("accountConfirmations/" + this.model.get("confirmationToken"), {
							trigger : true
						});
					}
				}
			});
		},
		formSchemas : {
			confirmationToken : {
				"default" : {
					title : 'Please check your email for a confirmation key',
					type : 'Text',
					validators : [ 'required' ]
				}
			},
		},

	});
*/
	// Report Dataset Model
	// This model is used by the router controller when a
	// subjectModelTypeFragment/reports
	// route is matched, where the subjectModelType matches a model type's URL fragent.
	// The controller uses the ReportDataSetModel
	// as the route model after configuring it with the targe rRoute model
	// type, from which the ReportDataSetModel obtains any custom configuration
	// for route layouts, views and form/grid schemas according to the following table:
	// ReportDataSet                    ModelType
	// getLayoutViewType()              getReportLayoutType()
	// getCollectionViewType()          getReportCollectionViewType()
	// getPathFragment()                getPathFragment() + "/reports"
	// getFormSchemaKey()               "report"
	// getReportKpiOptions()            getReportKpiOptions(this.get("reportType"/*URL param*/)
	// -----------------------------------------
	Calipso.model.ReportDataSetModel = Calipso.model.GenericModel.extend({
		subjectModelType : null,
		// TODO: inline form tmpl
		defaults : {
			formTemplateKey : "horizontal",
			kpi : "sum",
			timeUnit : "DAY",
			reportType : "Businesses",
			calipsoFormSubmitButton : "Show Report"
		},
		initialize : function() {
			Calipso.model.GenericModel.prototype.initialize.apply(this, arguments);
			//this.subjectModelType = options.subjectModelType;
			var subjectModelType = this.get("subjectModelType");
			//console.log("Calipso.model.ReportDataSetModel#initialize, subjectModelType: ");
			//console.log(subjectModelType);
			//console.log("Calipso.model.ReportDataSetModel#initialize, attributes: ");
			//console.log(this.attributes);
			if (!(_.isNull(subjectModelType) || _.isUndefined(subjectModelType))) {
				this.set("reportType", subjectModelType.getReportTypeOptions()[0]);
				var now = new Date();
				this.set("period", (now.getUTCMonth() + 1) + '/' + now.getUTCFullYear());
			}
		},
		getPathFragment : function() {
			return this.get("subjectModelType").getPathFragment() + "/reports";
		},
		getFormSchemaKey : function() {
			return "report";
		},
		getCollectionViewType : function() {
			return this.get("subjectModelType").getReportCollectionViewType();
		},
		getLayoutViewType : function() {
			return this.get("subjectModelType").getReportLayoutType ? this.get("subjectModelType").getReportLayoutType() : Calipso.view.ModelDrivenReportLayout;
		},
		getReportTypeOptions : function() {
			return this.get("subjectModelType").getReportTypeOptions ? this.get("subjectModelType").getReportTypeOptions() : null;
		},
		getReportKpiOptions : function(reportType) {
			var options;
			if (!reportType) {
				reportType = this.get("reportType");
			}

			if (this.get("subjectModelType").getReportKpiOptions) {
				options = this.get("subjectModelType").getReportKpiOptions(reportType);
			}

			if (!options) {
				options = [ {
					val : "sum",
					label : 'Sum'
				}, {
					val : "count",
					label : 'Count'
				} ];
			}
			return options;
		},
		getFormSchema : function(actionName) {
			//console.log("Calipso.model.ReportDataSetModel#getFormSchema actionName: " + actionName);
			var formSchema = {};
			var reportTypeOptions = this.getReportTypeOptions();
			if (reportTypeOptions) {
				formSchema.reportType = {
					title : "Report Type",
					type : 'Select',
					options : reportTypeOptions,
					template : this.fieldTemplate
				// TODO: validate option
				// validators : [ 'required' ]
				};
			}

			formSchema.kpi = {
				title : "KPI",
				type : 'Select',
				options : this.getReportKpiOptions(),
				template : this.fieldTemplate
			// TODO: validate option
			// validators : [ 'required' ]
			};
			formSchema.timeUnit = {
				title : "by",
				type : 'Select',
				options : [ {
					val : "DAY",
					label : 'Day'
				}, {
					val : "MONTH",
					label : 'Month'
				} ],
				template : this.fieldTemplate
			// TODO: validate option
			// validators : [ 'required' ]
			};

			formSchema.period = {
				title : "Period",
				type : Calipso.components.backboneform.Datetimepicker,
				template : this.fieldTemplate,
				config : {
					locale : Calipso.util.getLocale(),
					format : 'MM/YYYY',
					viewMode : 'months',
					widgetPositioning : {
						horizontal : "right"
					}
				},
				validators : [ 'required' ]
			};
			//console.log("Calipso.model.ReportDataSetModel#getFormSchema formSchema: ");
			//console.log(formSchema);
			return formSchema;
		},
		getGridSchema : function(kpi) {
			//console.log("Calipso.model.ReportDataSetModel#getGridSchema kpi: " + kpi);
			// sum or count
			if (!kpi) {
				kpi = this.get("kpi");
				//console.log("Calipso.model.ReportDataSetModel#getGridSchema this.kpi: " + kpi);
			}
			var schema = [ {
				name : "label",
				label : "",
				editable : false,
				cell : "text",
			} ];
			//console.log("Calipso.model.ReportDataSetModel#getGridSchema returns: ");
			var entries = this.wrappedCollection.first().get("entries");
			for (var i = 0; i < entries.length; i++) {
				schema.push({
					name : "entries." + i + ".entryData." + kpi,
					label : entries[i].label,
					editable : false,
					cell : Calipso.components.backgrid.ChildNumberAttributeCell,
				});
			}
			//console.log("Calipso.model.ReportDataSetModel#getGridSchema returns: ");
			//console.log(schema);
			return schema;
		},
		fieldTemplate : _.template('\
    <div class="form-group field-<%= key %>">&nbsp;\
      <label class="control-label" for="<%= editorId %>">\
        <% if (titleHTML){ %><%= titleHTML %>\
        <% } else { %><%- title %><% } %>\
      </label>&nbsp;\
     <span data-editor></span>\
    </div>&nbsp;\
  '),
	},
	// static members
	{
		parent : Calipso.model.GenericModel,
	});

	Calipso.model.ReportDataSetModel.getTypeName = function() {
		return "Calipso.model.ReportDataSetModel";
	};

	Calipso.model.ReportDataSetModel.getItemViewType = function() {
		return Calipso.view.ReportFormView;
	};

	Calipso.model.ReportDataSetModel.getCollectionViewType = function() {
		return Calipso.view.ModelDrivenReportView;
	};

});
