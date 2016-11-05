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
define([ "lib/calipsolib/util", 'underscore', 'handlebars', 'moment', 'backbone', 'marionette', ], function(Calipso, _, Handlebars, moment, Backbone, BackboneMarionette) {

	//
	Calipso.util.Session = Backbone.Model.extend({

		initialize : function(options) {
			Backbone.Model.prototype.initialize.apply(this, arguments);
			var _session = this;
			this.userDetails = Calipso.model.UserDetailsModel.create();
		},
		getRoles : function(){
			return this.isAuthenticated() ? this.userDetails.get("roles") : [];
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
			}
		},

		// Attempt to load the user using the "remember me" cookie token, if any
		// exists.
		// The cookie should not be accessible by js. Here we let the server pick
		// it up
		// by itself and return the user details if appropriate
		start : function() {
			var _self = this;
			// Backbone.methodOverride = true;
			this.userDetails.fetch({
				//async : false,
				reset : true,
                //url : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails",

			});

		},
		// Logout the user here and on the server side.
		logout : function() {

			var _self = this;
			this.userDetails.destroy({
				url : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/userDetails",

				success : function(model, response, options) {

						console.log("Calipso.session.logout success");

						Calipso.navigate("/", {
							trigger : false
						});
						window.location.reload();
						//_self.reset();
				},

			});
		},
		getBaseUrl : function() {
			return Calipso.getBaseUrl();
		}

	});

});
