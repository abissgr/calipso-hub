define([ 'marionette', 'controllers/MainController' ], 
function(Marionette, MainController) {

	return Marionette.AppRouter.extend({

		controller : MainController,

		appRoutes : {
			'' : 'home',
			':mainNavigationTab' : 'mainNavigationRoute',
			'home' : 'home',
			'login' : 'login',
			'logout' : 'logout'
		},

	});

});