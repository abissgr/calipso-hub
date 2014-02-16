define([ 'backbone', 'backbone-bootstrap-modal', 'backbone-forms', 'backgrid',
		'model/generic-model', 'view/about-view' ],
		function(Backbone, BackboneBootstrapModal, BackboneForm, Backgrid,
				GenericModel, AboutView) {
			var TextModel = GenericModel.extend({
				// urlRoot: function() {
				// console.log("TextModel#urlRoot");
				// return window.calipso.getBaseUrl() + "/api/rest/user";
				// },
				
				schemaComplete : function() {
					return {//
						"name" : {
							"search": 'Text',
							"update": {
								type: 'Text',
								validators : [ 'required' ],
								editorAttrs: { 'readonly': 'readonly' }
							},
							"default": {
								type: 'Text',
								validators : [ 'required' ]
							}
						},
						path : {
							"search": 'Text',
							"default": {
								type: 'Text',
								validators : [ 'required' ]
							}
						},
						
					};
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
					name : "userName",
					label : "username",
					cell : "string"
				}, {
					name : "firstName",
					label : "firstName",
					editable : true,
					cell : "string"
				}, {
					name : "lastName",
					label : "lastName",
					editable : true,
					cell : "string"
				}, {
					name : "email",
					label : "email",
					cell : "email"
				}, {
					name : "createdDate",
					label : "created",
					cell : "date"
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