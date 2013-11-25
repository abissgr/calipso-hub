define([ 'backbone', 'resthub', 'hbs!template/userform' ], function(Backbone,
		Resthub, userFormTemplate) {

	var UserFormView = Backbone.View.extend({ // Resthub
		
		 initialize: function() {
			 this.listenTo(this.model, 'invalid', this.invalid);
			 Backbone.Validation.bind(this);
		 },
		 
		 template : userFormTemplate,

		tagName : 'form',

		events: {
			    submit: 'save'
		},
		
	    render: function() {
	        this.$el.html(userFormTemplate(this.model.toJSON()));
	        return this;
	      },
		
		save : function() {	
			this.model.validate(); // shouldn't be needed
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
			return false;
		}	
	});

	return UserFormView;

});