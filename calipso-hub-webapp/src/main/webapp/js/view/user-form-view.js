define([ 'backbone', 'resthub', 'hbs!template/userform' ], function(Backbone,
		Resthub, userFormTemplate) {

	var UserFormView = Backbone.View.extend({ // Resthub
		 template : userFormTemplate,
	//	template : Handlebars.compile(userFormTemplate),
		tagName : 'form',

		events: {
			    submit: 'save'
		},
		
	    render: function() {
	    	 console.log("RENDER");
	    	console.log("in user-form-view MODEL = "+JSON.stringify(this.model));
	        this.$el.html(userFormTemplate(this.model.toJSON()));
	        return this;
	      },
		
		save : function() {	
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
		//	console.log("saved. (maybe)");
		//	console.log("modelfirstName="+this.model.get("firstName"));
			return false;//
		}	
	});

	return UserFormView;

});