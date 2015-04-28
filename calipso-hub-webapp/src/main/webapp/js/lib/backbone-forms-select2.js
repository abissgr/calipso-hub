define(['jquery', 'underscore', 'backbone', 'select2', 'backbone-forms'], function($, _, Backbone, select2) {

    /**
     * Select2
     *
     * A simple Select2 - jQuery based replacement for the Select editor.
     *
     * Usage: Works the same as Select editor, with the following extensions for Select2:
     * schema.config: configuration object passed to Select2
     * schema.multiple: sets 'multiple' property on the HTML <select>
     *
     * Example:
     * schema: {title: {type:'Select2', options:['Mr','Mrs',Ms], config: {}, multiple: false}
     */
    Backbone.Form.editors.SimpleTypeSelect2 = Backbone.Form.editors.Select.extend({
        render : function() {
            this.setOptions(this.schema.options);
            var multiple = this.schema.multiple;
            var config = this.schema.config || {};

            var elem = this;
            setTimeout(function() {
                if (multiple) {
                    elem.$el.prop('multiple', true);
                }
                elem.$el.select2(config);
            }, 0);

            return this;
        }
    });

    /**
     * ModelSelect2
     *
     * A simple Select2 - jQuery based replacement for the Select editor
     * that selects a model VS a string value.
     *
     * Usage: Works the same as Select editor, with the following extensions for Select2:
     * schema.config: configuration object passed to Select2
     * schema.multiple: sets 'multiple' property on the HTML <select>
     *
     * Example:
     * schema: {title: {type:'Select2', options:['Mr','Mrs',Ms], config: {}, multiple: false}
     */
    Backbone.Form.editors.ModelSelect2 = Backbone.Form.editors.SimpleTypeSelect2.extend({
        //  select a model VS a string value.
        getValue : function() {
            var simpleValue = this.$el.val();
            var value = this.schema.options.findWhere({
                id : simpleValue
            });
            console.log("getValue, simpleValue: " + simpleValue);
            console.log(value);
            return value;
        },
        // https://github.com/powmedia/backbone-forms/issues/291
        setValue : function(value) {
      	  var modelId;
           if(value && value.id){
         	  console.log("setValue, picking up id: " + value.id);
         	  modelId = value.id;
           }
           this.$el.val(modelId);
           this.$el.select2("val", modelId);
        },
    });

    /*
     * Use the Select2 v4 Theme for Bootstrap, see
     * https://github.com/fk/select2-bootstrap-theme
     */
    $.fn.select2.defaults.set("theme", "bootstrap");

});
