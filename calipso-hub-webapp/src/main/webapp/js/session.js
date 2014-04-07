define([ 'jquery', 'underscore', 'backbone', 'vent' ],

function($, _, Backbone, vent) {
	var baseUrl;
	var calipsoMainScript = document.getElementById("calipso-script-main");
	// calipso in host page
	if (calipsoMainScript) {
		var basePathEnd = calipsoMainScript.src.indexOf("/js/lib/require.js");
		baseUrl = calipsoMainScript.src.substring(0, basePathEnd);
	} else {
		// fallback, will only work with a root web application context
		baseUrl = window.location.protocol + "//" + window.location.host;
	}
	console.log("session, base URL:" + baseUrl);

	var UserDetailsModel = Backbone.Model.extend({

		url : baseUrl + '/api-auth/userDetails',

		sync : function(method, model, options) {
			options = options || {};
			options.timeout = 30000;
			// options.dataType = "jsonp"; // JSON is default.
			return Backbone.sync(method, model, options);
		},

	});
	//
	var Session = Backbone.Model.extend({
		userDetails : false,
		// Creating a new session instance will attempt to load
		// the user using a "remember me" cookie token, if one exists.
		initialize : function() {
			this.load();
		},

		// Returns true if the user is authenticated.
		isAuthenticated : function() {
			return this.userDetails && this.userDetails.get && this.userDetails.get("id");
		},
		ensureLoggedIn : function() {
			if (!this.isAuthenticated()) {
				this.fw = "/client/" + routeHelper.mainRoutePart;
				// we do not need the Search suffix in the route path to match
				if (routeHelper.contentNavTabName != "Search") {
					this.fw += "/" + routeHelper.contentNavTabName;
					// TODO: note HTTP params
				}
				Backbone.history.navigate("client/login", {
					trigger : true
				});

				$('#session-info').hide();
			}
		},
		// used to store an intercepted URL for use at a later time, for example
		// after login
		fw : null,
		// Saving will try to login the user
		save : function(model) {
			console.log("session.save called, doing nothing");
			var _self = this;
			var userNameOrEmail = model.get('email') ? model.get('email') : model.get('username');
			new UserDetailsModel().save({
				email : userNameOrEmail,
				password : model.get('password')
			}, {
				success : function(model, response) {
					// If the login was successful set the user for the whole
					// application.
					// Also do post-successful login stuff, e.g. redirect to previous
					// page.
					if (model.id) {

						vent.trigger('session:created', model);
						if (false/* app.afterLoginRedirectUrl */) {

						} else {

						}
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
		load : function() {
			var _self = this;
			// Backbone.methodOverride = true;
			new UserDetailsModel().fetch({
				async : false,
				url : baseUrl + "/api-auth/userDetails/remembered",
				success : function(model, response, options) {
					if (model.id) {
						_self.userDetails = model;
					}
				}
			});

		},
		// Logout the user here and on the server side.
		destroy : function() {
			if (this.userDetails) {
				this.userDetails.url = baseUrl + "/api-auth/userDetails/logout";
				this.userDetails.save();
				this.userDetails.clear();
				this.userDetails = null;
			}
		},
		getBaseUrl : function() {
			return baseUrl;
		},

	});

	return new Session();

});