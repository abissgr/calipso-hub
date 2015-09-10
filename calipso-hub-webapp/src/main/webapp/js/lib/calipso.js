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

define([
	'underscore', 'handlebars', 'calipso-hbs', 'moment',
	'backbone', 'backbone.paginator', 'backbone-forms', 'backbone-forms-bootstrap3', 'backbone-forms-select2',
  'marionette',
  'backgrid', 'backgrid-moment', 'backgrid-text', 'backgrid-responsive-grid', 'backgrid-paginator',
  /*'metis-menu', 'morris', */'bloodhound', 'typeahead', 'bootstrap-datetimepicker','bootstrap-switch',
  'jquery-color', 'jquery-intlTelInput', 'q', 'chart'],
function(
	_, Handlebars, calipsoTemplates, moment,
	Backbone, PageableCollection, BackboneForms, BackboneFormsBootstrap, BackboneFormsSelect2,
	BackboneMarionette,
	Backgrid, BackgridMoment, BackgridText, BackgridResponsiveGrid, BackgridPaginator,
	/*MetisMenu, */Morris, Bloodhoud, Typeahead, BackboneDatetimepicker, BootstrapSwitch,
	jqueryColor, intlTelInput, q, chartjs) {


	/**
	 * Calipso namespace
	 * @namespace
	 */
	var Calipso = {
		config : {},
		util : {},
		components : {},
		collection : {},
		model : {},
		customModel : {},
		view : {},
		controller : {},
		hbs: {}
	};

	// Get the DOM manipulator for later use
	Calipso.$ = Backbone.$;
	Calipso.decodeParamRegex = /\+/g;
	Calipso.decodeParam = function(s) {
		return decodeURIComponent(s.replace(Calipso.decodeParamRegex, " "));
	};

	Calipso.getHttpUrlParams = function(url) {
		var urlParams = {};
		if (!url) {
			url = window.location.href;
		}
		var queryString = url.indexOf("?") > -1 ? url.substring(url.indexOf("?") + 1) : "";
		var keyValuePairs = queryString.split('&');
		for ( var i in keyValuePairs) {
			var keyValuePair = keyValuePairs[i].split('=');
			urlParams[Calipso.decodeParam(keyValuePair[0])] = (keyValuePair.length > 1) ? this.decodeParam(keyValuePair[1]) : null;
		}
		;
		return urlParams;
	};

	Calipso.getDefaultFetchOptions = function(){
		return {
			// use traditional HTTP params
			traditional: true,
			// handle status codes
			statusCode: {
				401: function(){
					console.log("Backbone.$.ajaxSetup 401");
					window.alert("Your session has expired");
					Calipso.navigate("login");
				},
				403: function() {
					console.log("Backbone.$.ajaxSetup 403");
					window.alert("Your session has expired");
					Calipso.navigate("login");
				}
			}
		};

	}
	/**
	 * Utility method to stop events.
	 * @param  {event} e
	 * @return {void}
	 */
	Calipso.stopEvent = function(e) {
		Calipso.vent.trigger("calipso:stoppedEvent given: ", e);
		var event = e ? e : window.event;
		if (event.preventDefault) {
			event.preventDefault();
		} else {
			event.returnValue = false;
			event.stop();
		}
		if (event.stopPropagation) {
			event.stopPropagation();
		} else {
			event.cancelBubble = true;
		}
		Calipso.vent.trigger("calipso:stoppedEvent", e);
	};
	/**
	 * Update bootstrap badges
	 * @param  {[String]} the jquery selector to use
	 * @param  {[stStringring]} the new badge text
	 * @return {[type]}
	 */
	Calipso.updateBadges = function(selector, text) {
		// e.g. update visual notification counters
		//console.log("Notifications count: " + text);
		if (text) {
			//console.log("Showing notification counters...");
			$(selector).text(text).removeClass("hidden").show();
		} else {
			//console.log("Hiding notification counters...");
			$(selector).text(text).hide();
		}
	};
	/**
	 * Get a conbfiguration property
	 * @param  {[String]} the property name
	 * @return {[type]}
	 */
	Calipso.getConfigProperty = function(propertyName) {
		return Calipso.config[propertyName];
	};
	/**
	 * Get a conbfiguration property
	 * @param  {[String]} the property name
	 * @return {[type]}
	 */
	Calipso.socialLogin = function(e) {

			console.log("Calipso.socialLogin, clicked:");
			Calipso.stopEvent(e);
			var clicked = $(e.currentTarget);

			console.log(clicked);
			var providerNames = ["facebook", "linkedin", "twitter", "google"];
			var providerName;

			for(var i=0; i < providerNames.length; i++){
				console.log("Calipso.socialLogin looking for className: " + "btn-social-login-" + providerNames[i]);
				if(clicked.hasClass("btn-social-login-" + providerNames[i])){
					providerName = providerNames[i];
					break;
				}
			}

			if(!providerName){
				throw "Clicked element does not match a social provider";
			}
			// target='SignIn'
			var formHtml = "<form class='social-signin-form' action='" +
				Calipso.getBaseUrl() + "/signin/" + providerName +"' method='POST' role='form'>" +
			//"<input type='hidden' name='scope' value='email' />" +
			//"<input type='hidden' name='scope' value='emailure' />" +
			//"<input type='hidden' name='topWindowDomain' value='" + window.location.hostname + "' />" +
				"</form>";
			$("div.social-form-container").html(formHtml);
			$("form.social-signin-form").submit();
			return false;
	};
	/**
	 * Use the MainRouter to navigate to the given route
	 * @param  {[String]} the route URL
	 * @param  {[Object]} the options hash
	 * @return {[type]}
	 */
	Calipso.navigate = function(url, options) {
		Calipso.app.routers["MainRouter"].navigate(url, options);
	};

	Calipso.walk =  function(currentStepValue, pathSteps, stepIndex){
		var value;
		if(stepIndex == undefined){
			stepIndex = 0;
		}
		var propName = pathSteps[stepIndex];
		if(currentStepValue && propName){
			value = Calipso.getObjectProperty(currentStepValue, propName);
			stepIndex++;
			if(value && stepIndex < pathSteps.length){
				value = Calipso.walk(value, pathSteps, stepIndex);
			}
		}
		return value;
	};


	Calipso.setPathValue = function(obj, path, value) {
		var pathOrig = path;
		if(path.indexOf(".") >= 0 || path.indexOf("[") >= 0){
			path = path.replace(/\[(.*?)\]/g,'.$1');
		}
		var steps = path.split(".");
		var targetProp = steps.pop();
		if(steps.length > 0){
			obj = Calipso.walk(obj, steps);
		}
		if(!obj){
			throw "Calipso.setPathValue: invalid path " + pathOrig;
		}
		if(obj.set){
			obj.set(targetProp, value);
		}
		else{
			obj[targetProp] = value;
		}
	};
	Calipso.getPathValue = function(obj, path, defaultValue) {
		if(path.indexOf(".") >= 0 || path.indexOf("[") >= 0){
			path = path.replace(/\[(.*?)\]/g,'.$1');
		}
		var value = Calipso.walk(obj, path.split("."));
		if(defaultValue
			&& (_.isUndefined(value) || _.isNull(value))){
			value = defaultValue;
		}
		return value;
	};


	Calipso.getObjectProperty = function(obj, propName, defaultValue) {
		var prop;
		if(obj){
			if(obj.get && !_.isUndefined(obj.get(propName))){
				prop = obj.get(propName);
			}
			else if(!_.isUndefined(obj[propName])){
				prop = obj[propName];
			}
			else if(!_.isUndefined(defaultValue)){
				prop = defaultValue;
			}
		}
		return prop;
	};
	Calipso.getTemplate = function(name) {
		return calipsoTemplates[name];
	}
	Calipso.initializeApp = function(customConfig) {
		customConfig = customConfig || {};
		var config = {
			contextPath : "/",
			headerViewType : Calipso.view.HeaderView,
			footerViewType : Calipso.view.FooterView,
			loginViewType : Calipso.view.LoginView,
			sessionType : Calipso.util.Session,
			apiAuthPath : "/apiauth",
		};
		Calipso.config = _.defaults(customConfig, config);

		// console.log("Setting up Calipso.session...");
		var SessionType = Calipso.getConfigProperty("sessionType");
		Calipso.session = new SessionType();
		// console.log("Calipso.session has been configured");

		// console.log("Setting up Calipso.app...");
		Calipso.app = new Marionette.Application({
			config : Calipso.config,
			routers : {}
		});
		// application configuration
		Calipso.app.addRegions({
			headerRegion : "#calipsoHeaderRegion",
			mainContentRegion : "#calipsoMainContentRegion",
			modalRegion : Calipso.view.ModalRegion,
			footerRegion : "#calipsoFooterRegion"
		});

		Calipso.app.addInitializer(function(options) {

			// init ALL app routers
			_(options.routers).each(function(routerClass) {
				var router = new routerClass();
				Calipso.app.routers[routerClass.getTypeName()] = router;
				// console.log("initialized router: " + routerClass.getTypeName());
			});

			Calipso.modelTypesMap = {};
			var parseModel = function(ModelType) {
				if(ModelType.prototype.getTypeName() != "Calipso.model.ReportDataSetModel"
					&& ModelType.prototype.getTypeName() != "Calipso.model.UserRegistrationModel"
					&& ModelType.prototype.getTypeName() != "Calipso.model.GenericModel"){

					var model = new ModelType();
					Calipso.modelTypesMap[model.getPathFragment()] = ModelType;

				}
			};
			_(Calipso.model).each(parseModel);
			_(Calipso.customModel).each(parseModel);
		});
		// console.log("Calipso.app has been configured");

		// register a handlebars helper for menuentries
		Handlebars.registerHelper("baseUrl", function() {
			return Calipso.getBaseUrl();
		});
		Handlebars.registerHelper("menuEntries", function() {
			// console.log("menu entries...");

			var menuEntries = {};
			var modelTypesMap = Calipso.modelTypesMap;
			var modelType;
			for ( var modelKey in modelTypesMap) {
				modelType = modelTypesMap[modelKey];
				//TODO
				// console.log("Add menu entrie for  model type: " + modelType.prototype.getTypeName() + " and key: " + modelType.prototype.getPathFragment() + ", showInMenu: " + modelType.prototype.showInMenu);
				//				if(modelType.prototype.showInMenu){
				if (true) {
					menuEntries[modelType.prototype.getPathFragment()] = {
						label : modelType.label,
						modelKey : modelType.modelKey
					};
				}
			}
			return (menuEntries);
		});

		// register comparison helper
		Handlebars.registerHelper('ifCond', function(v1, operator, v2, options) {

			switch (operator) {
			case '==':
				return (v1 == v2) ? options.fn(this) : options.inverse(this);
			case '===':
				return (v1 === v2) ? options.fn(this) : options.inverse(this);
			case '<':
				return (v1 < v2) ? options.fn(this) : options.inverse(this);
			case '<=':
				return (v1 <= v2) ? options.fn(this) : options.inverse(this);
			case '>':
				return (v1 > v2) ? options.fn(this) : options.inverse(this);
			case '>=':
				return (v1 >= v2) ? options.fn(this) : options.inverse(this);
			case '&&':
				return (v1 && v2) ? options.fn(this) : options.inverse(this);
			case '||':
				return (v1 || v2) ? options.fn(this) : options.inverse(this);
			default:
				return options.inverse(this);
			}
		});

		////////////////////////////////
		// app init/events
		////////////////////////////////
		// initialize header, footer, history
		Calipso.app.on("start", function() {

			//	try "remember me"
			Calipso.session.load();

			// render basic structure
			Calipso.app.headerRegion.show(new Calipso.config.headerViewType({
				model : Calipso.session.userDetails
			}));

			// TODO: move after loading the sidebar DOM
			//Loads the correct sidebar on window load,
			//collapses the sidebar on window resize.
			$(function() {
				$(window).bind("load resize", function() {
					width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
					if (width < 768) {
						$('div.sidebar-collapse').addClass('collapse');
					} else {
						$('div.sidebar-collapse').removeClass('collapse');
					}
				});
			});

			Calipso.app.footerRegion.show(new Calipso.config.footerViewType());

			var pushStateSupported = _.isFunction(history.pushState);
			var contextPath = Calipso.getConfigProperty("contextPath");

			// console.log("Calipso.app.on start, contextPath: " + contextPath);
			if (contextPath.length > 1) {
				// add leading slash if missing
				if (contextPath.indexOf("/") != 0) {

					//console.log("Calipso.app.on start, adding slash prefix");
					contextPath = "/" + contextPath;
				}

				// add ending slash if missing
				if (contextPath.substr(-1) != '/') {

					//console.log("Calipso.app.on start, adding slash suffix");
					contextPath += '/';
				}

			}
			var startRoot = contextPath + "client/";
			Backbone.history.start({
				root : startRoot,
				pushState : pushStateSupported
			});

		});

		Calipso.vent.on('app:show', function(appView, navigaeToUrl) {
			var $wrapper = $("#container");
			if (appView.containerClass && $wrapper && appView.containerClass != $wrapper.attr("class")) {
				$wrapper.attr("class", appView.containerClass);
			}
			Calipso.app.mainContentRegion.show(appView);
			if(navigaeToUrl){
				Calipso.navigate(navigaeToUrl, {
					trigger : false
				});
			}
		});


		Calipso.vent.on('session:social-popup', function(providerId) {
			// remove any pre-existing form
			$("#calipso-social-signin-form").remove();
			var oForm = document.getElementById("calipso-social-signin-form");
			var wrapper = $('#hiddenWrapper');
			// create form if it doesn't exist
			if (!oForm) {
				var formHtml = "<form id='calipso-social-signin-form' action='" + window.calipsoBasePath + "/signin/" + providerId + "'  method='POST' role='form'>" +
				//"<input type='hidden' name='scope' value='email' />" +
				//"<input type='hidden' name='scope' value='emailure' />" +
				"<input type='hidden' name='topWindowDomain' value='" + window.location.hostname + "' />" + "</form>";
				wrapper.append(formHtml);
				oForm = document.getElementById("calipso-social-signin-form");
			}
			// figure out position
			var w = 500;
			var h = 400;
			// Fixes dual-screen position for most browsers
			var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
			var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;
			var left = ((screen.width / 2) - (w / 2)) + dualScreenLeft;
			var top = ((screen.height / 2) - (h / 2)) + dualScreenTop;
			// open popup window and POST to it
			var calipsoSocialSignInWin = window.open("", "SignIn", "width=" + w + ",height=" + h + ",toolbar=0,scrollbars=0,status=0,resizable=0,location=0,menuBar=0,left=" + left + ",top=" + top);

			var interval = window.setInterval(function() {
				//console.log("in interval");
				try {
					if (calipsoSocialSignInWin == null || calipsoSocialSignInWin.closed) {
						//console.log("window closed");
						window.clearInterval(interval);
						//closeCallback(win);
						calipso.tryRememberMe();
					}
				} catch (e) {
				}
			}, 1000);

			calipsoSocialSignInWin.focus();
			oForm.submit();
			// remove form
			$("#calipso-social-signin-form").remove();
			return false;
		});
		Calipso.vent.on('session:created', function(userDetails) {

			$("#page-wrapper").removeClass("anonymous");
			$("#page-wrapper").addClass("loggedIn");


			$("#container").removeClass("container");
			$("#container").addClass("container-fluid");
			// console.log("vent event session:created");
			// update otification counters
			var count = userDetails ? userDetails.get("notificationCount") : 0;

			Calipso.updateBadges(".badge-notifications-count", userDetails ? userDetails.get("notificationCount") : 0);

			Calipso.session.userDetails = userDetails;
			Calipso.app.headerRegion.show(new Calipso.config.headerViewType({
				model : Calipso.session.userDetails
			}));

			// send logged in user on their way
			var fw = "home";
			if (Calipso.app.fw) {
				fw = Calipso.app.fw;
				Calipso.app.fw = null;
			}
			// console.log("session:created, update model: " + userDetails.get("email") + ", navigating to: " + fw);

			Calipso.navigate(fw, {
				trigger : true
			});
			//window.location = fw;
		});
		Calipso.vent.on('session:destroy', function(userDetails) {

			$("#page-wrapper").removeClass("loggedIn");
			$("#page-wrapper").addClass("anonymous");


			$("#container").addClass("container");
			$("#container").removeClass("container-fluid");

			Calipso.session.destroy();
			Calipso.app.headerRegion.show(new Calipso.config.headerViewType({
				model : userDetails
			}));
			Calipso.navigate("home", {
				trigger : true
			});
		});
		Calipso.vent.on('nav-menu:change', function(modelkey) {
			// console.log("vent event nav-menu:change");

		});
		Calipso.vent.on('modal:show', function(view) {
			console.log("vent event modal:show");
			Calipso.app.modalRegion.show(view);
		});
		/**
		 * @example Calipso.vent.trigger('modal:showInLayout', {view: someView, template: someTemplate, title: "My title"});
		 */
		Calipso.vent.on('modal:showInLayout', function(properties) {
			// make sure a view is provided
			if (!properties.view) {
				throw "A 'view' property is required on vent trigger 'modal:showInLayout'.";
			}
			// assemble properties
			var layoutProperties = {
				childView : properties.view,
				title : properties.title
			};
			if (!properties.template) {
				layoutProperties.template = properties.template;
			}
			// show
			var modalLayoutView = new Calipso.view.ModalLayout(layoutProperties);
			Calipso.app.modalRegion.show(modalLayoutView);
		});
		Calipso.vent.on('modal:destroy', function() {
			// console.log("vent event modal:destroy");
			Calipso.app.modalRegion.closeModal();
		});

	};

	// //////////////////////////////////////
	// Region
	// //////////////////////////////////////
	Calipso.view.ModalRegion = Marionette.Region.extend(/** @lends Calipso.view.ModalRegion.prototype */
	{
		el : "#calipsoModalRegion",
		initialize : function(options){

				// listen to the modal region
				var showHandler = function (e) {
					$('.modal .modal-body')
						.css('overflow-y', 'auto')
						.css('max-height', $(window).height() * 0.7)
						.find('input[type=text],textarea,select').filter(':visible:enabled:first').focus();
				};

				var $el = $(this.el);
				$el.on('shown.bs.modal', showHandler);
				$el.on('show.bs.modal', showHandler);
		},
		onShow : function(view, region, options) {
			view.on("destroy", this.hideModal, this);
			this.$el.modal('show');
		},
		hideModal : function() {
			this.$el.modal('hide');
		},
		closeModal : function() {
			this.hideModal();
			this.reset();
		}
	});

	// //////////////////////////////////////
	// Collection
	// //////////////////////////////////////
	Calipso.collection.GenericCollection = PageableCollection.extend(
	/** @lends Calipso.collection.GenericCollection.prototype */
	{
		mode : "server",
		data : {},
		/**
		 * Initial pagination states
		 */
		state : {
			/**
			 * The first page index. Set to 0 if your server API uses 0-based indices.
			 */
			firstPage : 0,
			currentPage : 0,
			pageSize : 10,
		},
    /**
     A translation map to convert PageableCollection state attributes
     to the query parameters accepted by your server API.

     You can override the default state by extending this class or specifying
     them in `options.queryParams` object hash to the constructor.

     @property {Object} queryParams
     @property {string} [queryParams.currentPage="number"]
     @property {string} [queryParams.pageSize="size"]
     @property {string} [queryParams.totalPages="totalPages"]
     @property {string} [queryParams.totalRecords="totalElements"]
     @property {string} [queryParams.sortKey="properties"]
     @property {string} [queryParams.order="sort"]
     @property {string} [queryParams.directions={"-1": "ASC", "1": "DESC"}] A
     map for translating a PageableCollection#state.order constant to
     the ones your server API accepts.
  	*/
    queryParams: {
      totalPages: "totalPages",
			pageSize: "size",
			currentPage: "page",
      totalRecords: "totalElements",
      sortKey: "properties",
      order : "direction",//"direction"?
      directions: {
        "-1": "ASC",
        "1": "DESC"
      }
    },
		getTypeName : function() {
			return this.prototype.getTypeName();
		},
		initialize : function(attributes, options) {
			PageableCollection.prototype.initialize.apply(this, arguments);
			options || (options = {});
			if (options.model && options.model.prototype.getTypeName()) {
				this.model = options.model;
				//console.log("GenericCollection#initialize, model: " + this.model.prototype.getTypeName());
			}
			else{
				throw "GenericCollection#initialize: options.model is required and must be a subtype of Genericmodel";
			}
			if(!options.url){
				this.url =  Calipso.getBaseUrl() + '/api/rest/' + this.model.prototype.getPathFragment();
			}
			// use given grid columns if provided, or the
			// default model columns otherwise
			if (options.schemaForGrid) {
				this.schemaForGrid = options.schemaForGrid;
			}

			if (options.data) {
				if (options.data[""] || options.data[""] == null) {
					delete options.data[""];
				}
				this.data = options.data;
				this.state = this.parseState(this.data, this.queryParams, this.state, {});

			}

			if (options.url) {
				this.url = options.url;
			}
		},
		fetch : function(options) {
			if(options && options.data){
				this.state = this.parseState(options.data, this.queryParams, this.state, {});
			}
			return PageableCollection.prototype.fetch.apply(this, arguments);
		},
		hasCriteria : function(){
			var minData = 0;
			var ignoredCriteria = ["page", "size", "direction"];
			for(var i=0; i < ignoredCriteria.length; i++){
				if(this.data[ignoredCriteria[i]] != undefined){
					minData++;
				}
			}
			return  _.size(this.data) > minData;
		},
		getGridSchema : function() {
			// use explicit configuration if available
			var configuredSchema = this.schemaForGrid;
			// try obtaining the grid schema from the model otherwise
			if (!configuredSchema && this.model && this.model.prototype.getGridSchema) {
				configuredSchema = this.model.prototype.getGridSchema();
			}

			// ensure proper configuration is available
			if (!configuredSchema) {
				throw "A grid schema has not been given and the collection model does not offer one or is undefined";
			}
			return configuredSchema;
		},
		getPathFragment : function() {
			return this.prototype.getPathFragment();
		},
		parseState: function (resp, queryParams, state, options) {
      if (resp) {
        var newState = _.clone(state);
        var serverState = resp;

				var intKeys = ["firstPage", "currentPage", "pageSize", "totalPages", "totalRecords"];
        _.each(_.pairs(_.omit(queryParams, "directions")), function (kvp) {
          var k = kvp[0], v = kvp[1];
          var serverVal = serverState[v];
          if (!_.isUndefined(serverVal) && !_.isNull(serverVal)){
						newState[k] = serverVal;
						// enforce integers when applicable

						if($.inArray(k, intKeys) > -1){
						  if (typeof serverVal == "string") {
						    newState[k] = parseInt(serverVal) || 0;
						  }
						}
					}
        });
				//ize:10, number:0, sort:[{direction:"ASC", property:"price", ignoreCase:false, ascending:true}],
				// totalElements:10, lastPage:true, totalPages:1, numberOfElements:10, firstPage:true})

        if (serverState.order) {
          newState.order = _.invert(queryParams.directions)[serverState.order.toUpperCase()] * 1;
        }
        else if (serverState.sort && serverState.sort.direction) {
          newState.order = _.invert(queryParams.directions)[serverState.sort.direction.toUpperCase()] * 1;
        }
        else if (serverState.direction) {
          newState.order = _.invert(queryParams.directions)[serverState.direction.toUpperCase()] * 1;
        }

				return newState;
      }
    },
		parseRecords : function(response, options) {
			var _self = this;
			var itemsArray = response;
			if (response.content) {
				itemsArray = response.content;
			} else {
				this.total = itemsArray.length;
				this.totalPages = 1;
			}
			return itemsArray;
		}

	});
	/**
	 * Get the name of this class
	 * @returns the class name as a string
	 */
	Calipso.collection.GenericCollection.prototype.getTypeName = function(instance) {
		return "Calipso.collection.GenericCollection";
	};

	Calipso.collection.AllCollection = Backbone.Collection.extend({
		initialize : function(attributes, options) {
			if (options) {
				if (options.url) {
					this.url = options.url;
				}
			}
		},
		fetch : function(options) {
			options || (options = {});
			var data = (options.data || {});
			options.data = {
				page : "no"
			};
			return Backbone.Collection.prototype.fetch.call(this, options);
		}

	});
	Calipso.collection.AllCollection.prototype.getTypeName = function(instance) {
		return "Calipso.collection.AllCollection";
	};
	/**
	 *
	 * backbone-polling v1.0.0
	 * https://github.com/pedrocatre/backbone-polling
	 *
	 * Copyright (c) 2013 Pedro Catré
	 *
	 * Licensed under the MIT License
	 */
	Calipso.collection.PollingCollection = Calipso.collection.GenericCollection.extend(/** @lends Calipso.collection.PollingCollection.prototype */
	{

		/**
		 * Id returned by the setTimeout function that the plugin uses to specify a delay between fetch requests to the
		 * data source
		 */
		_backbonePollTimeoutId : undefined,

		/**
		 * Control variable used to stop fetch requests
		 */
		_backbonePollEnabled : false,

		/**
		 * Default settings for the plugin
		 */
		_backbonePollSettings : {
			refresh : 60000, // rate at which the plugin fetches data, default one minute
			fetchOptions : Calipso.getDefaultFetchOptions(), // options for the fetch request
			retryRequestOnFetchFail : true
		// automatically retry request on fetch failure
		},

		/**
		 * Specify custom options for the plugin
		 * @param pollOptions object used to customize the plugin’s behavior
		 */
		configure : function(pollOptions) {
			this._backbonePollSettings = $.extend(true, {}, this._backbonePollSettings, pollOptions);
		},

		/**
		 * Starts the process of polling data from the server
		 * @returns {*}
		 */
		startFetching : function() {
			this._backbonePollEnabled = true;
			this._refresh(1);
			return this;
		},

		/**
		 * Periodically fetch data from a data source
		 * @param refreshRateMs rate in milliseconds at which the plugin fetches data
		 * @returns {*}
		 * @private
		 */
		_refresh : function(refreshRateMs) {
			this._backbonePollTimeoutId = setTimeout(_.bind(function() {
				if (this._backbonePollTimeoutId) {
					clearTimeout(this._backbonePollTimeoutId);
				}
				// Return if _refresh was called but the fetching is stopped
				// should not go this far since the timeout is cleared when fetching is stopped.
				if (!this._backbonePollEnabled) {
					return;
				}

				this.fetchRequest = this.fetch(this._backbonePollSettings.fetchOptions);

				this.fetchRequest.done(_.bind(function() {
					this.trigger('refresh:loaded');
					this._refresh(this._backbonePollSettings.refresh);
				}, this)).fail(_.bind(function() {
					this.trigger('refresh:fail');

					// If retryRequestOnFetchFail is true automatically retry request
					if (this._backbonePollSettings.retryRequestOnFetchFail) {
						this._refresh(this._backbonePollSettings.refresh);
					} else {
						this.stopFetching();
					}
				}, this)).always(_.bind(function() {
					this.trigger('refresh:always');
				}, this));
			}, this), refreshRateMs);
			return this;
		},

		/**
		 * Abort pending fetch requests
		 * @returns {*}
		 */
		abortPendingFetchRequests : function() {
			if (!_.isUndefined(this.fetchRequest) && !_.isUndefined(this.fetchRequest['abort'])) {
				this.fetchRequest.abort();
			}
			return this;
		},

		/**
		 * Checks to see if the plugin is polling data from a data source
		 * @returns {boolean} true if is fetching, false if it is not fetching
		 */
		isFetching : function() {
			return !(_.isUndefined(this._backbonePollTimeoutId));
		},

		/**
		 * Stops the process of polling data from the server
		 * @returns {*}
		 */
		stopFetching : function() {
			this._backbonePollEnabled = false;
			if (this.isFetching()) {
				if (this._backbonePollTimeoutId) {
					clearTimeout(this._backbonePollTimeoutId);
				}
				this._backbonePollTimeoutId = undefined;
			}
			this.abortPendingFetchRequests();
			return this;
		}

	});

	/**
	 * Get the name of this class
	 * @returns the class name as a string
	 */
	Calipso.collection.PollingCollection.prototype.getTypeName = function(instance) {
		return "Calipso.collection.PollingCollection";
	};
	//////////////////////////////////////////
	// Models
	//////////////////////////////////////////
	/**
	 * Abstract model implementation to extend through your own models.
	 * Subclasses of this model should follow the model driven
	 * design conventions used in the Calipso backbone stack, note the " REQUIRED" parts
	 * of the example for details. Properly extending this class allows
	 * "model driven" routes, forms, grids and selection of item/collection/layout views.
	 *
	 * @example
	 * // Load module
	 * require(['models/generic-model'], function(GenericModel) {
	 * 	// define our person model subclass
	 * 	var PersonModel = GenericModel.extend({
	 * 		// add stuff here
	 * 	},
	 * 	// static members
	 * 	{	// OPTIONAL: set the form schema cache behavior
	 * 		formSchemaCache : this.FORM_SCHEMA_CACHE_CLIENT
	 * 	});
	 *
	 * 	// REQUIRED: our subclass name
	 * 	PersonModel.prototype.getTypeName = function() {
	 * 		return "PersonModel";
	 * 	}
	 * 	// REQUIRED: our subclass URL path fragment,
	 * 	// e.g. "persons" for PersonModel. Used for dynamic MArionette router routes.
	 * 	PersonModel.prototype.getPathFragment = function() {
	 * 		return "persons";
	 * 	}
	 *
	 * 	// REQUIRED: our subclass grid schema
	 * 	PersonModel.prototype.getGridSchema = function() {
	 * 		//...
	 * 	}
	 * 	// REQUIRED: our subclass form schema
	 * 	PersonModel.prototype.getPrototypeFormSchemas = function() {
	 * 		//...
	 * 	}
	 *
	 * 	// OPTIONAL: our subclass layout view,
	 * 	// defaults to ModelDrivenBrowseLayout
	 * 	PersonModel.prototype.getLayoutViewType = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass collection view,
	 * 	// defaults to ModelDrivenCollectionGridView
	 * 	PersonModel.prototype.getCollectionViewType = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass item view,
	 * 	// defaults to ModelDrivenFormView
	 * 	PersonModel.prototype.getItemViewType = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass item view template,
	 * 	PersonModel.prototype.getItemViewTemplate = function() {
	 * 		//...
	 * 	}
	 * 	// OPTIONAL: our subclass business key,
	 * 	// used to check if the model has been loaded from the server.
	 * 	// defaults to "name"
	 * 	PersonModel.prototype.getBusinessKey = function() {
	 * 		//...
	 * 	}
	 *
	 * });
	 * @constructor
	 * @requires Backbone
	 * @requires Backgrid
	 * @augments module:Backbone.Model
	 */
	Calipso.model.GenericModel = Backbone.Model.extend(
	/** @lends Calipso.model.GenericModel.prototype */
	{
		getFormSubmitButton : function(){
			return null;
		},
		skipDefaultSearch : false,
		/**
		 * Returns the URL for this model, giving precedence  to the collection URL if the model belongs to one,
		 * or a URL based on the model path fragment otherwise.
		 */
		url : function() {
			var sUrl = this.collection && _.result(this.collection, 'url') ? _.result(this.collection, 'url') : Calipso.session.getBaseUrl() + '/api/rest/' + this.getPathFragment()/*_.result(this, 'urlRoot')*/|| urlError();
			//console.log("GenericModel#url, sUrl: " + sUrl);
			if (!this.isNew()) {
				sUrl = sUrl + (sUrl.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.get("id"));
			}
			// console.log("GenericModel#url: " + sUrl + ", is new: " + this.isNew() + ", id: " + this.get("id"));
			return sUrl;
		},
		/**
		 * Retusn true if the model is just a search collection wrapper, false otherwise
		 */
		isSearchModel : function(){
			return this.wrappedCollection ? true :false;
		},
		/*
		 * Will return <code>search</code> if the model is a search model,
		 * <code>create</code> if the model is new ans not a search model,
		 * <code>update</code> otherwise. The method is used to choose an appropriate
		 * form schema during form generation, see GenericFormView
		 */
		getFormSchemaKey : function(){
			var formSchemaKey;
			if(this.isSearchModel()){
				formSchemaKey = "search";
			}
			else{
				formSchemaKey = this.isNew() ? "create" : "update";
			}
			return formSchemaKey;
		},
		getFormTemplateKey : function(){
			var schemaKey = this.getFormSchemaKey();
			var formTemplateKey = "vertical";
			if( schemaKey.indexOf("report") == 0){
				formTemplateKey = "nav";
			}
			return formTemplateKey;
		},
		/**
		 * Get the URL path fragment for this model. Calls the prototype method with the same name.
		 * @returns the URL path fragment as a string
		 */
		getPathFragment : function() {
			return this.prototype.getPathFragment(this);
		},
		/**
		 * Get the name of this class. Calls the prototype method with the same name.
		 * TODO: switch to named constructors
		 * @returns the class name as a string
		 */
		getTypeName : function() {
			return this.prototype.getTypeName();
		},
		/**
		 *  Check if the model wants search result collections of it's type to be cached.
		 *  Calls the prototype method with the same name.
		 */
		isCollectionCacheable : function() {
			return this.prototype.isCollectionCacheable && this.prototype.isCollectionCacheable();
		},
		/**
		 * Get the layout view for this model. To specify a layout for your model under a static
		 * or instance context, override {@link Calipso.model.GenericModel.prototype.getLayoutViewType}
		 * or {@link getLayoutViewType} respectively in your subclass.
		 *
		 * Layout views defined this way are picked up by controllers.
		 *
		 * @see {@link Calipso.model.GenericModel.prototype.getLayoutViewType}
		 */
		getLayoutViewType : function() {
			return this.prototype.getLayoutViewType(this);
		},
		getLayoutOptions : function() {
			return this.prototype.getLayoutOptions(this);
		},
		/**
		 * Get the collection view type for collections of this model. To specify a collection
		 * view for your model under a static or instance context, override
		 * {@link Calipso.model.GenericModel.prototype.getCollectionViewType} or
		 * {@link getCollectionViewType} respectively in your subclass.
		 *
		 * Collection views defined this way are picked up by layout views..
		 *
		 * @see {@link Calipso.model.GenericModel.prototype.getCollectionViewType}
		 */
		getCollectionViewType : function() {
			return this.prototype.getCollectionViewType(this);
		},
		/**
		 * Get the collection view type for collections of this model. To specify a collection
		 * view for your model under a static or instance context, override
		 * {@link Calipso.model.GenericModel.prototype.getCollectionViewType} or
		 * {@link getCollectionViewType} respectively in your subclass.
		 *
		 * Collection views defined this way are picked up by layout views..
		 *
		 * @see {@link Calipso.model.GenericModel.prototype.getCollectionViewType}
		 */
		getReportCollectionViewType : function() {
			return this.prototype.getReportCollectionViewType(this);
		},
		/**
		 * Get the item view type for this model. To specify an item view for your model under a static
		 * or instance context, override {@link Calipso.model.GenericModel.prototype.getItemViewType}
		 * or {@link getItemViewType} respectively in your subclass.
		 *
		 * Layout views defined this way are picked up by controllers.
		 *
		 * @see {@link Calipso.model.GenericModel.prototype.getItemViewType}
		 */
		getItemViewType : function() {

			// console.log("GenericModel.getItemViewType() called, will return GenericFormView");
			return this.prototype.getItemViewType(this);
		},
		/**
		 * Get the item view template for this model. the template is picked up and
		 * used by item views like GenericView.  To specify an item view template for
		 * your model under a static or instance context,
		 * override {@link Calipso.model.GenericModel.prototype.getItemViewTemplate}
		 * or {@link getItemViewTemplate} respectively in your subclass.
		 *
		 * Layout views defined this way are picked up by controllers.
		 *
		 * @see {@link Calipso.model.GenericModel.prototype.getItemViewType}
		 */
		getItemViewTemplate : function() {
			// console.log("GenericModel.getItemViewTemplate() called");
			return this.prototype.getItemViewTemplate(this);
		},
		/**
		 * Get the complete set of form schemas. You can also obtain the form schema for
		 * a specific action like "create", "update" or "search" using
		 * {@linkcode getFormSchema} instead.
		 *
		 * To define form schemas for your subclass under a static or instance context on the client-side, override
		 * {@link Calipso.model.GenericMogetPrototypeFormSchemasgetPrototypeFormSchemas} or {@link getFormSchemas} respectively.
		 *
		 * This method will attempt to retrieve the model schema in the following order:
		 * 	<ul><li>Schema set to the model by the server</li>
		 * 	<li>schemas defined by the model's prototype object</li>
		 * 	</ul>
		 *
		 * Form schemas are picked up by form views like {@linkcode GenericFormView} or layout views
		 * that use such form views in their regions.
		 *
		 * @see {@link Calipso.model.GenericModel.prototype.getPrototypeFormSchemas}
		 */
		getFormSchemas : function() {

			// console.log("GenericModel.getFormSchemas");
			var formSchema;

			// console.log("GenericModel.getFormSchemas, schema mode: " + this.formSchemaCacheMode);
			if (this.formSchemaCacheMode == this.FORM_SCHEMA_CACHE_CLIENT) {
				formSchema = this.getPrototypeFormSchemas(this);
			}
			if (formSchema) {
			} else {
				formSchema = this.getPrototypeFormSchemas(this);
			}
			return formSchema;
		},
		/**
		 * Get the form schema for a specific action like "create", "update", "search" or "report".
		 *
		 * To define form schemas for your subclass under a static or instance context, override
		 * {@link Calipso.model.GenericModel.prototype.getPrototypeFormSchemas} or {@link getFormSchemas} respectively.
		 *
		 * Form schemas are used by form views like {@linkcode GenericFormView} or layout views
		 * that use such form views in their regions.
		 *
		 * @param {string} actionName for example "create", "update" or "search"
		 * @see {@link getFormSchemas}
		 * @todo implement optional merging of superclass schemas by using the supermodel.parent property
		 */
		getFormSchema : function(actionName) {
			// decide based on model persistence state if no action was given
			if (!actionName) {
				//console.log("GenericModel#getFormSchema actionName: "+ actionName);
				// console.log("GenericModel.prototype.schema, this: " + this);
				// console.log("GenericModel.prototype.schema, caller is " + arguments.callee.caller.toString());
				actionName = this.getFormSchemaKey();
				//console.log("GenericModel#getFormSchema actionName: "+ actionName);
			}
			// the schema to build for the selected action
			var formSchema = {};
			// get the complete schema to filter out from
			// console.log("GenericModel#getFormSchema calling : this.getFormSchemas()");
			var formSchemas = this.getFormSchemas();

			// for each property, select the appropriate schema entry for the given
			// action
			var propertySchema;
			var propertySchemaForAction;
			for ( var propertyName in formSchemas) {
				if (formSchemas.hasOwnProperty(propertyName)) {
					propertySchema = formSchemas[propertyName];

					// if a schema exists for the property
					if (propertySchema) {
						// try obtaining a schema for the specific action
						var partialSchema = propertySchema[actionName];
						// support default fallback
						if (!partialSchema) {
							partialSchema = propertySchema["default"];
						}
						if(partialSchema){
							propertySchemaForAction = {};
							// extend on top of "extend" if avalable
							if(partialSchema.extend){
								var extendArr = partialSchema.extend;
								if(!$.isArray(extendArr) ){
									extendArr = [extendArr];
								}
								for(var i = 0; i < extendArr.length; i++){
									var toAdd = extendArr[i];
									// if ref to another action key, resolve it
									if(toAdd instanceof String || typeof toAdd === "string"){
										toAdd = propertySchema[toAdd+''];
									}
									$.extend(true, propertySchemaForAction, toAdd);
								}
							}
							// add explicit schema for action key
							$.extend(true, propertySchemaForAction, partialSchema);
						}
						// add final schema for field
						if (propertySchemaForAction) {
							formSchema[propertyName] = propertySchemaForAction;
						}
					}
					else{
						console.log("WARNING GenericModel#getFormSchema, no "+ actionName + "schema found for property: "+ propertyName);
					}
				}
				else{
					console.log("WARNING GenericModel#getFormSchema, no schema found for property: "+ actionName);
				}

				// reset
				propertySchema = false;
				propertySchemaForAction = false;
			}
			return formSchema;
		},
		initialize : function() {
			Backbone.Model.prototype.initialize.apply(this, arguments);
			var thisModel = this;
			// make any submit button available to templates
			if( this.getFormSubmitButton()){
				this.set("calipsoFormSubmitButton", this.getFormSubmitButton());
			}
			this.on("change", function(model, options) {
				if (options && options.save === false) {
					return;
				}
			});
		},
		sync: function() {
			// partial update hints
			if(!this.isNew()){
				this.set("changedAttributes", this.changedAttributes());
			}
			return Backbone.Model.prototype.sync.apply(this, arguments);
		},
	}, {
		// static members
		/** (Default) 0Do not retrieve the form schema from the server */
		FORM_SCHEMA_CACHE_CLIENT : "FORM_SCHEMA_CACHE_CLIENT",
		/** Retrieve the form schema only once for all model instances */
		FORM_SCHEMA_CACHE_STATIC : "FORM_SCHEMA_CACHE_STATIC",
		/** Retrieve the form schema only once per model instance */
		FORM_SCHEMA_CACHE_INSTANCE : "FORM_SCHEMA_CACHE_INSTANCE",
		/** Retrieve the form schema every time it is accessed */
		FORM_SCHEMA_CACHE_NONE : "FORM_SCHEMA_CACHE_NONE",
		formSchemaCacheMode : this.FORM_SCHEMA_CACHE_CLIENT,
		typeName : "Calipso.model.GenericModel",
		label : "GenericModel",
		create : function(attrs, options){
			return new this(attrs, options);
		}

	});

	Calipso.model.GenericModel.prototype.getLayoutOptions = function(){
		return {};
	};
	Calipso.model.GenericModel.prototype.isPublic = function(){
		return false;
	};
	Calipso.model.GenericModel.prototype.showInMenu = false;
	/**
	 * Get the model class URL fragment corresponding to your server
	 * side controller, e.g. "users" for UserModel. Model subclasses
	 * are required to implement this method.
	 * @returns the URL path fragment as a string
	 */
	Calipso.model.GenericModel.prototype.getPathFragment = function(instance) {
		throw "Model subclasses must implement GenericModel.prototype.getPathFragment";
	};

	/**
	 * Stores a map acting as a typeahead sources cache. Uses source config (pathFragment, query and wildcard) as the key;
	 */
	Calipso.model.GenericModel.prototype.typeaheadSources = {};
	Calipso.model.GenericModel.prototype.getTypeaheadSource = function(options) {
		var _thisProto = this;
		var config = {
				query: "?name=%25wildcard%25",
				wildcard : "wildcard",
				pathFragment : _thisProto.getPathFragment(),
		};
		_.extend(config, options);
		var sourceKey = config.pathFragment + config.wildcard + config.query;
		// if not lready created
		if (!_thisProto.typeaheadSources[sourceKey]) {
			var sourceUrl = Calipso.session.getBaseUrl() + "/api/rest/" + config.pathFragment + config.query;
			//console.log(_thisProto.getTypeName() + "#getTypeaheadSource creating new source for url " + sourceUrl);
			var bloodhound = new Bloodhound({
				remote : {
					url : sourceUrl,
					wildcard : config.wildcard,
					transform : function(response) {
						//console.log(_thisProto.getTypeName() + ' transform', response.content);
						return response.content;
					}
				},
				identify : function(obj) {
					return obj.id;
				},
				queryTokenizer : Bloodhound.tokenizers.whitespace,
				datumTokenizer : function(d) {
					return Bloodhound.tokenizers.whitespace(d.name);
				},
			});
			bloodhound.initialize();
			_thisProto.typeaheadSources[sourceKey] = bloodhound.ttAdapter();
		}

		return _thisProto.typeaheadSources[sourceKey];
	};
	/**
	 * Get the name of this class
	 * @returns the class name as a string
	 */
	Calipso.model.GenericModel.prototype.getTypeName = function(instance) {
		return "Calipso.model.GenericModel";
	};
	/**
	 * Check if the model type requires it's search collections to be cached
	 */
	Calipso.model.GenericModel.prototype.isCollectionCacheable = function() {
		return false;
	};

	/**
	 * Override this to declaratively define
	 * grid views for your subclass
	 */
	Calipso.model.GenericModel.prototype.getGridSchema = function(instance) {
		//console.log("GenericModel.prototype.getGridSchema() called, will return undefined");
		return undefined;
	};

	/**
	 * Override this in your subclass to declaratively define
	 * form views for the default or custom actions
	 */
	Calipso.model.GenericModel.prototype.getPrototypeFormSchemas = function(instance) {
		//console.log("GenericModel.prototype.getFormSchema() called, will return undefined");
		return undefined;
	};

	/**
	 * Override this to define a default layout view at a static context for your subclass,
	 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenBrowseLayout}
	 */
	Calipso.model.GenericModel.prototype.getLayoutViewType = function(instance) {
		console.log("GenericModel.prototype.getLayoutViewType() called, will return ModelDrivenSearchLayout");
		return Calipso.view.ModelDrivenSearchLayout;
	};

	/**
	 * Override this to define a default collection view like the
	 * default {@link ModelDrivenCollectionGridView}
	 * at a static context for your subclass,
	 *@returns {@link ModelDrivenCollectionGridView}
	 */
	Calipso.model.GenericModel.prototype.getCollectionViewType = function(instance) {
		//console.log("GenericModel.prototype.getCollectionViewType() called, will return ModelDrivenCollectionGridView");
		return Calipso.view.ModelDrivenCollectionGridView;
	};

	/**
	 * Override this to define a default report view like the
	 * default {@link ModelDrivenCollectionGridView}
	 * at a static context for your subclass,
	 *@returns {@link Calipso.view.ModelDrivenReportView}
	 */
	Calipso.model.GenericModel.prototype.getReportCollectionViewType = function(instance) {
		return Calipso.view.ModelDrivenReportView;
	};

	/**
	 * Override this to define a default layout view at a static context for your subclass,
	 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenSearchLayout}
	 */
	Calipso.model.GenericModel.prototype.getItemViewType = function(instance) {
		//console.log("GenericModel.prototype.getItemViewType() called, will return GenericFormView");
		return Calipso.view.GenericFormView;
	};
	/**
	 * Override this to define a default layout view at a static context for your subclass,
	 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenSearchLayout}
	 */
	Calipso.model.GenericModel.prototype.getItemViewTemplate = function(instance) {
		//console.log("GenericModel.prototype.getItemViewTemplate() called, will return null");
		return null;
	};
	/**
	 * Get the name of the model's business key property. The property name is used to
	 * check whether a model instance has been loaded from the server. The default is "name".
	 *
	 * @returns the business key if one is defined by the model class, "name" otherwise
	 */
	Calipso.model.GenericModel.prototype.getBusinessKey = function(instance) {
		return "name";
	};

	Calipso.model.UserModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.UserModel.prototype */
	{
		toString : function() {
			return this.get("username");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		parent : Calipso.model.GenericModel,
		label : "User",
		formSchemaCacheMode : this.FORM_SCHEMA_CACHE_STATIC
	});

	Calipso.model.UserModel.prototype.showInMenu = true;
	/**
	 * Get the model class URL fragment corresponding this class
	 * @returns the URL path fragment as a string
	 */
	Calipso.model.UserModel.prototype.getPathFragment = function(instance) {
		return "users";
	};

	Calipso.model.UserModel.prototype.getTypeName = function(instance) {
		return "UserModel";
	};

	Calipso.model.UserModel.prototype.getPrototypeFormSchemas = function(instance) {
		console.log("UserModel.prototype.getPrototypeFormSchemas for " + instance.getTypeName());
		var rolesCollection = new Calipso.collection.AllCollection([], {
			url : function() {
				return Calipso.session.getBaseUrl() + "/api/rest/" + Calipso.model.RoleModel.prototype.getPathFragment();
			},
			model : Calipso.model.RoleModel,
		});
		var text = {type : 'Text'};
		var textRequired = {type : 'Text', validators : [ 'required' ]};
		return {//
			firstName : {
				"search" : text,
				"default" : textRequired,
			},
			lastName : {
				"search" : text,
				"default" : textRequired,
			},
			username : {
				"search" : text,
				"default" : textRequired,
			},
			email : {
				"search" : {
					type : 'Text',
					title: "Email",
					dataType: "email",
					validators : [ 'email' ]
				},
				"default" : {
					type : 'Text',
					title: "Email",
					dataType: "email",
					validators : [ 'required', 'email' ]
				}
			},
			telephone : {
				"default" : {
					type : Calipso.components.backboneform.Tel,
					dataType: "tel",
					validators : [ Calipso.components.backboneform.validators.digitsOnly ]
				}
			},
			cellphone : {
				"default" : {
					type : Calipso.components.backboneform.Tel,
					dataType: "tel",
					validators : [ Calipso.components.backboneform.validators.digitsOnly ]
				},
			},
			active : {
				"base" : {
					type : 'Checkbox',
					title: "Active",
				},
				"create" : {
					extend: "base",
					help: "Select to skip email confirmation"
				},
				"update" : {
					extend: "base",
				},
			},
			roles : {
				"base" : {
					title: "Roles",
					type : Backbone.Form.editors.ModelSelect2,
					options: rolesCollection,
					multiple: true,
				},
				"search" : {
					title: "Roles",
					type : Backbone.Form.editors.ModelSelect2,
					options: rolesCollection,
					multiple: true,
				},
				"create" : {
					title: "Roles",
					type : Backbone.Form.editors.ModelSelect2,
					options: rolesCollection,
					multiple: true,
					validators : [ 'required' ],
				},
				"update" : {
					title: "Roles",
					type : Backbone.Form.editors.ModelSelect2,
					options: rolesCollection,
					multiple: true,
				},
			}
		};

	};

	Calipso.model.UserModel.prototype.getGridSchema = function(instance) {
		return [ {
			name : "username",
			label : "Username",
			cell : Calipso.components.backgrid.ViewRowCell,
			editable : false
		}, {
			name : "firstName",
			label : "First Name",
			editable : false,
			cell : "string"
		}, {
			name : "lastName",
			label : "Last Name",
			editable : false,
			cell : "string"
		}, {
			name : "email",
			label : "Email",
			cell : "email",
			editable : false
		}, {
			name : "createdDate",
			label : "Created",
			cell : "date",
			editable : false
		}, {
			name : "edit",
			label : "",
			editable : false,
			cell : Calipso.components.backgrid.EditRowInModalCell,
			headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
		} ];
	};
	Calipso.model.UserModel.prototype.getOverviewSchema = function(instance) {
		return [ {
			member : "username",
			label : "username"
		}, {
			fetchUrl : "/api/rest/users",
			// merge in this model if missing:
			// modelType: Foobar,
			member : "mergedAttribute",
			label : "merged attribute",
			viewType : Calipso.view.CollectionMemberGridView
		} ];
	};

	// Role model
	// ---------------------------------------
	Calipso.model.RoleModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.RoleModel.prototype */
	{
		toString : function() {
			return this.get("name");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		parent : Calipso.model.GenericModel,
		label : "Role",
		formSchemaCacheMode : this.FORM_SCHEMA_CACHE_STATIC
	});

	Calipso.model.RoleModel.prototype.showInMenu = true;
	/**
	 * Get the model class URL fragment corresponding this class
	 * @returns the URL path fragment as a string
	 */
	Calipso.model.RoleModel.prototype.getPathFragment = function(instance) {
		return "roles";
	};

	Calipso.model.RoleModel.prototype.getTypeName = function(instance) {
		return "RoleModel";
	};

	Calipso.model.RoleModel.prototype.getPrototypeFormSchemas = function(instance) {
		var schemas = instance.get("formSchema");
		return schemas ? schemas : {//
			name : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			description : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			}
		};

	};

	Calipso.model.RoleModel.prototype.getGridSchema = function(instance) {
		return [ {
			name : "name",
			label : "Name",
			cell : Calipso.components.backgrid.ViewRowCell,
			editable : false
		}, {
			name : "description",
			label : "Description",
			editable : false,
			cell : "string"
		}, {
			name : "edit",
			label : "",
			editable : false,
			cell : Calipso.components.backgrid.EditRowInModalCell,
			headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
		} ];
	};

	// Notification Model
	// -----------------------------------------
	Calipso.model.BaseNotificationModel = Calipso.model.GenericModel.extend({
	},
	// static members
	{
		parent : Calipso.model.GenericModel,
	});

	Calipso.model.BaseNotificationModel.prototype.getPathFragment = function(instance) {
		return "baseNotifications";
	};

	Calipso.model.BaseNotificationModel.prototype.getTypeName = function(instance) {
		return "BaseNotificationModel";
	};

	Calipso.model.UserDetailsModel = Calipso.model.UserModel.extend(
	/** @lends Calipso.model.UserDetailsModel.prototype */
	{
		isSearchModel : function(){
			return false;
		},
		toString : function() {
			return this.get("username");
		},
		sync : function(method, model, options) {
			var _this = this;
			options = options || {};
			options.timeout = 30000;
			if (!options.url) {
				options.url = Calipso.session.getBaseUrl() +
					Calipso.getConfigProperty("apiAuthPath") + "/" +
					_this.getPathFragment();
			}
			// options.dataType = "jsonp"; // JSON is default.
			return Backbone.sync(method, model, options);
		}

	});
	/**
	 * Get the model class URL fragment corresponding this class
	 * @returns the URL path fragment as a string
	 */
	Calipso.model.UserDetailsModel.prototype.getPathFragment = function() {
		return "userDetails";
	};
	Calipso.model.UserDetailsModel.prototype.getTypeName = function() {
		return "UserDetailsModel";
	};
	Calipso.model.UserDetailsModel.prototype.getLayoutViewType = function() {
		return Calipso.view.UserRegistrationLayout;
	};


		// User Registration Model
		// -----------------------

		/**
		 * Subclasses UserModel to provide layout, forms etc. configuration
		 * for user registration flows.
		 */
		Calipso.model.UserRegistrationModel = Calipso.model.GenericModel.extend(
		/** @lends Calipso.model.UserRegistrationModel */{
			label: "Register",
			getFormSubmitButton : function(){
				return "<i class=\"fa fa-floppy-o\"></i>&nbsp;Register"
			},
			getFormTemplateKey : function(){
				return "auth";
			}
		}, {
			// static members
			parent: Calipso.model.UserModel,
			label: "Register"
		});

		Calipso.model.UserRegistrationModel.prototype.getPathFragment = function(instance) {
		return "users";
		};
		Calipso.model.UserRegistrationModel.prototype.showInMenu = false;
		Calipso.model.UserRegistrationModel.prototype.getTypeName = function(instance) {
			return "Calipso.model.UserRegistrationModel";
		};

		Calipso.model.UserRegistrationModel.prototype.getItemViewType = function(instance) {
			return Calipso.view.GenericFormPanelView;
		};
		/**
		 * Get the model class URL fragment corresponding this class
		 * @returns the URL path fragment as a string

		Calipso.model.UserRegistrationModel.prototype.getPathFragment = function(instance) {
			return "userRegistrations";
		};*/
		/**
		 * Override this to define a default layout view at a static context for your subclass,
		 * like {@link ModelDrivenCrudLayout} or {@link ModelDrivenBrowseLayout}
		 */
		Calipso.model.UserRegistrationModel.prototype.getLayoutViewType = function(instance) {
			console.log("UserRegistrationModel.prototype.getLayoutViewType() called, will return ModelDrivenSearchLayout");
			return Calipso.view.UserRegistrationLayout;
		};
		Calipso.model.UserRegistrationModel.prototype.getPrototypeFormSchemas = function(instance) {
			console.log("UserRegistrationModel.prototype.getPrototypeFormSchemas for " + instance.getTypeName());
			var requiredText = {
				type : 'Text',
				validators : [ 'required' ]
			};
			var passwordText = {
				type : 'Password',
				validators : [ 'required' ]
			};
			var passwordConfirm = {
				type : 'Password',
				validators: [{
					type: 'match', field: 'password', message: 'Passwords must match!'
				}]
			};
			return {//
				firstName : {
					"create" : requiredText,
					"update" : requiredText
				},
				lastName : {
					"create" : requiredText,
					"update" : requiredText
				},
				username : {
					"create" : requiredText,
					"update" : requiredText
				},
				email : {
					"search" : {
						title : "Username or email",
						type : 'Text',
					},
					"default" : {
						type : 'Text',
						validators : [ 'required', 'email' ]
					}
				},
				password : {
					"create" : passwordText,
					"update" : passwordText
				},
				passwordConfirm : {
					"create" : passwordConfirm,
					"update" : passwordConfirm
				},
			};

		};


	Calipso.model.UserDetailsConfirmationModel = Calipso.model.UserDetailsModel.extend(
	/** @lends Calipso.model.UserDetailsModel.prototype */
	{
		getFormSubmitButton : function(){
			return "<i class=\"fa fa-floppy-o\"></i>&nbsp;Confirm"
		}
	},
	{
		label: "Email Confirmation"
	});
	/**
	 * Get the model class URL fragment corresponding this class
	 * @returns the URL path fragment as a string
	 */
	Calipso.model.UserDetailsConfirmationModel.prototype.getPathFragment = function() {
		return "accountConfirmations";
	};
	Calipso.model.UserDetailsConfirmationModel.prototype.getFormSchema = function(instance) {
		return {//
			confirmationToken : {
				title : 'Please check your email for a confirmation key',
				type : 'Text',
				validators : [ 'required' ]
			}
		};
	};
Calipso.model.UserDetailsConfirmationModel.prototype.getItemViewType = function() {
	return Calipso.view.GenericFormPanelView.extend(
		{
			commit : function(e) {
					Calipso.stopEvent(e);
					if(!this.isFormValid()){
						return false;
					}
					// if no validation errors,
					// use the email confirmation link route
					else{
						Calipso.navigate("accountConfirmations/" + this.model.get("confirmationToken"), {
							trigger : true
						});
					}
			}
		});

};


		// Report Dataset Model
		// This model is used by the router controller when a
		// subjectModelTypeFragment/reports
		// route is matched, where the subjectModelType matches a model type's URL fragent.
		// The controller uses the ReportDataSetModel
		// as the route model after configuring it with the targe rRoute model
		// type, from which the ReportDataSetModel obtains any custom configuration
		// for route layouts, views and form/grid schemas according to the following table:
		// ReportDataSet                    ModelType
		// getLayoutViewType()              prototype.getReportLayoutType()
		// getCollectionViewType()          prototype.getReportCollectionViewType()
		// getPathFragment()                prototype.getPathFragment() + "/reports"
		// getFormSchemaKey()               "report"
		// getReportKpiOptions()            prototype.getReportKpiOptions(this.get("reportType"/*URL param*/)
		// -----------------------------------------
	Calipso.model.ReportDataSetModel = Calipso.model.GenericModel.extend({
		subjectModelType : null,
		// TODO: inline form tmpl
		defaults : {
			formTemplateKey : "horizontal",
			kpi : "sum",
			timeUnit : "DAY",
			reportType : "Businesses",
			calipsoFormSubmitButton : "Show Report"
		},
		initialize : function() {
			Calipso.model.GenericModel.prototype.initialize.apply(this, arguments);
			//this.subjectModelType = options.subjectModelType;
			var subjectModelType = this.get("subjectModelType");
			console.log("Calipso.model.ReportDataSetModel#initialize, subjectModelType: ");
			console.log(subjectModelType);
			console.log("Calipso.model.ReportDataSetModel#initialize, attributes: ");
			console.log(this.attributes);
			if(!(_.isNull(subjectModelType) || _.isUndefined(subjectModelType))){
				this.set("reportType", subjectModelType.prototype.getReportTypeOptions()[0]);
				var now = new Date();
				this.set("period", (now.getUTCMonth()+1) + '/' + now.getUTCFullYear());
			}
		},
		getPathFragment : function(){
			return this.get("subjectModelType").prototype.getPathFragment() + "/reports";
		},
		getFormSchemaKey : function(){
			return "report";
		},
		getCollectionViewType : function(){
			return this.get("subjectModelType").prototype.getReportCollectionViewType();
		},
		getLayoutViewType : function(){
			return this.get("subjectModelType").prototype.getReportLayoutType
				? this.get("subjectModelType").prototype.getReportLayoutType()
				: Calipso.view.ModelDrivenReportLayout;
		},
		getReportTypeOptions : function(){
			return this.get("subjectModelType").prototype.getReportTypeOptions
				? this.get("subjectModelType").prototype.getReportTypeOptions()
				: null;
		},
		getReportKpiOptions : function(reportType){
			var options;
			if(!reportType){
				reportType = this.get("reportType");
			}

			if(this.get("subjectModelType").prototype.getReportKpiOptions){
				options = this.get("subjectModelType").prototype.getReportKpiOptions(reportType);
			}

			if(!options){
				options = [
					{ val: "sum", label: 'Sum' },
					{ val: "count", label: 'Count' }
				];
			}
			return options;
		},
		getFormSchema : function(actionName){
			console.log("Calipso.model.ReportDataSetModel#getFormSchema actionName: " + actionName);
			var formSchema = {};
			var reportTypeOptions = this.getReportTypeOptions();
			if(reportTypeOptions){
				formSchema.reportType = {
					title: "Report Type",
					type : 'Select',
					options : reportTypeOptions,
					template: this.fieldTemplate
					// TODO: validate option
					// validators : [ 'required' ]
				};
			}

			formSchema.kpi = {
				title: "KPI",
				type: 'Select',
				options : this.getReportKpiOptions(),
				template: this.fieldTemplate
				// TODO: validate option
				// validators : [ 'required' ]
			};
			formSchema.timeUnit = {
				title: "by",
				type: 'Select',
				options : [
					{ val: "DAY", label: 'Day' },
					{ val: "MONTH", label: 'Month' }
				],
				template: this.fieldTemplate
				// TODO: validate option
				// validators : [ 'required' ]
			};

			formSchema.period = {
				title: "Period",
				type: Calipso.components.backboneform.Datetimepicker,
				template: this.fieldTemplate,
				config: {
					locale: 'en',
          format: 'MM/YYYY',
					viewMode: 'months',
					widgetPositioning: {
						horizontal : "right"
					}
				},
				validators : [ 'required' ]
			};
			console.log("Calipso.model.ReportDataSetModel#getFormSchema formSchema: ");
			console.log(formSchema);
			return formSchema;
		},
		getGridSchema : function(kpi){
		console.log("Calipso.model.ReportDataSetModel#getGridSchema kpi: " + kpi);
			// sum or count
			if(!kpi){
				kpi = this.get("kpi");
				console.log("Calipso.model.ReportDataSetModel#getGridSchema this.kpi: " + kpi);
			}
			var schema = [{
				name : "label",
				label : "",
				editable : false,
				cell : "text",
			}];
			console.log("Calipso.model.ReportDataSetModel#getGridSchema returns: ");
			var entries = this.wrappedCollection.first().get("entries");
			for(var i = 0 ; i < entries.length; i++){
				schema.push({
					name : "entries." + i + ".entryData." + kpi,
					label : entries[i].label,
					editable : false,
					cell : Calipso.components.backgrid.ChildNumberAttributeCell,
				});
			}
			console.log("Calipso.model.ReportDataSetModel#getGridSchema returns: ");
			console.log(schema);
			return schema;
		},
		fieldTemplate : _.template('\
	    <div class="form-group field-<%= key %>">&nbsp;\
	      <label class="control-label" for="<%= editorId %>">\
	        <% if (titleHTML){ %><%= titleHTML %>\
	        <% } else { %><%- title %><% } %>\
	      </label>&nbsp;\
        <span data-editor></span>\
	    </div>&nbsp;\
	  '),
	},
	// static members
	{
		parent : Calipso.model.GenericModel,
	});


	Calipso.model.ReportDataSetModel.prototype.getTypeName = function() {
		return "Calipso.model.ReportDataSetModel";
	};


	Calipso.model.ReportDataSetModel.prototype.getItemViewType = function() {
		return Calipso.view.ReportFormView;
	};

	Calipso.model.ReportDataSetModel.prototype.getCollectionViewType = function() {
		return Calipso.view.ModelDrivenReportView;
	};

	//////////////////////////////////////////////////
	// UI components
	//////////////////////////////////////////////////
	Calipso.components.backgrid = {};
	Calipso.components.backboneform = {};
	Calipso.components.backboneform.validators = {
		digitsOnly : function (value, formValues) {
      if(value){
				var reg = /^\d+$/;
	      if (!reg.test(value)){
	       	return {
	        	message: 'Numerical digits only'
					};
	      }
			}
    },
	};
	Calipso.components.backboneformTemplates = {
			vertical :  _.template('\
		    <div class="form-group field-<%= key %>">\
		      <label class="control-label" for="<%= editorId %>">\
		        <% if (titleHTML){ %><%= titleHTML %>\
		        <% } else { %><%- title %><% } %>\
		      </label>\
		      <div class="">\
		        <span data-editor></span>\
		        <p class="help-block" data-error></p>\
		        <p class="help-block"><%= help %></p>\
		      </div>\
		    </div>\
		  '),
			inline :  _.template('\
		    <div class="form-group field-<%= key %>">\
		      <label class="control-label" for="<%= editorId %>">\
		        <% if (titleHTML){ %><%= titleHTML %>\
		        <% } else { %><%- title %><% } %>\
		      </label>\
		      <div class="">\
		        <span data-editor></span>\
		        <p class="help-block" data-error></p>\
		        <p class="help-block"><%= help %></p>\
		      </div>\
		    </div>\
		  ')
	};
	Calipso.components.backgrid.SmartHighlightRow = Backgrid.Row.extend({
  	initialize: function() {
			Backgrid.Row.prototype.initialize.apply(this, arguments);
      this.listenTo(this.model, 'change', function (model) {
				console.log("Calipso.components.backgrid.SmartHighlightRow caught model change");
        this.$el.toggleClass('bg-warning', model.hasChanged());
      });
      this.listenTo(this.model, 'sync', function (model) {
				console.log("Calipso.components.backgrid.SmartHighlightRow caught model sync");
				// creating an empty element and applying our class to it to get bootstrap class bg color
				var origBg = this.$el.css("background-color");
				var bgcolor = $("<div>").appendTo("body").addClass("bg-success").css("background-color");

				this.$el.removeClass('bg-warning');
				this.$el.animate({ backgroundColor: bgcolor }, {queue:true,duration:1500});
				this.$el.animate({ backgroundColor: origBg }, {queue:true,duration:5000});
      });
      this.listenTo(this.model, 'added', function (model) {
				console.log("Calipso.components.backgrid.SmartHighlightRow caught model added");
				// creating an empty element and applying our class to it to get bootstrap class bg color
				var origBg = this.$el.css("background-color");
				var bgcolor = $("<div>").appendTo("body").addClass("bg-success").css("background-color");
        this.$el.removeClass('bg-warning');
				this.$el.animate({ backgroundColor: bgcolor }, {queue:true,duration:1500});
				this.$el.animate({ backgroundColor: origBg }, {queue:true,duration:5000});
      });
    }
	});
	Calipso.components.backgrid.ViewRowCell = Backgrid.StringCell.extend(
	/** @lends Calipso.components.backgrid.ViewRowCell.prototype */
	{
		className : "view-row-cell",
		initialize : function(options) {
			Backgrid.StringCell.prototype.initialize.apply(this, arguments);
			this.viewRowEvent = "layout:viewModel";
		},
		events : {
			"click" : "viewRow"
		},
		viewRow : function(e) {
			// console.log("ViewRowCell#viewRow, rowModel: " + this.model.getTypeName());
			Calipso.stopEvent(e);
			Calipso.vent.trigger(this.viewRowEvent, this.model);
		},
		render : function() {
			this.$el.empty();
			var model = this.model;
			var formattedValue = this.formatter.fromRaw(model.get(this.column.get("name")), model);
			this.$el.append($("<a>", {
				tabIndex : -1,
				title : formattedValue
			}).text(formattedValue));
			this.delegateEvents();
			return this;
		}

	});

	//

	Calipso.components.backgrid.BootstrapSwitchCell = Backgrid.BooleanCell.extend(
	/** @lends Calipso.components.backgrid.BootstrapSwitchCell.prototype */
	{
		render : function() {
			Backgrid.BooleanCell.prototype.render.apply(this, arguments);
			var _this = this;
			setTimeout(function(){
				_this.$el.find("input").bootstrapSwitch();
			}, 250);
			return this;
		}
	});

	Calipso.components.backgrid.EditRowCell = Backgrid.Cell.extend(
	/** @lends Calipso.components.backgrid.EditRowCell.prototype */
	{
		tagName : "td",
		className: "modal-button-cell modal-button-cell-edit",
		events : {
			"click" : "editEntry",
			"click button" : "editEntry",
		},
		editEntry : function(e) {
			Calipso.stopEvent(e);
			var rowModel = this.model;
			//console.log("EditInTabCell#editEntry "+ rowModel.getTypeName()+'#'+rowModel.get("id"));
			// console.log("editRow, rowModel: " + rowModel.constructor.name);
			Calipso.vent.trigger("genericShowContent", rowModel);
		},
		render : function() {
			this.$el.html("<button class='btn btn-xs btn-info' title='Edit entry'><i class='glyphicon glyphicon-edit'></i>&nbsp;Edit</button>");
			//this.delegateEvents();
			return this;
		}
	});

	Calipso.components.backgrid.EditRowInModalCell = Calipso.components.backgrid.EditRowCell.extend(
	/** @lends Calipso.components.backgrid.EditRowCell.prototype */
	{
		editEntry: function(e) {
			Calipso.stopEvent(e);
			var rowModel = this.model;
			var ContentViewType = rowModel.getItemViewType();
			var contentView = new ContentViewType({
				model : rowModel,
				modal : true
			});
			var title = "Edit: ";
			if(rowModel.get("name")){
				title += rowModel.get("name");
			}
			else if(rowModel.get("label")){
				title += rowModel.get("label");
			}
			else{
				//title += rowModel.get("id");
			}
			Calipso.vent.trigger("modal:showInLayout", {
				view: contentView,
				title: title
			});
		}
	});


	Calipso.components.backgrid.ChildStringAttributeCell = Backgrid.StringCell.extend({
		render : function() {
			var path = this.column.get("path");
			if(!path){
				path = this.column.get("name");
			}
			var result = Calipso.getPathValue(this.model, path);
			if(!(_.isUndefined(result) || _.isNull(result))){
				this.$el.text(result);
			}
			this.delegateEvents();
			return this;
		},
	});

	Calipso.components.backgrid.ChildNumberAttributeCell = Backgrid.NumberCell.extend({
		render : function() {
			var path = this.column.get("path");
			if(!path){
				path = this.column.get("name");
			}
			var result = Calipso.getPathValue(this.model, path);
			if(!(_.isUndefined(result) || _.isNull(result))){
				console.log("type of result: "+	(typeof result));
				this.$el.text(this.formatter.fromRaw(result));
			}
			this.delegateEvents();
			return this;
		},
	});

	Calipso.components.backgrid.CreateNewHeaderCell  = Backgrid.HeaderCell.extend({

		tagName : "th",
		className : "renderable backgrid-create-new-header-cell",
		events : {
			"click" : "createNewForManualEdit"
		},
		initialize : function(options) {
			Backgrid.HeaderCell.prototype.initialize.apply(this, arguments);
			console.log("Calipso.components.backgrid.CreateNewHeaderCell#nitialize");
		},
		createNewForManualEdit : function(e) {
			//console.log("CreateNewHeaderCell#newRow, rowModel: " + this.collection.model);
			Calipso.stopEvent(e);
			Calipso.vent.trigger("layout:createModel", {modelType: this.collection.model});
		},
		render : function() {
			var html = $("<button title='Create new' class='btn btn-xs btn-success'><i class='fa fa-file-text'></i>&nbsp;New</button>");
			this.$el.html(html);
			//this.delegateEvents();
			return this;
		}
	});

	Calipso.components.backgrid.CreateNewInModalHeaderCell = Calipso.components.backgrid.CreateNewHeaderCell.extend({

		createNewForManualEdit : function(e) {
			Calipso.stopEvent(e);
			var rowModel = this.collection.model.create();
			var ContentViewType = rowModel.getItemViewType();
			var contentView = new ContentViewType({
				model : rowModel,
				modal : true,
				addToCollection : this.collection
			});
			var title = "New ";
			if(this.collection.model.label){
				title += this.collection.model.label;
			}
			else{
				title += "entry";
			}
			Calipso.vent.trigger("modal:showInLayout", {
				view: contentView,
				title: title
			});
		},
	});


	Calipso.components.backboneform.Form = Backbone.Form.extend({
			hintRequiredFields: true,
			capitalizeKeys : true,

		  /**
		   * Constructor
		   *
		   * @param {Object} [options.schema]
		   * @param {Backbone.Model} [options.model]
		   * @param {Object} [options.data]
		   * @param {String[]|Object[]} [options.fieldsets]
		   * @param {String[]} [options.fields]
		   * @param {String} [options.idPrefix]
		   * @param {Form.Field} [options.Field]
		   * @param {Form.Fieldset} [options.Fieldset]
		   * @param {Function} [options.template]
		   * @param {Boolean|String} [options.submitButton]
		   * @param {Boolean|String} [options.hintRequiredFields]
		   */
		  initialize: function(options) {
				var hintRequiredFields = options.hintRequiredFields;
				if(!_.isUndefined(hintRequiredFields)){
					this.hintRequiredFields = hintRequiredFields;
				}
				Backbone.Form.prototype.initialize.apply(this, arguments);
			},
			isRequired : function(schema){
				var required = schema.required;
				if(!required && schema.validators){
					required = $.inArray('required', schema.validators) > -1;
				}
				return required;
			},
		  /**
		   * Creates a Field instance
		   *
		   * @param {String} key
		   * @param {Object} schema       Field schema
		   *
		   * @return {Form.Field}
		   */
		  createField: function(key, schema) {
				if(this.hintRequiredFields && this.isRequired(schema)){
					var suffix = "";
					var hint = '<sup class="text-danger"><i class="fa fa-asterisk"></i></sup>';
					var title = schema.titleHTML;
					if(!title){
						title = schema.title;
						if(!title){
							title = key;
							if(this.capitalizeKeys){
								// insert a space before all caps
						    title = title.replace(/([A-Z])/g, ' $1')
						    // uppercase the first character
						    .replace(/^./, function(str){ return str.toUpperCase(); });
							}
						}
						schema.title = undefined;
					}
					var length = title.length;
					title.trim();
					for(var i = title.length; i < length; i++){
						suffix += ' ';
					}
					if(title.lastIndexOf(":") == title.length - 1){
						title = title.substring(0, title.length - 1);
						suffix = ":" + suffix;
					}
					schema.titleHTML = title + hint + suffix;
				}
				return Backbone.Form.prototype.createField.apply(this, arguments);
		  },

		  render: function() {
		    var self = this,
		        fields = this.fields,
		        $ = Backbone.$;

		    //Render form
		    var $form = $($.trim(this.template(_.result(this, 'templateData'))));
		    if(this.$el){
					console.log("render: preserving given el");
					var attributes = $form.prop("attributes");
					// loop through <select> attributes and apply them on <div>
					$.each(attributes, function() {
							if(this.name == "class"){
					    	self.$el.addClass(this.value);
							}
							else if(this.name != "id"){
					    	self.$el.attr(this.name, this.value);
							}
					});
					this.$el.html($form.html());
					$form = this.$el;
				}
				else{
		    	this.setElement($form);
				}
		    //Render standalone editors
		    $form.find('[data-editors]').add($form).each(function(i, el) {
		      var $container = $(el),
		          selection = $container.attr('data-editors');

		      if (_.isUndefined(selection)) return;

		      //Work out which fields to include
		      var keys = (selection == '*')
		        ? self.selectedFields || _.keys(fields)
		        : selection.split(',');

		      //Add them
		      _.each(keys, function(key) {
		        var field = fields[key];

		        $container.append(field.editor.render().el);
		      });
		    });

		    //Render standalone fields
		    $form.find('[data-fields]').add($form).each(function(i, el) {
		      var $container = $(el),
		          selection = $container.attr('data-fields');

		      if (_.isUndefined(selection)) return;

		      //Work out which fields to include
		      var keys = (selection == '*')
		        ? self.selectedFields || _.keys(fields)
		        : selection.split(',');

		      //Add them
		      _.each(keys, function(key) {
		        var field = fields[key];

		        $container.append(field.render().el);
		      });
		    });

		    //Render fieldsets
		    $form.find('[data-fieldsets]').add($form).each(function(i, el) {
		      var $container = $(el),
		          selection = $container.attr('data-fieldsets');

		      if (_.isUndefined(selection)) return;

		      _.each(self.fieldsets, function(fieldset) {
		        $container.append(fieldset.render().el);
		      });
		    });

		    //Set class
		    $form.addClass(this.className);

		    return this;
		  },

			toJson : function() {
				var nodeName = this.$el[0].nodeName.toLowerCase();
				return _.reduce((nodeName == "form" ? this.$el : this.$("form")).serializeArray(), function(hash, pair) {
					if (pair.value) {
						hash[pair.name] = pair.value;
					}
					return hash;
				}, {});
			},
	});

	Calipso.components.backboneform.NumberText = Backbone.Form.editors.Text.extend({
		getValue : function() {
			var value = Backbone.Form.editors.Text.prototype.getValue.apply(this, arguments);
			if(!(_.isUndefined(value) || _.isNull(value) || value == "")){
				return value * 1;
			}
			else {
				return null;
			}
		},
	});

	Calipso.components.backboneform.Tel = Backbone.Form.editors.Text.extend({
		render : function() {
			Backbone.Form.editors.Text.prototype.render.apply(this, arguments);
			var _this = this;

			setTimeout(function(){
				_this.$el.intlTelInput();
			}, 250);
			return this;
		},
	});
	/*
	 * shows remaining chars
	 */
	Calipso.components.backboneform.LengthText = Backbone.Form.editors.Text.extend({
		maxLength: null,
		initialize : function(options) {
			Backbone.Form.editors.Text.prototype.initialize.call(this, options);
			if (this.schema.maxLength) {
				this.maxLength = this.schema.maxLength;
			}
		},
		/**
		 * Adds the editor to the DOM
		 */
		render : function() {
			var _this = this;
			function create() {
				if(_this.maxLength){
					var pHelp = _this.$el.parent().parent().find('.help-block:not([data-error])').first();
					pHelp.append("<span class=\"chars-remaining\">" + _this.maxLength + ' characters remaining</span>');

			    _this.$el.keyup(function() {
			        var text_length = _this.getValue() ? _this.getValue().length : 0;
			        var text_remaining = _this.maxLength - text_length;
							var c = text_remaining == 1 ? "character" : "characters"
					    var $msgElem = _this.$el.parent().parent().find('.chars-remaining');
							$msgElem.html(text_remaining + ' '+c+' remaining');

							if(text_remaining < 0){
								$msgElem.addClass('text-danger');
							}
							else{
								$msgElem.removeClass('text-danger');
							}

			    });
				}
			}

			setTimeout(create, 250);
			return this;
		}
	});
	/*
	 *  based on typeahead/bloodhound 0.11.1, see
	 * https://github.com/twitter/typeahead.js
	 */
	Calipso.components.backboneform.Typeahead = Backbone.Form.editors.Text.extend({
		tagName : 'div',
		//className: "form-control",
		typeaheadSource : null,
		minLength: 2,
		placeholder : "",
		initialize : function(options) {
			Backbone.Form.editors.Text.prototype.initialize.call(this, options);
			// set the options source
			if (this.schema && this.schema.typeaheadSource) {
				this.typeaheadSource = this.schema.typeaheadSource;
			} else {
				throw "Missing required option: 'typeaheadSource'";
			}
			if (this.schema.minLength) {
				this.minLength = this.schema.minLength;
			}
			if (this.schema.placeholder) {
				this.placeholder = " placeholder=\"this.schema.placeholder\" ";
			}
			this.$el.removeAttr("id class name type autocomplete");
			this.$el.html('<input type="text" id="' + this.id + '" name="' + this.getName() + '"  class="form-control"  autocomplete="off" ' + this.placeholder + '/>');
		},
		/**
		 * Adds the editor to the DOM
		 */
		render : function() {
			var _this = this;
			function create() {
				var $el = _this.$el.find("#" + _this.id);

//				var editorAttrs = _this.schema.editorAttrs;
//				if(editorAttrs){
//					$.each(editorAttrs, function() {
//						$el.attr(this.name, this.value);
//					});
//				}
				$el.typeahead({
							minLength : _this.minLength,
							highlight : true,
							hint : true
						},
						_this.typeaheadSource
				);

			}
			// apply typeahead after the field has been added to the DOM
			setTimeout(create, 250);
			return this;
		}
	});
	Calipso.components.backboneform.TypeaheadObject = Calipso.components.backboneform.Typeahead.extend({
		initialize : function(options) {
			Calipso.components.backboneform.Typeahead.prototype.initialize.call(this, options);
			this.$el.removeAttr("id class name type autocomplete");
			this.$el.html('<input type="hidden" id="' + this.id + '" name="' + this.getName() + '" />' +
					'<input type="text" class="form-control" id="' + this.id + 'Typeahead" name="' + this.getName() + 'Typeahead" autocomplete="off" ' + this.placeholder + '/>');
		},
		/**
		 * Adds the editor to the DOM
		 */
		render : function() {
			var _this = this;
			var $hidden = _this.$el.find("#" + _this.id);
			var $el = _this.$el.find("#" + _this.id + "Typeahead");
			function create() {

//				var editorAttrs = _this.schema.editorAttrs;
//				if(editorAttrs){
//					$.each(editorAttrs, function() {
//						$el.attr(this.name, this.value);
//					});
//				}
				$el.typeahead({
						minLength : _this.minLength,
						highlight : true,
						hint : true
					},
					_this.typeaheadSource
				).on('typeahead:selected', function(e, suggestion, name) {
					_this.setValue(suggestion, name);
				});
				$el.bind('typeahead:change', function(e, query) {
					_this.setValue(null);
				});
				if(_this.value){
					var val = Calipso.getObjectProperty(_this.value, "id", _this.value);
					var nameVal = Calipso.getObjectProperty(_this.value, "name");
					$hidden.val(val);
					$el.typeahead('val', nameVal);
				}
			}
			// apply typeahead after the field has been added to the DOM
			setTimeout(create, 250);
			return this;
		},
		setValue : function(value, name) {
			console.log("Calipso.components.backboneform.TypeaheadObject#setValue, value: '" + value + "', name: " + name);
			var _this = this;
			if(!value){
				value = null;
			}
			this.value = value;
			if(name){
				_this.$el.find("#" +this.id).attr("name", name);
				_this.$el.find("#" +this.id).val(value && value.id ? value.id : value);
			}
		},
		getValue : function() {
			console.log("Calipso.components.backboneform.TypeaheadObject#getValue, value: '" + this.value + "'");
			var value = this.value;
			var query = this.$el.find("#" + this.id + "Typeahead").typeahead('val');
			// if empty value or input, return plain
			return value && query ? value : null;
		},
	});

	// uses  https://github.com/Eonasdan/bootstrap-datetimepicker
	Calipso.components.backboneform.Datetimepicker = Backbone.Form.editors.Text.extend({

		initialize : function(options) {
			Backbone.Form.editors.Text.prototype.initialize.call(this, options);
			// set the options source
			if (this.schema && this.schema.config) {
				this.config = this.schema.config;
			} else {
				this.config = {locale : 'en'}
			}
			var allowInputToggle = this.config.allowInputToggle;
			// enable allowInputToggle by default
			if(_.isNull(allowInputToggle) || _.isUndefined(allowInputToggle)){
				this.config.allowInputToggle = true;
			}
		},
		callDataFunction : function(functionName, param){
			console.log("callDataFunction:  " + functionName + ", param: " + param);
			this.$el.data("DateTimePicker")[functionName](param);
		},
		render : function() {
			Backbone.Form.editors.Text.prototype.render.apply(this, arguments);
			var _this = this;
			var doRender = function(){
				_this.$el.attr('autocomplete','off');
				_this.$el.parent().addClass("input-group");
				_this.$el.parent().append("<span class=\"input-group-addon\"><span class=\"glyphicon glyphicon-calendar\"></span></span>");
				_this.$el.parent().datetimepicker(_this.config);
				console.log("Calipso.components.backboneform.Datetimepicker#render, _this.value: " + _this.value);
				var value = _this.schema.fromProperty
					? _this.model.get(_this.schema.fromProperty)
					: _this.value;
				if (value) {
					var initValue = new Date(value);
					_this.$el.parent().data("DateTimePicker").date(initValue);
				}
			}
			// apply picker after the field has been added to the DOM
			setTimeout(doRender, 250);
			return this;
		},
		getValue : function() {
			return this.$el.parent().data("DateTimePicker").date();
		},
	});
	//////////////////////////////////////////////////
	// Layouts
	//////////////////////////////////////////////////

	Calipso.view.AbstractLayout = Backbone.Marionette.LayoutView.extend({
		taName: "div",
		/** Stores the default forward path to use after a successful action */
		defaultForward : null,
		/** Stores the final configuration */
		config : null,
		skipSrollToTop : false,
		/**
		 * Get the default config.
		 */
		getDefaultConfig : function(){
			var defaultConfig = {};
			// superDefaultConfig = _.result(**SuperClassHere**.prototype, 'getDefaultConfig')
			// _.extend(superDefaultConfig, defaultConfig);
			return defaultConfig;
		},
		/**
		 * Get an array of required option names. An error will be thrown
		 * during initialization if anyone is undefined or null.
		 */
		getRequiredOptionNames : function(){
			return [];
		},
		/**
		 * Validate the final configuration. Called during initialization
		 * by {Calipso.view.MainLayout#configure}. Internal use only.
		 */
		_validateConfiguration : function(){
			var missing = [];
			var requiredOptionNames = this.getRequiredOptionNames();
			if(requiredOptionNames && requiredOptionNames.length > 0){
				for(var i=0; i < requiredOptionNames.length; i++){
					var requiredOptionName = requiredOptionNames[i];
					var finalOption = this.config[requiredOptionName];
					if(finalOption == undefined || finalOption == null){
						missing.push(requiredOptionName);
					}
				}
			}
			if(missing.length > 0){
				throw this.prototype.getTypeName() +
					"#validateConfiguration ERROR: missing required options" +
					missing.toString();
			}
		},
		/**
		 * (Re) Configure by overwriting this.config with according to
		 * this.model.getLayoutOptions and then by relevant options only.
		 * Finally, validate configuration for missing required options.
		 */
		configure : function(options){
			if (options.model) {
				this.model = options.model;
				if (this.model.wrappedCollection) {
					this.searchResultsCollection = this.model.wrappedCollection;
				}
			}
			// initialize config using defaults
			this.config = this.getDefaultConfig();
			// (re) configure according to this.model.getLayoutOptions
			// _.extend(this.config, this.model.prototype.getLayoutOptions(this.model));
			// and then again by relevant options only
			options = options || {};
			_.extendOwn(this.config, options);
			console.log("Calipso.view.MainLayout#configure, final config: ");
			console.log(this.config);
			// validate config
			this._validateConfiguration();
		},
		onBeforeRender: function(){
			// set up final bits just before rendering the view's `el`
			// TODO move this method call into non-public marionette API?
			this.configure(this.options);
		},
		initialize : function(options){
			Backbone.Marionette.LayoutView.prototype.initialize.apply(this, arguments);
			console.log("Calipso.view.MainLayout#initialize");
			if(!this.skipSrollToTop){
				$(window).scrollTop(0);
			}
		},
		getTypeName : function() {
			return this.prototype.getTypeName();
		}
	}, {
		getTypeName : function() {
			return "MainLayout"
		}
	});


	Calipso.view.MainLayout = Calipso.view.AbstractLayout.extend({
		className : "container configurable-fluid",
	});

	Calipso.view.ModalLayout = Calipso.view.AbstractLayout.extend({
		template : Calipso.getTemplate('modal-layout'),
		events : {
			"click a.modal-close" : "closeModal"
		},
		regions : {
			modalBodyRegion : ".modal-body"
		},
		childView : null,
		title : null,
		initialize : function(options) {
			Calipso.view.AbstractLayout.prototype.initialize.apply(this, arguments);
			var _this = this;
			if (options.childView) {
				this.childView = options.childView;
			}
			if (options.title) {
				this.title = options.title;
			}
		},
		onShow : function() {
			// render title
			if (this.title) {
				this.$el.find(".modal-title").empty().append(this.title);
			}
			// render child view
			this.modalBodyRegion.show(this.childView);
		},
		closeModal : function(e) {
			Calipso.stopEvent(e);
			Calipso.vent.trigger("modal:close");
		}

	}, {
		getTypeName : function() {
			return "Calipso.view.ModalLayout";
		}
	});
	Calipso.view.HeaderNotificationsRegion = Backbone.Marionette.Region.extend({
		el: "#calipsoHeaderView-notificationsRegion",
		attachHtml: function(view){
			console.log("Calipso.view.HeaderNotificationsRegion.attachHtml");
			this.$el.clear().
				append(
						'<a href="#" data-toggle="dropdown" class="dropdown-toggle">' +
						'<i class="fa fa-bell fa-fw"></i>'+
						'<sup class="badge badge-primary badge-notifications-count hidden"></sup>' +
						'<i class="fa fa-caret-down"></i>',
						view.el);
		}
	});
	Calipso.view.HeaderNotificationsRegion.prototype.attachHtml = function(view){
		console.log("Calipso.view.HeaderNotificationsRegion.prototype.attachHtml");
		this.$el.clear().
			append(
					'<a href="#" data-toggle="dropdown" class="dropdown-toggle">' +
					'<i class="fa fa-bell fa-fw"></i>'+
					'<sup class="badge badge-primary badge-notifications-count hidden"></sup>' +
					'<i class="fa fa-caret-down"></i>',
					view.el);
	};

	Calipso.view.HeaderView = Calipso.view.MainLayout.extend(
	/** @lends Calipso.view.HeaderView.prototype */
	{
		className: "container",
		template : Calipso.getTemplate('header'),
		id : "navbar-menu",
		className : "col-sm-12",
		events : {
			"click a.login" : "login",
			"click a.register" : "register",
			"click a.logout" : "logout",
		},
		regions : {
			//menuRegion : "#calipsoHeaderView-menuRegion",
			notificationsRegion : {
				// appends the notifications without clearing the link,
				// fixes HTML structure issue
				regionClass : Calipso.view.HeaderNotificationsRegion
			}
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
			if(Calipso.isAuthenticated()){
				// load and render notifications list
				var notifications = new Calipso.collection.PollingCollection([], {
					url : Calipso.session.getBaseUrl() + "/api/rest/baseNotifications",
					model: Calipso.model.BaseNotificationModel
				});

				//console.log("HeaderView, created notifications collection: " + notifications + ", url: " + notifications.url);
				var notificationsView = new Calipso.view.TemplateBasedCollectionView({
					tagName : "ul",
					className: "dropdown-menu dropdown-notifications",
					template : Calipso.getTemplate("headerNotificationsCollectionView"),
					childViewOptions : {
						template : Calipso.getTemplate("headerNotificationsItemView"),
					},
					collection : notifications,
				});
				this.notificationsRegion.show(notificationsView);
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
			Calipso.navigate("login", {
				trigger : true
			});
		}
	}, {
		getTypeName : function() {
			return "HeaderView";
		}
	});
	Calipso.view.ModelDrivenBrowseLayout = Calipso.view.MainLayout.extend(
	/** @lends Calipso.view.ModelDrivenBrowseLayout.prototype */
	{
		tagName : 'div',
		id : "calipsoModelDrivenBrowseLayout",
		template : Calipso.getTemplate('md-browse-layout'),
		/**
		 * Get the default config. Overwrites {Calipso.view.MainLayout#getDefaultConfig}
		 */
		getDefaultConfig : function(){
			var defaultConfig = Calipso.view.MainLayout.prototype.getDefaultConfig.apply(this);
			// now set this class' default config
			defaultConfig.skipToSingleResult = false;
			return defaultConfig;
		},
		regions : {
			contentRegion : "#calipsoModelDrivenBrowseLayout-contentRegion"
		},
		onShow : function() {
			console.log("ModelDrivenBrowseLayout#onShow");
			this.showContent(this.model);
		},
		initialize : function(options) {
			Calipso.view.MainLayout.prototype.initialize.apply(this, arguments);
			var _this = this;

			this.listenTo(Calipso.vent, "updateSearchLocation", function(model) {
				console.log("Calipso.view.ModelDrivenBrowseLayout caugh genericFormSaved, showing this.model: "+_this.model);
				_this.updateSearchLocation(model);
			}, this);

			this.listenTo(Calipso.vent, "genericFormSaved", function(model) {
				console.log("Calipso.view.ModelDrivenBrowseLayout caugh genericFormSaved, showing this.model: "+_this.model);
				_this.onGenericFormSaved(model);
			}, this);
			this.listenTo(Calipso.vent, "genericShowContent", function(model) {
				console.log("Calipso.view.ModelDrivenBrowseLayout caugh genericShowContent, showing given model: "+ model.getTypeName()+'#'+model.get("id"));
				_this.onGenericShowContent(model);
			});

			this.listenTo(Calipso.vent, "genericFormSearched", function(model) {
				console.log("Calipso.view.ModelDrivenBrowseLayout caugh genericFormSearched, showing given model: "+model);
				_this.onGenericFormSearched(model);
			});
			// vent handling might be overriden by subclasses
			if (!options.dontListenTo) {
				this.listenTo(Calipso.vent, "layout:viewModel", function(itemModel) {
					var options = {model: itemModel, formSchemaKey: "view"};
					_this.showItemViewForModel(options);
				}, this);
				this.listenTo(Calipso.vent, "layout:createModel", function(options) {
					if(!options.formSchemaKey){
						options.formSchemaKey = "create"
					}
					if(!options.model && options.modelType){
						options.model = options.modelType.create();
					}
					_this.showItemViewForModel(options);
				}, this);
//				this.listenTo(Calipso.vent, "layout:updateModel", function(itemModel) {
//					_this.showItemViewForModel(itemModel, "update");
//				}, this);
			}

		},
		onGenericFormSaved : function(model){
			console.log("Calipso.view.ModelDrivenBrowseLayout#onGenericFormSaved");
			this.showContent(model);
		},
		onGenericFormSearched : function(model){
			console.log("Calipso.view.ModelDrivenBrowseLayout#onGenericFormSearched");
			this.showContent(model);
		},
		onGenericShowContent : function(model){
			console.log("Calipso.view.ModelDrivenBrowseLayout#onGenericShowContent");
			this.showContent(model);
		},
		showItemViewForModel : function(itemModel, formSchemaKey) {
			console.log("Calipso.view.ModelDrivenBrowseLayout#showItemViewForModel");
			if (!formSchemaKey) {
				formSchemaKey = "view";
			}
			//  get item view type for model
			var ItemViewType = itemModel.getItemViewType();
				console.log("Calipso.view.ModelDrivenBrowseLayout#showItemViewForModel, view type: " + ItemViewType.getTypeName());
			console.log();
			// console.log("ModelDrivenBrowseLayout on childView:openGridRowInTab, ItemViewType: " + ItemViewType.getTypeName());
			// create new item view instance with model
			var childView = new ItemViewType({
				formSchemaKey : formSchemaKey,
				model : itemModel
			});
			// show item view
			this.contentRegion.show(childView);
			var navUrl = itemModel.getPathFragment() + "/" + (itemModel.isNew() ? "new" : itemModel.get("id"));
			if (formSchemaKey != "view") {
				navUrl += "/" + formSchemaKey;
			}
			Calipso.navigate(navUrl, {
				trigger : false
			});
		},
		updateSearchLocation : function(){
			if(this.model && this.model.isSearchModel && this.model.isSearchModel()){
				var searchedUrl = "" + this.model.getPathFragment();
				if (this.model.wrappedCollection && this.model.wrappedCollection.data) {
					searchedUrl = searchedUrl + "?" + $.param(this.model.wrappedCollection.data);
				}
				if(searchedUrl){
					Calipso.navigate(searchedUrl, {
						trigger : false
					});
				}
			}
		},
		showContent : function(routeModel) {
			$(window).scrollTop(0);
			var _this = this;
			var isSearch = routeModel.isSearchModel();
			console.log("ModelDrivenBrowseLayout#showContent, isSearch: " + isSearch);
			// get content view
			var singleResultType = !isSearch
			|| ( _this.config.skipToSingleResult && searchResultsCollection.length == 1);
			console.log("ModelDrivenBrowseLayout.showContent: '" + routeModel.get("id") + "', _this.config.skipToSingleResult: " + _this.config.skipToSingleResult + ", collection length: " + (routeModel.wrappedCollection ? routeModel.wrappedCollection.length : "none"));
			// get the model collection view type
			var ContentViewType;
			var contentView;
			if (isSearch) {
				contentView = this.getSearchResultsViewForModel(routeModel);
				// change location bar if appropriate
				this.updateSearchLocation()
			} else {
				console.log("ModelDrivenBrowseLayout#showContent 2");
				ContentViewType = routeModel.getItemViewType();
				// create a new collection instance
				contentView = new ContentViewType({
					model : routeModel
				});

			}
//			console.log("ModelDrivenBrowseLayout.showContent, ContentViewType: " + ContentViewType.prototype.getTypeName());

			//TODO reuse active view if of the same type
			this.contentRegion.show(contentView);
			// change location bar if appropriate


		},
		getSearchResultsViewForModel : function(routeModel){
			var _this = this;
			var searchResultsView;
			var ContentViewType;
			if (_this.config.skipToSingleResult && routeModel.wrappedCollection.length == 1) {
				var singleModel = routeModel.wrappedCollection.first();
				console.log("ModelDrivenBrowseLayout#getSearchResultsViewForModel, building ItemView for single result: " + singleModel.getTypeName() + '#' + singleModel.get("id"));
				var ContentViewType = routeModel.getItemViewType();
				searchResultsView = new ContentViewType({
					model : singleModel
				});
			} else {
				console.log("ModelDrivenBrowseLayout#getSearchResultsViewForModel, building CollectionView for results of type " + routeModel.getTypeName());
				ContentViewType = routeModel.getCollectionViewType();
				searchResultsView = new ContentViewType({
					model : routeModel,
					collection : routeModel.wrappedCollection
				});
				return searchResultsView;
			}



		},
		showFormForModel : function(routeModel, region, forceShow) {
			console.log("Calipso.view.ModelDrivenSearchLayout#showSidebar");
			var _this = this;
			// create the search form view if not there
			if(forceShow || !region.hasView()){
				console.log("Calipso.view.ModelDrivenSearchLayout#showSidebar search form with formSchemaKey " + _this.model.getFormSchemaKey());
				var ContentViewType = routeModel.getItemViewType();
				var formView = new ContentViewType({
					formSchemaKey :  _this.model.getFormSchemaKey(),
					model : routeModel
				});
				// show the search form
				region.show(formView);
			}

		},

	}, {
		// static members
		getTypeName : function() {
			return "ModelDrivenBrowseLayout";
		}
	});

	Calipso.view.ModelDrivenSearchLayout = Calipso.view.ModelDrivenBrowseLayout.extend(
	/** @lends Calipso.view.ModelDrivenSearchLayout.prototype */
	{
		tagName : 'div',
		id : "calipsoModelDrivenSearchLayout",
		template : Calipso.getTemplate('md-search-layout'),
		/**
		 * Get the default config. Overwrites {Calipso.view.MainLayout#getDefaultConfig}
		 */
		getDefaultConfig : function(){
			var defaultConfig = Calipso.view.ModelDrivenBrowseLayout.prototype.getDefaultConfig.apply(this);
			// now set this class' default config
//			defaultConfig.hideSidebarOnSearched = false;
//			defaultConfig.skipInitialResultsIfNoCriteria = true;
			return defaultConfig;
		},
		onGenericFormSearched : function(options) {
			var _this = this;
			console.log("Calipso.view.ModelDrivenSearchLayout caugh genericFormSearched, showing this.model: "+_this.model);
			_this.collapseSearchForm();
			_this.expandSearchResults();
			_this.showContent(_this.model);
		},
		initialize : function(options) {
			Calipso.view.ModelDrivenBrowseLayout.prototype.initialize.apply(this, arguments);
			console.log("Calipso.view.ModelDrivenSearchLayout#initialize");
			var _this = this;

			if (this.options.searchResultsCollection) {
				this.searchResultsCollection = options.searchResultsCollection;
			}
			else{
				this.searchResultsCollection = this.model.wrappedCollection;
			}
		},
		regions : {
			sidebarRegion : "#calipsoModelDrivenSearchLayout-sideBarRegion",
			contentRegion : "#calipsoModelDrivenSearchLayout-contentRegion"
		},
		onShow : function() {
			console.log("Calipso.view.ModelDrivenSearchLayout#onShow");
			var _this = this;
			var hasCriteria =  this.searchResultsCollection && this.searchResultsCollection.hasCriteria();
			var skipDefaultSearch = this.model.skipDefaultSearch && !hasCriteria;
			console.log("Calipso.view.ModelDrivenSearchLayout#onShow, hasCriteria = " + hasCriteria + ", skipDefaultSearch: " + skipDefaultSearch);

			this.showSidebar(this.model);
			if(skipDefaultSearch){
				console.log("Calipso.view.ModelDrivenSearchLayout#onShow calling expandSearchForm");
				this.expandSearchForm();
			}
			else{
				console.log("Calipso.view.ModelDrivenSearchLayout#onShow calling expandSearchResults");
				this.expandSearchResults();
				console.log("Calipso.view.ModelDrivenSearchLayout#onShow calling showContent");
				this.showContent(this.model);
			}



		},
		expandSearchForm : function(){
			console.log("ModelDrivenSearchLayout#expandSearchForm");
			this.$el.find("#collapseOne").collapse('show');
		},
		collapseSearchForm : function(){
			console.log("ModelDrivenSearchLayout#collapseSearchForm");
			this.$el.find("#collapseOne").collapse('hide');
		},
		expandSearchResults : function(){
			console.log("ModelDrivenSearchLayout#expandSearchResults");
			this.$el.find("#collapseTwo").collapse('show');
		},
		collapseSearchResults : function(){
			console.log("ModelDrivenSearchLayout#collapseSearchResults");
			this.$el.find("#collapseTwo").collapse('hide');
		},
		showSidebar : function(routeModel) {
			this.showFormForModel(routeModel, this.sidebarRegion);

		},
//		showItemViewForModel : function(itemModel, formSchemaKey) {
//			console.log("SHOULD NOT BE CALLED: showItemViewForModel");
//			Calipso.view.ModelDrivenBrowseLayout.prototype.showItemViewForModel.apply(this, arguments);
//			this.hideSidebar();
//		},
//		hideSidebar : function() {
//			console.log("ModelDrivenSearchLayout#hideSidebar");
//			this.$el.find("#calipsoModelDrivenSearchLayout-sideBarRegion").hide();
//			this.$el.find("#calipsoModelDrivenSearchLayout-contentRegion").removeClass("col-sm-9 col-sm-6").addClass("col-sm-12");
//		},
//		hideContent : function() {
//			console.log("ModelDrivenSearchLayout#hideContent");
//			this.$el.find("#calipsoModelDrivenSearchLayout-contentRegion").hide();
//			this.$el.find("#calipsoModelDrivenSearchLayout-sideBarRegion").removeClass("col-sm-3 col-sm-9").addClass("col-sm-12");
//		},

	}, {
		getTypeName : function() {
			return "ModelDrivenSearchLayout";
		}
	});

	Calipso.view.ModelDrivenReportLayout = Calipso.view.ModelDrivenSearchLayout.extend(
	/** @lends Calipso.view.ModelDrivenReportLayout.prototype */
	{
		template : Calipso.getTemplate('md-report-layout'),
	}, {
		getTypeName : function() {
			return "Calipso.view.ModelDrivenReportLayout";
		}
	});
	/*
		var ModelDrivenOverviewLayout = MainLayout.extend({
			template : Calipso.getTemplate('md-overview-layout'),
			onShow : function() {
				console.log("ModelDrivenOverviewLayout#onShow");
				var tabLabelsView = new TabLabelsCollectionView({
					collection : this.collection
				});
				var tabContentsView = new TabContentsCollectionView({
					collection : this.collection
				});
				this.tabLabelsRegion.show(tabLabelsView);
				this.tabContentsRegion.show(tabContentsView);

			},
		},
		// static members
		{
	 		getTypeName: function(){return "ModelDrivenCrudLayout"}
		});

		var ModelDrivenCrudLayout = MainLayout.extend({
			template : tmpl,
			tagName : "div",
			className : "col-sm-12",
			regions : {
				tabLabelsRegion : '#calipsoTabLabelsRegion',
				tabContentsRegion : '#calipsoTabContentsRegion'
			},
			initialize: function(options){

				Marionette.LayoutView.prototype.initialize.apply(this, arguments);
				if(options.collection){
					this.collection = options.collection;
				}
				else if(options.model && options.model.constructor.getTypeName() == "GenericCollectionWrapperModel"){
						this.collection = options.model.wrappedCollection;
				}
				if(!this.collection){
					throw "no collection or collection wrapper model was provided";
				}
				console.log("ModelDrivenCrudLayout.initialize, collection size: " + this.collection.length  +
						", collection.model: "+this.collection.model.getTypeName());
		  },
			onShow : function() {
				console.log("ModelDrivenCrudLayout#onShow");
				var tabLabelsView = new TabLabelsCollectionView({
					collection : this.collection
				});
				var tabContentsView = new TabContentsCollectionView({
					collection : this.collection
				});
				this.tabLabelsRegion.show(tabLabelsView);
				this.tabContentsRegion.show(tabContentsView);

			},
		},
		// static members
		{
	 		getTypeName: function(){return "ModelDrivenCrudLayout"}
		});

		var TabLabelsCollectionView = Backbone.Marionette.CollectionView.extend({
			className : 'nav nav-pills',
			tagName : 'ul',
			childViewContainer : '.nav-tabs',
			getItemView : function(item) {
				return Backbone.Marionette.ItemView.extend({
					tagName : 'li',
					className : 'md-crud-layout-tab-label',
					id : "md-crud-layout-tab-label-" + item.get("id"),
					template : tmplTabLabel,

					events : {
						"click .show-tab": "viewTab",
						"click .destroy-tab" : "destroyTab"
					},
					 viewTab: function(e) {
						 console.log("TabPaneCollectionView.childView#viewTab");
						 e.stopPropagation();
						 e.preventDefault();
						 vent.trigger("viewTab", this.model);
					 },
					destroyTab : function(e) {
						console.log("TabPaneCollectionView.childView#destroyTab");
						e.stopPropagation();
						e.preventDefault();
	//					this.model.collection.remove(this.model);
						this.destroy();
						vent.trigger("viewTab", {
							id : "Search"
						});
					},
				});
			}
		});

		var TabContentsCollectionView = Backbone.Marionette.CollectionView.extend({
			tagName : 'div',
			getItemView : function(item) {
				var ItemViewClass;
				if(item){
					if (item.get("childView")) {
						ItemViewClass = item.get("childView");
					} else {
						ItemViewClass = GenericFormTabContentView;
					}
					console.log("TabContentsCollectionView#getItemView for item class " + item.constructor.getTypeName() + " returns: " + ItemViewClass.getTypeName());
				}
				return ItemViewClass;
			},
			buildItemView: function(item, ItemViewClass){
				console.log("TabContentsCollectionView#buildItemView, ItemView: "+ItemViewClass.getTypeName()+", item: "+item.constructor.getTypeName() + ", wrapped collection: "+item.wrappedCollection);
				if(item){
					var options = {model: item};
					if(item && item.wrappedCollection){
						options.collection = item.wrappedCollection;
					}
				    // do custom stuff here

				    var view = new ItemViewClass(options);

				    // more custom code working off the view instance

				    return view;
				}
			  },
		});
		*/
	///////////////////////////////////////////////////////
	// Views
	///////////////////////////////////////////////////////
	// plumbing for handlebars template helpers
	// Marionette.ItemView.prototype.mixinTemplateHelpers = function(target) {
	Marionette.View.prototype.mixinTemplateHelpers = function(target) {
		var self = this;
		var templateHelpers = Marionette.getOption(self, "templateHelpers");
		var result = {};

		target = target || {};

		if (_.isFunction(templateHelpers)) {
			templateHelpers = templateHelpers.call(self);
		}

		// This _.each block is what we're adding
		_.each(templateHelpers, function(helper, index) {
			if (_.isFunction(helper)) {
				result[index] = helper.call(self);
			} else {
				result[index] = helper;
			}
		});

		return _.extend(target, result);
	};
	// The recursive tree view
	//	var TreeView = Backbone.Marionette.CompositeView.extend({
	//	    template: "#node-template",
	//
	//	    tagName: "li",
	//
	//	    initialize: function(){
	//	        // grab the child collection from the parent model
	//	        // so that we can render the collection as children
	//	        // of this parent node
	//	        this.collection = this.model.nodes;
	//	    },
	//
	//	    appendHtml: function(cv, iv){
	//	        cv.$("ul:first").append(iv.el);
	//	    },
	//	    onRender: function() {
	//	        if(_.isUndefined(this.collection)){
	//	            this.$("ul:first").remove();
	//	        }
	//	    }
	//	});
	//
	Calipso.view.ItemView = Marionette.ItemView.extend(
	/** @lends Calipso.view.NotFoundView.prototype */
	{}, {
		getTypeName : function() {
			return "ItemView";
		}
	});

	Calipso.view.TemplateBasedItemView = Marionette.ItemView.extend(
	/** @lends Calipso.view.TemplateBasedItemView.prototype */
	{
		template : Calipso.getTemplate("templateBasedItemView"),//_.template('{{#if url}}<a href="{{url}}">{{/if}}{{#if name}}<h5>{{name}}</h5>{{else}}{{#if title}}<h5>{{title}}</h5>{{/if}}{{/if}}{{#if description}}{{description}}{{/if}}{{#if url}}</a>{{/if}}'),
		tagName : "li",
		initialize : function(options) {
			Marionette.ItemView.prototype.initialize.apply(this, arguments);
			console.log("TemplateBasedItemView#initialize, item: " + this.model);
		},
	}, {
		getTypeName : function() {
			return "Calipso.view.TemplateBasedItemView";
		}
	});
	Calipso.view.TemplateBasedCollectionView = Marionette.CompositeView.extend(
	/** @lends Calipso.view.TemplateBasedCollectionView.prototype */
	{
		template : Calipso.getTemplate("templateBasedCollectionView"),//_.template('<div id="calipsoTemplateBasedCollectionLayout-collectionViewRegion"></div>'),
		tagName : "ul",
		childView : Calipso.view.TemplateBasedItemView,
		pollCollectionAfterDestroy : false,
		childViewOptions : {
			tagName : "li",
		},
		initialize : function(models, options) {
			Marionette.CompositeView.prototype.initialize.apply(this, arguments);
			options = options || {};
			if (!this.collection && options.model && options.model.isSearchable()) {
				this.collection = options.model.wrappedCollection;
				console.log("TemplateBasedCollectionLayout#initialize, got options.model.wrappedCollection: " + this.collection + ", url: " + this.collection.url);
			}
			console.log("TemplateBasedCollectionView#initialize, collection: " + this.collection);
		},
		onShow : function() {
			var _self = this;
			// poll collection?
			if (this.collection.getTypeName && this.collection.getTypeName() == "Calipso.collection.PollingCollection") {
				if (this.options.pollOptions) {
					// Specify custom options for the plugin.
					// You can also call this function inside the collection's initialize function and pass the
					// options for the plugin when instantiating a new collection.
					this.collection.configure(this.options.pollOptions);
				}
				// initialize polling if needed
				if (!this.collection.isFetching()) {
					this.collection.startFetching();
				}
			}
			// fetch collection?
			else if (this.options.forceFetch) {
				console.log("TemplateBasedCollectionView#onShow,  size: " + this.collection.length);
				_self.collection.fetch({
					url : _self.collection.url,
					success : function(collection, response, options) {
						console.log("TemplateBasedCollectionView#onShow#renderCollectionItems,  size: " + collection.length);
						//Marionette.CollectionView.prototype.onShow.apply(this);
					},
					error : function(collection, response, options) {
						alert("failed fetching collection");
					}
				});
			} else {
				//Marionette.CollectionView.prototype.onShow.apply(this);
			}

		},
		/**
		 * Stop polling the collection if appropriate
		 */
		onBeforeDestroy : function() {
			if (!this.pollCollectionAfterDestroy) {
				if (this.collection.getTypeName && this.collection.getTypeName() == "Calipso.collection.PollingCollection") {
					console.log("TemplateBasedCollectionView#onBeforeDestroy, stop polling for collection URL: " + this.collection.url);
					this.collection.stopFetching();
					this.collection.reset();
					this.collection = null;
				}
			}
		}
	/** use the template defined by the child if any
	buildChildView: function(child, ChildViewClass, childViewOptions){
		  var options = _.extend({}, childViewOptions);
		  options.model = child;
		  console.log("buildChildView, childViewOptions.template: "+childViewOptions.template);
		  if(child.childViewTemplate){
			  options.template = child.childViewTemplate;
		  }
		  return new ChildViewClass(options);
		}*/
	}, {
		getTypeName : function() {
			return "Calipso.view.TemplateBasedCollectionView";
		}
	});
	Calipso.view.NotFoundView = Marionette.ItemView.extend(
	/** @lends Calipso.view.NotFoundView.prototype */
	{
		className : 'container span8 home',
		template : Calipso.getTemplate('notfound')
	}, {
		getTypeName : function() {
			return "NotFoundView";
		}
	});
	Calipso.view.FooterView = Marionette.ItemView.extend(
	/** @lends Calipso.view.FooterView.prototype */
	{
		className : "container",
		template : Calipso.getTemplate('footer'),
		id : "navbar-menu",
		className : "col-sm-12"
	}, {
		getTypeName : function() {
			return "FooterView";
		}
	});

	Calipso.view.StackView = Marionette.View.extend(
	/** @lends Calipso.view.StackView.prototype */
	{

		hasRootView : false,

		// Define options for transitioning views in and out
		defaults : {
			inTransitionClass : 'slideInFromRight',
			outTransitionClass : 'slideOutToRight',
			animationClass : 'animated',
			transitionDelay : 1000,
			'class' : 'stacks',
			itemClass : 'stack-item'
		},

		initialize : function(options) {
			this.views = [];
			options = options || {};
			this.options = _.defaults({}, this.defaults, options);
		},

		setRootView : function(view) {
			this.hasRootView = true;
			this.views.push(view);
			view.render();
			view.$el.addClass(this.options.itemClass);
			this.$el.append(view.$el);
		},

		render : function() {
			this.$el.addClass(this.options['class']);
			return this;
		},

		// Pop the top-most view off of the stack.
		pop : function() {
			var self = this;
			if (this.views.length > (this.hasRootView ? 1 : 0)) {
				var view = this.views.pop();
				this.transitionViewOut(view);
			}
		},

		// Push a new view onto the stack.
		// The itemClass will be auto-added to the parent element.
		push : function(view) {
			this.views.push(view);
			view.render();
			view.$el.addClass(this.options.itemClass);
			this.transitionViewIn(view);
			//console.log(this.views);
		},

		// Transition the new view in.
		// This is broken out as a method for convenient overriding of
		// the default transition behavior. If you only want to change the
		// animation use the trasition class options instead.
		transitionViewIn : function(view) {
			//console.log('in', this.options);
			this.trigger('before:transitionIn', this, view);
			view.$el.addClass('hiddenToRight');
			this.$el.append(view.$el);

			// Wait a brief moment so it triggers the css transactions
			// If we don't delay, at least in my minimal testing, Chrome
			// does not animate the content but instead snaps-to-position.
			_.delay(function() {
				view.$el.addClass(this.options.animationClass);
				view.$el.addClass(this.options.inTransitionClass);
				_.delay(function() {
					view.$el.removeClass('hiddenToRight');
					this.trigger('transitionIn', this, view);
				}.bind(this), this.options.transitionDelay);
			}.bind(this), 1);
		},

		// Trastition a view out.
		// This is broken out as a method for convenient overriding of
		// the default transition behavior. If you only want to change the
		// animation use the trasition class options instead.
		transitionViewOut : function(view) {
			this.trigger('before:transitionOut', this, view);
			view.$el.addClass(this.options.outTransitionClass);
			_.delay(function() {
				view.destroy();
				this.trigger('transitionOut', this, view);
				//console.log(this.views);
			}.bind(this), this.options.transitionDelay);
		}

	});

	Calipso.view.ModelDrivenCollectionView = Marionette.ItemView.extend(
	/**
	 * @param options object members:
	 *  - collection/wrappedCollection
	 *  - callCollectionFetch: whether to fetch the collection from the server
	 * @lends Calipso.view.ModelDrivenCollectionView.prototype
	 */
	{

	},
	// static members
	{
		getTypeName : function() {
			return "ModelDrivenCollectionView";
		}
	});

	// make backgrid tables responsive
	var BackgridCellInitialize = Backgrid.Cell.prototype.initialize;
	Backgrid.Cell.prototype.initialize = function() {
		BackgridCellInitialize.apply(this, arguments);
		this.$el.attr("data-title", this.column.get("label"));
	}

	Calipso.view.ModelDrivenCollectionGridView = Marionette.ItemView.extend(
	/**
	 * @param options object members:
	 *  - collection/wrappedCollection
	 *  - callCollectionFetch: whether to fetch the collection from the server
	 * @lends Calipso.view.ModelDrivenCollectionGridView.prototype
	 * */
	{
		// Define view template
		template : Calipso.getTemplate('md-collection-grid-view'),
		events : {
			"click button.btn-windowcontrols-destroy" : "back"
		},
		collection : null,
		backgrid : null,
		back : function(e) {
			Calipso.stopEvent(e);
			window.history.back();
		},
		initialize : function(options) {
			console.log("ModelDrivenCollectionGridView.initialize, options: " + options);
			Marionette.ItemView.prototype.initialize.apply(this, arguments);
			if (options.collection) {
				this.collection = options.collection;
			} else if (options.model && options.model.wrappedCollection) {
				this.collection = options.model.wrappedCollection;
			}
			if (!this.collection) {
				throw "no collection or collection wrapper model was provided";
			}

			if (options.callCollectionFetch) {
				this.callCollectionFetch = options.callCollectionFetch;
			}
			console.log("ModelDrivenCollectionGridView.initialize, callCollectionFetch: " + this.callCollectionFetch);
			console.log("ModelDrivenCollectionGridView.initialize, collection: " + (this.collection ? this.collection.length : this.collection));
		},
		onGridRendered : function() {

		},
		onCollectionFetched : function() {

		},
		onCollectionFetchFailed : function() {

		},
		getGrid : function(gridOptions){
			return new Backgrid.Grid(gridOptions);
		},
		onShow : function() {
			var _self = this;
			// in case of a report we need a grid schema key
			console.log("ModelDrivenCollectionGridView.onShow,  _self.collection.url: " + _self.collection.url);
			var gridSchema = _self.model.getGridSchema();
			console.log("ModelDrivenCollectionGridView.onShow,  _self.model.getGridSchema: ");
			console.log(_self.model.getGridSchema());

			console.log("ModelDrivenCollectionGridView.onShow, collection: " + _self.collection);

			console.log("ModelDrivenCollectionGridView.onShow, collection.data: " + _self.collection.data);
			console.log(_self.collection.data);

			this.backgrid = this.getGrid({
				className : "backgrid responsive-table",
				columns : gridSchema,
				row : Calipso.components.backgrid.SmartHighlightRow,
				collection : _self.collection,
				emptyText : "No records found"
			});

			this.$('.backgrid-table-container').append(this.backgrid.render().$el);
			_self.listenTo(_self.collection, "backgrid:refresh", _self.showFixedHeader);
			var paginator = new Backgrid.Extension.Paginator({

				// If you anticipate a large number of pages, you can adjust
				// the number of page handles to show. The sliding window
				// will automatically show the next set of page handles when
				// you click next at the end of a window.
				windowSize : 10, // Default is 10

				// Used to multiple windowSize to yield a number of pages to slide,
				// in the case the number is 5
				slideScale : 0.25, // Default is 0.5

				// Whether sorting should go back to the first page
				goBackFirstOnSort : false, // Default is true

				collection : _self.collection
			});

			this.$('.backgrid-paginator-container').append(paginator.render().el);
			//						console.log("ModelDrivenCollectionGridView.onShow, collection url: "+);
			this.onGridRendered();
			if (this.callCollectionFetch) {
				var fetchOptions = {
					reset : true,
					url : _self.collection.url,
					success : function() {
						_self.onCollectionFetched();
						_self.$(".loading-indicator").hide();
						_self.$(".loading-indicator-back").hide();
					},
					error : function() {
						_self.onCollectionFetchFailed();
						_self.$(".loading-indicator").hide();
						_self.$(".loading-indicator-back").hide();
					}

				};
				if (_self.collection.data) {
					if (_self.collection.data[""] || _self.collection.data[""] == null) {
						delete _self.collection.data[""];
					}
					fetchOptions.data = _self.collection.data;
					//
				}
				_self.collection.fetch(fetchOptions);
				this.callCollectionFetch = false;
			} else {
				_self.$(".loading-indicator").hide();
				_self.$(".loading-indicator-back").hide();
				_self.showFixedHeader();
			}
			// this.collection.fetch();main info

			// console.log("ModelDrivenCollectionGridView showed");

		},
		showFixedHeader : function() {
			var fixedHeaderContainer = this.$el.find(".backgrid-fixed-header");
			if (fixedHeaderContainer.length > 0) {
				var tableOrig = this.$el.find(".backgrid").first();
				var table_clone = tableOrig.clone().removeClass('backgrid').empty();
				var origThead = this.$el.find(".backgrid > thead");

				table_clone.append(origThead.clone()).addClass('header_fixed backgrid_clone').css('left', tableOrig.offset().left);

				fixedHeaderContainer.empty().append(table_clone);

				tableOrig.find('tbody tr').first().children().each(function(i, e) {
					$(table_clone.find('tr').children()[i]).width($(e).width());
				});
				origThead.hide();
				table_clone.removeAttr('style');
				console.log("ModelDrivenCollectionGridView showFixedHeader, added header");
			} else {
				console.log("ModelDrivenCollectionGridView showFixedHeader, skipped adding header");
			}

			//tableOrig.find('thead').first().hide();
		}

	},
	// static members
	{
		getTypeName : function() {
			return "ModelDrivenCollectionGridView";
		}
	});

	Calipso.view.ModelDrivenReportView = Calipso.view.ModelDrivenCollectionGridView.extend({
		tagName: "div",
		// Define view template
		template : Calipso.getTemplate('md-report-view'),
		chartOptions : {
			responsive: true,
			maintainAspectRatio: false,
//		    bezierCurveTension : 0.7,
      //bezierCurve: false
	    ///Boolean - Whether grid lines are shown across the chart
	    scaleShowGridLines : true,

	    //String - Colour of the grid lines
	    scaleGridLineColor : "rgba(0,0,0,.05)",

	    //Number - Width of the grid lines
	    scaleGridLineWidth : 1,

	    //Boolean - Whether to show horizontal lines (except X axis)
	    scaleShowHorizontalLines: true,

	    //Boolean - Whether to show vertical lines (except Y axis)
	    scaleShowVerticalLines: true,

	    // Boolean - Whether to show labels on the scale
	    scaleShowLabels: true,

	    pointDotRadius : 3,
	    //Number - Pixel width of point dot stroke
	    pointDotStrokeWidth : 2,
	    datasetStrokeWidth : 2,
	    // Interpolated JS string - can access value
	    scaleLabel: "<%=value%>",
	   	multiTooltipTemplate: "<%= datasetLabel %> - <%= value %>",
		  //String - A legend template
		  legendTemplate : "<ul class=\"list-inline\"><% for (var i=0; i<segments.length; i++){%><li class=\"list-group-item\"><span style=\"color:<%=segments[i].fillColor%> \"><i class=\"fa fa-bookmark\"></i>&nbsp;</span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>"

		},
		colors : [
      "91, 144, 191",
      "163, 190, 140",
      "171, 121, 103",
      "208, 135, 112",
      "180, 142, 173",
      "235, 203, 139",
			"39, 165, 218", //"#5DA5DA" , // blue
			"250, 164, 58", //"#FAA43A" , // orange
			"96, 189, 104", //"#60BD68" , // green
			"241, 124, 176", //"#F17CB0" , // pink
			"178, 145, 47", //"#B2912F" , // brown
			"178, 118, 178", //"#B276B2" , // purple
			"222, 207, 63", //"#DECF3F" , // yellow
			"241, 88, 84", //"#F15854" , // red
			"77, 77, 77", //"#4D4D4D" , // gray
			"0, 0, 0", //"#000000" , // black
		],
		initialize : function(options) {
			//console.log("ReportView.initialize, options: " + options);
			Calipso.view.ModelDrivenCollectionGridView.prototype.initialize.apply(this, arguments);
			var self = this;
			if(options && options.chartOptions){
				_.extend(this.chartOptions, options.chartOptions);
			}
			//this.bindTo(this.model, "change", this.modelChanged);
		},
		getGrid : function(gridOptions){
			gridOptions.columnsToPin = 1;
			gridOptions.minScreenSize = 5000;
			gridOptions.className = "backgrid";
			console.log("building responsive grid...");
			return new Backgrid.Extension.ResponsiveGrid(gridOptions);
		},
		onGridRendered : function() {
		  this.backgrid.setSwitchable({});
		},
		onShow : function() {
			Calipso.view.ModelDrivenCollectionGridView.prototype.onShow.apply(this);

			var model = this.model, _this = this;
		// Get the context of the canvas element we want to select
			var canvas = this.$(".chart")[0];
			var $canvas = $(canvas);
//			$canvas.attr("width", $canvas.parent().attr("width"));
//			$canvas.attr("height", $canvas.parent().attr("height"));
			var ctx = canvas.getContext("2d");
			var chartData = this.getDataForAttribute(this.model.get("kpi"));
			var dataTotals = [];
			_.each(chartData.datasets, function(dataset, index) {

				//console.log("Calipso.view.ModelDrivenReportView#onShow each:chartData, dataset: ");
				//console.log(dataset);
				  var color = _this.colors[index];
				  dataset.fillColor = "rgba("+color+",0.02)";
				  dataset.strokeColor = "rgba("+color+",0.8)";
				  dataset.pointColor = "rgba("+color+",0.8)";
				  dataset.pointStrokeColor = "rgba("+color+",0)";
				  dataset.pointHighlightFill = "#fff";
				  dataset.pointHighlightStroke = "rgba("+color+",04)";

				  // add totals dataset
				  var sum = 0;
				  for (var i = 0; i < dataset.data.length; i++) {
					  sum += parseFloat(dataset.data[i]);
					}
				  dataTotals.push({
					  label: dataset.label,
			        value: sum % 1 === 0 ? sum : parseFloat(sum.toFixed(2)),
			        color:"rgba("+color+",0.8)",
			        highlight: "rgba("+color+",0.4)",
				  });
			});

			this.chart = new Chart(ctx).Line(chartData, this.chartOptions);


			var canvasTotals = this.$(".chart-totals")[0];
			var $canvasTotals = $(canvasTotals);
//			$canvasTotals.attr("width", $canvasTotals.parent().attr("width"));
//			$canvasTotals.attr("height", $canvasTotals.parent().attr("height"));
			var ctxTotals = canvasTotals.getContext("2d");

			this.chartTotals = new Chart(ctxTotals).PolarArea(dataTotals, this.chartOptions);

			  //then you just need to generate the legend
			  var legend = this.chartTotals.generateLegend();
			  this.$('.chart-legend').append(legend);

//			this.chart.onclick = function(value, category) {
//				self.trigger("click", category);
//			};
		},
		modelChanged : function() {
//			if (this.chart && this.seriesSource()) {
//				this.chart.redraw();
//			}
		},
		getDataForAttribute : function(entryAttribute){
			var data = {};
			var reportDataSets = this.model.wrappedCollection;
			//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, datasets: ");
			//console.log(reportDataSets);

			// add dataset labels
			var labels = [];
			var entries = reportDataSets.first().get("entries");
			for(var i = 0 ; i < entries.length; i++){
				labels.push(entries[i].label);
			}
			data.labels = labels;
			//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, data labels: ");
			//console.log(data.labels);

			// add datasets
			var datasets = [];

			reportDataSets.each(function(child, index) {
				// the new dataset...
				//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, child: ");
				//console.log(child);
				var dataset = {};

				// ... it's label...
				dataset.label = child.get("label");

				// ... and it's data entries
				var dataSetData = [];
				for(var i = 0 ; i < child.get("entries").length; i++){
					//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, child.entries: ");
					dataSetData.push(child.get("entries")[i].entryData[entryAttribute]);
				}
				dataset.data = dataSetData;
				//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, dataset.data: ");
				//console.log(dataset.data);

				// add the dataset
				datasets.push(dataset);
			}, reportDataSets);

			// add the datasets to the returned data
			data.datasets = datasets;

			// return the data
			//console.log("Calipso.view.ModelDrivenReportView#getDataForAttribute, returns data: ");
			//console.log(data);
			return data;

		},

	});

	Calipso.view.MainContentNavView = Marionette.ItemView.extend({

		// Define view template
		template : Calipso.getTemplate('MainContentNavView'),

		initialize : function(options) {
			Marionette.ItemView.prototype.initialize.apply(this, arguments);
		},
		onDomRefresh : function() {
			console.log("MainContentTabsView onDomRefresh");
		}

	}, {
		getTypeName : function() {
			return "MainContentNavView";
		}
	});

	Calipso.view.GenericView = Marionette.ItemView.extend({
		// Define view template
		tagName : 'div',
		className : "calipsoView",
		events : {
			"click button.destroy" : "back"
		},

		back : function(e) {
			Calipso.stopEvent(e);
			window.history.back();
		},
		// dynamically set the id
		initialize : function(options) {
			var _this = this;
			Marionette.ItemView.prototype.initialize.apply(this, arguments);
			this.$el.prop("id", "tab-" + this.model.get("id"));
			var childViewTemplate = this.model.getItemViewTemplate();
			if (childViewTemplate) {
				this.tmpl = childViewTemplate;
			}

			this.formTemplate = this.options.formTemplate ? this.options.formTemplate : Backbone.Form.template;
			if (!_this.model.isNew() && options.forceFetch) {
				var modelUrl = Calipso.session.getBaseUrl() + "/api/rest" + "/" + this.model.getPathFragment() + "/" + this.model.get("id");
				//console.log("GenericView#initialize, fetching model " + modelUrl);
				this.model.fetch({
					async : false,
					url : modelUrl
				});
			}
		},
		formSchemaKey : "view",
	}, {
		// static members
		getTypeName : function() {
			return "GenericView";
		}
	});

	// Model Driven Form View
	Calipso.view.GenericFormView = Marionette.ItemView.extend({
		/**
		 * Cals the static method of the same name. Returns a Backbone.Form template
		 * @param  {[String]} the template key, usually one of {"horizontal", "inline", "vertical"}
		 * @return {[type]} the compiled template
		 */
		getFormTemplate : function(templateKey){
			templateKey || (templateKey = this.formTemplateKey);
			return this.constructor.getFormTemplate(this, templateKey);
		},
		formTemplateKey : "horizontal",
		modal : false,
		addToCollection : null,
		// Define view template
		formSchemaKey : null,
		formTitle: "options.formTitle",
		template : Calipso.getTemplate('md-form-view'),
		templateHelpers : {
			formSchemaKey : function() {
				return this.formSchemaKey;
			},
			formTitle : function() {
				var title = Calipso.getObjectProperty(this.model, "label", "");
				return title;
			},
		},

		initialize : function(options) {
			Marionette.ItemView.prototype.initialize.apply(this, arguments);

			if (options.modal) {
				this.modal = options.modal;
			}

			if (options.addToCollection) {
				this.addToCollection = options.addToCollection;
			}

			if (options.model) {
				this.model = options.model;
			}
			if (!this.model) {
				throw "GenericFormView: a 'model' option is required";
			}
			// set schema key, from options or model
			if (options.formSchemaKey) {
				this.formSchemaKey = options.formSchemaKey;
			}
			else{
				this.formSchemaKey = this.model.getFormSchemaKey();
			}
			if (options.formTemplateKey) {
				this.formTemplateKey = options.formTemplateKey;
			}
			else if (this.model.get("formTemplateKey")) {
				this.formTemplateKey = this.model.get("formTemplateKey");
			}
			// use vertical form for searches
			else if(this.formSchemaKey.slice(0, 6) == "search"){
				this.formTemplateKey =  "vertical";
			}

			// grab a handle for the search results collection if any, from options or model
			if (this.options.searchResultsCollection) {
				this.searchResultsCollection = options.searchResultsCollection;
			}
			else if (this.model.wrappedCollection) {
				this.searchResultsCollection = this.model.wrappedCollection;
			}
			//
			this.formTemplate = this.getFormTemplate(options.formTemplateKey ? options.formTemplateKey : this.formTemplateKey);


		},
		events : {
			"click a.btn-social-login" : "socialLogin",
			"click button.submit" : "commit",
			"submit form" : "commit",
			"click button.cancel" : "cancel",
			"submit" : "commitOnEnter",
			"keypress input[type=password]" : "commitOnEnter",
			"keypress input[type=text]" : "commitOnEnter"
		},
		commitOnEnter : function(e) {
			if (e.keyCode != 13) {
				return;
			} else {
				this.commit(e);
			}
		},
		isFormValid : function(){
			var isValid = false;
			if (window.Placeholders) {
				Placeholders.disable();
			}
			var errors = this.form.commit({
				validate : true
			});
			if (errors) {
				var errorMsg = "" + errors._others;
				if (window.Placeholders) {
					Placeholders.enable();
				}
				if (this.modal) {
					$(".modal-body").scrollTop(0);
				}
			}
			else{
				isValid = true;
			}

			return isValid;
		},
		commit : function(e) {
			var _this = this;
			Calipso.stopEvent(e);
			if(!this.isFormValid()){
				return false;
			}
			// if no validation errors
			else{
				// Case: create/update
				if (_this.formSchemaKey.indexOf("create") == 0 || _this.formSchemaKey.indexOf("update") == 0) {
					// persist changes

					_this.model.save(null, {
						success:function(model, response){
							if(_this.addToCollection){

								_this.addToCollection.add(_this.model);
								_this.model.trigger("added");
							}
							if(_this.modal){
								Calipso.vent.trigger("modal:destroy");
							}
							else{
								Calipso.vent.trigger("genericFormSaved", model);
							}
						},
						error:function(){
					      alert("Failed persisting changes");
					    }
					});
				}
				else{
					var newData = this.form.toJson();
					this.searchResultsCollection.data = newData;
					this.searchResultsCollection.fetch({
						reset : true,
						data : newData,
						success : function() {
							// signal successful retreival of search results
							// for the currently active layout to handle presentation
							Calipso.vent.trigger("genericFormSearched", _this.model);
						},

						// Generic error, show an alert.
						error : function(model, response) {
							alert("Failed retreiving search results");
						}

					});

				}
				// Case: search
				//else if (this.formSchemaKey.substring(0, 6) == "search") {}
			}
			// search entities?
		},
		cancel : function() {
			window.history.back();
		},
		onShow : function() {
			var _self = this;

			// get appropriate schema
			var formSchema = _self.model.getFormSchema(_self.formSchemaKey);

			// TODO: add a property in generic model to flag view behavior (i.e. get add http:.../form-schema to the model before rendering)
			if (formSchema && _.size(formSchema) > 0) {
				_self.renderForm();
			} else {
				var fetchScemaUrl = Calipso.session.getBaseUrl() + "/" + _self.model.getPathFragment() + '/' + (_self.model.isNew() ? "new" : _self.model.get("id"));

				_self.model.fetch({
					url : fetchScemaUrl,
					success : function(model, response, options) {
						_self.renderForm();
					},
					error : function(model, response, options) {
						console.log("Error fetching model from server");
						alert("Error fetching model from server");
					}
				});
			}

		},
		renderForm : function() {
			var _self = this;
			var formSchema = _self.model.getFormSchema(_self.formSchemaKey);
			var formSubmitButton = _self.model.getFormSubmitButton ? _self.model.getFormSubmitButton() : false;
			if(!formSubmitButton){
				if(_self.formSchemaKey.indexOf("search") == 0){
					formSubmitButton = "<i class=\"glyphicon glyphicon-search\"></i>&nbsp;Search";
				}
				else if(_self.formSchemaKey.indexOf("create") == 0
					|| _self.formSchemaKey.indexOf("update") == 0){
					formSubmitButton = "<i class=\"fa fa-floppy-o\"></i>&nbsp;Save";
				}
				else{
					formSubmitButton = "Submit";
				}
			}
			//;(_self.formSchemaKey);

			// render form
			if (Calipso.session.searchData && (!_self.searchResultsCollection.data)) {
				_self.model.set(Calipso.session.searchData);
				_self.searchResultsCollection.data = Calipso.session.searchData;
			} else {
				console.log("GenericFormView#onShow, No session.searchData to add");
			}
			var formOptions = {
				model : _self.model,
				schema : formSchema,
				template : _self.getFormTemplate(_self.model.getFormTemplateKey())
			};
			// model driven submit button?
			if(formSubmitButton){
				formOptions.submitButton = formSubmitButton;
			}
			this.form = new Calipso.components.backboneform.Form(formOptions);
			this.form.setElement(this.$el.find(".generic-form-view").first()).render();
			this.$el.find('input[type=text],textarea,select').filter(':visible:enabled:first').focus();
			// generic-form-view
			//					$(selector + ' textarea[data-provide="markdown"]').each(function() {
			//						var $this = $(this);
			//
			//						if ($this.data('markdown')) {
			//							$this.data('markdown').showEditor()
			//						} else {
			//							$this.markdown($this.data())
			//						}
			//
			//					});
		},
		getFormData : function getFormData($form) {
			var unindexed_array = $form.serializeArray();
			var indexed_array = {};

			$.map(unindexed_array, function(n, i) {
				indexed_array[n['name']] = n['value'];
			});

			return indexed_array;
		},
		socialLogin : function(e) {
			Calipso.socialLogin(e);
		},
	},
	// static members
	{
		getTypeName : function() {
			return "GenericFormView";
		},
		formTemplates : {
			horizontal : _.template('\
					<form autocomplete=\"off\" class="form-horizontal" role="form">\
					<div data-fieldsets></div>\
					<% if (submitButton) { %>\
					<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
					<% } %>\
					</form>\
			'),
			nav : _.template('\
					<nav class="navbar navbar-default">\
					<form autocomplete=\"off\" class="navbar-form navbar-left" role="form">\
					<span data-fields="*"></span>\
					<% if (submitButton) { %>\
					<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
					<% } %>\
					</form>\
					</nav>\
			'),
			inline : _.template('\
					<form autocomplete=\"off\" class="form-inline" role="form">\
					<span data-fields="*"></span>\
					<% if (submitButton) { %>\
					<div class="form-group"><button type="submit" class="submit btn btn-primary"><%= submitButton %></button></div>\
					<% } %>\
					</form>\
			'),
			vertical : _.template('\
					<form autocomplete=\"off\" role="form">\
					<div data-fieldsets></div>\
					<% if (submitButton) { %>\
					<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
					<% } %>\
					</form>\
			'),
			auth : _.template('\
					<form autocomplete=\"off\" role="form">\
					<div data-fieldsets></div>\
					<% if (submitButton) { %>\
					<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
					<span class="pull-right">\
						<% if (submitButton.indexOf("Reg") == -1) { %>\
					   <small>Need an account?</small>\
					   <a title="Click to register" class="btn btn-success" href="/register">Register</a>\
						<% } %>\
					   <small>or sign-in with</small>\
					    <div role="group" class="btn-group">\
					        <a class="btn btn-default btn-social-login btn-social-login-facebook">\
					            <i class="fa fa-facebook-f"></i><!-- &#160;facebook -->\
					        </a>\
					        <a class="btn btn-default btn-social-login btn-social-login-linkedin">\
					            <i class="fa fa-linkedin"></i><!-- &#160;linkedin  -->\
					        </a>\
					        <!--a class="btn btn-default btn-social-login btn-social-login-twitter">\
					            <i class="fa fa-twitter"></i><!-- &#160;twitter -->\
					        <a class="btn btn-default btn-social-login btn-social-login-google">\
					            <i class="fa fa-google-plus"></i><!-- &#160;google+ -->\
					        </a>\
					    </div>\
					</span>\
					<% } %>\
					</form>\
			')

		},
		/**
		 * Returns a Backbone.Form template
		 * @param  {[Calipso.view.GenericFormView]} the form view instance
		 * @param  {[String]} the template key, usually one of {"horizontal", "inline", "vertical"}
		 * @return {[type]} the compiled template
		 */
		getFormTemplate : function(instance, templateKey){
			templateKey = templateKey ? templateKey : "horizontal";
			return this.formTemplates[templateKey];
		}
	});


	Calipso.view.GenericFormPanelView = Calipso.view.GenericFormView.extend({
		template : Calipso.getTemplate('md-formpanel-view'),
	},{});
	Calipso.view.ReportFormView = Calipso.view.GenericFormView.extend({
		renderForm : function(){
			Calipso.view.GenericFormView.prototype.renderForm.apply(this, arguments);
			var _this = this;
			// switch period mode
	    this.form.on('timeUnit:change', function(form, timeUnitEditor) {
			  var timeUnit = timeUnitEditor.getValue();
				var viewMode = timeUnit == "DAY" ? "months" : "years";
				var format = timeUnit == "DAY" ? 'MM/YYYY' : 'YYYY';
				form.fields.period.editor.callDataFunction("viewMode", viewMode);
				form.fields.period.editor.callDataFunction("format", format);
	    });
		}
	}, {
		getTypeName : function() {
			return "Calipso.view.ReportFormView";
		}
	});

	Calipso.view.AbstractItemView = Backbone.Marionette.ItemView.extend({

		initialize : function(options) {

			if (!options || !options.id) {
				this.id = _.uniqueId(this.getTypeName() + "_");
				$(this.el).attr('id', this.id);
			}
			Marionette.ItemView.prototype.initialize.apply(this, arguments);
		},
		//				render : function() {
		//					$(this.el).attr('id', Marionette.getOption(this, "id"));
		//				},
		templateHelpers : {
			viewId : function() {
				return Marionette.getOption(this, "id");
			}
		},
		getTypeName : function() {
			return this.constructor.getTypeName();
		},
		//override toString to return something more meaningful
		toString : function() {
			return this.getTypeName() + "(" + JSON.stringify(this.attributes) + ")";
		}
	}, {
		getTypeName : function() {
			return "AbstractItemView";
		}
	});

	Calipso.view.MainLayout.prototype.getTypeName = function() {
		return "MainLayout";
	};

	/*
	 * Default Login View implementation. Can be overriden with
	 * Calipso.initializeApp({loginViewType: Foobar})
	 */
	Calipso.view.LoginView = Marionette.ItemView.extend({
		template : Calipso.getTemplate('login'),
		initialize : function(options) {
			Marionette.ItemView.prototype.initialize.apply(this, arguments);
			$(window).scrollTop(0);
		},
		/**
		 * Get the name of this class
		 * @returns the class name as a string
		 */
		getTypeName : function() {
			return this.prototype.getTypeName();
		},
		events : {
			"click .register" : "register",
			"click a.btn-social-login" : "socialLogin",
			"click button.form-login-submit" : "commit",
			"submit .form-login" : "commit",
		},
		commit : function(e) {
			console.log("Calipso.view.LoginView#commit");
			Calipso.stopEvent(e);
			var _this = this;
			var userDetails = new Calipso.model.UserDetailsModel({
				email : this.$('.input-email').val(),
				password : this.$('.input-password').val(),
				newPassword : this.$('.new-password').val(),
				newPasswordConfirm : this.$('.new-password-confirm').val()
			});
				console.log(userDetails);
			Calipso.session.save(userDetails);
		},
		socialLogin : function(e) {
			Calipso.socialLogin(e);
		},
		/*
		register : function(e) {
			console.log("Calipso.view.LoginView#register");
			Calipso.stopEvent(e);
			var _this = this;

			// pickup form data
			var formData = {
				password : this.$('.input-password').val()
			};
			var usernameOrEmail = this.$('.input-email').val();
			if(usernameOrEmail){
				if(usernameOrEmail.indexOf("@") > 0){
					formData.email = usernameOrEmail;
				}
				else{
					formData.username = usernameOrEmail;
				}
			}

			var userModel = new Calipso.model.UserModel(formData);
			var registrationForm = new Calipso.view.GenericFormView({
				model : userModel
			});
			Calipso.vent.trigger('app:show', registrationForm, "register");

		},
		*/
//		onShow : function() {
//			// hide session info in nav bar
//			console.log("LoginView.onShow, hiding session-info");
//			$("#session-info").hide();
//			$("#changePassToggle").bind("click", function() {
//				$("#changePassSection").toggleClass("tmpl-hint");
//				$("i").toggleClass("ion-ios7-arrow-thin-up");
//			});
//		},

	}, {

	});

	/**
	 * Get the name of this class
	 * @returns the class name as a string
	 */
	Calipso.view.LoginView.prototype.getTypeName = function() {
		return "LoginView";
	};

	Calipso.view.AppLayout = Calipso.view.MainLayout.extend({
		tagName : "div",
		template : Calipso.getTemplate('applayout'),// _.template(templates.applayout),
		regions : {
			navRegion : "#calipsoAppLayoutNavRegion",
			contentRegion : "#calipsoAppLayoutContentRegion"
		}
	}, {
		getTypeName : function() {
			return "AppLayout";
		}
	});

	// Tab Layout
	Calipso.model.TabModel = Calipso.model.GenericModel.extend({
		getPathFragment : function() {
			return null;
		}
	});

	Calipso.collection.TabCollection = Backbone.Collection.extend({
		model : Calipso.model.GenericModel,
		initialize : function() {
			this.listenTo('add', this.onModelAdded, this);
			this.listenTo('remove', this.onModelRemoved, this);
		},
		onModelAdded : function(model, collection, options) {
			//_self.tabKeys[model.get("id")] = model;
		},
		onModelRemoved : function(model, collection, options) {
			//_self.tabKeys[model.get("id")] = null;
		},
	});
	Calipso.model.TabModel.prototype.getTypeName = function(instance) {
		return "TabModel";
	};

	Calipso.view.TabLayout = Backbone.Marionette.LayoutView.extend({
		template : Calipso.getTemplate('generic-crud-layout'),
		tagName : "div",
		className : "col-sm-12",
		regions : {
			tabLabelsRegion : '#calipsoTabLabelsRegion',
			tabContentsRegion : '#calipsoTabContentsRegion'
		},
		onShow : function() {
			console.log("TabLayout#onShow");
			var tabLabelsView = new TabLabelsCollectionView({
				collection : this.collection
			});
			var tabContentsView = new TabContentsCollectionView({
				collection : this.collection
			});
			this.tabLabelsRegion.show(tabLabelsView);
			this.tabContentsRegion.show(tabContentsView);

		},
	},
	// static members
	{
		typeName : "TabLayout",
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
		}
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
	});
	//	this.tabs = new TabCollection([
	//		 * (routeHelper.routeModel) ]);
	/*
	 * TODO
	 *
	 * initCrudLayout : function(routeHelper){ if((!this.layout) ||
	 * this.layout.getTypeName() != "AppLayout"){ //
	 * console.log("AbstractController#initCrudLayout, calling
	 * this.ensureActiveLayout()"); this.ensureActiveLayout(); } else{ //
	 * console.log("AbstractController#initCrudLayout, not updating
	 * this.layout: "+this.layout.getTypeName()); } var _self = this;
	 *
	 *   var tabLayout = new
	 * TabLayout({collection: this.tabs});
	 *
	 * vent.on("childView:openGridRowInTab", function(itemModel) {
	 * vent.trigger("openGridRowInTab", itemModel); });
	 * vent.on("openGridRowInTab", function(itemModel) {
	 * console.log("openGridRowInTab"); _self.tabs.add(itemModel);
	 * vent.trigger("viewTab", itemModel); }); vent.on("viewTab",
	 * function(itemModel) { this.layout.contentRegion.show(new
	 * itemmodel.childView(itemmodel)); //
	 * Calipso.navigate("client/"+_self.lastMainNavTabName+"/"+itemModel.get("id"), { //
	 * trigger : false // }); _self.syncMainNavigationState(null,
	 * itemModel.get("id")); });
	 *
	 * this.layout.contentRegion.show(tabLayout); },
	 */

	//////////////////////////////////////////////////
	// vent
	//////////////////////////////////////////////////

	Calipso.util.Vent = Backbone.Wreqr.EventAggregator.extend({

		constructor : function(debug) {

			this.commands = new Backbone.Wreqr.Commands();
			this.reqres = new Backbone.Wreqr.RequestResponse();

			Backbone.Wreqr.EventAggregator.prototype.constructor.apply(this, arguments);

		}

	});

	_.extend(Calipso.util.Vent.prototype, {
		// Command execution, facilitated by Backbone.Wreqr.Commands
		execute : function() {
			var args = Array.prototype.slice.apply(arguments);
			this.commands.execute.apply(this.commands, args);
		},

		// Request/response, facilitated by Backbone.Wreqr.RequestResponse
		request : function() {
			var args = Array.prototype.slice.apply(arguments);
			return this.reqres.request.apply(this.reqres, args);
		}
	});
	Calipso.vent = new Calipso.util.Vent();

	//////////////////////////////////////////////////
	// session
	//////////////////////////////////////////////////
	Calipso._baseUrl = false;
	Calipso.getBaseUrl = function(){
		if(!Calipso._baseUrl){
			var calipsoMainScript = document.getElementById("calipso-script-main");
			// calipso in host page
			if (calipsoMainScript) {
				var basePathEnd = calipsoMainScript.src.indexOf("/js/lib/require.js");
				Calipso._baseUrl = calipsoMainScript.src.substring(0, basePathEnd);
			} else {
				// fallback, will only work with a root web application context
				Calipso._baseUrl = window.location.protocol + "//" + window.location.host;
			}
		}
		return Calipso._baseUrl;
	}


	//
	Calipso.util.Session = Backbone.Model.extend({
		userDetails : false,
		// Creating a new session instance will attempt to load
		// the user using a "remember me" cookie token, if one exists.
		initialize : function() {
			var _this = this;
			//	this.load();
			// register handlebars helpers

			// register session related handlebars helpers
			Handlebars.registerHelper("getUserDetailsProperty", function(propName, options) {
				var prop = "";
				if (_this.isAuthenticated()) {
					prop = _this.userDetails.get(propName);
				}
				return (prop);
			});
			Handlebars.registerHelper("getUserDetailsMetadatum", function(metaName, options) {
				var metaValue = "";
				if (_this.isAuthenticated() && _this.userDetails.get("metadata")) {
					metaValue = _this.userDetails.get("metadata")[metaName];
				}
				return (metaValue);
			});
			/**
			 * Check if the loggedin user has any of the given roles. Any numberof roles can be passed to the helper.
			 * @example
			 *  {{#ifUserInRole "ROLE_MANAGER" "ROLE_ADMIN"}}  <p>User is either a Manager or an Administrator! </p>{{/ifUserInRole}}
			 */
			// TODO: move these helpers to root scope
			// and replace _this.userDetails with Calipso.session.userDetails
			Handlebars.registerHelper("ifUserInRole", function() {
				var hasRole = false;

				// only process if the user is authenticated
				if (_this.isAuthenticated()) {
					//Last argument is the options object.
					var options = arguments[arguments.length - 1];

					// now get input roles, the ones to check for just a single match
					var inputRoles = [];
					for (var i = 0; i < arguments.length-1; i++) {
						inputRoles.push(arguments[i]);
					}
					;
					// now check if user has any of the given roles
					if (inputRoles) {
						var ownedRoles = _this.userDetails.get("roles");
						var inputRole;
						for (var j = 0; j < inputRoles.length && hasRole == false; j++) {
							inputRole = inputRoles[j];
							for (var k = 0; k < ownedRoles.length && hasRole == false; k++) {
								var ownedRole = ownedRoles[k];
								if (inputRole == ownedRole.name) {
									hasRole = true;
								}
							}
						}
					}
					return hasRole ? options.fn(this) : options.inverse(this);

				}
			});


			/**
			 * Calculate "from" now using the given date
			 * @example {{#momentFromNow someDate}}
			 */
			Handlebars.registerHelper('momentFromNow', function(date) {
				return window.moment ? moment(date).fromNow() : date;
			});
		},

		// Returns true if the user is authenticated.
		isAuthenticated : function() {
			var isAuth = this.userDetails && this.userDetails.get && this.userDetails.get("id");
			console.log("session#isAuthenticated: " + isAuth);
			console.log(this.userDetails);
			return isAuth;
		},
		ensureLoggedIn : function() {
			if (!this.isAuthenticated()) {
				// TODO: save FW to redirect after loggingin
				//				this.fw = "/" + routeHelper.mainRoutePart;
				//				// we do not need the Search suffix in the route path to match
				//				if (routeHelper.contentNavTabName != "Search") {
				//					this.fw += "/" + routeHelper.contentNavTabName;
				//					// TODO: note HTTP params
				//				}
				Calipso.navigate("login", {
					trigger : true
				});

				$('#session-info').hide();
			}
		},
		// used to store an intercepted URL for use at a later time, for example
		// after login
		fw : null,

		createCookie : function(name, value, days) {
			var expires;
			if (days) {
				var date = new Date();
				date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
				expires = "; expires=" + date.toGMTString();
			} else {
				expires = "";
			}
			document.cookie = name + "=" + value + expires + "; path=/";
		},
		getCookie : function(c_name) {
			if (document.cookie.length > 0) {
				c_start = document.cookie.indexOf(c_name + "=");
				if (c_start != -1) {
					c_start = c_start + c_name.length + 1;
					c_end = document.cookie.indexOf(";", c_start);
					if (c_end == -1) {
						c_end = document.cookie.length;
					}
					return unescape(document.cookie.substring(c_start, c_end));
				}
			}
			return "";
		},
		deleteCookie : function(name, path, domain) {
			if (getCookie(name)) {
				document.cookie = name + "=" + ((path) ? ";path=" + path : "") + ((domain) ? ";domain=" + domain : "") + ";expires=Thu, 01 Jan 1970 00:00:01 GMT";
			}
		},
		// Saving will try to login the user
		save : function(model) {
			console.log("session.save called");
			console.log(model);
			var _self = this;
			var usernameOrEmail = model.get('email') ? model.get('email') : model.get('username');
			model.save( null, {
				success : function(model, response) {
					// If the login was successful set the user for the whole
					// application.
					// Also do post-successful login stuff, e.g. redirect to previous
					// page.
					_self.userDetails = model;
					if (model.get("id")) {
						Calipso.vent.trigger('session:created', _self.userDetails);
					}
					// login failed, show error
					else {
						// todo: show marionette/form error, clear fields
						window.alert("Invalid credentials!");
					}
				},

				// Generic error, show an alert.
				error : function(model, response) {
					alert("Authentication failed!");
				}

			});

		},

		// Attempt to load the user using the "remember me" cookie token, if any
		// exists.
		// The cookie should not be accessible by js. Here we let the server pick
		// it up
		// by itself and return the user details if appropriate
		load : function(loadUrl) {
			var _self = this;
			// Backbone.methodOverride = true;
			new Calipso.model.UserDetailsModel().fetch({
				async : false,
				url : loadUrl ? loadUrl : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails/remembered",
				success : function(model, response, options) {
					if (model.id) {
						_self.userDetails = model;
						Calipso.vent.trigger('session:created', _self.userDetails);
					}
				}
			});

		},

		// Logout the user here and on the server side.
		destroy : function() {
			if (this.userDetails) {
				this.userDetails.url = Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails/logout";
				this.userDetails.save(null, {
					async : false,
					url : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails/logout",
					success : function(model, response) {
						this.userDetails = model;
					},
					error : function() {
						this.userDetails = null;
						// TODO: have constants defined by dev.properties > calipso.properties > index.jsp
//						this.deleteCookie("JSESSIONID");
//						this.deleteCookie("calipso-sso");
					}
				});
				this.userDetails.clear();
				this.userDetails = null;
			}
		},
		getBaseUrl : function() {
			return Calipso.getBaseUrl();
		}

	});

	Calipso.isAuthenticated = function(){
		return Calipso.session && Calipso.session.isAuthenticated();
	}


	////////////////////////////////////////////////////////////////
	// Handlebars Helpers
	////////////////////////////////////////////////////////////////
	/**
	 * @example
	 * {{#ifLoggedIn}} <p>User is logged in! </p>{{/ifLoggedIn}}
	 */
	Handlebars.registerHelper("ifLoggedIn", function(options) {
		var loggedIn = false;
		if (Calipso.isAuthenticated()) {
			loggedIn = true;
		}
		//consolelog("Helper ifLoggedIn returns "+loggedIn);
		return loggedIn ? options.fn(this) : options.inverse(this);
	});
	/**
	 * @example
	 * {{#ifLoggedOut}} <p>User is NOT logged in!</p> {{/ifLoggedOut}}
	 */
	Handlebars.registerHelper("ifLoggedOut", function(options) {
		var loggedOut = true;
		if (Calipso.isAuthenticated()) {
			loggedOut = false;
		}
		//consolelog("Helper ifLoggedOut returns "+loggedOut);
		return loggedOut ? options.fn(this) : options.inverse(this);
	});
	// //////////////////////////////////////
	// Search cache
	// //////////////////////////////////////
	Calipso.util.cache = {
		collections : {},
		/**
		 * Returns a cache entry key as
		 * <code>collectionOptions.model.getPathFragment + "/" + (collectionOptions.useCase ? collectionOptions.useCase : "search")</code>
		 * @param collectionOptions the options used to create the collection and cache entry key
		 */
		buildCacheEntryKey : function(collectionOptions) {
			var key = collectionOptions.pathFragment ? collectionOptions.pathFragment : collectionOptions.model.prototype.getPathFragment() + "/" + (collectionOptions.useCase ? collectionOptions.useCase : "search");
			//consolelog("Calipso.util.cache#buildCacheEntryKey: " + key);
			return key;
		},
		/**
		 * Obtain a cached  for the given model type, criteria and use case.
		 * The method will return the cached collection if a match is available
		 * with the same search criteria or a new one otherwise.
		 *
		 * @param collectionOptions the options used to create the collection
		 * @return the collection created or matching the given options
		 */
		getCollection : function(collectionOptions) {
			if(!collectionOptions){
				throw "Calipso.cache.getCollection: options  are required";
			}


			if(!collectionOptions.model || !collectionOptions.model.prototype.getTypeName){
				throw "Calipso.cache.getCollection: options.model is required and must be a GenericModel subtype";
			}
			var key = this.buildCacheEntryKey(collectionOptions);
			//consolelog("Calipso.util.cache.getCollection for key: " + key);
			var collection = this.collections[key];
			// create a fresh collection when no cache entry is found,
			// or when the model doesn't want caching for it's collections,
			// or when the criteria have changed
			if (!collection || !collectionOptions.model.prototype.isCollectionCacheable() || !this.compareSearchCriteria(collection.data, collectionOptions.data)) {
				//consolelog("Calipso.util.cache#getCollection creating fresh collection for key: " + key);
				collection = new Calipso.collection.GenericCollection([], collectionOptions);
				this.collections[key] = collection;
			} else {
				//consolelog("Calipso.util.cache#getCollection returning cached collection for key: " + key);
			}
			return collection;
		},
		/**
		 * Remove a collection entry from the cache if a match is found.
		 * @param collectionOptions the options used to create the collection
		 * @return the removed collection, if any
		 */
		removeCollection : function(collectionOptions) {
			var key = this.buildCacheEntryKey(collectionOptions);
			var collection = this.collections[key];
			if (collection) {
				this.collections[key] = null;
			}
			//consolelog("Calipso.util.cache#removeCollection for key: " + key);
			return collection;
		},
		/**
		 * Create a fresh collection with the given options and replace the
		 * corresponding cache entry if one exists.
		 * @param collectionOptions the options used to create the collection
		 */
		getFreshCollection : function(options) {
			this.removeCollection(options);
			return this.getCollection(options);
		},
		/**
		 * Perform a single-level property comparison
		 * of objects that correspond to HTTP parameters
		 */
		compareSearchCriteria : function(o1, o2) {
			//consolelog("Calipso.util.cache#compareSearchCriteria, o1: " + o1 + ", o2: " + o2);
			//consolelog(o1);
			//consolelog(o2);
			for ( var p in o1) {
				if (o1.hasOwnProperty(p)) {
					if (o1[p] + "" !== o2[p] + "") {
						//consolelog("Calipso.util.cache#compareSearchCriteria returns false");
						return false;
					}
				}
			}
			for ( var p in o2) {
				if (o2.hasOwnProperty(p)) {
					if (o1[p] + "" !== o2[p] + "") {
						//consolelog("Calipso.util.cache#compareSearchCriteria returns false");
						return false;
					}
				}
			}
			//consolelog("Calipso.util.cache#compareSearchCriteria returns true");
			return true;
		}
	};

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
			if (!Calipso.isAuthenticated()) {
				return this._redir("login");
				;
			}
			this.layout.contentRegion.show(new HomeLayout());
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

		login : function() {
			var loginModel = new Calipso.model.UserModel( {
				email : Calipso.session.get('email'),
				issuer: Calipso.session.get('issuer'),

				getLayoutViewType : function() {
					return Calipso.view.ModelDrivenBrowseLayout;
				},
			});

			var view = new Calipso.config.loginViewType({
				model : loginModel
			});

			view.on('app:login', this.authenticate);
			//consolelog("AbstractController#login, showing login view");
			Calipso.vent.trigger('app:show', view);
		},
		loginRegistered : function() {
			var loginModel = new Calipso.model.UserModel( {
				email : Calipso.session.get('email'),
				issuer: Calipso.session.get('issuer'),

				getLayoutViewType : function() {
					return Calipso.view.ModelDrivenBrowseLayout;
				},
			});

			var view = new Calipso.config.loginViewType({
				template : Calipso.getTemplate('loginRegistered'),
				model : loginModel
			});

			view.on('app:login', this.authenticate);
			//consolelog("AbstractController#login, showing login view");
			Calipso.vent.trigger('app:show', view);
		},
 		accountConfirm : function(confirmationToken) {
 			if(confirmationToken){
 	 			var url = Calipso.session.getBaseUrl() +
 	 				Calipso.getConfigProperty("apiAuthPath") +
 	 				"/accountConfirmations/" + confirmationToken;
 	 			var options = Calipso.app.routeOptions;
 	 			// TODO: leave any forward at Calipso.app.fw
 	 			Calipso.session.load(url);
 			}
 			else{
 				throw "accountConfirm route requires the confirmation token as a URI component";
 			}
 		},
		authenticate : function(args) {
			// console.log('MainController authenticate called');
			var self = this;
			var email = this.$('input[name="email"]').val();
			var password = this.$('input[name="password"]').val();

			$.when(this.model.authenticate(email, password)).then(function(model, response, options) {
				Calipso.session.save(model);
				Calipso.session.load();
				// console.log('MainController authenticate navigating to home');
				Calipso.navigate("home", {
					trigger : true
				});
			}, function(model, xhr, options) {
				self.$el.find('.alert').show();
			});
		},

		logout : function() {
			Calipso.vent.trigger("session:destroy");
			// this.login();
			window.parent.destroy();
		},
		register : function(){
			this.showLayoutForModel(new Calipso.model.UserRegistrationModel());

		},
		/**
		 * Instantiate and show a layout for the given model
		 * @param  {Calipso.model.GenericModel} givenModel the model for which the layout will be shown
		 * @param  {Calipso.view.MainLayout]} the layout type to use. If absent the method will
		 *                                             obtain the layout type from givenModel.getLayoutType()
		 */
		showLayoutForModel : function(givenModel, GivenModelLayoutType){
			// make sure to choose a layout type
			if(!GivenModelLayoutType){
				GivenModelLayoutType = givenModel.getLayoutViewType();
			}
			// instantiate and show the layout
			var layout = new GivenModelLayoutType({
				model : givenModel
			});
			Calipso.vent.trigger("app:show", layout);
		},
		/**
		 * Get the model type corresponding to the given
		 * business key/URI componenent
		 */
		getModelType : function(modelTypeKey) {
			// load model Type
			var ModelType;
			if (Calipso.modelTypesMap[modelTypeKey]) {
				ModelType = Calipso.modelTypesMap[modelTypeKey];
				// console.log("AbstractController#getModelType, modelTypeKey: " + modelTypeKey + ", ModelType: " + ModelType.prototype.getTypeName());
			} else {
				var modelForRoute;
				var modelModuleId = "model/" + _.singularize(modelTypeKey);
				if (!require.defined(modelModuleId)) {
					require([ modelModuleId ], function(module) {
						ModelType = module;
					});
				} else {
					ModelType = require(modelModuleId);
				}

			}
			if (!ModelType) {
				throw "No matching model type was found for key: " + modelModuleId;
			}
			// console.log("getModelType, key: "+modelTypeKey+", type: "+ModelType.prototype.getPathFragment());
			return ModelType;
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
		 *           {@linkcode Calipso.model.GenericModel.prototype.getBusinessKey} to
		 *           define another property name.
		 * @see Calipso.model.GenericModel.prototype.getBusinessKey
		 */
		getModelForRoute : function(ModelType, modelId, httpParams) {
			//console.log("AbstractController#getModelForRoute, modelId: " + modelId + ", httpParams: " + httpParams + ", type: " + ModelType.prototype.getTypeName());

			// Obtain a model for the view:
			// if a model id is present, obtain a promise
			// for the corresponding instance
			var modelForRoute;
			if (modelId) {
				// console.log("AbstractController#getModelForRoute, looking for model id:" + modelId + ", type:" + ModelType.prototype.getTypeName());
				//modelForRoute = ModelType.all().get(modelId);
				if (modelForRoute) {
					// console.log("getModelForRoute, cached model: " + modelForRoute);
				} else {
					// console.log("getModelForRoute, creating model: " + modelForRoute);
					modelForRoute = ModelType.create({
						id : modelId,
					//url : Calipso.session.getBaseUrl() + "/api/rest/" + modelModuleId + "/" + id
					});

					// console.log("getModelForRoute, created model: " + modelForRoute);
				}
			}
			else {
				// create a model to use as a wrapper for a collection of
				// instances of the same type, fill it with any given search criteria
				if(!httpParams){
					httpParams = {};
				}
				modelForRoute = new ModelType(httpParams);
//				modelForRoute.set("isSearchModel", true);
				var collectionOptions = {
					model : ModelType,
					url : Calipso.session.getBaseUrl() + "/api/rest/" + ModelType.prototype.getPathFragment()
				};
				if (httpParams) {
					if (httpParams[""] || httpParams[""] == null) {
						delete httpParams[""];
					}
					collectionOptions.data = httpParams;
				}
				modelForRoute.wrappedCollection = Calipso.util.cache.getCollection(collectionOptions);

			}
			//console.log("AbstractController#getModelForRoute, model type: " + modelForRoute.prototype.getTypeName() + ", id: " + modelForRoute.get("id") + ", collection URL: " + Calipso.session.getBaseUrl() + "/api/rest/" + modelForRoute.getPathFragment());
			return modelForRoute;
		},
		mainNavigationReportRoute : function(mainRoutePart, queryString) {
			// console.log("AbstractController#mainNavigationReportRoute, mainRoutePart: " + mainRoutePart + ", queryString: " + queryString);

			// TODO: temp fix
			var isReport = window.location.href.indexOf("/reports") > -1 ;
			// console.log("AbstractController#mainNavigationReportRoute, isReport: " + isReport);
			if(!isReport) {
				this.mainNavigationSearchRoute(mainRoutePart, queryString);
			}
			else{
				var _self = this;
				var httpParams = Calipso.getHttpUrlParams();

				// get the model the report focuses on
				var ModelType = this.getModelType(mainRoutePart);
				if (!Calipso.isAuthenticated() && !ModelType.prototype.isPublic()) {
					return this._redir("login");
				}

				// build a report dataset collection using the model's report URL
				var reportModel = new Calipso.model.ReportDataSetModel({subjectModelType: ModelType});
				var collectionOptions = {
					model : Calipso.model.ReportDataSetModel,
					url : Calipso.session.getBaseUrl() + "/api/rest/" + reportModel.getPathFragment(),
					pathFragment : reportModel.getPathFragment(),
				};
				if (httpParams) {
					if (httpParams[""] || httpParams[""] == null) {
						delete httpParams[""];
					}
					collectionOptions.data = httpParams;
				}
				var reporDataSetCollection = Calipso.util.cache.getCollection(collectionOptions);

				// fetch and render report
				var renderFetchable = function() {
					// console.log("AbstractController#mainNavigationReportRoute calling showLayoutForModel");
					// show the layout type corresponding to the requested model
					reportModel.wrappedCollection = reporDataSetCollection;
					_self.showLayoutForModel(reportModel);
				};
				// console.log("AbstractController#mainNavigationReportRoute calling reporDataSetCollection.fetch");
				reporDataSetCollection.fetch({
					//url : collectionUrl,
					data : reporDataSetCollection.data
				}).then(renderFetchable);

			}

		},
		/**
		 *
		 */
		mainNavigationSearchRoute : function(mainRoutePart, queryString) {
			// console.log("AbstractController#mainNavigationSearchRoute, mainRoutePart: " + mainRoutePart + ", queryString: " + queryString);
			//			for (var i = 0, j = arguments.length; i < j; i++) {
			//				console.log("AbstractController#mainNavigationSearchRoute, argument: " + (arguments[i] + ' '));
			//			}
			var httpParams = Calipso.getHttpUrlParams();
			this.mainNavigationCrudRoute(mainRoutePart, null, httpParams);

		},
		mainNavigationCrudRoute : function(mainRoutePart, modelId, httpParams) {
			var _self = this;
			var qIndex = modelId ? modelId.indexOf("?") : -1;
			if (qIndex > -1) {
				modelId = modelId.substring(0, qIndex);
			}
			// build the model instance representing the current request

			var ModelType = this.getModelType(mainRoutePart);
			if (!Calipso.isAuthenticated() && !ModelType.prototype.isPublic()) {
				return this._redir("login");
			}
			var modelForRoute = this.getModelForRoute(ModelType, modelId, httpParams);
			// decide whether we are fetching a model or a collection
			var fetchable = modelForRoute.get("id") ? modelForRoute : modelForRoute.wrappedCollection;
			var skipDefaultSearch = modelForRoute.skipDefaultSearch &&  modelForRoute.wrappedCollection.hasCriteria();
			// promise to fetch then render
			// console.log("AbstractController#mainNavigationCrudRoute, mainRoutePart: " + mainRoutePart + ", model id: " + modelForRoute.get("id") + ", skipDefaultSearch: " + skipDefaultSearch);
			var renderFetchable = function() {

				// show the layout type corresponding to the requested model
				_self.showLayoutForModel(modelForRoute);

				// update page header tabs etc.
				_self.syncMainNavigationState(modelForRoute);
			};
			if (!skipDefaultSearch && fetchable.length == 0) {
				fetchable.fetch({
					data : fetchable.data
				}).then(renderFetchable);
			} else {
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
 				template: Calipso.getTemplate(templateName)
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
			console.log("MainController#editItem, item: " + item);
		}

	});


	//////////////////////////////////////////////////////
	// user Registration: model, layout etc. Uses the
	// userRegistrations path fragment/route.
	//////////////////////////////////////////////////////

	Calipso.view.UserRegistrationLayout = Calipso.view.ModelDrivenBrowseLayout.extend(
	/** @lends Calipso.view.UserRegistrationLayout.prototype */
	{
		initialize : function(options) {
			Calipso.view.ModelDrivenBrowseLayout.prototype.initialize.apply(this, arguments);
			// console.log("Calipso.view.UserRegistrationLayout#initialize");
		},

		onGenericFormSaved : function(model){
			// if the user is active just navigate to login
			// TODO: add message
			if(model.get("active") == true){
				// console.log("Calipso.view.UserRegistrationLayout#onGenericFormSaved, user is active, saving to session");

				Calipso.navigate("loginRegistered", {
					trigger : true
				});
			}
			else{
				// console.log("Calipso.view.UserRegistrationLayout#onGenericFormSaved, user is active, fwrding to confirmation form");
				var confirmationModel = new Calipso.model.UserDetailsConfirmationModel();
				this.showContent(confirmationModel);
			}
		},
	}, {
		// static members
		getTypeName : function() {
			return "UserRegistrationLayout";
		}
	});
	// AMD define happens at the end for compatibility with AMD loaders
	// that don't enforce next-turn semantics on modules.

	return Calipso;

});
