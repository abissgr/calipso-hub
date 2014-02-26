define([ 'marionette', 'app', 'view/AppLayoutView', 'view/HomeView', 'model/LoginModel', 'view/LoginView', 'session', 'vent', 
         'model/host', 'model/text', 'model/user',
         'view/generic-collection-grid-view', 'collection/generic-collection'],
		
function(Marionette, CalipsoApp, AppLayoutView, HomeView, LoginModel, LoginView, session, vent,
		HostModel, TextModel, UserModel,
		GenericCollectionGridView, GenericCollection) {

	// private
	var _initializeLayout = function() {
		MainController.layout = new AppLayoutView({
			model : session
		});

		MainController.layout.on("show", function() {
			vent.trigger("layout:rendered");
		});

		vent.trigger('app:show', MainController.layout);
	};

	vent.on("layout:rendered", function() {
		console.log('layout:rendered (MainController)');
	});

	var MainController = {

		modelsMap : {
			'hosts' : HostModel,
			'texts' : TextModel,
			'users' : UserModel,
		},
		home : function() {

			console.log('MainController home called');
			if (!session.isAuthenticated()) {
				Backbone.history.navigate("#login", {
					trigger : true
				});
				return false;
			}

			var homeView = new HomeView();

			_initializeLayout();

			MainController.layout.content.show(homeView);
		},

		login : function() {

			var loginModel = new LoginModel({
				email : session.get('email'),
				issuer : session.get('issuer')
			});

			var view = new LoginView({
				model : loginModel
			});

			view.on('app:login', MainController.authenticate);

			vent.trigger('app:show', view);
		},

		authenticate : function(args) {
			console.log('MainController authenticate called');
			var self = this;
			var email = this.$('input[name="email"]').val();
			var password = this.$('input[name="password"]').val();

			$.when(this.model.authenticate(email, password)).then(function(model, response, options) {
				var token = model.get('token');
				session.save(model);
				session.load();
				console.log('MainController authenticate navigating to home');
				Backbone.history.navigate("#home", {
					trigger : true
				});
			}, function(model, xhr, options) {
				self.$el.find('.alert').show();
			});
		},

		logout : function() {
			session.destroy();
			Backbone.history.navigate("#login", {
				trigger : true
			});
		},

		mainNavigationRoute : function(mainNavigationTab) {
			console.log("main, mainNavigationTab: " + mainNavigationTab);
			// sync main menu state
			this.syncMainNavigationState(mainNavigationTab);

			if (typeof this[mainNavigationTab] == 'function') {
				// proceed with actual route
				this[mainNavigationTab]();
				return;
			} else if (this.modelsMap[mainNavigationTab]) {
				contentRegionView = this.genericMainNavigationView($('#main'), mainNavigationTab);
			} else {
				window.alert("No route found!");
			}

			console.log("mainNavigationRoute, contentRegionView: " + contentRegionView);
			// layout.contentRegion.show(contentRegionView);
			MainController.layout.content.show(contentRegionView);

		},
		genericMainNavigationView : function(viewRoot, mainNavigationTab, entityKey) {
			var navigationView;
			var viewModel = this.modelsMap[mainNavigationTab];
			console.log("genericMainNavigationView, viewModel: " + viewModel);
			var viewRoute = "/api/rest/" + mainNavigationTab + "/";
			console.log("genericMainNavigationView, viewRoute: " + viewRoute);
			// is a specific entity requested?
			// if(entityKey){
			//	        		
			// }
			// else{
			// no specific entity requested,
			// go for collection view
			var viewCollection = new GenericCollection([], {
				model : viewModel,
				url : CalipsoApp.getCalipsoAppBaseUrl() + viewRoute
			});
			navigationView = new GenericCollectionGridView({/* root:viewRoot, */
				collection : viewCollection
			});
			// }

			console.log("genericMainNavigationView, navigationView: " + navigationView);
			return navigationView;
		},
		syncMainNavigationState : function(mainNavigationTab) {
			$('.navbar-nav li.active').removeClass('active');
			$('#mainNavigationTab-' + mainNavigationTab).addClass('active');
		},

	};

	return MainController;
});