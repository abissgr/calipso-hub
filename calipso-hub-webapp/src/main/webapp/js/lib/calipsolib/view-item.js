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

define(
[ "lib/calipsolib/util", 'underscore', 'handlebars', 'backbone', 'marionette', 'moment', 'backbone-forms', 'backgrid' ],
function(Calipso, _, Handlebars, Backbone, BackboneMarionette, moment, BackboneForms, Backgrid) {


	// The recursive tree view
	//	var TreeView = Backbone.Marionette.CompositeView.extend({
	//	    template: "#node-template",
	//
	//	    tagName: "li",
	//
	//	    initialize: function(){
	//	        // grab the child collection from the parent model
	//	        // so that we can render the collection as children
	//	        // of this parent node
	//	        this.collection = this.model.nodes;
	//	    },
	//
	//	    appendHtml: function(cv, iv){
	//	        cv.$("ul:first").append(iv.el);
	//	    },
	//	    onRender: function() {
	//	        if(_.isUndefined(this.collection)){
	//	            this.$("ul:first").remove();
	//	        }
	//	    }
	//	});
	//


	Calipso.view.TemplateBasedItemView = Calipso.view.ItemView.extend(
	/** @lends Calipso.view.TemplateBasedItemView.prototype */
	{
		template : Calipso.getTemplate("templateBasedItemView"),//_.template('{{#if url}}<a href="{{url}}">{{/if}}{{#if name}}<h5>{{name}}</h5>{{else}}{{#if title}}<h5>{{title}}</h5>{{/if}}{{/if}}{{#if description}}{{description}}{{/if}}{{#if url}}</a>{{/if}}'),
		tagName : "li",
		initialize : function(models, options) {
			Calipso.view.ItemView.prototype.initialize.apply(this, arguments);
		},
		attributes : function() {
			return this.getOption("attributes");
		},
		getTemplate : function() {
			return this.getOption("template");
		},
	}, {
		typeName : "Calipso.view.TemplateBasedItemView",
	});




	Calipso.view.NotFoundView = Calipso.view.ItemView.extend(
	/** @lends Calipso.view.NotFoundView.prototype */
	{
		className : 'container span8 home',
		template : Calipso.getTemplate('notfound')
	},
	// static members
	{
		typeName : "Calipso.view.NotFoundView",
	});
	Calipso.view.FooterView = Marionette.ItemView.extend(
	/** @lends Calipso.view.FooterView.prototype */
	{
		className : "container",
		template : Calipso.getTemplate('footer'),
		className : "col-sm-12"
	},
	// static members
	{
		typeName : "Calipso.view.FooterView",
	});

	Calipso.view.UseCaseItemView = Calipso.view.ItemView.extend({
		schemaType : null,
		mergableOptions : [ 'useCaseContext', 'formOptions' ],
		templateHelpers : {
			viewId : function() {
				return Marionette.getOption(this, "id");
			},
		},
		initialize : function(options) {
			Calipso.view.ItemView.prototype.initialize.apply(this, arguments);
			var _this = this;
			this.mergeOptions(options, this.mergableOptions);
		},
		onBeforeShow : function() {
			//TODO: roles, masks, nested usecases
			this.useCase = this.useCaseContext.getChild(this.regionName, this.schemaType+"View");
			this.schema = this.buildSchema();
		},
		/** builds a UI-specific schema for the given fields */
		buildSchema : function(fields) {
			var _this = this, schemaType = this.schemaType, isArray = this.schemaType == "backgrid", schema = null;

			fields || (fields = this.useCase.getFields());
				//console.log(_this.getTypeName() + "#buildSchema fields: ");
			//console.log(fields);
			if (fields) {
				var schemaEntry, baseSchemaEntry, overrideSchemaEntry;

				schema = isArray ? [] : {};
				_.each(fields, function(field, key) {
					//console.log(_this.getTypeName() + "#buildSchema key: " + key + ", field: ");
					//console.log(field);
					baseSchemaEntry = Calipso.fields[field.fieldType][schemaType];
					overrideSchemaEntry = field[schemaType];
					// if a schema entry exists, add it
					if (baseSchemaEntry || overrideSchemaEntry) {
						// merge to new object
						schemaEntry = _.extend({}, baseSchemaEntry || {}, overrideSchemaEntry || {});

						//TODO: also labelPropertyCopy
						if (_this.keyPropertyCopy) {
							schemaEntry[_this.keyPropertyCopy] = key;
						}
						if (_this.labelPropertyCopy) {
							schemaEntry[_this.labelPropertyCopy] = field.label;
						}

						// if expected schema is of type array, push
						if (isArray) {
							schema.push(schemaEntry);
						}// if expected schema is of type objet, add
						else {
							schema[key] = schemaEntry;
						}
					}

				});
			}
			//console.log("UseCaseItemView#buildSchema schema: ");
			//console.log(schema);
			return schema;
		},
	}, {
		typeName : "Calipso.view.UseCaseItemView",
	});

	Calipso.view.ModelDrivenCollectionGridView = Calipso.view.UseCaseItemView.extend(
	/**
	 * @param options object members:
	 *  - collection or model.wrappedCollection
	 *  - callCollectionFetch: whether to fetch the collection from the server
	 * @lends Calipso.view.ModelDrivenCollectionGridView.prototype
	 * */
	{
		schemaType : "backgrid",
		keyPropertyCopy : "name",
		labelPropertyCopy : "label",

		collection : null,
		backgrid : null,
		// Define view template
		template : Calipso.getTemplate('md-collection-grid-view'),
		events : {
			"click button.btn-windowcontrols-destroy" : "back"
		},

		templateHelpers : {
			viewId : function() {
				return Marionette.getOption(this, "id");
			},
			resultsInfo : function() {
				var resultsInfo = this.model.wrappedCollection.state;
				var pastResults = (resultsInfo.pageSize * (resultsInfo.currentPage - resultsInfo.firstPage));
				resultsInfo.pageStart = pastResults + 1;
				resultsInfo.pageEnd = pastResults + this.model.wrappedCollection.length;
				return resultsInfo;
			},
		},
		back : function(e) {
			Calipso.stopEvent(e);
			window.history.back();
		},
		getResultsInfo : function() {
			var resultsInfo = this.model.wrappedCollection.state;
			var pastResults = (resultsInfo.pageSize * (resultsInfo.currentPage - resultsInfo.firstPage));
			resultsInfo.pageStart = pastResults + 1;
			resultsInfo.pageEnd = pastResults + this.model.wrappedCollection.length;
			return resultsInfo;
		},
		initialize : function(options) {
			//console.log("ModelDrivenCollectionGridView.initialize, options: " + options);
			Calipso.view.UseCaseItemView.prototype.initialize.apply(this, arguments);

			this.collection = options.collection || options.model.wrappedCollection;
			if (!this.collection) {
				throw "no collection or collection wrapper model was provided";
			}

			if (options.callCollectionFetch) {
				this.callCollectionFetch = options.callCollectionFetch;
			}
			//console.log("ModelDrivenCollectionGridView.initialize, callCollectionFetch: " + this.callCollectionFetch);
			//console.log("ModelDrivenCollectionGridView.initialize, collection: " + (this.collection ? this.collection.length : this.collection));
		},
		onGridRendered : function() {

		},
		onCollectionFetched : function() {

		},
		onCollectionFetchFailed : function() {

		},
		onShow : function() {
			var _self = this;
			//console.log("GRID onshow schema: ");
			//console.log(this.schema);
			this.backgrid = new Backgrid.Grid({
				className : "backgrid table-striped responsive-table",
				columns : _self.schema,
				row : Calipso.components.backgrid.SmartHighlightRow,
				collection : _self.collection,
				emptyText : "No records found"
			});

			this.$('.backgrid-table-container').append(this.backgrid.render().$el);
			// TODO: refresh stuff
			_self.listenTo(_self.collection, "backgrid:refresh", function(){
				var resultsInfo = _self.getResultsInfo();
				_self.$el.find(".resultsInfo").html(
					resultsInfo.pageStart + " - " + resultsInfo.pageEnd + " / " + resultsInfo.totalRecords);
			});
			var paginator = new Backgrid.Extension.Paginator({

				// If you anticipate a large number of pages, you can adjust
				// the number of page handles to show. The sliding window
				// will automatically show the next set of page handles when
				// you click next at the end of a window.
				windowSize : 10, // Default is 10

				// Used to multiple windowSize to yield a number of pages to slide,
				// in the case the number is 5
				slideScale : 0.25, // Default is 0.5

				// Whether sorting should go back to the first page
				goBackFirstOnSort : false, // Default is true

				collection : _self.collection
			});

			this.$('.backgrid-table-container').append(paginator.render().el);
			//						//console.log("ModelDrivenCollectionGridView.onShow, collection url: "+);
			this.onGridRendered();
			if (this.callCollectionFetch) {
				var fetchOptions = {
					reset : true,
					url : _self.collection.url,
					success : function() {
						_self.onCollectionFetched();
						_self.$(".loading-indicator").hide();
						_self.$(".loading-indicator-back").hide();
					},
					error : function() {
						_self.onCollectionFetchFailed();
						_self.$(".loading-indicator").hide();
						_self.$(".loading-indicator-back").hide();
					}

				};
				if (_self.collection.data) {
					if (_self.collection.data[""] || _self.collection.data[""] == null) {
						delete _self.collection.data[""];
					}
					fetchOptions.data = _self.collection.data;
					//
				}
				_self.collection.fetch(fetchOptions);
				this.callCollectionFetch = false;
			} else {
				_self.$(".loading-indicator").hide();
				_self.$(".loading-indicator-back").hide();
			}
			// this.collection.fetch();main info

			// console.log("ModelDrivenCollectionGridView showed");

		},

	},
	// static members
	{
		typeName : "Calipso.view.ModelDrivenCollectionGridView",
	});

	/*
	 * Use-case driven form view.
	 * @fires GenericFormView#model:sync when the model is persisted successfully
	 */
	Calipso.view.GenericFormView = Calipso.view.UseCaseItemView.extend({
		schemaType : "form",
		/**
		 * Cals the static method of the same name. Returns a Backbone.Form template
		 * @param  {[String]} the template key, usually one of {"horizontal", "inline", "vertical"}
		 * @return {[type]} the compiled template
		 */
		getFormTemplate : function(templateKey) {
			return this.constructor.getFormTemplate(templateKey)
		},
		getFieldTemplate : function(templateKey) {
			return this.constructor.getFieldTemplate(templateKey)
		},
		formTemplateKey : "horizontal",
		modal : false,
		hasDraft : false,
		addToCollection : null,
		// Define view template
		formTitle : "options.formTitle",
		template : Calipso.getTemplate('md-form-view'),
		events : {
			"click .btn-social-login" : "socialLogin",
			"click .open-modal-page" : "openModalPage",
			"click button.submit" : "commit",
			"submit form" : "commit",
			"click button.cancel" : "cancel",
			"submit" : "commitOnEnter",
			"keypress input[type=password]" : "commitOnEnter",
			"keypress input[type=text]" : "commitOnEnter"
		},
		initialize : function(options) {
			Calipso.view.UseCaseItemView.prototype.initialize.apply(this, arguments);
			//console.log("FORM options: ");
			//console.log(options);
			this.mergeOptions(options, [ "modal", "addToCollection", "formTemplateKey", "formTemplate" ]);
			//console.log("FORM modal: " + this.modal);
			// grab a handle for the search results collection if any, from options or model
			//TODO: remove
			if (this.options.searchResultsCollection) {
				this.searchResultsCollection = options.searchResultsCollection;
			} else if (this.model.wrappedCollection) {
				this.searchResultsCollection = this.model.wrappedCollection;
			}
			//
			if (!this.formTemplate) {
				this.formTemplate = this.getFormTemplate(this.formTemplateKey ? this.formTemplateKey : this.model.getFormTemplateKey());
			}

		},
		openModalPage : function(e) {
			Calipso.stopEvent(e);
			var $a = $(e.currentTarget);
			var pageView = new Calipso.view.TemplateBasedItemView({
				template : Calipso.getTemplate($a.data("page")),
				tagName : "div"
			});
			Calipso.vent.trigger('modal:showInLayout', {
				view : pageView,
				title : $a.data("title")
			});
		},
		commitOnEnter : function(e) {
			if (e.keyCode != 13) {
				return;
			} else {
				this.commit(e);
			}
		},
		isFormValid : function() {
			var isValid = false;
			if (window.Placeholders) {
				Placeholders.disable();
			}
			var errors = this.form.commit({
				validate : true
			});
			if (errors) {
				var errorMsg = "" + errors._others;
				if (window.Placeholders) {
					Placeholders.enable();
				}
				if (this.modal) {
					$(".modal-body").scrollTop(0);
				}
			} else {
				isValid = true;
			}

			return isValid;
		},
		commit : function(e) {
			var _this = this;
			Calipso.stopEvent(e);
			if (!this.isFormValid()) {
				return false;
			}
			// if no validation errors
			else {
				// Case: create/update

				/*_this.useCase.key.indexOf("create") == 0 || _this.useCase.key.indexOf("update") == 0 */
				if (_this.useCase.key.indexOf("search") != 0) {
					// persist changes

					_this.model.save(null, {
						success : function(model, response) {
							sessionStorage.removeItem(_this.getDraftKey());
							_this.hasDraft = false;
							if (_this.addToCollection) {
								_this.addToCollection.add(_this.model);
								_this.model.trigger("added");
							}
							// trigger model:sync
							_this.triggerMethod("model:sync", {
								model : _this.model,
								view : _this,
								collection : _this.collection
							});
							if (_this.modal) {
								Calipso.vent.trigger("modal:destroy");
							} else {
								Calipso.vent.trigger("genericFormSaved", model);
							}
						},
						error : function() {
							alert("Failed persisting changes");
						}
					});
				} else {
					// Case: search
					var newData = this.form.toJson();
					this.searchResultsCollection.data = newData;
					this.searchResultsCollection.fetch({
						reset : true,
						data : newData,
						success : function() {
							// signal successful retreival of search results
							// for the currently active layout to handle presentation
							Calipso.vent.trigger("genericFormSearched", _this.model);
						},

						// Generic error, show an alert.
						error : function(model, response) {
							alert("Failed retreiving search results");
						}

					});

				}
			}
			// search entities?
		},
		cancel : function() {
			window.history.back();
		},
		onShow : function() {
			var _self = this;
			//console.log("_self.formSchemaKey: " + _self.formSchemaKey);
			// get appropriate schema
			var formSchema = this.schema;

			// TODO: add a property in generic model to flag view behavior (i.e. get add http:.../form-schema to the model before rendering)
			if (formSchema) {
				_self.renderForm(formSchema);
			} else {
				var fetchScemaUrl = Calipso.getBaseUrl() + "/" + _self.model.getPathFragment() + '/' + (_self.model.isNew() ? "new" : _self.model.get("id"));

				_self.model.fetch({
					url : fetchScemaUrl,
					success : function(model, response, options) {
						_self.renderForm();
					},
					error : function(model, response, options) {
						//console.log("Error fetching model from server");
						alert("Error fetching model from server");
					}
				});
			}

		},
		renderForm : function(formSchema) {
			var _self = this;

			if (!formSchema) {
				formSchema = _self.model.getFormSchema(_self.formSchemaKey);
			}
			var formSubmitButton = _self.model.getFormSubmitButton ? _self.model.getFormSubmitButton() : false;
			if (!formSubmitButton) {
				if (_self.useCaseContext.key.indexOf("search") == 0) {
					formSubmitButton = "<i class=\"glyphicon glyphicon-search\"></i>&nbsp;Search";
				} else if (_self.useCaseContext.key.indexOf("create") == 0 || _self.useCaseContext.key.indexOf("update") == 0) {
					formSubmitButton = "<i class=\"fa fa-floppy-o\"></i>&nbsp;Save";
				} else {
					formSubmitButton = "Submit";
				}
			}
			//;(_self.formSchemaKey);

			// render form
			if (Calipso.session.searchData && (!_self.searchResultsCollection.data)) {
				_self.model.set(Calipso.session.searchData);
				_self.searchResultsCollection.data = Calipso.session.searchData;
			} else {
				var draft = sessionStorage.getItem(_self.getDraftKey());
				if (draft) {
					draft = JSON.parse(draft);
					_self.model.set(draft);
				}
			}

			var formOptions = {
				model : _self.model,
				schema : formSchema,
				formSchemaKey : _self.formSchemaKey,
				template : _self.getFormTemplate(),
				fieldTemplate : _self.getFieldTemplate(),
			};
			// model driven submit button?
			if (formSubmitButton) {
				formOptions.submitButton = formSubmitButton;
			}
			this.form = new Calipso.components.backboneform.Form(formOptions);
			//this.$el.append(this.form.el);
			this.form.setElement(this.$el.find(".generic-form-view").first()).render();
			this.$el.find('input, select').filter(':visible:enabled:first').focus();
			this.onFormRendered();

			// flag changed
			this.listenTo(this.form, 'change', function() {
				_self.hasDraft = true;
			});

			// proxy model events to parent layout
			this.listenTo(this.form, "all", function(eventName) {
				//console.log(_self.getTypeName() + " triggering event form:" + eventName);
				_self.triggerMethod("form:" + eventName, {
					model : _self.model,
					view : _self,
					collection : _self.collection
				});
			});
		},
		onFormRendered : function() {

		},
		getFormData : function getFormData($form) {
			var unindexed_array = $form.serializeArray();
			var indexed_array = {};

			$.map(unindexed_array, function(n, i) {
				indexed_array[n['name']] = n['value'];
			});

			return indexed_array;
		},
		socialLogin : function(e) {
			console.log(this.getTypeName() + "#socialLogin");
			Calipso.socialLogin(e);
		},
		getDraftKey : function() {
			return this.model.getPathFragment() + '/' + this.model.get("id") + '/' + this.formSchemaKey;
		},
		onBeforeDestroy : function(e) {
			if (this.hasDraft) {
				// keep a session draft of changes
				var draft = this.form.getDraft();
				delete draft.currentStepIndex;

				sessionStorage.setItem(this.getDraftKey(), JSON.stringify(draft));
			}
			this.form.close();
		},
	},{
		// static members
		typeName : "Calipso.view.GenericFormView",
		/**
		 * Returns a Backbone.Form template
		 * @param  {[Calipso.view.GenericFormView]} the form view instance
		 * @param  {[String]} the template key, usually one of {"horizontal", "inline", "vertical"}
		 * @return {[type]} the compiled template
		 */
		getFormTemplate : function(instance, templateKey) {
			templateKey = /*templateKey ? templateKey :*/"horizontal";
			//console.log("consstructor.getFormTemplate, templateKey: " + templateKey);
			return Calipso.util.formTemplates[templateKey];
		},
		getFieldTemplate : function(instance, templateKey) {
			templateKey = /*templateKey ? templateKey :*/"horizontal";
			//console.log("consstructor.getFieldTemplate, templateKey: " + templateKey);
			return Calipso.util.formTemplates["field-" + templateKey];
		},
	});

	Calipso.view.GenericFormPanelView = Calipso.view.GenericFormView.extend({
		template : Calipso.getTemplate('md-formpanel-view'),
	},
	// static members
	{
		typeName : "Calipso.view.GenericFormPanelView",
	});

	Calipso.view.AbstractItemView = Calipso.view.ItemView.extend({

		templateHelpers : {
			viewId : function() {
				return Marionette.getOption(this, "id");
			},
		},
		initialize : function(options) {

			if (!options || !options.id) {
				this.id = _.uniqueId(this.getTypeName() + "_");
				$(this.el).attr('id', this.id);
			}
			Calipso.view.ItemView.prototype.initialize.apply(this, arguments);
		},
		getTypeName : function() {
			return this.constructor.getTypeName();
		},
		//override toString to return something more meaningful
		toString : function() {
			return this.getTypeName() + "(" + JSON.stringify(this.attributes) + ")";
		},
	},
	// static members
	{
		typeName : "Calipso.view.AbstractItemView",
	});

	Calipso.view.UserProfileView = Calipso.view.AbstractItemView.extend({
		template : Calipso.getTemplate('userProfile'),
	},
	// static members
	{
		typeName : "Calipso.view.UserProfileView",
	});

	Calipso.collection.TabCollection = Backbone.Collection.extend({
		initialize : function() {
			if (!Calipso.model.TabModel) {
				Calipso.model.TabModel = Calipso.model.GenericModel.extend({
					getPathFragment : function() {
						return null;
					}
				});

				Calipso.model.TabModel.getTypeName = function(instance) {
					return "TabModel";
				};
			}
			this.model = Calipso.model.GenericModel;
			this.listenTo('add', this.onModelAdded, this);
			this.listenTo('remove', this.onModelRemoved, this);
		},
		onModelAdded : function(model, collection, options) {
			//_self.tabKeys[model.get("id")] = model;
		},
		onModelRemoved : function(model, collection, options) {
			//_self.tabKeys[model.get("id")] = null;
		},
	},
	// static members
	{
		typeName : "Calipso.view.TabCollection",
	});

	Calipso.view.TabLabelsCollectionView = Backbone.Marionette.CollectionView.extend({
		className : 'nav nav-pills',
		tagName : 'ul',
		itemTemplate : Calipso.getTemplate('tab-label'),
		childViewContainer : '.nav-tabs',
		initialize : function(options) {
			Marionette.CollectionView.prototype.initialize.apply(this, arguments);

		},
		getItemView : function(item) {
			var _this = this;
			return Backbone.Marionette.ItemView.extend({
				tagName : 'li',
				className : 'calipso-tab-label',
				id : "calipso-tab-label-" + item.get("id"),
				template : _this.itemTemplate,
				events : {
					"click .show-tab" : "viewTab",
					"click .destroy-tab" : "destroyTab"
				},
				/**
				 this.listenTo(Calipso.vent, "layout:viewModel", function(itemModel) {
					_this.showItemViewForModel(itemModel, "view")
				}, this);
				 */
				viewTab : function(e) {
					Calipso.stopEvent(e);
					CalipsoApp.vent.trigger("viewTab", this.model);
				},
				destroyTab : function(e) {
					Calipso.stopEvent(e);
					//					this.model.collection.remove(this.model);
					this.destroy();
					CalipsoApp.vent.trigger("viewTab", {
						id : "Search"
					});
				},
			});
		},
	},
	// static members
	{
		typeName : "Calipso.view.TabLabelsCollectionView",
	});

	Calipso.view.TabContentsCollectionView = Backbone.Marionette.CollectionView.extend({
		tagName : 'div',
		getItemView : function(item) {
			var someItemSpecificView = item.getItemViewType ? item.getItemViewType() : null;
			if (!someItemSpecificView) {
				someItemSpecificView = Calipso.view.GenericFormView;
			}
			return someItemSpecificView;
		},
		buildItemView : function(item, ItemViewClass) {

			var options = {
				model : item
			};
			if (item && item.wrappedCollection) {
				options.searchResultsCollection = item.wrappedCollection;
			}
			// do custom stuff here

			var view = new ItemViewClass(options);

			// more custom code working off the view instance

			return view;
		},
	},
	// static members
	{
		typeName : "Calipso.view.TabContentsCollectionView",
	});

	return Calipso;
});
