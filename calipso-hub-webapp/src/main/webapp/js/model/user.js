define(['backbone'], function(Backbone) {
    var UserModel = Backbone.Model.extend({
    	url : "/api/rest/user",
        defaults: {
          //  firstName: "empty name"
        },
        validate: function(attrs) {
            if (/^\s*$/.test(attrs.userName)) {
              return 'User Name cannot be blank.';
            }
          },
     
        validation: {
            userName: {
              required: true,
              msg: 'A User Name is required.'
            }
        }
        

    });
    return UserModel;
});