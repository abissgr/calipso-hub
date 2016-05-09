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
[ "lib/calipsolib/view-collection", 'underscore', 'handlebars', 'backbone', 'marionette', 'moment', 'backbone-forms', 'backgrid' ],
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



	Calipso.view.HomeLayout = Calipso.view.Layout.extend({
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

	Calipso.view.UseCaseLayout = Calipso.view.Layout.extend({
		taName : "div",
		useCaseContext : null,
		// TODO:
		skipSrollToTop : false,
		// regionName : viewType
		regionViewTypes : {},
		viewEvents : {},
		events : {
			"click  button.layout-showCreateFormModal" : "showCreateFormModal"
		},

		showCreateFormModal : function(e) {
			console.log("CshowCreateFormModal: " + this.model.constructor.getTypeName());
			Calipso.stopEvent(e);
			Calipso.vent.trigger("layout:createModel", {
				modelType : this.model.constructor
			});
		},
		initialize : function(options) {
			if(options && options.useCaseContext && options.useCaseContext.viewOptions){
				var viewOptions = options.useCaseContext.viewOptions;
				delete options.useCaseContext.viewOptions;
				_.extend(options, viewOptions);
			}
			Calipso.view.Layout.prototype.initialize.apply(this, arguments);
			if (!this.skipSrollToTop) {
				$(window).scrollTop(0);
			}
			this.useCaseContext = options.useCaseContext;
			this.childViewOptions = _.extend(
				this.useCaseContext.childViewOptions || {},
				options.childViewOptions || {}
			);
		},
		onShow : function() {
			var _this = this;
			//console.log(this.getTypeName() + ".onShow regionViewTypes: ");
			//console.log(this.regionViewTypes);
			_.each(this.regionViewTypes, function(ViewType, regionName, list) {

				_this.showChildView(regionName, new ViewType(
					_.extend(
						{
							model : _this.useCaseContext.model,
							useCaseContext : _this.useCaseContext
						},
						_this.childViewOptions)));
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

	Calipso.view.HeaderView = Calipso.view.Layout.extend(
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


	Calipso.view.TabLayout = Calipso.view.Layout.extend({
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
			Calipso.view.Layout.prototype.initialize.apply(this, arguments);
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

// TODO: remove? sum all related types to tabs file?
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

	Calipso.view.AppLayout = Calipso.view.Layout.extend({
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

	Calipso.view.UserProfileLayout = Calipso.view.BrowseLayout.extend({
		regionViewTypes : {
			contentRegion : Calipso.view.UserProfileView,
		},
	},
	// static members
	{
		typeName : "Calipso.view.UserProfileLayout"
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
				var fw = Calipso.app.fw || "/home";
				Calipso.app.fw = null;
				Calipso.navigate(fw, {
					trigger : true
				});
			}
			// else just follow useCase.defaultNext configuration
			else {
				Calipso.view.UseCaseLayout.prototype.onModelSync.apply(this, arguments);
			}
		},
	},
	// static members
	{
		typeName : "Calipso.view.UserDetailsLayout"
	});


			Calipso.view.UserRegistrationLayout = Calipso.view.BrowseLayout.extend(
			/** @lends Calipso.view.UserRegistrationLayout.prototype */
			{

				template : Calipso.getTemplate('userRegistration-layout'),
				viewEvents : {
					"model:sync" : "onModelSync"
				},
				initialize : function(options) {
					Calipso.view.BrowseLayout.prototype.initialize.apply(this, arguments);
				},
				onModelSync : function(options) {
					Calipso.navigate("/page/userRegistrationSubmitted", {
						trigger : true
					});
				},
			}, {
				// static members
				typeName : "UserRegistrationLayout"
			});
});
