define(['backbone', 'backbone-pageable', 'model/user'], function (Backbone, BackbonePageableCollection, User) {
	//Backbone.Collection = PageableCollection;
    var Users = Backbone.PageableCollection.extend({
	

        // Reference to this collection's model.
        model: User,
        url:'api/rest/user', // was api/user
        	
    	// Initial pagination states
//		state: {
//			firstPage: 1,
//			 currentPage: 1,
//		  pageSize: 15,
//		},
//		 // You can remap the query parameters from `state` keys from
//		// the default to those your server supports
//		queryParams: {
//			 currentPage: "page",
//			    pageSize: "size",
//			    totalPages: "totalPages",
//			    totalRecords: "totalElements",
//			    sortKey: "properties",
//			    direction: "order"
//		},
//		
//		parseState: function (resp, queryParams, state, options) {
//		  return {
//			  totalRecords: resp.totalElements
//			 };
//		},
//		
//		parseRecords: function (resp, options) {
//		  return resp.content	;
//		}


		// Parse the JSON response and get the total number of elements.
		// Return only the content JSON element, that contains the users.
		// These are necessary for paging to work.
		parse: function(resp) {
			this.total = resp.totalElements;
			this.totalPages = resp.totalPages;
			return resp.content;
		}
    });
    return Users;
});
