define(['backbone', 'view/about-view', 'view/generic-collection-grid-view', 'collection/generic-collection', 'model/host', 'model/text', 'model/user'], 
function (Backbone, AboutView, GenericCollectionGridView, GenericCollection, HostModel, TextModel, UserModel) {
	// Override Backbone.sync to use X-HTTP-Method-Override
    Backbone.emulateHTTP = true;
    //Backbone.emulateJSON
  
	
	
    var AppRouter = Backbone.Router.extend({

        initialize: function() {
            Backbone.history.start({ pushState: true, root: "/" });
        },

        routes:{
            '':'home',
            'client/home':'home',
            'client/hosts':'hosts',
            'client/text':'text',
            'client/users':'users',
            'client/about':'about'
        },

        home:function () {
            //new SamplesView({root:$('#main')});
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
        text:function () {
			console.log("text");
        	var textCollection = new GenericCollection([], {
        		model: TextModel,
        		// GenericCollection will use it's model's prototype.getDefaultSchemaForGrid() by default
        		//schemaForGrid: UserModel.prototype.getDefaultSchemaForGrid(),
        		url: window.calipso.getBaseUrl() + "/api/rest/cms/text/"
        	});

			console.log("ress, resCollection model: "+textCollection.model);
            new GenericCollectionGridView({root:$('#main'), collection:textCollection}).render();
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
        about:function () {
            new AboutView({root:$('#main')});
        },
        
        
    });
    

	console.log("AppRouter done ");

    return AppRouter;

});