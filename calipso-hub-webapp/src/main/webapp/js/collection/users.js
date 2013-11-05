define(['backbone', 'model/user'], function (Backbone, User) {

    var Users = Backbone.Collection.extend({

        // Reference to this collection's model.
        model: User,
        url:'api/user' // was api/user

    });
    return Users;
});
