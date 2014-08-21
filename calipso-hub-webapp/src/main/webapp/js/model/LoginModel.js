define(['backbone', 'session'], function(Backbone, session) {

  return Backbone.Model.extend({

		modelKey: "login",

		url: session.getBaseUrl()+'/api-auth',

    defaults: {
      email    : '',
      password : '',
      token    : ''
    },

    authenticate: function (email, password) {
   	 var authToken =  'manos:manos';
       
//      var authToken = btoa(email + ':' + password);
//      var dfd = new $.Deferred();
//
//      this.set({
//        email : email
//      },{
//        silent: true
//      });
//
//      this.fetch({
//        beforeSend: function (xhr) {
//          xhr.setRequestHeader("Authorization", "Basic " + authToken);
//        },
//        success: function(model, response, options) {
//          dfd.resolve(model, response, options);
//        },
//        error: function(model, xhr, options) {
//          dfd.reject(model, xhr, options);
//        }
//      });

      return dfd.promise();
    }

  },
	showInMenu : false,
	label : "LoginModel"
  );

});
