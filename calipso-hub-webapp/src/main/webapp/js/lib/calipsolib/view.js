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

			//////////////////////////////////////////////////
			// Layouts
			//////////////////////////////////////////////////

			Calipso.view.AbstractLayout = Backbone.Marionette.LayoutView.extend({
				taName : "div",
				/** Stores the default forward path to use after a successful action */
				defaultForward : null,
				/** Stores the final configuration */
				config : null,
				skipSrollToTop : false,
				onDomRefresh : function() {
					this.updateTitle();
				},
				/**
				 * Get the default config.
				 */
				getDefaultConfig : function() {
					var defaultConfig = {};
					// superDefaultConfig = _.result(**SuperClassHere**.prototype, 'getDefaultConfig')
					// _.extend(superDefaultConfig, defaultConfig);
					return defaultConfig;
				},
				/**
				 * Get an array of required option names. An error will be thrown
				 * during initialization if anyone is undefined or null.
				 */
				getRequiredOptionNames : function() {
					return [];
				},
				/**
				 * Validate the final configuration. Called during initialization
				 * by {Calipso.view.MainLayout#configure}. Internal use only.
				 */
				_validateConfiguration : function() {
					var missing = [];
					var requiredOptionNames = this.getRequiredOptionNames();
					if (requiredOptionNames && requiredOptionNames.length > 0) {
						for (var i = 0; i < requiredOptionNames.length; i++) {
							var requiredOptionName = requiredOptionNames[i];
							var finalOption = this.config[requiredOptionName];
							if (finalOption == undefined || finalOption == null) {
								missing.push(requiredOptionName);
							}
						}
					}
					if (missing.length > 0) {
						throw this.getTypeName() + "#validateConfiguration ERROR: missing required options" + missing.toString();
					}
				},
				/**
				 * (Re) Configure by overwriting this.config with according to
				 * this.model.getLayoutOptions and then by relevant options only.
				 * Finally, validate configuration for missing required options.
				 */
				configure : function(options) {
					if (options.model) {
						this.model = options.model;
						if (this.model.wrappedCollection) {
							this.searchResultsCollection = this.model.wrappedCollection;
						}
					}
					// initialize config using defaults
					this.config = this.getDefaultConfig();
					// (re) configure according to this.model.getLayoutOptions
					// _.extend(this.config, this.model.getLayoutOptions(this.model));
					// and then again by relevant options only
					options = options || {};
					_.extendOwn(this.config, options);
					// validate config
					this._validateConfiguration();
				},
				onBeforeRender : function() {
					// set up final bits just before rendering the view's `el`
					// TODO move this method call into non-public marionette API?
					this.configure(this.options);
				},
				updateTitle : function() {

					// update title
					var title = this.config.title;
					if (!title && this.model && this.model.getViewTitle) {
						title = this.model.getViewTitle();
					}
					if (title) {
						this.$el.find(".view-title:first").html(title);
					}
				},
				initialize : function(options) {
					Backbone.Marionette.LayoutView.prototype.initialize.apply(this, arguments);
					if (!this.skipSrollToTop) {
						$(window).scrollTop(0);
					}
				},
				getTypeName : function() {
					return this.constructor.getTypeName();
				}
			}, {
				getTypeName : function() {
					return "MainLayout"
				}
			});

			Calipso.view.MainLayout = Calipso.view.AbstractLayout.extend({
				className : "container configurable-fluid",
				initialize : function(options) {
					Calipso.view.AbstractLayout.prototype.initialize.apply(this, arguments);
				}
			});

			Calipso.view.ModalLayout = Calipso.view.AbstractLayout.extend({
				template : Calipso.getTemplate('modal-layout'),
				events : {
					"click a.modal-close" : "closeModal",
					"click button.modal-close" : "closeModal"
				},
				regions : {
					modalBodyRegion : ".modal-body"
				},
				childView : null,
				initialize : function(options) {
					Calipso.view.AbstractLayout.prototype.initialize.apply(this, arguments);
					var _this = this;
					if (options.childView) {
						this.childView = options.childView;
					}
				},
				onShow : function() {
					// render child view
					this.modalBodyRegion.show(this.childView);
				},
				closeModal : function(e) {
					Calipso.stopEvent(e);
					Calipso.vent.trigger("modal:close");
				}

			}, {
				getTypeName : function() {
					return "Calipso.view.ModalLayout";
				}
			});
			
			/*Calipso.view.HeaderNotificationsRegion = Backbone.Marionette.Region.extend({
				el : "#calipsoHeaderView-notificationsRegion",
				attachHtml : function(view) {
					this.$el.clear().append('<a href="#" data-toggle="dropdown" class="dropdown-toggle">' + '<i class="fa fa-bell fa-fw"></i>' + '<sup class="badge badge-primary badge-notifications-count hidden"></sup>' + '<i class="fa fa-caret-down"></i>', view.el);
				}
			});
			Calipso.view.HeaderNotificationsRegion.prototype.attachHtml = function(view) {
				this.$el.clear().append('<a href="#" data-toggle="dropdown" class="dropdown-toggle">' + '<i class="fa fa-bell fa-fw"></i>' + '<sup class="badge badge-primary badge-notifications-count hidden"></sup>' + '<i class="fa fa-caret-down"></i>', view.el);
			};*/

			Calipso.view.HeaderView = Calipso.view.MainLayout.extend(
			/** @lends Calipso.view.HeaderView.prototype */
			{
				className : "container",
				template : Calipso.getTemplate('header'),
				id : "navbar-menu",
				className : "col-sm-12",
				events : {
					"click a.login" : "login",
					"click a.register" : "register",
					"click a.logout" : "logout",
					"click a.locale" : "changeLocale",
				},
				regions : {

					menuRegion : "#calipsoHeaderView-menuRegion",
					notificationsRegion : "#calipsoHeaderView-notificationsRegion"
//					notificationsRegion : {
//						// appends the notifications without clearing the link,
//						// fixes HTML structure issue
//						regionClass : Calipso.view.HeaderNotificationsRegion
//					}
				},
				changeLocale : function(e) {
					console.log("changeLocale: ");
					console.log($(e.currentTarget).data("locale"));
					Calipso.stopEvent(e);
					Calipso.changeLocale($(e.currentTarget).data("locale"));
				},
				// TODO: investigate
				//		serializeData: function(){
				//			var _this = this;
				//			return {
				//				model: _this.model,
				//				message: "serializeData works",
				//			}
				//		},
				onShow : function() {
					// TODO:find whos triggering and change
					this.listenTo(Calipso.vent, "header:hideSidebar", function() {
						//				this.$el.find(".navbar-static-side").hide();
						//				$("#page-wrapper").attr("id", "page-wrapper-toggled");
					});
					this.listenTo(Calipso.vent, "header:showSidebar", function() {
						//				$("#page-wrapper-toggled").attr("id", "page-wrapper");
						//				this.$el.find(".navbar-static-side").show();
					});

					var menuModel = [ {
						url : "users",
						label : "Users"
					}, {
						url : "hosts",
						label : "Hosts"
					} ];// header-menu-item
					var MenuItemView = Backbone.Marionette.ItemView.extend({
						tagName : "li",
						template : Calipso.getTemplate('header-menuitem')
					});

					//			var MenuCollectionView = Backbone.Marionette.CollectionView.extend({
					//				tagName : "ul",
					//				template : Calipso.getTemplate('header-menuitem'),
					//				childView : MenuItemView
					//			});
					//			this.menuRegion.show(new MenuCollectionView(menuModel));
					if (Calipso.util.isAuthenticated()) {
						// load and render notifications list
						var notifications = new Calipso.collection.PollingCollection([], {
							url : Calipso.getBaseUrl() + "/api/rest/baseNotifications",
							model : Calipso.model.BaseNotificationModel
						});

						//console.log("HeaderView, created notifications collection: " + notifications + ", url: " + notifications.url);
						var notificationsView = new Calipso.view.TemplateBasedCollectionView({
							tagName : "ul",
							className : "dropdown-menu dropdown-notifications",
							template : Calipso.getTemplate("headerNotificationsCollectionView"),
							childViewOptions : {
								template : Calipso.getTemplate("headerNotificationsItemView"),
							},
							collection : notifications,
						});
						this.notificationsRegion.show(notificationsView);
						// update counter badges
						Calipso.updateBadges(".badge-notifications-count", Calipso.session.userDetails ? Calipso.session.userDetails.get("notificationCount") : 0);
					}

				},
				logout : function(e) {
					Calipso.stopEvent(e);
					Calipso.vent.trigger("session:destroy");
				},
				register : function(e) {
					Calipso.stopEvent(e);
					Calipso.navigate("register", {
						trigger : true
					});
				},
				login : function(e) {
					Calipso.stopEvent(e);
					Calipso.navigate("login", {
						trigger : true
					});
				}
			}, {
				getTypeName : function() {
					return "HeaderView";
				}
			});
			Calipso.view.ModelDrivenBrowseLayout = Calipso.view.MainLayout.extend(
			/** @lends Calipso.view.ModelDrivenBrowseLayout.prototype */
			{
				tagName : 'div',
				id : "calipsoModelDrivenBrowseLayout",
				template : Calipso.getTemplate('md-browse-layout'),
				/**
				 * Get the default config. Overwrites {Calipso.view.MainLayout#getDefaultConfig}
				 */
				getDefaultConfig : function() {
					var defaultConfig = Calipso.view.MainLayout.prototype.getDefaultConfig.apply(this);
					// now set this class' default config
					defaultConfig.skipToSingleResult = false;
					return defaultConfig;
				},
				regions : {
					contentRegion : "#calipsoModelDrivenBrowseLayout-contentRegion"
				},
				onShow : function() {
					this.showContent(this.model);
				},
				initialize : function(options) {
					Calipso.view.MainLayout.prototype.initialize.apply(this, arguments);
					var _this = this;

					this.listenTo(Calipso.vent, "updateSearchLocation", function(model) {
						_this.updateSearchLocation(model);
					}, this);

					this.listenTo(Calipso.vent, "genericFormSaved", function(model) {
						_this.onGenericFormSaved(model);
					}, this);
					this.listenTo(Calipso.vent, "genericShowContent", function(model) {
						_this.onGenericShowContent(model);
					});

					this.listenTo(Calipso.vent, "genericFormSearched", function(model) {
						_this.onGenericFormSearched(model);
					});
					// vent handling might be overriden by subclasses
					if (!options.dontListenTo) {
						this.listenTo(Calipso.vent, "layout:viewModel", function(itemModel) {
							var options = {
								model : itemModel,
								formSchemaKey : "view"
							};
							_this.showItemViewForModel(options);
						}, this);
						this.listenTo(Calipso.vent, "layout:createModel", function(options) {
							if (!options.formSchemaKey) {
								options.formSchemaKey = "create"
							}
							if (!options.model && options.modelType) {
								options.model = options.modelType.create();
							}
							_this.showItemViewForModel(options);
						}, this);
						//				this.listenTo(Calipso.vent, "layout:updateModel", function(itemModel) {
						//					_this.showItemViewForModel(itemModel, "update");
						//				}, this);
					}

				},
				onGenericFormSaved : function(model) {
					this.showContent(model);
				},
				onGenericFormSearched : function(model) {
					this.showContent(model);
				},
				onGenericShowContent : function(model) {
					this.showContent(model);
				},
				showItemViewForModel : function(options) {
					var itemModel = options.model;
					var formSchemaKey = itemModel ? itemModel.formSchemaKey : false;
					if (!formSchemaKey) {
						formSchemaKey = "view";
					}
					//  get item view type for model
					var ItemViewType = itemModel.getItemViewType();
					// console.log("ModelDrivenBrowseLayout on childView:openGridRowInTab, ItemViewType: " + ItemViewType.getTypeName());
					// create new item view instance with model
					var childView = new ItemViewType({
						formSchemaKey : formSchemaKey,
						model : itemModel
					});
					// show item view
					this.contentRegion.show(childView);
					var navUrl = itemModel.getPathFragment() + "/" + (itemModel.isNew() ? "new" : itemModel.get("id"));
					if (formSchemaKey != "view") {
						navUrl += "/" + formSchemaKey;
					}
					Calipso.navigate(navUrl, {
						trigger : false
					});
				},
				updateSearchLocation : function() {
					if (this.model && this.model.isSearchModel && this.model.isSearchModel()) {
						var searchedUrl = "" + this.model.getPathFragment();
						if (this.model.wrappedCollection && this.model.wrappedCollection.data) {
							searchedUrl = searchedUrl + "?" + $.param(this.model.wrappedCollection.data);
						}
						if (searchedUrl) {
							Calipso.navigate(searchedUrl, {
								trigger : false
							});
						}
					}
				},
				showContent : function(routeModel) {
					$(window).scrollTop(0);
					var _this = this;
					var isSearch = routeModel.isSearchModel();
					// get content view
					var singleResultType = !isSearch || (_this.config.skipToSingleResult && searchResultsCollection.length == 1);
					// get the model collection view type
					var ContentViewType;
					var contentView;
					if (isSearch) {
						contentView = this.getSearchResultsViewForModel(routeModel);
						// change location bar if appropriate
						this.updateSearchLocation()
					} else {
						ContentViewType = routeModel.getItemViewType();
						// create a new collection instance
						contentView = new ContentViewType({
							model : routeModel
						});

					}
					//TODO reuse active view if of the same type
					this.contentRegion.show(contentView);
					// change location bar if appropriate

				},
				getSearchResultsViewForModel : function(routeModel) {
					var _this = this;
					var searchResultsView;
					var ContentViewType;
					if (_this.config.skipToSingleResult && routeModel.wrappedCollection.length == 1) {
						var singleModel = routeModel.wrappedCollection.first();
						var ContentViewType = routeModel.getItemViewType();
						searchResultsView = new ContentViewType({
							model : singleModel
						});
					} else {
						ContentViewType = routeModel.getCollectionViewType();
						searchResultsView = new ContentViewType({
							model : routeModel,
							collection : routeModel.wrappedCollection
						});
						return searchResultsView;
					}

				},
				showFormForModel : function(routeModel, region, forceShow) {
					var _this = this;
					// create the search form view if not there
					if (forceShow || !region.hasView()) {
						var ContentViewType = routeModel.getItemViewType();
						var formView = new ContentViewType({
							model : routeModel
						});
						// show the search form
						region.show(formView);
					}

				},

			}, {
				// static members
				getTypeName : function() {
					return "ModelDrivenBrowseLayout";
				}
			});

			Calipso.view.ModelDrivenSearchLayout = Calipso.view.ModelDrivenBrowseLayout.extend(
			/** @lends Calipso.view.ModelDrivenSearchLayout.prototype */
			{
				tagName : 'div',
				id : "calipsoModelDrivenSearchLayout",
				template : Calipso.getTemplate('md-search-layout'),
				/**
				 * Get the default config. Overwrites {Calipso.view.MainLayout#getDefaultConfig}
				 */
				getDefaultConfig : function() {
					var defaultConfig = Calipso.view.ModelDrivenBrowseLayout.prototype.getDefaultConfig.apply(this);
					// now set this class' default config
					//			defaultConfig.hideSidebarOnSearched = false;
					//			defaultConfig.skipInitialResultsIfNoCriteria = true;
					return defaultConfig;
				},
				onGenericFormSearched : function(options) {
					var _this = this;
					_this.collapseSearchForm();
					_this.expandSearchResults();
					_this.showContent(_this.model);
				},
				initialize : function(options) {
					Calipso.view.ModelDrivenBrowseLayout.prototype.initialize.apply(this, arguments);
					var _this = this;

					if (this.options.searchResultsCollection) {
						this.searchResultsCollection = options.searchResultsCollection;
					} else {
						this.searchResultsCollection = this.model.wrappedCollection;
					}
				},
				regions : {
					sidebarRegion : "#calipsoModelDrivenSearchLayout-sideBarRegion",
					contentRegion : "#calipsoModelDrivenSearchLayout-contentRegion"
				},
				onShow : function() {
					var _this = this;
					var hasCriteria = this.searchResultsCollection && this.searchResultsCollection.hasCriteria();
					var skipDefaultSearch = this.model.skipDefaultSearch && !hasCriteria;

					this.showSidebar(this.model);
					if (skipDefaultSearch) {
						this.expandSearchForm();
					} else {
						this.expandSearchResults();
						this.showContent(this.model);
					}

				},
				expandSearchForm : function() {
					this.$el.find("#collapseOne").collapse('show');
				},
				collapseSearchForm : function() {
					this.$el.find("#collapseOne").collapse('hide');
				},
				expandSearchResults : function() {
					this.$el.find("#collapseTwo").collapse('show');
				},
				collapseSearchResults : function() {
					this.$el.find("#collapseTwo").collapse('hide');
				},
				showSidebar : function(routeModel) {
					this.showFormForModel(routeModel, this.sidebarRegion);

				},
			}, {
				getTypeName : function() {
					return "ModelDrivenSearchLayout";
				}
			});

			Calipso.view.ModelDrivenReportLayout = Calipso.view.ModelDrivenSearchLayout.extend(
			/** @lends Calipso.view.ModelDrivenReportLayout.prototype */
			{
				template : Calipso.getTemplate('md-report-layout'),
			}, {
				getTypeName : function() {
					return "Calipso.view.ModelDrivenReportLayout";
				}
			});
			///////////////////////////////////////////////////////
			// Views
			///////////////////////////////////////////////////////
			// plumbing for handlebars template helpers
			// Also provides i18n labels
			Marionette.View.prototype.mixinTemplateHelpers = function(target) {
				var self = this;
				var templateHelpers = Marionette.getOption(self, "templateHelpers");
				// add i18n labels from requirejs i18n
				var result = {
					labels : Calipso.util.getLabels()
				};
				target = target || {};

				if (_.isFunction(templateHelpers)) {
					templateHelpers = templateHelpers.call(self);
				}

				// This _.each block is what we're adding
				_.each(templateHelpers, function(helper, index) {
					if (_.isFunction(helper)) {
						result[index] = helper.call(self);
					} else {
						result[index] = helper;
					}
				});

				return _.extend(target, result);
			};
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
			Calipso.view.ItemView = Marionette.ItemView.extend(
			/** @lends Calipso.view.NotFoundView.prototype */
			{}, {
				getTypeName : function() {
					return "ItemView";
				}
			});

			Calipso.view.TemplateBasedItemView = Marionette.ItemView.extend(
			/** @lends Calipso.view.TemplateBasedItemView.prototype */
			{
				template : Calipso.getTemplate("templateBasedItemView"),//_.template('{{#if url}}<a href="{{url}}">{{/if}}{{#if name}}<h5>{{name}}</h5>{{else}}{{#if title}}<h5>{{title}}</h5>{{/if}}{{/if}}{{#if description}}{{description}}{{/if}}{{#if url}}</a>{{/if}}'),
				tagName : "li",
				initialize : function(models, options) {
					Marionette.ItemView.prototype.initialize.apply(this, arguments);
				},
				attributes : function() {
					return this.getOption("attributes");
				},
				getTemplate : function() {
					return this.getOption("template");
				},
			}, {
				getTypeName : function() {
					return "Calipso.view.TemplateBasedItemView";
				}
			});
			Calipso.view.TemplateBasedCollectionView = Backbone.Marionette.CompositeView.extend(
			/** @lends Calipso.view.TemplateBasedCollectionView.prototype */
			{
				//template : Calipso.getTemplate("templateBasedCollectionView"),//_.template('<div id="calipsoTemplateBasedCollectionLayout-collectionViewRegion"></div>'),
				tagName : "ul",
				attributes : {},
				template : _.template(''),
				childView : Calipso.view.TemplateBasedItemView,
				pollCollectionAfterDestroy : false,
				childViewOptions : {
					tagName : "li",
				},
				attributes : function() {
					return this.getOption("attributes");
				},
				getTemplate : function() {
					return this.getOption("template");
				},
				initialize : function(models, options) {
					Marionette.CompositeView.prototype.initialize.apply(this, arguments);
					options = options || {};
					if (!this.collection && options.model && options.model.isSearchable()) {
						this.collection = options.model.wrappedCollection;
						//console.log("TemplateBasedCollectionLayout#initialize, got options.model.wrappedCollection: " + this.collection + ", url: " + this.collection.url);
					}
					//console.log("TemplateBasedCollectionView#initialize, collection: " + this.collection);
				},
				/*
				onShow : function() {
					var _self = this;
					var show = true;
					// poll collection?
					if (this.collection.getTypeName && this.collection.getTypeName() == "Calipso.collection.PollingCollection") {
						if (this.options.pollOptions) {
							// Specify custom options for the plugin.
							// You can also call this function inside the collection's initialize function and pass the
							// options for the plugin when instantiating a new collection.
							this.collection.configure(this.options.pollOptions);
						}
						// initialize polling if needed
						if (!this.collection.isFetching()) {
							this.collection.startFetching();
						}
					}
					// fetch collection?
					else if (this.options.forceFetch) {
						show = false;
						//console.log("TemplateBasedCollectionView#onShow,  size: " + this.collection.length);
						_self.collection.fetch({
							url : _self.collection.url,
							success : function(collection, response, options) {
								console.log("TemplateBasedCollectionView#onShow#renderCollectionItems,  size: " + collection.length);
								//Backbone.Marionette.CompositeView.prototype.onShow.apply(_self);
							},
							error : function(collection, response, options) {
								alert("failed fetching collection");
							}
						});
					}
					if(show) {
						Backbone.Marionette.CompositeView.prototype.onShow.apply(this);
					}

				},*/
				/**
				 * Stop polling the collection if appropriate
				 */
				onBeforeDestroy : function() {
					if (!this.pollCollectionAfterDestroy) {
						if (this.collection.getTypeName && this.collection.getTypeName() == "Calipso.collection.PollingCollection") {
							//console.log("TemplateBasedCollectionView#onBeforeDestroy, stop polling for collection URL: " + this.collection.url);
							this.collection.stopFetching();
							this.collection.reset();
							this.collection = null;
						}
					}
				}
			/** use the template defined by the child if any
			buildChildView: function(child, ChildViewClass, childViewOptions){
				  var options = _.extend({}, childViewOptions);
				  options.model = child;
				("buildChildView, childViewOptions.template: "+childViewOptions.template);
				  if(child.childViewTemplate){
					  options.template = child.childViewTemplate;
				  }
				  return new ChildViewClass(options);
				}*/
			}, {
				getTypeName : function() {
					return "Calipso.view.TemplateBasedCollectionView";
				}
			});

			Calipso.view.TabLayout = Calipso.view.AbstractLayout.extend({
				template : Calipso.getTemplate('tabbed-layout'),
				tabClass : "nav nav-tabs",
				idProperty : "id",
				showOnselect : false,
				buttonTextProperty : "name",
				events : {
					"click a[data-toggle=\"tab\"]" : "showTabContent"
				},
				regions : {
					tabLabelsRegion : '.region-nav-tabs',
					tabContentsRegion : '.region-tab-content'
				},
				initialize : function(options) {
					Calipso.view.AbstractLayout.prototype.initialize.apply(this, arguments);
					this.mergeOptions(options);
				},
				/**
				 * Redraws the selected tab content when
				 * options.showOnselect is true
				 */
				showTabContent : function(e) {
					if (this.options.showOnselect) {
						var $link = $(e.currentTarget);
						this.tabContentsRegion.show(new this.itemViewType({
							model : this.options.collection.at($link.data("collectionIndex"))
						}));
					}
				},
				onShow : function() {
					var _this = this;
					if (this.collection.length > 0) {
						for (var i = 0; i < this.collection.length; i++) {
							var modelItem = this.collection.at(i);
							modelItem.set("tabActive", i == 0 ? true : false);
							modelItem.set("collectionIndex", i);
						}
					}

					var buttonTextProperty = this.getOption("buttonTextProperty");
					var idProperty = this.getOption("idProperty");
					var TabButtonItemView = Calipso.view.TemplateBasedItemView.extend({
						template : _.template('<a href="#tab<%= ' + idProperty + ' %>" ' + ' <% if (tabActive != undefined && tabActive){ %> class="active" <% } %>' + 'aria-controls="tab<%= ' + idProperty + ' %>" role="tab" ' + 'data-toggle="tab" data-collection-index="<%=collectionIndex%>"><%= ' + buttonTextProperty + ' %></a>'),
						tagName : "li",

						attributes : function() {
							// Return model data
							return {
								role : "presentation",
								class : this.model.get("tabActive") ? " active" : "",
							};
						}
					});
					var TabButtonsCollectionView = Calipso.view.TemplateBasedCollectionView.extend({
						tagName : "ul",
						className : _this.getOption("tabClass"),
						attributes : {
							role : "tablist"
						},
						childView : TabButtonItemView
					});
					this.tabLabelsRegion.show(new TabButtonsCollectionView({
						collection : this.collection
					}));

					var BaseItemViewType = _this.collection.model.getItemViewType() || Calipso.view.TemplateBasedItemView;
					_this.itemViewType = BaseItemViewType.extend({
						tagName : "div",
						template : _this.collection.model.getItemViewTemplate(),
						attributes : function() {
							// Return model data
							return {
								id : "tab" + this.model.get(idProperty),
								role : "tabpanel",
								class : "tab-pane" + (this.model.get("tabActive") ? " active" : ""),

							};
						}
					});

					var tabPanelsView;
					if (_this.options.showOnselect) {
						tabPanelsView = new _this.itemViewType({
							model : _this.options.collection.at(0)
						});
					} else {
						var TabPanelsCollectionView = Calipso.view.TemplateBasedCollectionView.extend({
							tagName : "div",
							template : _.template(''),
							className : "tab-content",
							childView : ItemViewType,
						});
						tabPanelsView = new TabPanelsCollectionView({
							collection : this.collection
						});
					}
					this.tabContentsRegion.show(tabPanelsView);

				},
			},
			// static members
			{
				typeName : "TabLayout",
			});
			Calipso.view.NotFoundView = Marionette.ItemView.extend(
			/** @lends Calipso.view.NotFoundView.prototype */
			{
				className : 'container span8 home',
				template : Calipso.getTemplate('notfound')
			}, {
				getTypeName : function() {
					return "NotFoundView";
				}
			});
			Calipso.view.FooterView = Marionette.ItemView.extend(
			/** @lends Calipso.view.FooterView.prototype */
			{
				className : "container",
				template : Calipso.getTemplate('footer'),
				className : "col-sm-12"
			}, {
				getTypeName : function() {
					return "FooterView";
				}
			});

			Calipso.view.StackView = Marionette.View.extend(
			/** @lends Calipso.view.StackView.prototype */
			{

				hasRootView : false,

				// Define options for transitioning views in and out
				defaults : {
					inTransitionClass : 'slideInFromRight',
					outTransitionClass : 'slideOutToRight',
					animationClass : 'animated',
					transitionDelay : 1000,
					'class' : 'stacks',
					itemClass : 'stack-item'
				},

				initialize : function(options) {
					this.views = [];
					options = options || {};
					this.options = _.defaults({}, this.defaults, options);
				},

				setRootView : function(view) {
					this.hasRootView = true;
					this.views.push(view);
					view.render();
					view.$el.addClass(this.options.itemClass);
					this.$el.append(view.$el);
				},

				render : function() {
					this.$el.addClass(this.options['class']);
					return this;
				},

				// Pop the top-most view off of the stack.
				pop : function() {
					var self = this;
					if (this.views.length > (this.hasRootView ? 1 : 0)) {
						var view = this.views.pop();
						this.transitionViewOut(view);
					}
				},

				// Push a new view onto the stack.
				// The itemClass will be auto-added to the parent element.
				push : function(view) {
					this.views.push(view);
					view.render();
					view.$el.addClass(this.options.itemClass);
					this.transitionViewIn(view);
					//(this.views);
				},

				// Transition the new view in.
				// This is broken out as a method for convenient overriding of
				// the default transition behavior. If you only want to change the
				// animation use the trasition class options instead.
				transitionViewIn : function(view) {
					//console.log('in', this.options);
					this.trigger('before:transitionIn', this, view);
					view.$el.addClass('hiddenToRight');
					this.$el.append(view.$el);

					// Wait a brief moment so it triggers the css transactions
					// If we don't delay, at least in my minimal testing, Chrome
					// does not animate the content but instead snaps-to-position.
					_.delay(function() {
						view.$el.addClass(this.options.animationClass);
						view.$el.addClass(this.options.inTransitionClass);
						_.delay(function() {
							view.$el.removeClass('hiddenToRight');
							this.trigger('transitionIn', this, view);
						}.bind(this), this.options.transitionDelay);
					}.bind(this), 1);
				},

				// Trastition a view out.
				// This is broken out as a method for convenient overriding of
				// the default transition behavior. If you only want to change the
				// animation use the trasition class options instead.
				transitionViewOut : function(view) {
					this.trigger('before:transitionOut', this, view);
					view.$el.addClass(this.options.outTransitionClass);
					_.delay(function() {
						view.destroy();
						this.trigger('transitionOut', this, view);
						//console.log(this.views);
					}.bind(this), this.options.transitionDelay);
				}

			});

			Calipso.view.ModelDrivenCollectionView = Marionette.ItemView.extend(
			/**
			 * @param options object members:
			 *  - collection/wrappedCollection
			 *  - callCollectionFetch: whether to fetch the collection from the server
			 * @lends Calipso.view.ModelDrivenCollectionView.prototype
			 */
			{

			},
			// static members
			{
				getTypeName : function() {
					return "ModelDrivenCollectionView";
				}
			});

			Calipso.view.ModelDrivenCollectionGridView = Marionette.ItemView.extend(
			/**
			 * @param options object members:
			 *  - collection/wrappedCollection
			 *  - callCollectionFetch: whether to fetch the collection from the server
			 * @lends Calipso.view.ModelDrivenCollectionGridView.prototype
			 * */
			{
				// Define view template
				template : Calipso.getTemplate('md-collection-grid-view'),
				events : {
					"click button.btn-windowcontrols-destroy" : "back"
				},
				collection : null,
				backgrid : null,
				back : function(e) {
					Calipso.stopEvent(e);
					window.history.back();
				},
				initialize : function(options) {
					//console.log("ModelDrivenCollectionGridView.initialize, options: " + options);
					Marionette.ItemView.prototype.initialize.apply(this, arguments);
					if (options.collection) {
						this.collection = options.collection;
					} else if (options.model && options.model.wrappedCollection) {
						this.collection = options.model.wrappedCollection;
					}
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
				getGrid : function(gridOptions) {
					return new Backgrid.Grid(gridOptions);
				},
				onShow : function() {
					var _self = this;
					// in case of a report we need a grid schema key
					//console.log("ModelDrivenCollectionGridView.onShow,  _self.collection.url: " + _self.collection.url);
					var gridSchema = _self.model.getGridSchema();
					//console.log("ModelDrivenCollectionGridView.onShow,  _self.model.getGridSchema: ");
					//console.log(_self.model.getGridSchema());

					//console.log("ModelDrivenCollectionGridView.onShow, collection: " + _self.collection);

					//console.log("ModelDrivenCollectionGridView.onShow, collection.data: " + _self.collection.data);
					//console.log(_self.collection.data);

					this.backgrid = this.getGrid({
						className : "backgrid responsive-table",
						columns : gridSchema,
						row : Calipso.components.backgrid.SmartHighlightRow,
						collection : _self.collection,
						emptyText : "No records found"
					});

					this.$('.backgrid-table-container').append(this.backgrid.render().$el);
					_self.listenTo(_self.collection, "backgrid:refresh", _self.showFixedHeader);
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

					this.$('.backgrid-paginator-container').append(paginator.render().el);
					//						console.log("ModelDrivenCollectionGridView.onShow, collection url: "+);
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
						_self.showFixedHeader();
					}
					// this.collection.fetch();main info

					// console.log("ModelDrivenCollectionGridView showed");

				},
				showFixedHeader : function() {
					var fixedHeaderContainer = this.$el.find(".backgrid-fixed-header");
					if (fixedHeaderContainer.length > 0) {
						var tableOrig = this.$el.find(".backgrid").first();
						var table_clone = tableOrig.clone().removeClass('backgrid').empty();
						var origThead = this.$el.find(".backgrid > thead");

						table_clone.append(origThead.clone()).addClass('header_fixed backgrid_clone').css('left', tableOrig.offset().left);

						fixedHeaderContainer.empty().append(table_clone);

						tableOrig.find('tbody tr').first().children().each(function(i, e) {
							$(table_clone.find('tr').children()[i]).width($(e).width());
						});
						origThead.hide();
						table_clone.removeAttr('style');
						//console.log("ModelDrivenCollectionGridView showFixedHeader, added header");
					} else {
						//console.log("ModelDrivenCollectionGridView showFixedHeader, skipped adding header");
					}

					//tableOrig.find('thead').first().hide();
				}

			},
			// static members
			{
				getTypeName : function() {
					return "ModelDrivenCollectionGridView";
				}
			});

			Calipso.view.ModelDrivenReportView = Calipso.view.ModelDrivenCollectionGridView.extend({
				tagName : "div",
				// Define view template
				template : Calipso.getTemplate('md-report-view'),
				chartOptions : {
					responsive : true,
					maintainAspectRatio : false,
					//		    bezierCurveTension : 0.7,
					//bezierCurve: false
					///Boolean - Whether grid lines are shown across the chart
					scaleShowGridLines : true,

					//String - Colour of the grid lines
					scaleGridLineColor : "rgba(0,0,0,.05)",

					//Number - Width of the grid lines
					scaleGridLineWidth : 1,

					//Boolean - Whether to show horizontal lines (except X axis)
					scaleShowHorizontalLines : true,

					//Boolean - Whether to show vertical lines (except Y axis)
					scaleShowVerticalLines : true,

					// Boolean - Whether to show labels on the scale
					scaleShowLabels : true,

					pointDotRadius : 3,
					//Number - Pixel width of point dot stroke
					pointDotStrokeWidth : 2,
					datasetStrokeWidth : 2,
					// Interpolated JS string - can access value
					scaleLabel : "<%=value%>",
					multiTooltipTemplate : "<%= datasetLabel %> - <%= value %>",
					//String - A legend template
					legendTemplate : "<ul class=\"list-inline\"><% for (var i=0; i<segments.length; i++){%><li class=\"list-group-item\"><span style=\"color:<%=segments[i].fillColor%> \"><i class=\"fa fa-bookmark\"></i>&nbsp;</span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>"

				},
				colors : [ "91, 144, 191", "163, 190, 140", "171, 121, 103", "208, 135, 112", "180, 142, 173", "235, 203, 139", "39, 165, 218", //"#5DA5DA" , // blue
				"250, 164, 58", //"#FAA43A" , // orange
				"96, 189, 104", //"#60BD68" , // green
				"241, 124, 176", //"#F17CB0" , // pink
				"178, 145, 47", //"#B2912F" , // brown
				"178, 118, 178", //"#B276B2" , // purple
				"222, 207, 63", //"#DECF3F" , // yellow
				"241, 88, 84", //"#F15854" , // red
				"77, 77, 77", //"#4D4D4D" , // gray
				"0, 0, 0", //"#000000" , // black
				],
				initialize : function(options) {
					//console.log("ReportView.initialize, options: " + options);
					Calipso.view.ModelDrivenCollectionGridView.prototype.initialize.apply(this, arguments);
					var self = this;
					if (options && options.chartOptions) {
						_.extend(this.chartOptions, options.chartOptions);
					}
					//this.bindTo(this.model, "change", this.modelChanged);
				},
				getGrid : function(gridOptions) {
					gridOptions.columnsToPin = 1;
					gridOptions.minScreenSize = 5000;
					gridOptions.className = "backgrid";
					//console.log("building responsive grid...");
					return new Backgrid.Extension.ResponsiveGrid(gridOptions);
				},
				onGridRendered : function() {
					this.backgrid.setSwitchable({});
				},
				onShow : function() {
					Calipso.view.ModelDrivenCollectionGridView.prototype.onShow.apply(this);

					var model = this.model, _this = this;
					// Get the context of the canvas element we want to select
					var canvas = this.$(".chart")[0];
					var $canvas = $(canvas);
					//			$canvas.attr("width", $canvas.parent().attr("width"));
					//			$canvas.attr("height", $canvas.parent().attr("height"));
					var ctx = canvas.getContext("2d");
					var chartData = this.getDataForAttribute(this.model.get("kpi"));
					var dataTotals = [];
					_.each(chartData.datasets, function(dataset, index) {

						//console.log("Calipso.view.ModelDrivenReportView#onShow each:chartData, dataset: ");
						//console.log(dataset);
						var color = _this.colors[index];
						dataset.fillColor = "rgba(" + color + ",0.02)";
						dataset.strokeColor = "rgba(" + color + ",0.8)";
						dataset.pointColor = "rgba(" + color + ",0.8)";
						dataset.pointStrokeColor = "rgba(" + color + ",0)";
						dataset.pointHighlightFill = "#fff";
						dataset.pointHighlightStroke = "rgba(" + color + ",04)";

						// add totals dataset
						var sum = 0;
						for (var i = 0; i < dataset.data.length; i++) {
							sum += parseFloat(dataset.data[i]);
						}
						dataTotals.push({
							label : dataset.label,
							value : sum % 1 === 0 ? sum : parseFloat(sum.toFixed(2)),
							color : "rgba(" + color + ",0.8)",
							highlight : "rgba(" + color + ",0.4)",
						});
					});

					this.chart = new Chart(ctx).Line(chartData, this.chartOptions);

					var canvasTotals = this.$(".chart-totals")[0];
					var $canvasTotals = $(canvasTotals);
					//			$canvasTotals.attr("width", $canvasTotals.parent().attr("width"));
					//			$canvasTotals.attr("height", $canvasTotals.parent().attr("height"));
					var ctxTotals = canvasTotals.getContext("2d");

					this.chartTotals = new Chart(ctxTotals).PolarArea(dataTotals, this.chartOptions);

					//then you just need to generate the legend
					var legend = this.chartTotals.generateLegend();
					this.$('.chart-legend').append(legend);

					//			this.chart.onclick = function(value, category) {
					//				self.trigger("click", category);
					//			};
				},
				modelChanged : function() {
					//			if (this.chart && this.seriesSource()) {
					//				this.chart.redraw();
					//			}
				},
				getDataForAttribute : function(entryAttribute) {
					var data = {};
					var reportDataSets = this.model.wrappedCollection;
					//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, datasets: ");
					//console.log(reportDataSets);

					// add dataset labels
					var labels = [];
					var entries = reportDataSets.first().get("entries");
					for (var i = 0; i < entries.length; i++) {
						labels.push(entries[i].label);
					}
					data.labels = labels;
					//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, data labels: ");
					//console.log(data.labels);

					// add datasets
					var datasets = [];

					reportDataSets.each(function(child, index) {
						// the new dataset...
						//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, child: ");
						//console.log(child);
						var dataset = {};

						// ... it's label...
						dataset.label = child.get("label");

						// ... and it's data entries
						var dataSetData = [];
						for (var i = 0; i < child.get("entries").length; i++) {
							//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, child.entries: ");
							dataSetData.push(child.get("entries")[i].entryData[entryAttribute]);
						}
						dataset.data = dataSetData;
						//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, dataset.data: ");
						//console.log(dataset.data);

						// add the dataset
						datasets.push(dataset);
					}, reportDataSets);

					// add the datasets to the returned data
					data.datasets = datasets;

					// return the data
					//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, returns data: ");
					//console.log(data);
					return data;

				},

			});

			Calipso.view.MainContentNavView = Marionette.ItemView.extend({

				// Define view template
				template : Calipso.getTemplate('MainContentNavView'),

				initialize : function(options) {
					Marionette.ItemView.prototype.initialize.apply(this, arguments);
				},
				onDomRefresh : function() {
					//console.log("MainContentTabsView onDomRefresh");
				}

			}, {
				getTypeName : function() {
					return "MainContentNavView";
				}
			});

			Calipso.view.GenericView = Marionette.ItemView.extend({
				// Define view template
				tagName : 'div',
				className : "calipsoView",
				events : {
					"click button.destroy" : "back"
				},

				back : function(e) {
					Calipso.stopEvent(e);
					window.history.back();
				},
				// dynamically set the id
				initialize : function(options) {
					var _this = this;
					Marionette.ItemView.prototype.initialize.apply(this, arguments);
					this.$el.prop("id", "tab-" + this.model.get("id"));
					var childViewTemplate = this.model.getItemViewTemplate();
					if (childViewTemplate) {
						this.tmpl = childViewTemplate;
					}

					this.formTemplate = this.options.formTemplate ? this.options.formTemplate : Backbone.Form.template;
					if (!_this.model.isNew() && options.forceFetch) {
						var modelUrl = Calipso.getBaseUrl() + "/api/rest" + "/" + this.model.getPathFragment() + "/" + this.model.get("id");
						//console.log("GenericView#initialize, fetching model " + modelUrl);
						this.model.fetch({
							async : false,
							url : modelUrl
						});
					}
				},
				formSchemaKey : "view",
			}, {
				// static members
				getTypeName : function() {
					return "GenericView";
				}
			});

			// Model Driven Form View
			Calipso.view.GenericFormView = Marionette.ItemView
					.extend(
							{
								/**
								 * Cals the static method of the same name. Returns a Backbone.Form template
								 * @param  {[String]} the template key, usually one of {"horizontal", "inline", "vertical"}
								 * @return {[type]} the compiled template
								 */
								getFormTemplate : function(templateKey) {
									templateKey || (templateKey = this.formTemplateKey);
									return this.constructor.getFormTemplate(this, templateKey);
								},
								formTemplateKey : "horizontal",
								modal : false,
								hasDraft : false,
								addToCollection : null,
								// Define view template
								formSchemaKey : null,
								formTitle : "options.formTitle",
								template : Calipso.getTemplate('md-form-view'),
								templateHelpers : {
									formSchemaKey : function() {
										return this.formSchemaKey;
									},
									formTitle : function() {
										var title = Calipso.getObjectProperty(this.model, "label", "");
										return title;
									},
								},

								initialize : function(options) {
									Marionette.ItemView.prototype.initialize.apply(this, arguments);

									if (options.modal) {
										this.modal = options.modal;
									}

									if (options.addToCollection) {
										this.addToCollection = options.addToCollection;
									}

									if (options.model) {
										this.model = options.model;
									}
									if (!this.model) {
										throw "GenericFormView: a 'model' option is required";
									}
									// set schema key, from options or model
									this.formSchemaKey = options.formSchemaKey;
									if (!this.formSchemaKey) {
										this.formSchemaKey = this.model.getFormSchemaKey();
									}
									//console.log("GenericFormView#initialize, formSchemaKey: " + this.formSchemaKey);
									if (options.formTemplateKey) {
										this.formTemplateKey = options.formTemplateKey;
									} else if (this.model.getFormTemplateKey()) {
										this.formTemplateKey = this.model.getFormTemplateKey();
									}
									// use vertical form for searches
									else if (this.formSchemaKey.slice(0, 6) == "search") {
										this.formTemplateKey = "vertical";
									}

									// grab a handle for the search results collection if any, from options or model
									if (this.options.searchResultsCollection) {
										this.searchResultsCollection = options.searchResultsCollection;
									} else if (this.model.wrappedCollection) {
										this.searchResultsCollection = this.model.wrappedCollection;
									}
									//
									if (options.formTemplate) {
										this.formTemplate = options.formTemplate;
									} else {
										this.formTemplate = this.getFormTemplate(options.formTemplateKey ? options.formTemplateKey : this.formTemplateKey);
									}

								},
								events : {
									"click a.btn-social-login" : "socialLogin",
									"click a.open-modal-page" : "openModalPage",
									"click button.submit" : "commit",
									"submit form" : "commit",
									"click button.cancel" : "cancel",
									"submit" : "commitOnEnter",
									"keypress input[type=password]" : "commitOnEnter",
									"keypress input[type=text]" : "commitOnEnter"
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
										if (_this.formSchemaKey.indexOf("create") == 0 || _this.formSchemaKey.indexOf("update") == 0) {
											// persist changes

											_this.model.save(null, {
												success : function(model, response) {
													sessionStorage.removeItem(_this.getDraftKey());
													_this.hasDraft = false;
													if (_this.addToCollection) {
														_this.addToCollection.add(_this.model);
														_this.model.trigger("added");
													}
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
									var formSchema = _self.model.getFormSchema(_self.formSchemaKey);

									// TODO: add a property in generic model to flag view behavior (i.e. get add http:.../form-schema to the model before rendering)
									if (formSchema && _.size(formSchema) > 0) {
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

									if (formSchema) {
										formSchema = _self.model.getFormSchema(_self.formSchemaKey);
									}
									var formSubmitButton = _self.model.getFormSubmitButton ? _self.model.getFormSubmitButton() : false;
									if (!formSubmitButton) {
										if (_self.formSchemaKey.indexOf("search") == 0) {
											formSubmitButton = "<i class=\"glyphicon glyphicon-search\"></i>&nbsp;Search";
										} else if (_self.formSchemaKey.indexOf("create") == 0 || _self.formSchemaKey.indexOf("update") == 0) {
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
										template : _self.getFormTemplate(_self.model.getFormTemplateKey())
									};
									// model driven submit button?
									if (formSubmitButton) {
										formOptions.submitButton = formSubmitButton;
									}
									this.form = new Calipso.components.backboneform.Form(formOptions);
									this.form.setElement(this.$el.find(".generic-form-view").first()).render();
									this.$el.find('label').filter(':visible:enabled:first').focus();
									this.onFormRendered();

									// flag changed
									this.listenTo(this.form, 'change', function() {
										_self.hasDraft = true;
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
							},
							// static members
							{
								getTypeName : function() {
									return "GenericFormView";
								},
								formTemplates : {
									horizontal : _.template('\
		    <form class="form-horizontal" role="form">\
		      <div data-fieldsets></div>\
		      <% if (submitButton) { %>\
		        <button type="submit" class="btn"><%= submitButton %></button>\
		      <% } %>\
		    </form>\
		  '),
									nav : _.template('\
					<nav class="navbar navbar-default">\
					<form autocomplete=\"off\" class="navbar-form navbar-left" role="form">\
					<span data-fields="*"></span>\
					<% if (submitButton) { %>\
					<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
					<% } %>\
					</form>\
					</nav>\
			'),
									inline : _.template('\
					<form autocomplete=\"off\" class="form-inline" role="form">\
					<span data-fields="*"></span>\
					<% if (submitButton) { %>\
					<div class="form-group"><button type="submit" class="submit btn btn-primary"><%= submitButton %></button></div>\
					<% } %>\
					</form>\
			'),
									vertical : _.template('\
					<form autocomplete=\"off\" role="form">\
					<div data-fieldsets></div>\
					<% if (submitButton) { %>\
					<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
					<% } %>\
					</form>\
			'),
									auth : _
											.template('\
					<form autocomplete=\"off\" role="form">\
					<div data-fieldsets></div>\
					<% if (submitButton) { %>\
					<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
					<span class="pull-right">\
						<% if (submitButton.indexOf("Reg") == -1) { %>\
					   <small>Need an account?</small>\
					   <a title="Click to register" class="btn btn-success" href="/register">Register</a>\
						<% } %>\
					   <small>or sign-in with</small>\
					    <div role="group" class="btn-group">\
					        <a class="btn btn-default btn-social-login btn-social-login-facebook">\
					            <i class="fa fa-facebook-f"></i><!-- &#160;facebook -->\
					        </a>\
					        <a class="btn btn-default btn-social-login btn-social-login-linkedin">\
					            <i class="fa fa-linkedin"></i><!-- &#160;linkedin  -->\
					        </a>\
					        <!--a class="btn btn-default btn-social-login btn-social-login-twitter">\
					            <i class="fa fa-twitter"></i><!-- &#160;twitter -->\
					        <a class="btn btn-default btn-social-login btn-social-login-google">\
					            <i class="fa fa-google-plus"></i><!-- &#160;google+ -->\
					        </a>\
					    </div>\
					</span>\
					<% } %>\
					</form>\
			'),

								},
								/**
								 * Returns a Backbone.Form template
								 * @param  {[Calipso.view.GenericFormView]} the form view instance
								 * @param  {[String]} the template key, usually one of {"horizontal", "inline", "vertical"}
								 * @return {[type]} the compiled template
								 */
								getFormTemplate : function(instance, templateKey) {
									templateKey = templateKey ? templateKey : "horizontal";
									return this.formTemplates[templateKey];
								}
							});

			Calipso.view.GenericFormPanelView = Calipso.view.GenericFormView.extend({
				template : Calipso.getTemplate('md-formpanel-view'),
			}, {});
			Calipso.view.ReportFormView = Calipso.view.GenericFormView.extend({
				renderForm : function() {
					Calipso.view.GenericFormView.prototype.renderForm.apply(this, arguments);
					var _this = this;
					// switch period mode
					this.form.on('timeUnit:change', function(form, timeUnitEditor) {
						var timeUnit = timeUnitEditor.getValue();
						var viewMode = timeUnit == "DAY" ? "months" : "years";
						var format = timeUnit == "DAY" ? 'MM/YYYY' : 'YYYY';
						form.fields.period.editor.callDataFunction("viewMode", viewMode);
						form.fields.period.editor.callDataFunction("format", format);
					});
				}
			}, {
				getTypeName : function() {
					return "Calipso.view.ReportFormView";
				}
			});

			Calipso.view.AbstractItemView = Backbone.Marionette.ItemView.extend({

				initialize : function(options) {

					if (!options || !options.id) {
						this.id = _.uniqueId(this.getTypeName() + "_");
						$(this.el).attr('id', this.id);
					}
					Marionette.ItemView.prototype.initialize.apply(this, arguments);
				},
				//				render : function() {
				//					$(this.el).attr('id', Marionette.getOption(this, "id"));
				//				},
				templateHelpers : {
					viewId : function() {
						return Marionette.getOption(this, "id");
					}
				},
				getTypeName : function() {
					return this.constructor.getTypeName();
				},
				//override toString to return something more meaningful
				toString : function() {
					return this.getTypeName() + "(" + JSON.stringify(this.attributes) + ")";
				}
			}, {
				getTypeName : function() {
					return "AbstractItemView";
				}
			});

			Calipso.view.MainLayout.getTypeName = function() {
				return "MainLayout";
			};

			Calipso.view.AppLayout = Calipso.view.MainLayout.extend({
				tagName : "div",
				template : Calipso.getTemplate('applayout'),// _.template(templates.applayout),
				regions : {
					navRegion : "#calipsoAppLayoutNavRegion",
					contentRegion : "#calipsoAppLayoutContentRegion"
				}
			}, {
				getTypeName : function() {
					return "AppLayout";
				}
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
				}
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
			});

			Calipso.view.UserDetailsLayout = Calipso.view.ModelDrivenBrowseLayout.extend(
			/** @lends Calipso.view.UserDetailsLayout.prototype */
			{
				template : Calipso.getTemplate('userDetails-layout'),
				initialize : function(options) {
					Calipso.view.ModelDrivenBrowseLayout.prototype.initialize.apply(this, arguments);
					// //console.log("Calipso.view.UserRegistrationLayout#initialize");
				},
				regions : {
					contentRegion : "#calipsoModelDrivenBrowseLayout-contentRegion",
					forgotPasswordRegion : "#calipsoUserDetailsLayout-forgotPasswordRegion",
				},
				onShow : function() {
					Calipso.view.ModelDrivenBrowseLayout.prototype.onShow.apply(this, arguments);
					// add forgotten password form
					var changePwUserDetails = new Calipso.model.UserDetailsModel({
						formSchemaKey : "update-createToken",
						isResetPasswordReguest : true,
						email : this.model.get("email")
					});
					var ViewType = changePwUserDetails.getItemViewType();
					this.forgotPasswordRegion.show(new ViewType({
						model : changePwUserDetails,
					}));
					if (this.model.get("showResetPasswordForm")) {
						$("#loginCollapse1").collapse('hide');
						$("#loginCollapse2").collapse('show');
					}
				},
				onGenericFormSaved : function(model) {
					// model is not neccessarily the same as this.model
					if (model.get("isResetPasswordReguest")) {
						Calipso.navigate("/page/userRegistrationSubmitted", {
							trigger : true
						});
					} else {
						this.handleUserDetails(model);
					}
				},
				handleUserDetails : function(model) {
					// if user details model is valid attach to session and FW to home
					if (this.model.get("id")) {
						Calipso.vent.trigger('session:created', this.model);
					}
					// login failed, show error
					else if (!this.model.get("email") && !this.model.get("username")) {
						window.alert("Invalid credentials!");
					} else {
						console.log("handleUserDetails: doing nothing");
					}
				}
			}, {
				// static members
				getTypeName : function() {
					return "Calipso.view.UserDetailsLayout";
				}
			});

			Calipso.view.WizardLayout = Calipso.view.ModelDrivenBrowseLayout.extend({
				className : "container configurable-fluid",
				template : Calipso.getTemplate('wizard-layout'),
				events : {
					"click a.wizard-step" : "browseToStep"
				},
				regions : {
					stepRegion : ".wizard-step",
					navRegion : ".wizard-title"
				},
				getRequiredOptionNames : function() {
					return [ "steps" ];
				},
				showNav : function() {
					var _layout = this;
					// render navigation
					var NavCollectionView = Calipso.view.TemplateBasedCollectionView.extend({
						tagName : "ul",
						className : "dropdown-menu",
						childView : Calipso.view.TemplateBasedItemView.extend({
							initialize : function(options) {
								Calipso.view.TemplateBasedItemView.prototype.initialize.apply(this, arguments);
								var model = this.model;
								model.set("colIndex", _layout.stepsCollection.indexOf(model));
								model.set("shownIndex", model.get("colIndex") + 1);
								model.set("completed", model.get("colIndex") <= _layout.model.get("highestStepIndex"));
								model.set("accessible", model.get("colIndex") <= _layout.model.get("highestStepIndex") + 1);
								model.set("active", model.get("colIndex") == _layout.model.get("currentStepIndex"));

								// add bootstrap classes
								if (!this.model.get("accessible")) {
									this.$el.addClass("disabled");
								} else if (this.model.get("active")) {
									this.$el.addClass("active");
								}
							},
							onShow : function(options) {
								/*this.$el.find('a[title]').tooltip({
								 trigger: 'hover',
								 placement: 'bottom',
								 animate: true,
								 delay: 500,
								 container: 'body'
								});*/
							},
							onBeforeDestroy : function() {
								//this.$el.find('a[title]').tooltip('destroy');
							}
						}),
						childViewOptions : {
							tagName : "li",
							template : Calipso.getTemplate("wizardTabItem")
						}
					});
					this.navRegion.show(new NavCollectionView({
						collection : this.stepsCollection
					}));

					this.$el.find(".wizard-title").prepend('<button type="button" class="btn btn-default dropdown-toggle" ' + ' data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' + Calipso.util.getLabels("calipso.words.step") + " " + (this.model.get("currentStepIndex") + 1) + " / " + this.stepsCollection.length + ' <span class="caret"></span></button>');

				},
				onShow : function() {
					var _layout = this;
					this.stepsCollection = new Calipso.collection.GenericCollection(_layout.config.steps, {
						model : Calipso.model["GenericModel"]
					});
					// render child view
					this.showStep(parseInt(this.model.get("currentStepIndex") + "") + 1);
				},
				getStepModel : function(step) {
					var model = step.model;
					if (!model && step.modelPath) {
						if (step.modelPath) {
							model = Calipso.getPathValue(this.model, step.modelPath);
							if (_.isUndefined(model)) {
								throw ("Calipso.view.WizardLayout#getStepModel: no value found for modelPath: " + step.modelPath);
							}
						}
					}
					if (!model && step.modelType) {
						model = new step.modelType();
						if (step.modelPath) {
							Calipso.setPathValue(this.model, step.modelPath, model);
						}
						if (step.parentModelPath) {
							Calipso.setPathValue(model, step.parentModelPath, this.model);
						}
					}
					if (!model) {
						model = this.model;
					}
					if (!model) {
						throw "Could not resolve or create step view model";
					}
					step.model = model;
					return model;
				},
				browseToStep : function(e) {
					var $link = $(e.currentTarget)
					this.showStep(parseInt($link.data("step")));
				},
				showStep : function(stepIndex) {
					var step = this.config.steps[stepIndex];
					if (!step) {
						throw "No wizard step defined for key: " + stepIndex;
					}
					var view = step.view;
					if (!view) {
						var viewOptions = step.viewOptions ? step.viewOptions : {};
						viewOptions.model = this.getStepModel(step);
						var ViewType = step.viewType ? step.viewType : viewOptions.model.getItemViewType();
						view = new ViewType(viewOptions);
					}
					// render child view
					var stepTitle = step.title ? step.title : ("Step " + (stepIndex + 1));
					this.$el.find(".step-title").empty().append(stepTitle);
					this.currentStepIndex = stepIndex;
					this.model.set("currentStepIndex", stepIndex);
					this.stepRegion.show(view);
					$('html, body').animate({
						scrollTop : 0
					}, 500);

					// render nav/progress view
					this.showNav();
				},

				onGenericFormSaved : function(model) {
					this.showStep(parseInt(this.model.get("currentStepIndex") + "") + 1);
				},
			}, {
				getTypeName : function() {
					return "Calipso.view.WizardLayout";
				}
			});
			//////////////////////////////////////////////////////
			// user Registration: model, layout etc. Uses the
			// userRegistrations path fragment/route.
			//////////////////////////////////////////////////////

			Calipso.view.UserRegistrationLayout = Calipso.view.ModelDrivenBrowseLayout.extend(
			/** @lends Calipso.view.UserRegistrationLayout.prototype */
			{

				template : Calipso.getTemplate('userRegistration-layout'),
				initialize : function(options) {
					Calipso.view.ModelDrivenBrowseLayout.prototype.initialize.apply(this, arguments);
				},
				onGenericFormSaved : function(model) {

					Calipso.navigate("/page/userRegistrationSubmitted", {
						trigger : true
					});
				},
			/*
			onGenericFormSaved : function(model) {
				// if the user is active just navigate to login
				// TODO: add message
				if (model.get("active") == true) {
					// console.log("Calipso.view.UserRegistrationLayout#onGenericFormSaved, user is active, saving to session");

					Calipso.navigate("loginRegistered", {
						trigger : true
					});
				} else {
					var usernameOrEmail = model.get("email");
					if (!usernameOrEmail) {
						usernameOrEmail = model.get("username");
					}

					Calipso.navigate("changePasswordWithToken/" + usernameOrEmail, {
						trigger : true
					});

				}
			},
			*/
			}, {
				// static members
				getTypeName : function() {
					return "UserRegistrationLayout";
				}
			});
			Calipso.view.UserProfileView = Calipso.view.TemplateBasedItemView.extend(
			/** @lends Calipso.view.UserProfileView.prototype */
			{
				tagName : "div",
				template : Calipso.getTemplate("userProfile"),
			}, {
				getTypeName : function() {
					return "Calipso.view.UserProfileView";
				}
			});
			Calipso.view.UserProfileLayout = Calipso.view.ModelDrivenBrowseLayout.extend(
			/** @lends Calipso.view.UserProfileLayout.prototype */
			{
				initialize : function(options) {
					Calipso.view.ModelDrivenBrowseLayout.prototype.initialize.apply(this, arguments);
					// console.log("Calipso.view.UserProfileLayout#initialize");
				},
			}, {
				// static members
				getTypeName : function() {
					return "UserProfileLayout";
				}
			});

		});
