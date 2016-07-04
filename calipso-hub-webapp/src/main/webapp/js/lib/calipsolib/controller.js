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
		[  "lib/calipsolib/uifield", "lib/calipsolib/view", 'underscore', 'backbone', 'marionette' ],
		function( Calipso, CalipsoView, _, Backbone, BackboneMarionette) {

	// //////////////////////////////////////
	// Controller
	// //////////////////////////////////////
	Calipso.Controller = Marionette.Controller.extend({
		constructor : function(options) {
			Marionette.Controller.prototype.constructor.call(this, options);
		},
		showView : function(view){
			Calipso.app.mainContentRegion.show(view);
		},
		toHome : function() {
			Calipso.navigate("home", {
				trigger : true
			});
		},
		home : function() {
			/*if (!Calipso.util.isAuthenticated()) {
				this._redir("userDetails/login");
			}
			else{*/
				this.showView(new Calipso.view.HomeLayout());
			//}
		},
		_redir : function(route, forwardAfter) {
			var url = Calipso.app.config.contextPath + "client/" + route;
			Calipso.app.fw = forwardAfter;
			//consolelog("AbstractController#_redir to " + url);
			Calipso.navigate(firstLevelFragment, {
				trigger : true
			});

		},
		_ensureLoggedIn : function(){
			var pass = Calipso.util.isAuthenticated();
			if (!pass) {
				Calipso.app.fw = Backbone.history.getFragment();
				console.log("_ensureLoggedIn FW: " + Calipso.app.fw);
				Calipso.navigate("userDetails/login", {
					trigger : true
				});
			}
			return pass;
		},
		myProfile : function() {
			if (this._ensureLoggedIn()) {
				this.showUseCaseView( "users", Calipso.session.userDetails.get("id"), "view", null);
			}
		},
		logout : function() {
			Calipso.session.logout();
			// this.login();
			//window.parent.destroy();
		},
		register : function() {
			this.showLayoutForModel(new Calipso.model.UserRegistrationModel());

		},
		/**
		 * Instantiate and show a layout for the given model
		 * @param  {Calipso.model.GenericModel} givenModel the model for which the layout will be shown
		 * @param  {Calipso.view.MainLayout]} the layout type to use. If absent the method will
		 *                                             obtain the layout type from givenModel.getLayoutType()

		// TODO: remove
		showLayoutForModel : function(givenModel, useCaseContext, layoutOptions) {
			// instantiate and show the layout
			var view = new useCaseContext.view({model: givenModel, useCaseContext: useCaseContext});
			//Calipso.vent.trigger("app:show", view);

			this.showView(view);
		},*/

		/**
		 *
		 */
		showEntitySearch : function(pathFragment, httpParams) {
			this.showUseCaseView(pathFragment, null, "search", httpParams);
		},
		showEntityView : function(pathFragment, modelId) {
			this.showUseCaseView(pathFragment, modelId, "view", null);
		},
		showUserDetailsView : function(useCaseKey, httpParams) {
			// temp line
			this.showUseCaseView( "userDetails", null, useCaseKey, httpParams);
			/*
			if(!useCaseKey){
				Calipso.navigate(
					Calipso.util.session.isAuthenticated() ? "userDetails/changePassword" : "userDetails/login" ,
					{
						trigger : true
					});
			}
			else{
				this.showUseCaseView( "userDetails", null, useCaseKey, httpParams);
			}
			*/
		},
		showUseCaseView : function(pathFragment, modelId, useCaseKey, httpParams) {

			httpParams = Calipso.getHttpUrlParams(httpParams);
			var _self = this;
			var qIndex = modelId ? modelId.indexOf("?") : -1;
			if (qIndex > -1) {
				modelId = modelId.substring(0, qIndex);
			}
			// build the model instance representing the current request
			console.log("SHOW USECASE VIEW");
			$.when(Calipso.util.getUseCaseFactory(pathFragment)).done(
				function(UseCaseFactory){
					// check for usecase routes for new instances
					if(UseCaseFactory.hasUseCase(modelId)){
						useCaseKey = modelId;
						modelId = null;
					}

					// check if model type is public
					if (UseCaseFactory.isPublic() || _self._ensureLoggedIn()) {
						var useCaseContext = UseCaseFactory.getUseCaseContext({
							key : useCaseKey, modelId : modelId, httpParams : httpParams, pathFragment : pathFragment
						});

						// TODO: move fetch logic to  useCase
						var model = useCaseContext.model;
						var skipDefaultSearch = model.skipDefaultSearch && model.wrappedCollection && model.wrappedCollection.hasCriteria();

						var renderFetchable = function() {
							_self.showView(useCaseContext.createView({regionName : "/", regionPath : "/"}));
							_self.syncMainNavigationState(model);
						};
						var fetchable = useCaseContext.getFetchable();

						if (model.getTypeName() != "Calipso.model.UserDetailsModel"
							&& (!model.wrappedCollection || (!skipDefaultSearch && fetchable.length == 0))) {
							fetchable.fetch({
								data : fetchable.data
							}).then(renderFetchable);
						} else {
							renderFetchable();
						}
					}
				}
			);

		},
		notFoundRoute : function() {
			this.showView(new Calipso.view.NotFoundView());

		},
		//		decodeParam : function(s) {
		//			return decodeURIComponent(s.replace(/\+/g, " "));
		//		},
		syncMainNavigationState : function(modelForRoute) {
			var pathFragment = modelForRoute.getPathFragment(), contentNavTabName = modelForRoute.get("id");
			//console.log("AbstractController#syncMainNavigationState, pathFragment: " + pathFragment + ", contentNavTabName: " + contentNavTabName);
			// update active nav menu tab
			if (pathFragment && pathFragment != this.lastMainNavTabName) {
				$('.navbar-nav li.active').removeClass('active');
				$('#mainNavigationTab-' + pathFragment).addClass('active');
				this.lastMainNavTabName = pathFragment;
			}
			// update active content tab
			if (contentNavTabName && contentNavTabName != this.lastContentNavTabName) {
				$('#calipsoTabLabelsRegion li.active').removeClass('active');
				//				$('#md-crud-layout-tab-label-' + contentNavTabName).addClass('active');
				// show coressponding content
				// console.log("show tab: "+contentNavTabName);
				$('#calipsoTabContentsRegion .tab-pane').removeClass('active');
				$('#calipsoTabContentsRegion .tab-pane').addClass('hidden');
				$('#tab-' + contentNavTabName).removeClass('hidden');
				$('#tab-' + contentNavTabName).addClass('active');
				this.lastContentNavTabName = contentNavTabName;
			}
		},
		/**
		* route for template-based pages ('page/:templateName')
		* @member BacCalipso.controller.AbstractController
		* @param {string} formattedData
		*/
		templatePage : function(templateName) {
			var pageView = new Calipso.view.TemplateBasedItemView({
				template : Calipso.getTemplate(templateName),
				tagName : "div"
			});
			this.showView(pageView);
			//Calipso.vent.trigger("app:show", pageView);
		},
		tryExplicitRoute : function(pathFragment, secondaryRoutePart) {
			if (typeof this[pathFragment] == 'function') {
				// render explicit route
				this[pathFragment](secondaryRoutePart);
			}
		},
		notFoundRoute : function(path) {
			// console.log("notFoundRoute, path: "+path);
			this.showView(new Calipso.view.NotFoundView());
		},
		editItem : function(item) {
			//console.log("MainController#editItem, item: " + item);
		}

	});

	return Calipso;

});
