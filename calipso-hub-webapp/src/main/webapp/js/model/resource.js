define([ 'backbone', 'backbone-bootstrap-modal', 'backbone-forms', 'backgrid', 'model/generic-model', 'model/host' ],
		function(Backbone, BackboneBootstrapModal, BackboneForm, Backgrid, GenericModel, HostModel) {
			var ResourceModel = GenericModel.extend({
				// urlRoot: function() {
				// console.log("TextModel#urlRoot");
				// return window.calipso.getBaseUrl() + "/api/rest/user";
				// },
				
				schemaComplete : function() {
					var schema = {//
						"host" : {
							"default": {
								type: 'NestedModel',
								model : HostModel,
							}
						},
						"path" : {
							"search": 'Text',
							"default": {
								type: 'Text',
								validators : [ 'required' ]
							}
						},
						
					};
					console.log("ResourceModel schema: "+schema);
					return schema;
				},

				
			//address: { type: 'NestedModel', model: Address }
			});

			

			ResourceModel.prototype.getDefaultSchemaForGrid = function() {
				return [/*
						 * { name: "id", // The key of the model attribute
						 * label: "ID", // The name to display in the header
						 * editable: false, // By default every cell in a column
						 * is editable, but *ID* shouldn't be // Defines a cell
						 * type, and ID is displayed as an integer without the
						 * ',' separating 1000s. cell:
						 * Backgrid.StringCell.extend({ orderSeparator: '' }) },
						 */
				{
					name : "path",
					label : "path",
					cell : "string"
				}];
			}
			console.log("ResourceModel done");
			return ResourceModel;
		});