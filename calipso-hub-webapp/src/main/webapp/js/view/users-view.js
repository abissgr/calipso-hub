define([ 'backbone', 'resthub', 'collection/users', 'model/user', 'view/userform', 'hbs!template/users' ],
function (Backbone, Resthub, Users, User, UserFormView, usersTemplate) {
    
    var UsersView = Resthub.View.extend({
        
        // Define view template
        template: usersTemplate,

        initialize:function () {
            // Initialize the collection
            this.collection = new Users();
            
            // Render the view when the collection is retreived from the server
            this.listenTo(this.collection, 'sync', this.render);
            
            // Request unpaginated URL
            this.collection.fetch({ data: { page: 'no'} });
        },
     
        events: {
        	/* click: 'toggleDetails', */
        	'click #createUser': 'create',
            dblclick: 'edit'
          },
          
          edit: function() {
              var userFormView = new UserFormView({root: this.$el, model: this.model});
              userFormView.render();
            },

        create: function() {
        	console.log("Create clicked");
        	var user = new User();
        	console.log("user = "+user);
            var userFormView = new UserFormView({root: this.$('#users-list'), model: user});
            userFormView.render();
          }

    });
    return UsersView;
});

