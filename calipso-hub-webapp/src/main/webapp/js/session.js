define([
  'jquery',
  'underscore',
  'backbone',
  'app',
  'model/user'
],

function ($, _, Backbone, CalipsoApp, UserModel){

  var Session = Backbone.Model.extend({
	  
	  // The current user details
	 userDetails: null,

    // Creating a new session instance will attempt to load
	 // the user using a "remember me" cookie token, if one exists.
    initialize: function() {
        this.load();
    },

    // Returns true if the user is authenticated.
    isAuthenticated: function() {
      return this.userDetails != null && this.userDetails.get("id") != null;
    },

    // Saving will try to login the user
    save: function(model) {
   	 console.log("session.save called, doing nothing");
   	 var _self = this;
   	 new UserModel().save({
				email: model.get('email'),
				userPassword: model.get('password')
			},{
				success: function(model, response){
					// If the login was successful set the user for the whole application.
					// Also do post-successful login stuff, e.g. redirect to previous page.
					if (model.id) {
						_self.userDetails = userDetails;
						if(false/*CalipsoApp.afterLoginRedirectUrl*/){
							
						}
						else{
							
						}
					}
					// login failed, show error
					else {
						// todo: show marionette/form error, clear fields
						window.alert("Invalid credentials!");
					}
				},

				// Generic error, show an alert.
				error: function(model, response){
					alert("Authentication failed!");
				}

			});

    },

    
	 // Attempt to load the user using the "remember me" cookie token, if any exists.
    // The cookie should not be accessible by js. Here we let the server pick it up
    // by itself and return the user details if appropriate
    load: function() {
   	var _self = this;
   	 // Backbone.methodOverride = true;
   	new UserModel().fetch({
 			url: CalipsoApp.getCalipsoAppBaseUrl() + "/api-auth/userDetails/remembered",
 			success: function(model, response, options) {
 				if (model.id) {
 					_self.userDetails = model;
 				}
 			}
 		});
 		
 	},
   // Logout the user here and on the server side.
 	destroy: function () {
   	 if (this.userDetails) {
   		 this.userDetails.url = CalipsoApp.getCalipsoAppBaseUrl() + "/api-auth/userDetails/logout";
   		 this.userDetails.save();
   		 this.userDetails.clear();
   		 this.userDetails = null;
 		}
    }

  });

  return new Session();

});