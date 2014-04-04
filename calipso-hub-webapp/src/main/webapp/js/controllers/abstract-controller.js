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
			this.userDetails = session.userDetails;
			Marionette.Controller.prototype.constructor.call(this, options);
			this._initializeLayout(options);

		},
		_initializeLayout : function(options){
			// console.log('AbstractController#_initializeLayout');
			if(options && options.layout){
				this.layout = options.layout;
			}
			else{
				this.layout = new AppLayoutView({
					model : session
				});
			}

			console.log('AbstractController#_initializeLayout, this.layout: ' + ((this.layout && this.layout.typeName) ? this.layout.typeName : this.layout));
			this.layout.on("show", function() {
				vent.trigger("layout:rendered");
			});

			vent.trigger('app:show', this.layout);
		},
		home : function() {
			console.log("AbstractController#home");
			if (!session.isAuthenticated()) {
				Backbone.history.navigate("client/login", {
					trigger : true
				});
				return false;
			}
			//var homeView = new HomeView();
			this._initializeLayout({layout: new GenericHomeLayout({
				model : session
			})});
		},

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
			// console.log('MainController authenticate called');
			var self = this;
			var email = this.$('input[name="email"]').val();
			var password = this.$('input[name="password"]').val();

			$.when(this.model.authenticate(email, password)).then(function(model, response, options) {
				session.save(model);
				session.load();
				// console.log('MainController authenticate navigating to home');
				Backbone.history.navigate("client/home", {
					trigger : true
				});
			}, function(model, xhr, options) {
				self.$el.find('.alert').show();
			});
		},

		logout : function() {

			app.vent.trigger('session:destroy', model);
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
			routeHelper.contentNavTabName = contentNavTabName ? contentNavTabName : "Search";
			
			// console.log("AbstractController#buildRouteHelper, contentNavTabName: "+ routeHelper.contentNavTabName);
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
			routeHelper.routeModel = new GenericCollectionWrapperModel(wrapperModelOptions);
			return routeHelper;
		},
		mainNavigationCrudRoute : function(mainRoutePart, contentNavTabName) {
			
			this.tryExplicitRoute(mainRoutePart, contentNavTabName);
			var routeHelper = this.buildRouteHelper(mainRoutePart, contentNavTabName);

			if (!CalipsoApp.userDetails) {
				CalipsoApp.fw = "/client/"+routeHelper.mainRoutePart+"/"+routeHelper.contentNavTabName;
				Backbone.history.navigate("client/login", {
					trigger : true
				});

				$('#session-info').hide();
				return false;
			}
			var _self = this;
			// console.log("AbstractController#mainNavigationCrudRoute, mainRoutePart: " + routeHelper.mainRoutePart + ", contentNavTabName: " + routeHelper.contentNavTabName + ", mainAreaChange: " + routeHelper.mainAreaChange);
			if(!this.tabs || routeHelper.mainAreaChange){
				this.initCrudLayout(routeHelper);
			}
			// add tab for entity if needed
			if(routeHelper.contentNavTabName != "Search"){
				if(!this.tabKeys[routeHelper.contentNavTabName]){
					var showModel = routeHelper.modelClass.all().get(routeHelper.contentNavTabName);
					if(showModel){
						// tab keys index updated automatically
						// console.log("adding new tab for existing model: " + showModel.get("id"));
						_self.tabs.add(showModel);
					}
					else{
						showModel = routeHelper.modelClass.create({id:routeHelper.contentNavTabName});
						showModel.fetch().then(function(){
							// console.log("adding new tab for fetched model: " + showModel.get("id"));
							_self.tabs.add(showModel);
						});
					}
				}
				else{
					// console.log("showing existing tab");
				}
			}

			this.syncMainNavigationState(routeHelper.mainRoutePart, routeHelper.contentNavTabName);

		},
		initCrudLayout : function(routeHelper){
			if((!this.layout) || this.layout.className != "AppLayoutView"){
		      // console.log("AbstractController#initCrudLayout, calling this._initializeLayout()");
				this._initializeLayout();
			}
			else{
		      // console.log("AbstractController#initCrudLayout, not updating this.layout: "+this.layout.className);
			}
			var _self = this;
			
			var TabModel      = Backbone.Model.extend();
			var TabCollection = Backbone.Collection.extend({ 
				model: GenericModel,
			   initialize: function () {
			        // console.log("AbstractController#initCrudLayout, TabCollection initializing");
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
		syncMainNavigationState : function(mainRoutePart, contentNavTabName) {
			console.log("AbstractController#syncMainNavigationState, mainRoutePart: " + mainRoutePart + ", contentNavTabName: "+contentNavTabName);
		// update active nav menu tab
			if(mainRoutePart && mainRoutePart != this.lastMainNavTabName){
				$('.navbar-nav li.active').removeClass('active');
				$('#mainNavigationTab-' + mainRoutePart).addClass('active');
				this.lastMainNavTabName = mainRoutePart;
			}
			// update active content tab
			if(contentNavTabName && contentNavTabName != this.lastContentNavTabName){
				$('#calipsoTabLabelsRegion li.active').removeClass('active');
				$('#generic-crud-layout-tab-label-' + contentNavTabName).addClass('active');
				// show coressponding content
				// console.log("show tab: "+contentNavTabName);
				$('#calipsoTabContentsRegion .tab-pane').removeClass('active');
				$('#calipsoTabContentsRegion .tab-pane').addClass('hidden');
				$('#tab-' + contentNavTabName).removeClass('hidden');
				$('#tab-' + contentNavTabName).addClass('active');
				this.lastContentNavTabName = contentNavTabName;
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
	