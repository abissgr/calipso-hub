define(['backbone', 'view/about-view', 'view/samples-view', 'view/users-view', 'collection/generic'], 
function (Backbone, AboutView, SamplesView, UsersView, GenericCollection) {
    var AppRouter = Backbone.Router.extend({

        initialize: function() {
            Backbone.history.start({ pushState: true, root: "/" });
        },

        routes:{
            '':'home',
            'home':'home',
            'users':'users',
            'about':'about'
        },

        home:function () {
            new SamplesView({root:$('#main')});
        },
        users:function () {
        	var userCollection = new GenericCollection();
            new UsersView({root:$('#main'), collection:userCollection});
        },
        about:function () {
            new AboutView({root:$('#main')});
        }
        
    });

    return AppRouter;

});