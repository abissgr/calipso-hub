define([ 'backbone', 'backbone-pageable' ],
function(Backbone, BackbonePageableCollection) {
	var GenericCollection = Backbone.PageableCollection.extend({
		mode: "server",
		
		initialize : function(attributes, options) {
			if (options.model) {
				this.model = options.model;	
			}
			console.log("GenericCollection#initialize, options: "+options);
			console.log("GenericCollection#initialize, this.model: "+this.model);
			console.log("GenericCollection#initialize, options.model: "+options.model);
			// use given grid columns if provided, or the
			// default model columns otherwise
							if (options.schemaForGrid) {
				console.log("GenericCollection#initialize: using options.schemaForGrid");
				this.schemaForGrid = options.schemaForGrid;
			} else {
				console.log("GenericCollectioninitialize: using this.model.prototype.schemaForGrid");
				this.schemaForGrid = this.model.prototype.getDefaultSchemaForGrid();
			}
			console.log("GenericCollection#initialize: this.schemaForGrid: " + this.schemaForGrid);
		},

		schemaForGrid : {},
		// Initial pagination states
		state: {
			firstPage: 1,
			currentPage: 1,
			pageSize: 15,
		},
		
		// You can remap the query parameters from `state keys from
		// the default to those your server supports
		queryParams: {
			 currentPage: "page",
			 pageSize: "size",
			 totalPages: "totalPages",
			 totalRecords: "totalElements",
			 sortKey: "properties",
			 direction: "order"
		},
						//		
//						 parseState: function (resp, queryParams, state, options) {
//							 return {
//								 totalRecords: resp.totalElements
//							 };
//						 },
//						//		
//						 parseRecords: function (resp, options) {
//							 return resp.content ;
//						 },

						// Parse the JSON response and get the total number of
						// elements.
						// Return only the content JSON element, that contains
						// the users.
						// These are necessary for paging to work.
//						parse : function(resp) {
//							this.total = resp.totalElements;
//							this.totalPages = resp.totalPages;
//							return resp.content;
//						}
		 parse: function(resp){
			this.total = resp.totalElements;
			this.totalPages = resp.totalPages;
		    var _resp = {};
		    _resp.results = [];
		    _.each(resp.content, function(model) {
		    	model.collection - this;
		        _resp.results.push(model);
		    });
		    return _resp.results;
		 }


	});
	console.log("GenericCollection done");
	return GenericCollection;
});
