define([ 'backbone', 'resthub', 'backgrid', 'backgrid-paginator', 'model/user', 'view/userform', 'hbs!template/generic' , 'collection/generic'],
function (Backbone, Resthub, Backgrid, BackgridExtensionPaginator, User, UserFormView, genericCollectionTemplate, GenericCollection) {
    //Backgrid.Extension.Paginator = Paginator;
    var UsersView = Resthub.View.extend({
        
        // Define view template
        template: genericCollectionTemplate,

        initialize:function (options) {
            // Initialize the collection
            this.collection = options.collection;
            
            // Render the view when the collection is retreived from the server
            this.listenTo(this.collection, 'sync', this.render);
            this.listenTo(this.collection, 'change', this.render);
            
            // Request unpaginated URL
            this.collection.fetch(/*{ data: { page: 'no'} }*/);
            
        },
        
		render: function() {
			UsersView.__super__.render.apply(this);
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
			var backgrid = new Backgrid.Grid({

				  columns: gridColumns,
				  collection: this.collection
			});
			

			$('#backgrid').append(backgrid.render().$el);
			var paginator = new Backgrid.Extension.Paginator({

				  // If you anticipate a large number of pages, you can adjust
				  // the number of page handles to show. The sliding window
				  // will automatically show the next set of page handles when
				  // you click next at the end of a window.
				  windowSize: 20, // Default is 10

				  // Used to multiple windowSize to yield a number of pages to slide,
				  // in the case the number is 5
				  slideScale: 0.25, // Default is 0.5

				  // Whether sorting should go back to the first page
				  goBackFirstOnSort: false, // Default is true

				  collection: this.collection
				});

			$('#backgrid-paginator').append(paginator.render().el);
			
			//this.collection.fetch();
		}
     /*
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
        	  $("#createUser").hide();
        	var user = new User();
            var userFormView = new UserFormView({root: this.$('#users-list'), model: user});
            userFormView.render();
          }
          */

    });
    return UsersView;
});

