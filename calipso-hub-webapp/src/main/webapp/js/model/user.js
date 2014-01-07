define(['backbone'], function(Backbone) {
    var UserModel = Backbone.Model.extend({
    	url : "/api/rest/user",
        defaults: {
          //  firstName: "empty name"
        },
        initialize: function() {
            Backbone.Model.prototype.initialize.apply(this, arguments);
            this.on("change", function(model, options) {
                if (options && options.save === false){
                    return;
                }
                console.log("Saving change");
                model.save();
            });
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