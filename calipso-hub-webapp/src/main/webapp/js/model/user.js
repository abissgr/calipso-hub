define([ 'backbone', 'backbone-bootstrap-modal', 'backbone-forms', 'backgrid', 'model/generic-model', 'view/about-view' ], function(Backbone, BackboneBootstrapModal, BackboneForm, Backgrid, GenericModel, AboutView) {
	var UserModel = GenericModel.extend({
//		urlRoot: function() {
//			console.log("UserModel#urlRoot");
//	        return window.calipso.getBaseUrl() + "/api/rest/user";
//	    },
		schema: {
	        userName:       'Text',
	        firstName:       'Text',
	        lastName:       'Text',
	        email:      { validators: ['required', 'email'] },
	    }
	});
	
	var EditCell = Backgrid.Cell.extend({
	    template: _.template('<button>edit</button>'),
	    events: {
	      "click": "editRow"
	    },
	    editRow: function (e) {
	      console.log("Hello");
	      e.preventDefault();
	      var user = this.model;
	      var form = new Backbone.Form({
	    	    model: user
	    	});//.render();
	      var modal = new Backbone.BootstrapModal({
	          animate: true,
	          content: form
	        }).open();
	    },
	    render: function () {
	      this.$el.html(this.template());
	      this.delegateEvents();
	      return this;
	    }
	});
	
	UserModel.prototype.getDefaultSchemaForGrid = function(){
		return [/*{
		  name: "id", // The key of the model attribute
		  label: "ID", // The name to display in the header
		  editable: false, // By default every cell in a column is editable, but *ID* shouldn't be
		  // Defines a cell type, and ID is displayed as an integer without the ',' separating 1000s.
		  cell: Backgrid.StringCell.extend({
		    orderSeparator: ''
		  })
		},*/
		{
		  name: "userName",
		  label: "username",
		  cell: "string"
		}, 
		{
		  name: "firstName",
		  label: "firstName",
		  editable: true,
		  cell: "string" 
		}, 
		{
		  name: "lastName",
		  label: "lastName",
		  editable: true,
		  cell: "string" 
		}, 
		{
		  name: "email",
		  label: "email",
		  cell: "email"
		}, 
		{
			name: "createdDate",
			label: "created"		,	
			cell: "date"
		},
		{
		  name: "edit",
		  label: "edit",
		  editable: false,
		  cell: EditCell
		}];
	}
	return UserModel;
});