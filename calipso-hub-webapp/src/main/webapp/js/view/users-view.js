define([ 'backbone', 'resthub', 'collection/users', 'hbs!template/users' ],
function (Backbone, Resthub, Users, usersTemplate) {
    
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
        }

    });
    return UsersView;
});