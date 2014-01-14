define(['backbone', 'view/about-view', 'view/samples-view', 'view/generic-collection-view', 'collection/generic', 'model/user'], 
function (Backbone, AboutView, SamplesView, GenericCollectionView, GenericCollection, User) {
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
        	var userCollection = new GenericCollection([], {
        		model: User,
     	        url:'api/rest/user', // was api/user
        	});
        	userCollection.setGridColumns(this.getGridColumnsForUser());
            new GenericCollectionView({root:$('#main'), collection:userCollection});
        },
        about:function () {
            new AboutView({root:$('#main')});
        },
        getGridColumnsForUser: function(){
    		var gridColumns = [{
    			  name: "id", // The key of the model attribute
    			  label: "ID", // The name to display in the header
    			  editable: false, // By default every cell in a column is editable, but *ID* shouldn't be
    			  // Defines a cell type, and ID is displayed as an integer without the ',' separating 1000s.
    			  cell: Backgrid.StringCell.extend({
    			    orderSeparator: ''
    			  })
    			},{
    			  name: "userName",
    			  label: "username",
    			  cell: "string"
    			}, {
    			  name: "firstName",
    			  label: "firstName",
    			  editable: true,
    			  cell: "string" 
    			}, {
    			  name: "lastName",
    			  label: "lastName",
    			  editable: true,
    			  cell: "string" 
    			}, {
    			  name: "email",
    			  label: "email",
    			  cell: "email"
    			}, {
    			  name: "createdDate",
    			  label: "created",
    			  cell: "datetime"
    			}];
    		return gridColumns;
        }
        
    });
    


    return AppRouter;

});