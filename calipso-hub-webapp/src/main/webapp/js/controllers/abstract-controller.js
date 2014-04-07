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
	vent = require('vent'),
	session = require('session'),
	//vent = require('vent'),
	AppLayoutView = require('view/AppLayoutView'),
	HomeView = require('view/home-view'),
	NotFoundView = require('view/NotFoundView'),
	LoginView = require('view/LoginView'),
	MainContentNavView = require('view/MainContentNavView'),
	TabLayout = require('view/md-crud-layout'),
	GenericFormView = require('view/GenericFormView'),
	GenericCollection = require('collection/generic-collection'),
	GenericModel = require('model/generic-model'),
	LoginModel = require('model/LoginModel'),
	ModelDrivenBrowseLayout = require('view/md-browse-layout');


	var AbstractController = Marionette.Controller.extend({
		constructor: function(options){
			console.log("AbstractController#constructor");
			Marionette.Controller.prototype.constructor.call(this, options);
			this.layout = new AppLayoutView({
				model : session
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
			this.layout.contentRegion.show(new HomeLayout());
		},

		login : function() {
			var UserModel = require('model/user');
			var loginModel = new UserModel({
				email : session.get('email'),
				issuer : session.get('issuer')
			});

			var view = new LoginView({
				model : loginModel
			});

			view.on('app:login', AbstractController.authenticate);
			console.log("AbstractController#login, showing login view");
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
			session.destroy();
			login();
		},
		notFoundRoute : function(path) {
//			console.log("notFoundRoute, path: "+path);
			this.layout.contentRegion.show(new NotFoundView());
		},
		/**
		 * Get a model representing the current request.
		 * 
		 * For an example, consider the URL [api-root]/users/[some-id]. First, 
		 * a model class is loaded based on the URL fragment representing the type, 
		 * e.g. "users" for UserModel.
		 * 
		 * A model instance is then created using some-id if provided or "search" otherwise. If 
		 * a backbone supermodel instance is already cached, it is reused.
		 * 
		 *  In case of "search" a collection of the given model type is initialized but, 
		 *  similarly to the model instance, it is not fetched from the server.
		 *  
		 * @param {string} modelTypeKey the URL fragment representing the model type key, e.g. "users" for UserModel
		 * @param {string} modelId the model identifier. The identifier may be either a primary or business key, 
		 * depending on your server side implementation. The default property name in client side models is "name".
		 * You can override {@linkcode GenericModel.prototype.getBusinessKey} to define another property name.
		 * @see (@link GenericModel.prototype.getBusinessKey}
		 */
		getModelForRoute : function(modelTypeKey, modelId){
			var modelForRoute;
			var ModelType = require("model/" + _.singularize( modelTypeKey ));
			if(!ModelType){
				throw "No matching model type was found for key: " + modelTypeKey;
			}
			if(modelId && modelId.toLowerCase() != "search"){
				// try cached models first
				modelForRoute = ModelType.all().get(modelId);
				// otherwise create a transient instance and let the view load it from the server  
				modelForRoute = ModelType.create(modelId);
			}
			else{
				// create a model to use as a wrapper for a collection of instances of the same type
				modelForRoute = ModelType.create("search");
				modelForRoute.wrappedCollection = new GenericCollection([], {
					model : ModelType,
					url : session.getBaseUrl() + "/api/rest/" +  modelForRoute.getPathFragment()
				});
			}
			console.log("AbstractController#getModelForRoute, collection URL: " + session.getBaseUrl() + "/api/rest/" +  modelForRoute.getPathFragment());
			return modelForRoute;
			
		},
		mainNavigationCrudRoute : function(mainRoutePart, contentNavTabName) {
			// build the model instancde representing the current request
			var modelForRoute = this.getModelForRoute(mainRoutePart, contentNavTabName);
			
			// get the layout type corresponding to the requested model
			var RequestedModelLayoutType = modelForRoute.getLayoutViewType();
			
			// show the layout 
			// TODO: reuse layout if of the same type
			var routeLayout = new RequestedModelLayoutType({model: modelForRoute});
			this.layout.contentRegion.show(routeLayout);
			
			// update page header tabs etc.
			this.syncMainNavigationState(modelForRoute);

		},

		/*
		 * TODO

		initCrudLayout : function(routeHelper){
			if((!this.layout) || this.layout.getTypeName() != "AppLayoutView"){
		      // console.log("AbstractController#initCrudLayout, calling this.ensureActiveLayout()");
				this.ensureActiveLayout();
			}
			else{
		      // console.log("AbstractController#initCrudLayout, not updating this.layout: "+this.layout.getTypeName());
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

         vent.on("itemView:openGridRowInTab", function(itemModel) {
         	vent.trigger("openGridRowInTab", itemModel);
         });
         vent.on("openGridRowInTab", function(itemModel) {
         	console.log("openGridRowInTab");
         	_self.tabs.add(itemModel);
         	vent.trigger("viewTab", itemModel);
         });
         vent.on("viewTab", function(itemModel) {
         	this.layout.contentRegion.show(new itemmodel.itemView(itemmodel));
//       	 	Backbone.history.navigate("client/"+_self.lastMainNavTabName+"/"+itemModel.get("id"), {
//					trigger : false
//				});
       	 	_self.syncMainNavigationState(null, itemModel.get("id"));
       	});
         
			this.layout.contentRegion.show(tabLayout);
		},
		*/
		syncMainNavigationState : function(modelForRoute) {
			var mainRoutePart = modelForRoute.getPathFragment(), contentNavTabName = modelForRoute.get("id");
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
				$('#md-crud-layout-tab-label-' + contentNavTabName).addClass('active');
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
	