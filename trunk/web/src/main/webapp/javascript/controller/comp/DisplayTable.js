Toobs.loadModule("Pager");
Toobs.loadModule("PagedSorter");
Toobs.Controller.useComp('PrintOptions');

Toobs.UI.DisplayTable = Class.create();
Toobs.UI.DisplayTable.prototype = {
  sorter: {},
  pager: {},
  initialize: function(component) {
	  Toobs.debug('DisplayTable - init() - component: ' + component);
    Toobs.Comp['DisplayTable'] = this;
    if (!this._actionDisable) {
  	  this._actionDisable = this.actionDisable.bindAsEventListener(this);
      this._actionListener = this.actionDispatcher.bindAsEventListener(this);
      this._selectorListener = this.selectorDispatcher.bindAsEventListener(this);
  	  this._mirrorListener = this.mirrorListener.bindAsEventListener(this);
  	  this._exportListener = this.exportListener.bindAsEventListener(this);
  
  	  this._bindTables = this.bindTables.bind(this);
  	  this._actionRebindHandler = this.actionRebind.bind(this);
  	  this._actionBindHandler = this.actionBinder.bind(this);
    }
    this._bindTables(component);
  },
  
  bindTables: function(component)
  {
    Toobs.debug('DisplayTable - bindTables() - component: ' + component);
    $$(".dt").each( function(e) {
      if (e.attributes.comp && e.attributes.comp.nodeValue != "" && (!component || e.attributes.comp.nodeValue.valueOf() == component.valueOf() ) )
      {
    	  Toobs.debug('DisplayTable - bindTables() - table comp: ' + e.attributes.comp.nodeValue + ' component: ' + component);
        
      	var comp = e.attributes.comp.nodeValue;
      	var ajax = false;
      	var target = comp + 'TableFrame';
      	if (e.attributes.ajax && e.attributes.ajax.nodeValue == 'true') {
      	  ajax = true;
      	}
        if (e.attributes.popup && e.attributes.popup.nodeValue != '') {
          target = e.attributes.popup.nodeValue + '_content';
        }
        Toobs.debug('bindTables - target: ' + target );
        this.sorter[comp] = new Toobs.UI.PagedSorter(comp, comp + 'Pager_head', target, comp + 'Table', { ajaxSort: ajax, ajaxPage: ajax, sortMessage: '', pageParamName: 'f', sizeParamName: 'm', callback: function() { this._actionBindHandler(comp, 'sorter') }.bind(this) } );
        this.pager[comp]  = new Toobs.UI.Pager(comp + 'Pager_foot', target, { ajaxSort: ajax, ajaxPage: ajax, pageParamName: 'f', sizeParamName: 'm', callback: function() { this._actionBindHandler(comp, 'pager') }.bind(this) } );
        this.actionBinder(comp);
      }
    }.bindAsEventListener(this));
    Toobs.debug('DisplayTable - bindTables().exit');
  },

  actionDisable: function()
  {
    this.disableAction = true;
  },

  actionDispatcher: function(evt)
  {
    var el = Event.element(evt);
    while (!el.attributes.guid)
    {
      el = el.parentNode;
    }

    var comp = el.attributes.comp.nodeValue;
  	var firstResult = 0;
  	if (this.pager[comp] && this.pager[comp].pagerElement) {
  	  this.pager[comp].pagerElement.attributes.firstResult.nodeValue;
    }

	  var url = el.attributes.ajaxAction.nodeValue + el.attributes.guid.nodeValue + "&" + "f=" + firstResult;

    this.doAction(url, comp);
    Event.stop(evt);
  },

  doAction:           function(url, comp)
  {
    new Ajax.Updater(this.pager[comp].contentElement, url, {
      evalScripts: true,
      onComplete:  function() {
        this._actionRebindHandler(comp);
      }.bind(this)
    });
  },

  actionBinder: function(comp, from)
  {
 	  $$(".ajaxAction").each( function(e) {
      if (e.attributes.guid && e.attributes.guid.nodeValue != "")
      {
      	if (e.attributes.comp.nodeValue.valueOf() == comp.valueOf()) {
          Event.observe(e, "click", this._actionListener, false);
        }
      }
    }.bindAsEventListener(this));

 	  $$(".toplink").each( function(e) {
      if (e.attributes.comp && e.attributes.comp.nodeValue != "")
      {
      	if (e.attributes.comp.nodeValue.valueOf() == comp.valueOf()) {
          Event.observe(e, "click", this._selectorListener, false);
        }
      }
    }.bindAsEventListener(this));
    
 	  $$(".mirror").each( function(e) {
      if (e.attributes.mirror && e.attributes.mirror.nodeValue != "")
      {
        Event.observe(e, "click", this._mirrorListener, false);
      }
    }.bindAsEventListener(this));

 	  $$(".expFmt").each( function(e) {
      if (e.attributes.comp && e.attributes.comp.nodeValue != "")
      {
      	if (e.attributes.comp.nodeValue.valueOf() == comp.valueOf()) {
          Event.observe(e, "click", this._exportListener, false);
        }
      }
    }.bindAsEventListener(this));

    Toobs.debug('DisplayTable.actionBinder() from ' + from);

    var target = comp + 'TableFrame';
 	  $$(".dt").each( function(e) {
      if (e.attributes.comp && e.attributes.comp.nodeValue != "") {
      	if (e.attributes.comp.nodeValue.valueOf() == comp.valueOf()) {
          if (e.attributes.pageEval && e.attributes.pageEval.nodeValue != "") {
            try {
              eval(e.attributes.pageEval.nodeValue);
            } catch (x) {
              Toobs.error('DisplayTable.pageEval() ' + x);
            }
          }
          if (e.attributes.popup && e.attributes.popup.nodeValue != '') {
            target = e.attributes.popup.nodeValue + '_content';
          }
        }
      }
    }.bindAsEventListener(this));
    
    if (from == 'pager') {
      this.sorter[comp] = new Toobs.UI.PagedSorter(comp, comp + 'Pager_head', target, comp + 'Table', { ajaxSort: true, ajaxPage: true, sortMessage: '', pageParamName: 'f', sizeParamName: 'm', callback: function() { this._actionBindHandler(comp, 'sorter') }.bind(this)} );
    } else if (from == 'sorter') {
      this.pager[comp]  = new Toobs.UI.Pager(comp + 'Pager_foot', target, { ajaxSort: true, ajaxPage: true, pageParamName: 'f', sizeParamName: 'm', callback: function() { this._actionBindHandler(comp, 'pager') }.bind(this)} );
    }
    this.disableAction = false;
  },
  
  mirrorListener: function(evt) {
    Toobs.debug('DisplayTable.mirrorListener()');
    var el = Event.element(evt);
    if (el.attributes.mirror && el.attributes.mirror.nodeValue != '') {
      $(el.attributes.mirror.nodeValue).value = el.value;
    }
  },
  
  exportListener: function(evt) {
    Toobs.debug('DisplayTable.exportListener()');
    var el = Event.element(evt);
    while (!el.attributes.comp)
    {
      el = el.parentNode;
    }
    Toobs.debug('DisplayTable.exportListener() - el: ' + el);
    if (el.attributes.comp && el.attributes.comp.nodeValue != "")
    {
      var table = $(el.attributes.comp.nodeValue + 'Table');
      var url = table.attributes.exportAction.nodeValue + '&format=' + el.attributes.format.nodeValue;
      if (el.attributes.showPage && el.attributes.showPage.nodeValue != "")
      {
        url = url + '&showPage=' + el.attributes.showPage.nodeValue;
      }
      if (el.attributes.showSimple && el.attributes.showSimple.nodeValue != "")
      {
        url = url + '&showSimple=' + el.attributes.showSimple.nodeValue;
      }
      
      var pop = Toobs.Popup.create(url, {
        title: '',
        id: 'export_popup',
        width: 300,
        height: 220,
        onComplete:  function() {
          Toobs.Comp.PrintOptions.initialize();
        }.bindAsEventListener(this)
      });
      if (pop) this._exportPopup = pop;
    }
  },
  
  selectorDispatcher: function(evt)
  {
    var el = Event.element(evt);
    while (!el.attributes.action)
    {
      el = el.parentNode;
    }
    var actionType = el.attributes.action.nodeValue;
    var comp = el.attributes.comp.nodeValue;
    
    switch(actionType) {
	case "select":
	  $$(".dtSelect").each( function(e) {
	    if (e.attributes.comp && e.attributes.comp.nodeValue == comp) { e.checked = true; }
	  }.bindAsEventListener(this));
	  break; 
	case "unselect":
	  $$(".dtSelect").each( function(e) {
	    if (e.attributes.comp && e.attributes.comp.nodeValue == comp) { e.checked = false; }
	  }.bindAsEventListener(this));
	  break; 
	case "conditional":
	  $$(".dtSelect").each( function(e) {
	    if (e.attributes.comp && e.attributes.comp.nodeValue == comp) {
          var selector = el.attributes.selector.nodeValue;
	      if (e.attributes[selector] && e.attributes[selector].nodeValue == el.attributes.value.nodeValue) {
	      	e.checked = true;
	      } else {
	      	e.checked = false;
	      }
	    }
	  }.bindAsEventListener(this));
	  break; 
	default:
	  break;
    }
  
  },
  
  actionRebind: function(comp)
  {
  	Toobs.debug('rebind ' + comp);
    $$(".dt").each( function(e) {
      if (e.attributes.comp && e.attributes.comp.nodeValue == comp)
      {
      	var ajax = false;
        var target = comp + 'TableFrame';
      	if (e.attributes.ajax && e.attributes.ajax.nodeValue == 'true') {
      	  ajax = true;
      	}
        if (e.attributes.popup && e.attributes.popup.nodeValue != '') {
          target = e.attributes.popup.nodeValue + '_content';
        }
        this.sorter[comp] = new Toobs.UI.PagedSorter(comp, comp + 'Pager_head', target, comp + 'Table', { ajaxSort: ajax, ajaxPage: ajax, sortMessage: '', pageParamName: 'f', sizeParamName: 'm', callback: function() { this._actionBindHandler(comp, 'sorter') }.bind(this)} );
        this.pager[comp]  = new Toobs.UI.Pager(comp + 'Pager_foot', target, { ajaxSort: ajax, ajaxPage: ajax, pageParamName: 'f', sizeParamName: 'm', callback: function() { this._actionBindHandler(comp, 'pager') }.bind(this)} );
        this.actionBinder(comp);
      }
    }.bindAsEventListener(this));

  }
  
}