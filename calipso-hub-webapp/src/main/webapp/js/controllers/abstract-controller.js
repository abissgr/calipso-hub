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
	HomeView = require('view/HomeView'),
	NotFoundView = require('view/NotFoundView'),
	LoginView = require('view/LoginView'),
	MainContentNavView = require('view/MainContentNavView'),
	TabLayout = require('view/generic-crud-layout'),
	GenericCollectionGridView = require('view/generic-collection-grid-view'),
	GenericFormView = require('view/GenericFormView'),
	GenericCollection = require('collection/generic-collection'),
	GenericModel = require('model/generic-model'),
	GenericCollectionWrapperModel = require('model/generic-collection-wrapper-model'),
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
		home : function() {

			if (!session.isAuthenticated()) {
				Backbone.history.navigate("client/login", {
					trigger : true
				});
				return false;
			}
			var homeView = new HomeView();
			_initializeLayout();
			MainController.layout.contentRegion.show(homeView);
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
		mainNavigationCrudRoute : function(mainRoutePart, contentNavTabName) {

			this.tryExplicitRoute(mainRoutePart, contentNavTabName);
			var mainAreaChange = (mainRoutePart != this.lastMainNavTabName);
			
			// note last main nav tab
			if(!mainRoutePart){
				mainRoutePart = this.lastMainNavTabName;
			}
			if(!contentNavTabName){
				contentNavTabName = "Search";
			}
			

			// render generic model driven view
			var ModelClass = require("model/" + _.singularize( mainRoutePart ));

			// a client side model might be an alias for another server model
			var collectionUrl = "/api/rest/" +  (ModelClass.prototype.serverModelKey ? ModelClass.prototype.serverModelKey : mainRoutePart);
			console.log("AbstractController#mainNavigationCrudRoute, model class: " + ModelClass.className + ", collectionUrl: "+collectionUrl);
			var _self = this;
			console.log("AbstractController#mainNavigationCrudRoute, mainRoutePart: " + mainRoutePart + ", contentNavTabName: " + contentNavTabName);
			if(!this.tabs || mainAreaChange){
				this.initCrudLayout(ModelClass, mainRoutePart, collectionUrl);
			}
			// add tab for entity if needed
			if(contentNavTabName != "Search"){
				if(!this.tabKeys[contentNavTabName]){
					console.log("adding new tab");
					// tab keys index updated automatically
					this.tabs.add(ModelClass.all().get(contentNavTabName));
				}
				else{
					console.log("showing existing tab");
				}
				
			}

			this.syncMainNavigationState(mainRoutePart, contentNavTabName);

		},
		initCrudLayout : function(ModelClass, mainRoutePart, collectionUrl){
			console.log("AbstractController#initCrudLayout, updating this.searchResults");
			var _self = this;
			// update grid collection
			this.searchResults = new GenericCollection([], {
				model : ModelClass,
				url : CalipsoApp.getCalipsoAppBaseUrl() + collectionUrl
			});
			// wrap in single model
			var searchResultsModel = new GenericCollectionWrapperModel({
				modelClass : ModelClass,
				wrappedCollection : this.searchResults
			});
			console.log("AbstractController#mainNavigationCrudRoute, searchResultsModel.get(name): "+
					searchResultsModel.get("name"));
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
            (searchResultsModel)      
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
				console.log("show tab: "+contentNavTabName);
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
	