define([ 'backbone', 'resthub', 'collection/users', 'model/user', 'view/userform', 'hbs!template/users' ],
function (Backbone, Resthub, Users, User, UserFormView, usersTemplate) {
    
    var UsersView = Resthub.View.extend({
        
        // Define view template
        template: usersTemplate,

        initialize:function () {
            // Initialize the collection
            this.collection = new Users();
            console.log("users-view init");
            // Render the view when the collection is retreived from the server
            this.listenTo(this.collection, 'sync', this.render);
            
            // Request unpaginated URL
            this.collection.fetch({ data: { page: 'no'} });
        },
     
        events: {
        	'click #createUser': 'create',
            dblclick: 'edit'
          },
          
          edit: function(event) {
        	  // userName is used as id of element
        	  var target = $(event.target).attr("id");
        	  
        	// get model for target user
        	  this.model = this.collection.findWhere({"userName" : target});
        	  
              var userFormView = new UserFormView({root: this.$el, model: this.model});
              userFormView.render();
            },

        create: function() {
        	var user = new User();
            var userFormView = new UserFormView({root: this.$('#users-list'), model: user});
            userFormView.render();
          }

    });
    return UsersView;
});

