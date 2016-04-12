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

define([ "lib/calipsolib/util", 'underscore', 'handlebars', 'moment', 'backbone', 'marionette', ], function(Calipso, _, Handlebars, moment, Backbone, BackboneMarionette) {

	//
	Calipso.util.Session = Backbone.Model.extend({

		initialize : function(options) {
			Backbone.Model.prototype.initialize.apply(this, arguments);
			var _session = this;
			this.userDetails = Calipso.model.UserDetailsModel.create();
			this.listenTo(this.userDetails, 'sync', function(){
				_session.reset();
			});
		},
		getRoles : function(){
			return _.extend({}, this.userDetails.get("roles"));
		},
		// Returns true if the user is authenticated.
		isAuthenticated : function() {
			return this.userDetails && this.userDetails.get && this.userDetails.get("id");
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
		/*
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
			var _self = this;
			var usernameOrEmail = model.get('email') ? model.get('email') : model.get('username');
			model.save(null, {
				success : function(model, response) {
					// If the login was successful set the user for the whole
					// application.
					// Also do post-successful login stuff, e.g. redirect to previous
					// page.
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

		*/
		reset : function(){
			// send logged in user on their way
			var fw = "home";
			if (Calipso.app.fw) {
				fw = Calipso.app.fw;
				Calipso.app.fw = null;
			}
			// reload the app if locale needs to be changed
			var userLocale = this.userDetails.get("locale");
			var oldLocale = localStorage.getItem("locale");
			if (!oldLocale || oldLocale != userLocale) {
				localStorage.setItem("locale", this.userDetails.get("locale"));

				Calipso.navigate(fw, {
					trigger : false
				});
				window.location.reload();
			} else {
				// locale is the same, proceed normally
				Calipso.updateHeaderFooter();
				/*
				Calipso.navigate(fw, {
					trigger : true
				});
				*/
			}

		},
		// Attempt to load the user using the "remember me" cookie token, if any
		// exists.
		// The cookie should not be accessible by js. Here we let the server pick
		// it up
		// by itself and return the user details if appropriate
		start : function(loadUrl) {
			console.log("Session start");
			var _self = this;
			// Backbone.methodOverride = true;
			return this.userDetails.fetch({
				//async : false,
				url : loadUrl ? loadUrl : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails",
				success : function(model, response, options) {
					console.log("Session trigger remmber");
					_self.trigger("remmber", model, options );
				}
			});

		},
		// Logout the user here and on the server side.
		destroy : function() {
			var _self = this;
			if (this.userDetails) {
				//this.userDetails.url = Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails";
				this.userDetails.destroy({
					async : false,
					url : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails",
					success : function(model, response) {
							_self.userDetails = model;
							_self.reset();
					},
				});
			}
		},
		getBaseUrl : function() {
			return Calipso.getBaseUrl();
		}

	});

});
