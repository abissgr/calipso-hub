define([ 'backbone' ], function(Backbone) {
	var UserModel = Backbone.Model.extend({
				url : "/api/rest/user",
				defaults : {
				// firstName: "empty name"
				},
				initialize: function () {
				    Backbone.Model.prototype.initialize.apply(this, arguments);
				    this.on("change", function (model, options) {
					    if (options && options.save === false) return;
					    model.save();
				    });
				},
				  
				validate : function(attrs) {
					if (/^\s*$/.test(attrs.userName)) {
						console.log('User Name cannot be blank.');
						return 'User Name cannot be blank.';
					}
				},
				save : function(attributes, options) {
					console.log("Saving change, attributes: "+attributes+", options: "+options);
					var result = Backbone.Model.prototype.save.call(this, attributes, options);
					console.log("Saved change");
				},

			// validation: {
			// userName: {
			// required: true,
			// msg: 'A User Name is required.'
			// }
			// }

			});
	return UserModel;
});