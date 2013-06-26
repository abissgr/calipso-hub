define(['backbone', 'view/about-view', 'view/samples-view', 'view/users-view'], function (Backbone, AboutView, SamplesView, UsersView) {
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
            new UsersView({root:$('#main')});
        },
        about:function () {
            new AboutView({root:$('#main')});
        }
        
    });

    return AppRouter;

});