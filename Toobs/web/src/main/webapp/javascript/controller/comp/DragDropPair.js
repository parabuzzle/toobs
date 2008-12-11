Autocompleter.Base.hide = function() {}

Toobs.UI.DragDropPair = Class.create();
Toobs.UI.DragDropPair.prototype = {

  initialize: function() {
    Toobs.debug('DragDropPair - init()');
    Toobs.Comp['DragDropPair'] = this;
    
    if (!this._onUpdateSource) {
      this._onUpdateSource = this.onUpdateSource.bindAsEventListener(this);
      this._onUpdateTarget = this.onUpdateTarget.bindAsEventListener(this);
      this._clickAddAll = this.clickAddAll.bindAsEventListener(this);
      this._initDragDrop = this.initDragDrop.bind(this);
      this._fixFFOverlap = this.fixFFOverlap.bind(this);
      this._processTargetNodes = this.processTargetNodes.bind(this);
      this._processSourceNodes = this.processSourceNodes.bind(this);
      this._initDragDrop = this.initDragDrop.bind(this);
      this._initDropTarget = this.initDropTarget.bind(this);
      this._initDropSource = this.initDropSource.bind(this);
    }    
    this._draggables = [];
    this.autoCompleter = null;
    
    try {
      dropInit = false;
      //if (Toobs.Util.isIE())
        Position.includeScrollOffsets = true;

  		this._initDragDrop();

    } catch (x) {
      Toobs.error(x);
    }
  },
  
  fixFFOverlap: function(revert) {
    if (Toobs.Util.isGecko()) {
      $$(".droplist").each( function(el) {
        if (revert) {
          el.style.overflow = 'auto';
        } else {
          el.style.overflow = 'hidden';
        }
      });
    }
  },
  
  initDragDrop: function() {
    Toobs.debug('DragDropPair - initDragDrop()');

    $$(".droplist").each( function(el) {
      var dropcat = 'global';
      if (el.attributes.dropcat) dropcat = el.attributes.dropcat.nodeValue;
      if (!this._draggables[dropcat]) this._draggables[dropcat] = [];
      this._draggables[dropcat][this._draggables[dropcat].length] = el.id;
    }.bindAsEventListener(this));

  	this._initDropSource();
  	this._initDropTarget();

    if ($('srcAddAll')) {
      Event.observe($('srcAddAll'), "click", this._clickAddAll, false);
    }
    Toobs.debug('DragDropPair - initDragDrop().exit');
  },

  clickAddAll: function(evt) {
    var el = Event.element(evt);
    
    var idPrefix = el.attributes.idPrefix.nodeValue;
    var source = $(idPrefix + 'Source');
    var target = $(idPrefix + 'Target');
    if (source.childNodes.length > 0) {
      for (var i = 0; i < source.childNodes.length; i++) {
        var ch = source.childNodes[i];
        var clone = ch.cloneNode(true);
        /*
        for (var j = 0; j < clone.childNodes.length; j++) {
          var ch1 = clone.childNodes[j];
          if (ch1.tagName == 'INPUT')
            ch1.disabled = null;
        }
        */
        target.appendChild(clone);
      }
      this._processTargetNodes(target, 1);
      source.innerHTML = '';
  
    	this._initDropSource();
    	this._initDropTarget();
    }    
  },
  
  initDropSource: function() {
    Toobs.debug('DragDropPair - initDropSource()');
    try {
      $$(".dropsource").each( function(el) {
        var constraint = false;
        if (el.attributes.constraint && el.attributes.constraint.nodeValue != '') {
          constraint = el.attributes.constraint.nodeValue;
        }
        var dropcat = 'global';
        if (el.attributes.dropcat) dropcat = el.attributes.dropcat.nodeValue;
      	Sortable.create(el.id,{tag:'li',dropOnEmpty: true, handle: 'dragimg', containment: this._draggables[dropcat],only:'dragli',constraint: constraint, onUpdate: this._onUpdateSource});
        try {
          this._srcPicker = $('srcPicker');
          if (this._srcPicker && !this.autoCompleter) {
        		this.autoCompleter = new Ajax.Autocompleter(this._srcPicker, el, this._srcPicker.attributes.pickerUrl.nodeValue, 
        		{ frequency: 0.75,
        			minChars: 1,
        			paramName: this._srcPicker.attributes.pickerParam.nodeValue,
        			onBlur: function(event) {}.bindAsEventListener(this),
        			onComplete: function(request) {
                try {
                  Toobs.debug('onComplete');
                  var ul = document.createElement('ul');
            		  this.autoCompleter.update.innerHTML = '';
                  ul.innerHTML = request.responseText
                  $A(ul.childNodes).each( function(e) {
                    if ($(e.id)) {
                      Element.remove(e);
                    }
                  });
                  Element.cleanWhitespace(ul);
                  if (ul.hasChildNodes()) {
                    this.autoCompleter.updateChoices(ul.innerHTML);
                  }
                  ul = null;
                	Sortable.create(el.id,{tag:'li',dropOnEmpty: true, handle: 'dragimg', containment: this._draggables[dropcat],only:'dragli',constraint: constraint, onUpdate: this._onUpdateSource});
                } catch (x) {
                  Toobs.error('dropSource srcPicker exception');
                  Toobs.error(x);
                };
        			}.bind(this)
        		});
        		this.autoCompleter.show();
        		this.autoCompleter.onClick = function() {
              Toobs.debug('myOnClick');
            }
        		this.autoCompleter.hide = function() {
              Toobs.debug('myHide');
        		  this.autoCompleter.update.innerHTML = '';
        		}.bind(this);
        		
        		if (this._srcPicker.attributes.focusEval && this._srcPicker.attributes.focusEval.nodeValue != '') {
        		  
        		  Toobs.debug('focusEval: ' + this._srcPicker.attributes.focusEval.nodeValue);
          		this.autoCompleter.options.callback = function(picker, entry) {
          		  var params = entry + '&' + eval(this._srcPicker.attributes.focusEval.nodeValue);
          		  Toobs.debug('##### autoCompleter callback params:' + params);
          		  return params
          		}.bind(this);
          		/*
              Event.observe(this._srcPicker, 'focus', 
                function(evt) {
                  try { 
                    eval(this._srcPicker.attributes.focusEval.nodeValue);
                  } catch (x) {
                    Toobs.error('srcPicker - focusEval - exception');
                    Toobs.logEx(x);
                  } 
                }.bindAsEventListener(this), false);
              */
        		}
          }
        } catch (x) {
          Toobs.error('dropSource srcPicker exception');
          Toobs.logEx(x);
        };
      	
      }.bindAsEventListener(this));

    } catch (x) {
      Toobs.error('DragDropPair - initDropSource - exception');
      Toobs.logEx(x);
    }
  },

  initDropTarget: function() {
    Toobs.debug('DragDropPair - initDropTarget()');
    try {
      $$(".droptarget").each( function(el) {
        var constraint = false;
        if (el.attributes.constraint && el.attributes.constraint.nodeValue != '') {
          constraint = el.attributes.constraint.nodeValue;
        }
        var dropcat = 'global';
        if (el.attributes.dropcat) dropcat = el.attributes.dropcat.nodeValue;
      	Sortable.create(el.id,{tag:'li',dropOnEmpty: true, handle: 'dragimg', containment: this._draggables[dropcat],only:'dragli',constraint: constraint, onUpdate: this._onUpdateTarget});
      }.bindAsEventListener(this));
    } catch (x) {
      Toobs.error('DragDropPair - initDropTarget - exception');
      Toobs.logEx(x);
    }
  },

	onUpdateSource:   function(d)
	{
    Toobs.debug('DragDropPair - onUpdateSource()');
    this._processSourceNodes(d);
    Toobs.debug('DragDropPair - onUpdateSource().exit');
	},

	onUpdateTarget: function(d)
	{
    Toobs.debug('DragDropPair - onUpdateTarget()');
    var order = 1;
    this._processTargetNodes(d, order);
    Toobs.debug('DragDropPair - onUpdateTarget().exit');
	},
	
	processTargetNodes: function(node, order) {
    $A(node.childNodes).each( function(e) {
      if (e.tagName == 'INPUT') {
        e.disabled = null;
      } else if (e.tagName == 'SPAN' && e.className == 'ddH') {
        e.className = 'ddS';
      } else if (e.tagName == 'LI') {
        var guid = e.id.substring(4);
        var theOrder = $('ddo-' + guid);
        if (theOrder) {
          theOrder.value = order;
          order++;
        }
      }
      if (e.hasChildNodes()) {
        this._processTargetNodes(e, order);
      }
    }.bind(this));
	},

	processSourceNodes: function(node) {
    $A(node.childNodes).each( function(e) {
      if (e.tagName == 'INPUT') {
        e.disabled = 'disabled';
      } else if (e.tagName == 'SPAN' && e.className == 'ddS') {
        e.className = 'ddH';
      }
      if (e.hasChildNodes()) {
        this._processSourceNodes(e);
      }
    }.bind(this));
	}
}