define(['app', 'underscore', 'backbone', 'marionette', 'hbs!template/login', 'model/userDetails' ],

function(app, _, Backbone, Marionette, tmpl, UserDetailsModel) {

	return Marionette.ItemView.extend({

		className : 'row',

		template : tmpl,

		events : {
			"click button" : "commit",
			"submit" : "commit"
		},
		commit : function(e){
			e.preventDefault();

			
			var _this = this;
			var userDetails = new UserDetailsModel();
			userDetails.save({
				email: this.$('.input-email').val(),
				password: this.$('.input-password').val(),
				// was used for testing
				// metadata: {"loginViewMetadatum":"true"}
			},{
				success: function(model, response){
					console.log("LoginView.commit, success");

					app.vent.trigger('session:created', model);
					
				},

				// Generic error, show an alert.
				error: function(model, response){
					alert("Authentication failed: ");
				}

			});

		},

		onShow : function() {
			// hide session info in nav bar
			console.log("LoginView.onShow, hiding session-info");
			$('#session-info').hide();
			
		},
		onBeforeClose : function() {
			// show session info in nav bar
			console.log("LoginView.onShow, showing session-info");
			$('#session-info').show();},

	},
	{
		typeName: "LoginView"
	});

});