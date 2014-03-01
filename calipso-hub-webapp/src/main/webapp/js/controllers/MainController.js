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
	GenericCollectionGridView = require('view/generic-collection-grid-view'),
	GenericFormView = require('view/GenericFormView'),
	GenericCollection = require('collection/generic-collection'),
	LoginModel = require('model/LoginModel'),
	HostModel = require('model/host');


//	vent.on("layout:rendered", function() {
//		console.log('layout:rendered (MainController)');
//	});

	var MainController = Marionette.Controller.extend({
		constructor: function(options){

			Marionette.Controller.prototype.constructor.call(this, options);
//			console.log('initialize');
			// _initializeLayout();
			this.layout.on("show", function() {
				vent.trigger("layout:rendered");
			});

			vent.trigger('app:show', this.layout);
		},
		layout : new AppLayoutView({
			model : session
		}),
		home : function() {

//			console.log('MainController home called');
			if (!session.isAuthenticated()) {
				Backbone.history.navigate("client/login", {
					trigger : true
				});
				return false;
			}

			var homeView = new HomeView();

			_initializeLayout();

			MainController.layout.content.show(homeView);
		},

		login : function() {

			var loginModel = new LoginModel({
				email : session.get('email'),
				issuer : session.get('issuer')
			});

			var view = new LoginView({
				model : loginModel
			});

			view.on('app:login', MainController.authenticate);

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
			this.layout.content.show(new NotFoundView());
		},
		mainNavigationRoute : function(mainNavTabName, genericViewTab) {
			if(!genericViewTab){
				genericViewTab = "results";
			}
//			console.log("main, mainNavTabName: " + mainNavTabName);

			if (typeof this[mainNavTabName] == 'function') {
				// render explicit route
				this[mainNavTabName]();
			} 
			else{
				// render generic model driven view
				var viewRoute = "/api/rest/" + mainNavTabName + "/";
				var modelDependency = 'model/'+mainNavTabName.slice(0, -1);
				var _self = this;
				console.log("mainNavTabName: " + mainNavTabName + ", genericViewTab: " + genericViewTab);
				if (genericViewTab == "results"){
//					console.log("rendering results");
					// this.modelsMap[mainNavTabName]) {

					// dynamically load the model matching the tab name
					// and feed it to the generic view. The view will
					// access the model schemas and do it's thing
					require([modelDependency], function(ModelClass) {
						var viewCollection = new GenericCollection([], {
							modelClass : ModelClass,
							url : CalipsoApp.getCalipsoAppBaseUrl() + viewRoute
						});
						var navigationView = new GenericCollectionGridView({
							collection : viewCollection
						});
						// render view
						_self.layout.mainContentNavRegion.show(new MainContentNavView());
						_self.layout.content.show(navigationView);
		        });
				}
				else{
					console.log("mainNavigationRoute, rendering generic form view, view model dependency: " + modelDependency);
					require([modelDependency], function(ModelClass) {
						var emptyModel = new ModelClass();
						console.log("mainNavigationRoute, ModelClass: "+ModelClass);
						var formView = new GenericFormView({
							model: emptyModel,
							// select the appropriate schema for create/update/search etc.
							schemaAction: genericViewTab
						});
						//var formView = new GenericFormView({model: new HostModel()});
						// render edit/new/search view based on backbone forms
						_self.layout.mainContentNavRegion.show(new MainContentNavView());
						_self.layout.content.show(formView);
					});
				}
				// update active nav menu tab
				$('.navbar-nav li.active').removeClass('active');
				$('#mainNavigationTab-' + mainNavTabName).addClass('active');
			}
		} 

		

	});
	return MainController;
});
	