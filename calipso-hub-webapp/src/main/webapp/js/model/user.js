define(['backbone'], function(Backbone) {
    var UserModel = Backbone.Model.extend({

        defaults: {
            firstName: "empty name"
        }

    });
    return UserModel;
});