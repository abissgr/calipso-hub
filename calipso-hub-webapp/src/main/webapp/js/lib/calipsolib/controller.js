/*
 * calipso-hub-webapp - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
		},
		register : function() {
			Calipso.navigate("useCases/users/register", {
				trigger : true
			});
		},
		showEntitySearch : function(pathFragment, httpParams) {
			this.showUseCaseView(pathFragment, null, "search", httpParams);
		},
		showEntityView : function(pathFragment, modelId) {
			this.showUseCaseView(pathFragment, modelId, "view", null);
		},
		showUserDetailsView : function(useCaseKey, httpParams) {
			// temp line
			this.showUseCaseView( "userDetails", null, useCaseKey, httpParams);
		},
		showUseCaseView : function(pathFragment, modelId, useCaseKey, httpParams) {
			httpParams = Calipso.getHttpUrlParams(httpParams);
			var _self = this;
			var qIndex = modelId ? modelId.indexOf("?") : -1;
			if (qIndex > -1) {
				modelId = modelId.substring(0, qIndex);
			}
			// build the model instance representing the current request
			$.when(Calipso.util.getUseCaseFactory(pathFragment)).done(
				function(UseCaseFactory){
					// check for usecase routes for new instances

					console.log("showUseCaseView, useCaseKey: " + useCaseKey + ", modelId: " + modelId);
					if(UseCaseFactory.hasUseCase(modelId)){
						useCaseKey = modelId;
						modelId = null;
					}
					console.log("showUseCaseView, useCaseKey: " + useCaseKey + ", modelId: " + modelId);

					// check if model type is public
					if (UseCaseFactory.isPublic() || _self._ensureLoggedIn()) {
						var useCaseContext = UseCaseFactory.getUseCaseContext({
							key : useCaseKey, modelId : modelId, httpParams : httpParams, pathFragment : pathFragment
						});

						// TODO: move fetch logic to  useCase
						var model = useCaseContext.model;
						console.log("showUseCaseView, model id: " + model.get("id"));
						var skipDefaultSearch = model.skipDefaultSearch && model.wrappedCollection && model.wrappedCollection.hasCriteria();

						var renderFetchable = function() {
							_self.showView(useCaseContext.createView({regionName : "/", regionPath : "/"}));
						};
						var fetchable = useCaseContext.getFetchable();
						console.log("showUseCaseView, model: " + model.getTypeName() + ", id: " + model.get("id"));
						console.log(model);
						console.log("showUseCaseView, fetchable: " + fetchable.getTypeName() + ", id: " + fetchable.get("id"));
						console.log(fetchable);
						if (model.get("id") && model.getTypeName() != "Calipso.model.UserDetailsModel"/*
							&& (!model.wrappedCollection || (!skipDefaultSearch && fetchable.length == 0))
						*/) {
							console.log("showUseCaseView, fetching");
							fetchable.fetch({
								data : fetchable.data
							}).then(renderFetchable);
						} else {
							console.log("showUseCaseView, skipped fetching");
							renderFetchable();
						}
					}
				}
			);

		},
		notFoundRoute : function() {
			this.showView(new Calipso.view.NotFoundView());

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
