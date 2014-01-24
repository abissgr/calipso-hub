define([ 'backbone', 'resthub', 'backgrid', 'backgrid-paginator', 'model/user', 'hbs!template/generic' , 'collection/generic-collection'],
function (Backbone, Resthub, Backgrid, BackgridExtensionPaginator, User, genericCollectionTemplate, GenericCollection) {
    //Backgrid.Extension.Paginator = Paginator;
    var GenericCollectionGridView = Resthub.View.extend(
    /** @lends collection/GenericCollectionGridView.prototype */
    {
    	
        
    	/**
         * A generic grid view for generic collections
         *
         * @augments external:Resthub.View
         * @constructs
         */
    	initialise: function() {
    		Resthub.View.prototype.initialize.apply(this, arguments);
    	},
    	
        // Define view template
        template: genericCollectionTemplate,

        
		render: function() {
			GenericCollectionGridView.__super__.render.apply(this);

			var backgrid = new Backgrid.Grid({
				  columns: this.collection.schemaForGrid,
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
			this.collection.fetch({reset: true});
			//this.collection.fetch();
		}

    });
    return GenericCollectionGridView;
});

