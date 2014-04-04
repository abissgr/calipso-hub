define(['backbone', 'app'], function(Backbone, app) {
	var UserDetailsModel = Backbone.Model.extend({

		url:app.getCalipsoAppBaseUrl()+'/api-auth/userDetails',
		
		sync: function(method, model, options){  
			options = options || {};
			options.timeout = 30000;  
			//options.dataType = "jsonp";  // JSON is default.
			return Backbone.sync(method, model, options);  
		},


	});
	return UserDetailsModel;
});