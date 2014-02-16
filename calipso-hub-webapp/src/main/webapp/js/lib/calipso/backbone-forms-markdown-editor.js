(function() {
	Form.editors.Markdown = Form.Editor.extend({

		tagName : 'textarea',

		defaultValue : '',

		previousValue : '',

		events : {
			'keyup' : 'determineChange',
			'keypress' : function(event) {
				var self = this;
				setTimeout(function() {
					self.determineChange();
				}, 0);
			},
			'select' : function(event) {
				this.trigger('select', this);
			},
			'focus' : function(event) {
				this.trigger('focus', this);
			},
			'blur' : function(event) {
				this.trigger('blur', this);
			}
		},

		initialize : function(options) {
			Form.editors.Base.prototype.initialize.call(this, options);
		},

		/**
		 * Adds the editor to the DOM
		 */
		render : function() {
			this.setValue(this.value);

			return this;
		},

		determineChange : function(event) {
			var currentValue = this.$el.val();
			var changed = (currentValue !== this.previousValue);

			if (changed) {
				this.previousValue = currentValue;

				this.trigger('change', this);
			}
		},

		/**
		 * Returns the current editor value
		 * 
		 * @return {String}
		 */
		getValue : function() {
			return this.$el.val();
		},

		/**
		 * Sets the value of the form element
		 * 
		 * @param {String}
		 */
		setValue : function(value) {
			this.$el.val(value);
		},

		focus : function() {
			if (this.hasFocus)
				return;

			this.$el.focus();
		},

		blur : function() {
			if (!this.hasFocus)
				return;

			this.$el.blur();
		},

		select : function() {
			this.$el.select();
		}

	});
})();