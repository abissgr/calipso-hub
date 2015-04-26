/**
 * Select2
 *
 * Renders Select2 - jQuery based replacement for select boxes
 *
 * Usage: Works the same as Select editor, with the following extensions for Select2:
 * schema.config: configuration object passed to Select2
 * schema.multiple: sets 'multiple' property on the HTML <select>
 *
 * Example:
 * schema: {title: {type:'Select2', options:['Mr','Mrs',Ms], config: {}, multiple: false}
 *
 * Also see:
 * https://gist.github.com/powmedia/5161061
 * https://gist.github.com/Integral/5156170
 */
define(['jquery', 'underscore', 'backbone', 'select2', 'backbone-forms'], function($, _, Backbone, select2) {

     Backbone.Form.editors.Select2= Backbone.Form.editors.Select.extend({
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
    $.fn.select2.defaults.set( "theme", "bootstrap" );

}); 