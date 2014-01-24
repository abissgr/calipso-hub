define([ 'backbone', 'resthub', 'hbs!template/userform' ], function(Backbone,
		Resthub, userFormTemplate) {

	var UserFormView = Resthub.View.extend({
		template : userFormTemplate,
		tagName : 'form',

		events: {
			    submit: 'save'
		},
		
		save : function() {
	
		// this.model.set("id", "qweqwe");
	// this.model.set(url, "/api/user");
		
	//	this.model.save();
			
			
			this.model.save( {
				"userName" : this.$('.userName').val(),
				"userPassword" : this.$('.userPassword').val(),

				"email" : this.$('.email').val(),
				
				"firstName" : this.$('.firstName').val(),
				"lastName" : this.$('.lastName').val(),

					success: function(model, resp, opt) {
								console.log("successfully saved" + resp);
					},
					error: function(model, resp, opt) {
								console.log("error", resp);
					}
		
		});
			//this.model.save();
			console.log("saved. (maybe)");
			console.log("modelfirstName="+this.model.get("firstName"));
			return false;//
		}	
	});

	return UserFormView;

});