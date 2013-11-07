define(['backbone'], function(Backbone) {
    var UserModel = Backbone.Model.extend({
    	url : "/api/user",
        defaults: {
            firstName: "empty name"
        }

    });
    return UserModel;
});