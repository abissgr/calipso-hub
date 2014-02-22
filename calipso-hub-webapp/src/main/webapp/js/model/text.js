define([ 'backbone', 'backbone-bootstrap-modal', 'backbone-forms', 'backgrid', 'model/generic-model', 'model/resource', 'model/host' ],
		function(Backbone, BackboneBootstrapModal, BackboneForm, Backgrid, GenericModel, ResourceModel, HostModel) {
			var TextModel = ResourceModel.extend({
				// urlRoot: function() {
				// console.log("TextModel#urlRoot");
				// return window.calipso.getBaseUrl() + "/api/rest/user";
				// },
				
				schemaComplete : function() {
					// superclass schema
					var superSchema = TextModel.__super__.schemaComplete.call(this);
					// own schema
					var schema = {
						"sourceContentType" : {
							"search": { type: 'Select', options: [ 'text/plain', 'text/x-markdown', 'text/html' ] },
							"default": { 
								type: 'Select', 
								options: [ 'text/plain', 'text/x-markdown', 'text/html' ], 	
								validators : [ 'required' ]
							}
						},
						"source" : {
							"search": 'Text',
							"update": {
								type: 'Text',
								validators : [ 'required' ],
							},
							"default": {
								type: 'Text',
								validators : [ 'required' ]
							}
						}
						
					};
					// return merged schemas
					return $.extend({}, superSchema, schema);
				},

				
			
			});

			var EditCell = Backgrid.Cell.extend({
				template : _.template('<button class="btn btn-warning">edit</button>'),
				events : {
					"click" : "editRow"
				},
				editRow : function(e) {
					console.log("Hello");
					e.preventDefault();
					var user = this.model;
					var userSchema = user.isNew() ? user.schema("create") : user.schema("update");
					var form = new Backbone.Form({
						model : user,
						schema : userSchema,
					});

					var modal = new Backbone.BootstrapModal({
						title : 'My form',
						content : form,
						okBtn : 'save'
					}).open();

					modal.on('ok', function() {
						var errs = form.commit();
						if (errs){
							return modal.preventClose();
						}
						var sUrl = user.url+"/"+user.get("id");
					    //(base.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.id);
						user.save({}, {url: sUrl});
						//user.save();
					});
				},
				render : function() {
					this.$el.html(this.template());
					this.delegateEvents();
					return this;
				}
			});

			TextModel.prototype.getDefaultSchemaForGrid = function() {
				return [{
					name : "path",
					label : "path",
					cell : "string"
				}, {
					name : "edit",
					label : "edit",
					editable : false,
					cell : EditCell
				} ];
			}
			console.log("TextModel done");
			return TextModel;
		});