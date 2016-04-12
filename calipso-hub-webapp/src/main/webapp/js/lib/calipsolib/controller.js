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
	Calipso.controller.AbstractController = Marionette.Controller.extend({
		constructor : function(options) {
			//consolelog("AbstractController#constructor");
			Marionette.Controller.prototype.constructor.call(this, options);
			this.layout = new Calipso.view.AppLayout({
				model : Calipso.session
			});
			Calipso.vent.trigger('app:show', this.layout);

		},
		toHome : function() {
			Calipso.navigate("home", {
				trigger : true
			});
		},
		home : function() {
			//consolelog("AbstractController#home");
			if (!Calipso.util.isAuthenticated()) {
				this._redir("userDetails/login");
			}
			else{
				this.layout.contentRegion.show(new Calipso.view.HomeLayout());
			}
		},

		_redir : function(firstLevelFragment, forwardAfter) {
			var url = Calipso.app.config.contextPath + "client/" + firstLevelFragment;
			Calipso.app.fw = forwardAfter;
			//consolelog("AbstractController#_redir to " + url);
			Calipso.navigate(firstLevelFragment, {
				trigger : true
			});
			return false;
		},
		myProfile : function() {
			if (!Calipso.util.isAuthenticated()) {
				Calipso.navigate("login", {
					trigger : true
				});
			} else {
				this.mainNavigationCrudRoute("userProfile", Calipso.session.userDetails.get("id"))
			}
		},
		/*login : function() {
			if (Calipso.util.isAuthenticated()) {
				window.alert("Please logout before attempting a new login");
			} else {
				this.showLayoutForModel(new Calipso.model.UserDetailsModel(), null, null);
			}
		},*/
		renderTokenPasswordChangeForm : function(username, token) {
			var model = new Calipso.model.UserDetailsModel({
				username : username,
				resetPasswordToken : token,
				formSchemaKey : "create-withToken",
			});
			this.showLayoutForModel(model, null, null);
		},
		accountConfirm : function(confirmationToken) {
			if (confirmationToken) {
				var url = Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/accountConfirmations/" + confirmationToken;
				var options = Calipso.app.routeOptions;
				// TODO: leave any forward at Calipso.app.fw
				Calipso.session.load(url);
			} else {
				throw "accountConfirm route requires the confirmation token as a URI component";
			}
		},
		changePassword : function() {
			var userDetails = Calipso.session.userDetails;
			if (!Calipso.util.isAuthenticated()) {
				userDetails = new Calipso.model.UserDetailsModel({
					showResetPasswordForm : true
				});
				var httpParams = Calipso.getHttpUrlParams();
				if(httpParams.email){
					userDetails.set("email", httpParams.email);
				}
			}
			this.showLayoutForModel(userDetails, null, null);
		},
		logout : function() {
			Calipso.vent.trigger("session:destroy");
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
		 */
		showLayoutForModel : function(givenModel, useCaseContext, layoutOptions) {
			// instantiate and show the layout
			var view = new useCaseContext.view({model: givenModel, useCaseContext: useCaseContext});
			Calipso.vent.trigger("app:show", view);
		},
		/**
		 * Get a model representing the current request.
		 *
		 * For an example, consider the URL [api-root]/users/[some-id]. First,
		 * a model class is loaded based on the URL fragment representing the
		 * type, e.g. "users" for UserModel.
		 *
		 * A model instance is then created using some-id if provided or
		 * "search" otherwise. .
		 *
		 * In case of "search" a collection of the given model type is
		 * initialized but, similarly to the model instance, it is not fetched
		 * from the server.
		 *
		 * @param {string}
		 *           modelTypeKey the URL fragment representing the model type
		 *           key, e.g. "users" for UserModel
		 * @param {string}
		 *           modelId the model identifier. The identifier may be either
		 *           a primary or business key, depending on your server side
		 *           implementation. The default property name in client side
		 *           models is "name". You can override
		 *           {@linkcode Calipso.model.GenericModel.getBusinessKey} to
		 *           define another property name.
		 * @see Calipso.model.GenericModel.getBusinessKey
		 */
		getModelForRoute : function(ModelType, modelId, httpParams) {

			// Obtain a model for the view:
			// if a model id is present, obtain a promise
			// for the corresponding instance

			var modelForRoute;
			if (modelId) {
				modelForRoute = ModelType.create({
					id : modelId,
				});
			} else {
				// create a model to use as a wrapper for a collection of
				// instances of the same type, fill it with any given search criteria
				if (!httpParams) {
					httpParams = {};
				}
				modelForRoute = new ModelType(httpParams);
				var collectionOptions = {
					model : ModelType,
					url : Calipso.getBaseUrl() + "/api/rest/" + ModelType.getPathFragment()
				};
				if (httpParams) {
					if (httpParams[""] || httpParams[""] == null) {
						delete httpParams[""];
					}
					collectionOptions.data = httpParams;
				}
				modelForRoute.wrappedCollection = Calipso.util.cache.getCollection(collectionOptions);

			}
			return modelForRoute;
		},
		// TODO: remove
		mainNavigationReportRoute : function(mainRoutePart, queryString) {

			// TODO: temp fix
			var isReport = window.location.href.indexOf("/reports") > -1;
			// console.log("AbstractController#mainNavigationReportRoute, isReport: " + isReport);
			if (!isReport) {
				this.mainNavigationSearchRoute(mainRoutePart, queryString);
			} else {
				var _self = this;
				var httpParams = Calipso.getHttpUrlParams();

				// get the model the report focuses on
				var ModelType = Calipso.util.getModelType(mainRoutePart);
				if (!Calipso.util.isAuthenticated() && !ModelType.isPublic()) {
					return this._redir("login");
				}

				// build a report dataset collection using the model's report URL
				var reportModel = new Calipso.model.ReportDataSetModel({
					subjectModelType : ModelType
				});
				var collectionOptions = {
					model : Calipso.model.ReportDataSetModel,
					url : Calipso.getBaseUrl() + "/api/rest/" + reportModel.getPathFragment(),
					pathFragment : reportModel.getPathFragment(),
				};
				if (httpParams) {
					if (httpParams[""] || httpParams[""] == null) {
						delete httpParams[""];
					}
					collectionOptions.data = httpParams;
				}

				reportModel.wrappedCollection = Calipso.util.cache.getCollection(collectionOptions);
				;
				this.renderFetchable(reportModel);

			}

		},
		/**
		 *
		 */
		showEntitySearch : function(mainRoutePart, queryString) {
			var httpParams = Calipso.getHttpUrlParams();
			this.showUseCaseView(mainRoutePart, null, "search", httpParams);
		},
		showEntityView : function(mainRoutePart, modelId) {
			this.showUseCaseView(mainRoutePart, modelId, "view", null);
		},
		showUserDetailsView : function(useCaseKey, httpParams) {
			this.showUseCaseView( "userDetails", useCaseKey, useCaseKey, httpParams)
		},
		showUseCaseView : function(mainRoutePart, modelId, useCaseKey, httpParams) {
			console.log("showUseCaseView mainRoutePart: " + mainRoutePart + ", modelId: " + modelId + ", useCaseKey: " + useCaseKey);
			var _self = this;
			var qIndex = modelId ? modelId.indexOf("?") : -1;
			if (qIndex > -1) {
				modelId = modelId.substring(0, qIndex);
			}
			// build the model instance representing the current request

			var ModelType = Calipso.util.getModelType(mainRoutePart);

			if (!Calipso.util.isAuthenticated() && !ModelType.isPublic()) {
				return this._redir("login");
			}
			var model = this.getModelForRoute(ModelType, modelId, httpParams);

			// TODO: support loading of useCase modules?
			var useCaseContext = new Calipso.datatypes.UseCaseContext(
				$.extend({}, ModelType.useCases[useCaseKey], {key : useCaseKey}, {model : model})
			);

			// fetch model(s) and show view
			var fetchable = model.wrappedCollection ? model.wrappedCollection : model;

			// TODO: move to useCases
			// oif true, occupy the main region with the search form instead of
			// showing the form and grid side-by-side
			var skipDefaultSearch = model.skipDefaultSearch && model.wrappedCollection && model.wrappedCollection.hasCriteria();
			// promise to fetch then render
			// console.log("AbstractController#mainNavigationCrudRoute, mainRoutePart: " + mainRoutePart + ", model id: " + modelForRoute.get("id") + ", skipDefaultSearch: " + skipDefaultSearch);
			var renderFetchable = function() {

				Calipso.vent.trigger("app:show", useCaseContext.createView());

				// TODO: remove/move to header view events;
				// update page header tabs etc.
				// this has been left over from when the associated markup was
				// not part of some view
				_self.syncMainNavigationState(model);
			};
			if (model.getTypeName() != "Calipso.model.UserDetailsModel"
				&& (!model.wrappedCollection || (!skipDefaultSearch && fetchable.length == 0))) {
				//console.log("renderFetchable: fetch");
				fetchable.fetch({
					data : fetchable.data
				}).then(renderFetchable);
			} else {
				//console.log("renderFetchable: dont fetch");
				renderFetchable();
			}
		},
		notFoundRoute : function() {
			// build the model instancde representing the current request
			Calipso.vent.trigger("app:show", new Calipso.view.NotFoundView());

		},
		//		decodeParam : function(s) {
		//			return decodeURIComponent(s.replace(/\+/g, " "));
		//		},
		syncMainNavigationState : function(modelForRoute) {
			var mainRoutePart = modelForRoute.getPathFragment(), contentNavTabName = modelForRoute.get("id");
			//console.log("AbstractController#syncMainNavigationState, mainRoutePart: " + mainRoutePart + ", contentNavTabName: " + contentNavTabName);
			// update active nav menu tab
			if (mainRoutePart && mainRoutePart != this.lastMainNavTabName) {
				$('.navbar-nav li.active').removeClass('active');
				$('#mainNavigationTab-' + mainRoutePart).addClass('active');
				this.lastMainNavTabName = mainRoutePart;
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
			Calipso.vent.trigger("app:show", pageView);
		},
		tryExplicitRoute : function(mainRoutePart, secondaryRoutePart) {
			if (typeof this[mainRoutePart] == 'function') {
				// render explicit route
				this[mainRoutePart](secondaryRoutePart);
			}
		},
		notFoundRoute : function(path) {
			// console.log("notFoundRoute, path: "+path);
			this.layout.contentRegion.show(new Calipso.view.NotFoundView());
		},
		editItem : function(item) {
			//console.log("MainController#editItem, item: " + item);
		}

	});

	return Calipso;

});
