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

	Calipso.util.RegionManager = Backbone.Marionette.RegionManager.extend({
		addRegion : function(regionName, selector) {
			Backbone.Marionette.RegionManager.prototype.addRegion.apply(this, arguments);
			this.briefRegion(regionName);
		},
		addRegions : function(regions) {
			Backbone.Marionette.RegionManager.prototype.addRegions.apply(this, arguments);
			var _this = this;
			var region;
			_.each(_.keys(regions), function(regionName) {
				_this.briefRegion(regionName);
			});
		},
		briefRegion : function(regionName) {
			var region = this.get(regionName);
			region.regionName = regionName;
			region.regionPath = this.getRegionPath() + "." + regionName;
		},
		getRegionPath : function() {
			throw "Method getRegionPath not implemented"
		}
	}, {});

	//////////////////////////////////////////////////
	// Layouts
	//////////////////////////////////////////////////

	Calipso.view.CalipsoLayout = Backbone.Marionette.LayoutView.extend({
		initialize : function(options) {
			Backbone.Marionette.LayoutView.prototype.initialize.apply(this, arguments);
		},
		getTypeName : function() {
			return this.constructor.getTypeName();
		}
	}, {
		typeName : "Calipso.view.CalipsoLayout",
		getTypeName : function() {
			return this.typeName;
		}
	});

	Calipso.view.HomeLayout = Calipso.view.CalipsoLayout.extend({
		template : Calipso.getTemplate('homeLayout'),
		onShow : function() {
			var _this = this;
			//console.log("HomeLayout#onShow");
		}
	},
	// static members
	{
		typeName : "HomeLayout",
	});

	Calipso.view.UseCaseLayout = Calipso.view.CalipsoLayout.extend({
		taName : "div",
		useCaseContext : null,
		// TODO:
		skipSrollToTop : false,
		// regionName : viewType
		regionViewTypes : {},
		viewEvents : {},
		initialize : function(options) {
			Calipso.view.CalipsoLayout.prototype.initialize.apply(this, arguments);
			if (!this.skipSrollToTop) {
				$(window).scrollTop(0);
			}
			this.useCaseContext = options.useCaseContext;
			this.childViewOptions = options.childViewOptions || {};
		},
		onShow : function() {
			var _this = this;
			//console.log(this.getTypeName() + ".onShow regionViewTypes: ");
			//console.log(this.regionViewTypes);
			_.each(this.regionViewTypes, function(ViewType, regionName, list) {
				//console.log(_this.getTypeName() + ".onShow showing region: " + regionName + ", view: " + ViewType.getTypeName());
				_this.showChildView(regionName, new ViewType(_.extend({
					model : _this.useCaseContext.model,
					useCaseContext : _this.useCaseContext
				}, _this.childViewOptions)));
			});
		},
		getRegionManager : function() {
			var _layout = this;
			// custom logic
			var RegionManager = Calipso.util.RegionManager.extend({
				getRegionPath : function() {
					return _layout.regionPath;
				}
			}, {

			});
			return new RegionManager();
		},
		showChildView : function(regionName, view) {
			var _this = this;
			view.useCaseContext == (view.useCaseContext ? view.useCaseContext : this.useCaseContext);
			view.regionName = regionName;
			view.regionPath = this.regionPath + "." + regionName;

			// bind to view events according to viewEvents hash
			_.each(this.viewEvents, function(method, eventName, list) {
				//console.log(_this.getTypeName() + " subscribing to view event: " + eventName);
				_this.listenTo(view, eventName, function(options) {
					// if method is own method name
					if (_.isString(method) && _this[method]) {
						_this[method](options);
					}
					// if method is a function
					else if (_.isFunction(method)) {
						method(options);
					}
				});
			});
			Backbone.Marionette.LayoutView.prototype.showChildView.apply(this, arguments);
		},
		onModelSync : function(args) {
			// execute next useCase by default
			this.nextUseCase();
		},
		nextUseCase : function() {
			//console.log(this.getTypeName() + ".nextUseCase, navigating to defaultNext: " + this.useCaseContext.defaultNext);
			// TODO: handle from (and reuse) layout
			if (this.useCaseContext.defaultNext) {
				Calipso.navigate('/' + this.model.getPathFragment() + '/' + this.useCaseContext.defaultNext, {
					trigger : true
				})
			} else {
				throw "Use case does not define a defaultNext";
			}
		}
	}, {
		typeName : "Calipso.view.UseCaseLayout",
	});

	Calipso.view.ModalLayout = Calipso.view.UseCaseLayout.extend({
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
			Calipso.view.UseCaseLayout.prototype.initialize.apply(this, arguments);
			var _this = this;
			if (options.childView) {
				this.childView = options.childView;
			}
		},
		onShow : function() {
			// render child view
			this.showChildView("modalBodyRegion", this.childView);
		},
		closeModal : function(e) {
			Calipso.stopEvent(e);
			Calipso.vent.trigger("modal:close");
		}

	}, {
		typeName : "Calipso.view.ModalLayout"
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

	Calipso.view.HeaderView = Calipso.view.CalipsoLayout.extend(
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
				this.showChildView("notificationsRegion", notificationsView);
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
			Calipso.navigate("userDetails/login", {
				trigger : true
			});
		}
	}, {
		typeName : "Calipso.view.HeaderView"
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
			labels : Calipso.util.getLabels(),
			useCase : self.useCaseContext,
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
	{
		initialize : function(models, options) {
			Marionette.ItemView.prototype.initialize.apply(this, arguments);
		},
		getTypeName : function() {
			return this.constructor.getTypeName();
		}
	}, {
		typeName : "Calipso.view.ItemView",
		getTypeName : function() {
			return this.typeName;
		}
	});

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
		},
		/** use the template defined by the child if any
		buildChildView: function(child, ChildViewClass, childViewOptions){
			  var options = _.extend({}, childViewOptions);
			  options.model = child;
			("buildChildView, childViewOptions.template: "+childViewOptions.template);
			  if(child.childViewTemplate){
				  options.template = child.childViewTemplate;
			  }
			  return new ChildViewClass(options);
			},*/
		getTypeName : function() {
			return this.constructor.getTypeName();
		}
	}, {
		typeName : "Calipso.view.TemplateBasedCollectionView",
		getTypeName : function() {
			return this.typeName;
		}
	});

	Calipso.view.TabLayout = Calipso.view.CalipsoLayout.extend({
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
			Calipso.view.CalipsoLayout.prototype.initialize.apply(this, arguments);
			this.mergeOptions(options);
		},
		/**
		 * Redraws the selected tab content when
		 * options.showOnselect is true
		 */
		showTabContent : function(e) {
			if (this.options.showOnselect) {
				var $link = $(e.currentTarget);
				this.showChildView("tabContentsRegion", new this.itemViewType({
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
			this.showChildView("tabLabelsRegion", new TabButtonsCollectionView({
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
			this.showChildView("tabContentsRegion", tabPanelsView);

		},
	},
	// static members
	{
		typeName : "Calipso.view.TabLayout",
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
			this.useCase = this.useCaseContext.getUseCase(this.regionName, this.schemaType);
			this.schema = this.buildSchema();
		},
		/** builds a UI-specific schema for the given fields */
		buildSchema : function(fields) {
			var _this = this, schemaType = this.schemaType, isArray = this.schemaType == "backgrid", schema = null;

			fields || (fields = this.useCase.fields);
			if (fields) {
				var schemaEntry;
				var baseSchemaEntry;
				var overrideSchemaEntry;

				schema = isArray ? [] : {};
				_.each(fields, function(field, key) {
					//console.log(_this.getTypeName() + "#buildSchema key: " + key + ", field: ");
					//console.log(field);
					baseSchemaEntry = Calipso.datatypes[field.datatype][schemaType];
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
							schemaEntry[_this.labelPropertyCopy] = key;
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
	 *  - collection/wrappedCollection
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
		initialize : function(options) {
			//console.log("ModelDrivenCollectionGridView.initialize, options: " + options);
			Calipso.view.UseCaseItemView.prototype.initialize.apply(this, arguments);

			this.collection = options.collection || options.model.wrappedCollection;
			if (!this.collection) {
				throw "no collection or collection wrapper model was provided";
			} else {
				console.log(this.getTypeName() + "initialize, collection");
				console.log(this.collection);
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
				className : "backgrid responsive-table",
				columns : _self.schema,
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
		// TODO: REMOVE. old Ie8 feature
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
			"click a.btn-social-login" : "socialLogin",
			"click a.open-modal-page" : "openModalPage",
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
			console.log("consstructor.getFormTemplate, templateKey: " + templateKey);
			return Calipso.util.formTemplates[templateKey];
		},
		getFieldTemplate : function(instance, templateKey) {
			templateKey = /*templateKey ? templateKey :*/"horizontal";
			console.log("consstructor.getFieldTemplate, templateKey: " + templateKey);
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
			Calipso.view.prototype.initialize.apply(this, arguments);
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

	Calipso.view.AppLayout = Calipso.view.CalipsoLayout.extend({
		tagName : "div",
		template : Calipso.getTemplate('applayout'),
		regions : {
			navRegion : "#calipsoAppLayoutNavRegion",
			contentRegion : "#calipsoAppLayoutContentRegion"
		}
	},
	// static members
	{
		typeName : "Calipso.view.AppLayout",
	});

	Calipso.view.BrowseLayout = Calipso.view.UseCaseLayout.extend({
		template : Calipso.getTemplate('md-browse-layout'),
		regions : {
			contentRegion : "#calipsoModelDrivenBrowseLayout-contentRegion"
		},
		regionViewTypes : {
			contentRegion : Calipso.view.GenericFormPanelView,
		},
	},
	// static members
	{
		typeName : "Calipso.view.BrowseLayout"
	});

	Calipso.view.SearchLayout = Calipso.view.UseCaseLayout.extend({
		template : Calipso.getTemplate('md-search-layout'),
		regions : {
			formRegion : "#calipsoModelDrivenSearchLayout-sideBarRegion",
			resultsRegion : "#calipsoModelDrivenSearchLayout-contentRegion"
		},
		regionViewTypes : {
			formRegion : Calipso.view.GenericFormView,
			resultsRegion : Calipso.view.ModelDrivenCollectionGridView
		},
	},
	// static members
	{
		typeName : "Calipso.view.SearchLayout"
	});

	Calipso.view.DefaulfModalLayout = Calipso.view.UseCaseLayout.extend({
		template : Calipso.getTemplate('modal-layout'),
		events : {
			"click a.modal-close" : "closeModal",
			"click button.modal-close" : "closeModal"
		},
		regions : {
			modalBodyRegion : ".modal-body"
		},
		onShow : function() {
			// render child view
			this.showChildView("modalBodyRegion", this.options.childView);
		},
		closeModal : function(e) {
			Calipso.stopEvent(e);
			Calipso.vent.trigger("modal:close");
		}
	},
	// static members
	{
		typeName : "Calipso.view.DefaulfModalLayout"
	});

	Calipso.view.UserDetailsLayout = Calipso.view.BrowseLayout.extend(
	/** @lends Calipso.view.UserDetailsLayout.prototype */
	{
		template : Calipso.getTemplate('userDetails-layout'),
		viewEvents : {
			"model:sync" : "onModelSync"
		},
		onModelSync : function(options) {
			// if successful login
			if (this.model.get("id")) {
				// TODO: add 'forward' HTTP/URL param in controller cases
				var fw = this.model.get(fw) || "/home";
				console.log(this.getTypeName() + "#onModelSync successful login, fw: " + fw);
				Calipso.navigate(fw, {
					trigger : true
				});
			}
			// else just follow useCase.defaultNext configuration
			else {
				Calipso.view.UseCaseLayout.prototype.onModelSync.apply(this, arguments);
			}
		},
	/*onModelSync : function(model, response, options) {
		Calipso.session.reset(model);
		// if successful login
		if(this.model.get("id")){
			console.log(this.getTypeName() + "#onModelSync successful login");
		}
		// else just follow useCase configuration
		else{
			console.log(this.getTypeName() + "#onModelSync call super.onModelSync to apply nextUseCase config");
				Calipso.view.UseCaseLayout.prototype.onModelSync.apply(this, arguments);
		}
	},*/

	/*regions : {
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
		this.showChildView("forgotPasswordRegion", new ViewType({
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
	}*/
	},
	// static members
	{
		typeName : "Calipso.view.UserDetailsLayout"
	});
});
