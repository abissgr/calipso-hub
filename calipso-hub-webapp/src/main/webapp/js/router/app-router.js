define(['backbone', 'view/about-view', 'view/generic-collection-grid-view', 'collection/generic-collection', 'model/user', 'model/host'], 
function (Backbone, AboutView, GenericCollectionGridView, GenericCollection, UserModel, HostModel) {
	// Override Backbone.sync to use X-HTTP-Method-Override
    Backbone.emulateHTTP = true;
    //Backbone.emulateJSON
  
	
	
    var AppRouter = Backbone.Router.extend({

        initialize: function() {
            Backbone.history.start({ pushState: true, root: "/" });
        },

        routes:{
            '':'home',
            'home':'home',
            'hosts':'hosts',
            'users':'users',
            'about':'about'
        },

        home:function () {
            //new SamplesView({root:$('#main')});
        },
        users:function () {
			console.log("users");
        	var userCollection = new GenericCollection([], {
        		model: UserModel,
        		// GenericCollection will use it's model's prototype.getDefaultSchemaForGrid() by default
        		//schemaForGrid: UserModel.prototype.getDefaultSchemaForGrid(),
        		url: window.calipso.getBaseUrl() + "/api/rest/user/"
        	});

			console.log("users, userCollection model: "+userCollection.model);
            new GenericCollectionGridView({root:$('#main'), collection:userCollection}).render();
        },
        hosts:function () {
			console.log("hosts");
        	var hostCollection = new GenericCollection([], {
        		model: HostModel,
        		// GenericCollection will use it's model's prototype.getDefaultSchemaForGrid() by default
        		//schemaForGrid: UserModel.prototype.getDefaultSchemaForGrid(),
        		url: window.calipso.getBaseUrl() + "/api/rest/host/"
        	});

			console.log("hosts, hostCollection model: "+hostCollection.model);
            new GenericCollectionGridView({root:$('#main'), collection:hostCollection}).render();
        },
        about:function () {
            new AboutView({root:$('#main')});
        },
        
        
    });
    

	console.log("AppRouter done ");

    return AppRouter;

});