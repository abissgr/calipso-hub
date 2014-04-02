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
	CalipsoApp = require('app'),
	session = require('session'),
	vent = require('vent'),
	AppLayoutView = require('view/AppLayoutView'),
	HomeView = require('view/home-view'),
	NotFoundView = require('view/NotFoundView'),
	LoginView = require('view/LoginView'),
	MainContentNavView = require('view/MainContentNavView'),
	TabLayout = require('view/generic-crud-layout'),
	GenericCollectionGridView = require('view/generic-collection-grid-view'),
	GenericHomeLayout = require('view/generic-home-layout'),
	GenericFormView = require('view/GenericFormView'),
	GenericCollection = require('collection/generic-collection'),
	GenericModel = require('model/generic-model'),
	GenericCollectionWrapperModel = require('model/generic-collection-wrapper-model'),
	HomeModel = require('model/home'),
	LoginModel = require('model/LoginModel'),
	UserModel = require('model/user'),
	HostModel = require('model/host');


	var AbstractController = Marionette.Controller.extend({
		constructor: function(options){

			Marionette.Controller.prototype.constructor.call(this, options);
			if(options && options.layout){
				this.layout = options.layout;
			}
			else{
				this.layout = new AppLayoutView({
					model : session
				});
			}
			this.layout.on("show", function() {
				vent.trigger("layout:rendered");
			});

			vent.trigger('app:show', this.layout);

		},
//		home : function() {
//
//			if (!session.isAuthenticated()) {
//				Backbone.history.navigate("client/login", {
//					trigger : true
//				});
//				return false;
//			}
//			var homeView = new HomeView();
//			_initializeLayout();
//			MainController.layout.contentRegion.show(homeView);
//		},

		login : function() {

			var loginModel = new LoginModel({
				email : session.get('email'),
				issuer : session.get('issuer')
			});

			var view = new LoginView({
				model : loginModel
			});

			view.on('app:login', AbstractController.authenticate);

			vent.trigger('app:show', view);
		},

		authenticate : function(args) {
//			console.log('MainController authenticate called');
			var self = this;
			var email = this.$('input[name="email"]').val();
			var password = this.$('input[name="password"]').val();

			$.when(this.model.authenticate(email, password)).then(function(model, response, options) {
				session.save(model);
				session.load();
//				console.log('MainController authenticate navigating to home');
				Backbone.history.navigate("client/home", {
					trigger : true
				});
			}, function(model, xhr, options) {
				self.$el.find('.alert').show();
			});
		},

		logout : function() {
			session.destroy();
			Backbone.history.navigate("client/login", {
				trigger : true
			});
		},
		notFoundRoute : function(path) {
//			console.log("notFoundRoute, path: "+path);
			this.layout.contentRegion.show(new NotFoundView());
		},
		tabKeys: {},
		buildRouteHelper: function(mainRoutePart, contentNavTabName){
			var routeHelper = {};
			routeHelper.mainAreaChange = (mainRoutePart != this.lastMainNavTabName);
			// get main route part
			if(!mainRoutePart){
				if(this.lastMainNavTabName){
					// go previous route if one exists
					routeHelper.mainRoutePart = this.lastMainNavTabName;
				}
				else{
					// go to home if no previous route exists
					routeHelper.mainRoutePart = "homes";
				}
			}
			else{
				routeHelper.mainRoutePart = mainRoutePart;
			}
			// get secondary route part
			if(!contentNavTabName){
				routeHelper.contentNavTabName = "Search";
			}
			// get route model class
			routeHelper.modelClass = require("model/" + _.singularize( routeHelper.mainRoutePart ));

			console.log("AbstractController#buildRouteHelper, loaded model class: "+routeHelper.modelClass.className);
			var searchModelClass = routeHelper.modelClass.searchModel ?  routeHelper.modelClass.searchModel : routeHelper.modelClass
			// get route collection if applicable
			// a client side model might be an alias for another server model
			console.log("AbstractController#buildRouteHelper, updating this.searchResults");
			this.searchResults = new GenericCollection([], {
				model : routeHelper.modelClass,
				url : CalipsoApp.getCalipsoAppBaseUrl() + "/api/rest/" +  searchModelClass.apiUrlSegment
			});
			var wrapperModelOptions = {
				modelClass : routeHelper.modelClass,
				wrappedCollection : this.searchResults
			};
			if(routeHelper.mainRoutePart == "homes"){
				wrapperModelOptions.itemView = HomeView;
			}
			routeHelper.routeModel = new GenericCollectionWrapperModel(wrapperModelOptions);
			return routeHelper;
		},
		mainNavigationCrudRoute : function(mainRoutePart, contentNavTabName) {

			this.tryExplicitRoute(mainRoutePart, contentNavTabName);
			var routeHelper = this.buildRouteHelper(mainRoutePart, contentNavTabName);
			
			var _self = this;
			console.log("AbstractController#mainNavigationCrudRoute, mainRoutePart: " + routeHelper.mainRoutePart + ", contentNavTabName: " + routeHelper.contentNavTabName);
			if(!this.tabs || routeHelper.mainAreaChange){
				this.initCrudLayout(routeHelper);
			}
			// add tab for entity if needed
			if(routeHelper.contentNavTabName != "Search"){
				if(!this.tabKeys[routeHelper.contentNavTabName]){
					console.log("adding new tab");
					// tab keys index updated automatically
					this.tabs.add(routeHelper.modelClass.all().get(routeHelper.contentNavTabName));
				}
				else{
					console.log("showing existing tab");
				}
			}

			this.syncMainNavigationState(routeHelper);

		},
		initCrudLayout : function(routeHelper){
			var _self = this;
			// update grid collection
			
			console.log("AbstractController#mainNavigationCrudRoute, searchResultsModel.get(name): "+
					routeHelper.routeModel.get("name"));
			var TabModel      = Backbone.Model.extend();
			var TabCollection = Backbone.Collection.extend({ 
				model: GenericModel,
			   initialize: function () {
			        console.log("AbstractController#mainNavigationCrudRoute, TabCollection initializing");
			        this.bind('add', this.onModelAdded, this);
			        this.bind('remove', this.onModelRemoved, this);
			    }, 
			    onModelAdded: function(model, collection, options) {
			        _self.tabKeys[model.get("id")] = model;
			    },
			    onModelRemoved: function (model, collection, options) {
			   	 _self.tabKeys[model.get("id")] = null;
			    },
			});
			this.tabs = new TabCollection([
            (routeHelper.routeModel)      
         ]);
         var tabLayout = new TabLayout({collection: this.tabs});

         vent.on("openGridRowInTab", function(itemModel) {
         	_self.tabs.add(itemModel);
         	CalipsoApp.vent.trigger("viewTab", itemModel);
         });
         vent.on("viewTab", function(itemModel) {
       	 	Backbone.history.navigate("client/"+_self.lastMainNavTabName+"/"+itemModel.get("id"), {
					trigger : false
				});
       	 	_self.syncMainNavigationState(null, itemModel.get("id"));
       	});
         
			this.layout.contentRegion.show(tabLayout);
		},
		syncMainNavigationState : function(routeHelper) {
			console.log("AbstractController#syncMainNavigationState, mainRoutePart: " + routeHelper.mainRoutePart + ", contentNavTabName: "+routeHelper.contentNavTabName);
		// update active nav menu tab
			if(routeHelper.mainRoutePart && routeHelper.mainRoutePart != this.lastMainNavTabName){
				$('.navbar-nav li.active').removeClass('active');
				$('#mainNavigationTab-' + routeHelper.mainRoutePart).addClass('active');
				this.lastMainNavTabName = routeHelper.mainRoutePart;
			}
			// update active content tab
			if(routeHelper.contentNavTabName && routeHelper.contentNavTabName != this.lastContentNavTabName){
				$('#calipsoTabLabelsRegion li.active').removeClass('active');
				$('#generic-crud-layout-tab-label-' + routeHelper.contentNavTabName).addClass('active');
				// show coressponding content
				console.log("show tab: "+routeHelper.contentNavTabName);
				$('#calipsoTabContentsRegion .tab-pane').removeClass('active');
				$('#calipsoTabContentsRegion .tab-pane').addClass('hidden');
				$('#tab-' + routeHelper.contentNavTabName).removeClass('hidden');
				$('#tab-' + routeHelper.contentNavTabName).addClass('active');
				this.lastContentNavTabName = routeHelper.contentNavTabName;
			}
		},
		tryExplicitRoute : function(mainRoutePart, secondaryRoutePart){
			if (typeof this[mainRoutePart] == 'function') {
				// render explicit route
				this[mainRoutePart](secondaryRoutePart);
			} 
		},

		editItem : function(item) {
			console.log("MainController#editItem, item: "+item);
		}
		

	});
	return AbstractController;
});
	