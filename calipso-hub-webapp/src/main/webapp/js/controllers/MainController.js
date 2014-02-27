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
	LoginView = require('model/LoginModel'),
	GenericCollectionGridView = require('view/generic-collection-grid-view'),
	GenericCollection = require('collection/generic-collection'),
	HostModel = require('model/host'),
	TextModel = require('model/text'),
	UserModel = require('model/user');


	vent.on("layout:rendered", function() {
		console.log('layout:rendered (MainController)');
	});

	var MainController = Marionette.Controller.extend({
		constructor: function(options){
	      
	      Marionette.Controller.prototype.constructor.call(this, options);
	      console.log('initialize');
			//_initializeLayout();
			this.layout.on("show", function() {
		      vent.trigger("layout:rendered");
		    });

		    vent.trigger('app:show', this.layout);
	    },
		layout : new AppLayoutView({
			model : session
		}),
		modelsMap : {
			'hosts' : HostModel,
			'texts' : TextModel,
			'users' : UserModel,
		},
		home : function() {

			console.log('MainController home called');
			if (!session.isAuthenticated()) {
				Backbone.history.navigate("login", {
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
			console.log('MainController authenticate called');
			var self = this;
			var email = this.$('input[name="email"]').val();
			var password = this.$('input[name="password"]').val();

			$.when(this.model.authenticate(email, password)).then(function(model, response, options) {
				session.save(model);
				session.load();
				console.log('MainController authenticate navigating to home');
				Backbone.history.navigate("home", {
					trigger : true
				});
			}, function(model, xhr, options) {
				self.$el.find('.alert').show();
			});
		},

		logout : function() {
			session.destroy();
			Backbone.history.navigate("login", {
				trigger : true
			});
		},

		mainNavigationRoute : function(mainNavigationTab) {
			console.log("main, mainNavigationTab: " + mainNavigationTab);
			
			if (typeof this[mainNavigationTab] == 'function') {
				// proceed with actual route
				this[mainNavigationTab]();
				return;
			} else if (this.modelsMap[mainNavigationTab]) {
				contentRegionView = this.genericMainNavigationView(mainNavigationTab);
			} else {
				window.alert("No route found!");
			}

			console.log("mainNavigationRoute, contentRegionView: " + contentRegionView);
			this.layout.content.show(contentRegionView);
			console.log("mainNavigationRoute, callinf view onDomRefresh... ");
			contentRegionView.onDomRefresh();
			// update nav menu .selected
			vent.trigger("nav-menu:change", mainNavigationTab);
			

		},
		genericMainNavigationView : function(mainNavigationTab, entityKey) {
			var navigationView;
			var viewModel = this.modelsMap[mainNavigationTab];
			console.log("genericMainNavigationView, viewModel: " + viewModel);
			var viewRoute = "/api/rest/" + mainNavigationTab + "/";
			console.log("genericMainNavigationView, viewRoute: " + viewRoute);
			// is a specific entity requested?
			// if(entityKey){
			//	        		
			// }
			// else{
			// no specific entity requested,
			// go for collection view
			var viewCollection = new GenericCollection([], {
				model : viewModel,
				url : CalipsoApp.getCalipsoAppBaseUrl() + viewRoute
			});
			navigationView = new GenericCollectionGridView({
				collection : viewCollection
			});
			// }

			console.log("genericMainNavigationView, navigationView: " + navigationView);
			return navigationView;
		},

	});
	//);

	return MainController;
});