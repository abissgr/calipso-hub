define(['session', 'underscore', 'backbone', 'marionette', 'hbs!template/login', 'model/user' ],

function(session, _, Backbone, Marionette, tmpl, UserModel) {

	var LoginView = Marionette.ItemView.extend({

		className : 'row',

		template : tmpl,
		/**
		 * Get the name of this class
		 * @returns the class name as a string
		 */
		getTypeName : function() {
			return this.prototype.getTypeName();
		},
		events : {
			"click button" : "commit",
			"submit" : "commit"
		},
		commit : function(e){
			e.preventDefault();

			
			var _this = this;
			var userDetails = new UserModel({
				email: this.$('.input-email').val(),
				password: this.$('.input-password').val(),
				// was used for testing
				// metadata: {"loginViewMetadatum":"true"}
			});
			session.save(userDetails);

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
		
	});

	/**
	 * Get the name of this class
	 * @returns the class name as a string
	 */
	LoginView.prototype.getTypeName = function() {
		return "LoginView";
	}
	return LoginView;
});