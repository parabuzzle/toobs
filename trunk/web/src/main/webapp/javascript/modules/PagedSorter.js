Toobs.UI.PagedSorter = Class.create();
Toobs.UI.PagedSorter.prototype = {
  pager:null,
  initialize:  function(component, pagerElement, contentElement, containerElement)
  {
    this.options = Object.extend({
      ajaxPage:            true,
      ajaxSort:            true,
      callback:            null,
      sortMessage:         '',
      pageParamName:       'firstResult',
      sizeParamName:       'maximumResultSize',
      pageLastParam:       ''
    }, arguments[4] || {});

    this._component = component;
    this._pagerElement = pagerElement;
    this._contentElement = contentElement;
    this._containerElement = containerElement;
    this._actionDisable = this.actionDisable.bindAsEventListener(this);
    this._actionRebindHandler = this.actionRebind.bindAsEventListener(this);
    this._actionListener = this.actionDispatcher.bindAsEventListener(this);
    this.frameElement = $(this._contentElement);
    this.firstResult = 0;
    if (this.frameElement){
      Toobs.debug('PagedSorter.initialize() new pager');
      this.pager = new Toobs.UI.Pager(this._pagerElement,this._contentElement, { callback: this._actionRebindHandler, ajaxPage: this.options.ajaxPage, pageParamName: this.options.pageParamName, sizeParamName: this.options.sizeParamName, pageLastParam: this.options.pageLastParam } );
      if ($(this._pagerElement)) {
        this.firstResult = $(this._pagerElement).attributes.firstResult.nodeValue;
      }
    }
    this.actionBinder();
  },
  
  actionDisable: function()
  {
    this.disableAction = true;
  },

  actionDispatcher: function(evt)
  {
    var el = Event.element(evt);
    while (!el.attributes.order)
    {
      el = el.parentNode;
    }
    this.pagerElement = $(this._pagerElement);
    if (this.pagerElement) {
      this.firstResult = this.pagerElement.attributes.firstResult.nodeValue;
    }
    //alert(this.sortAction + el.attributes.order.nodeValue + "&" + this.options.pageParamName + "=" + this.firstResult);
    this.doAction(this.sortAction + el.attributes.order.nodeValue + "&" + this.options.pageParamName + "=" + this.firstResult);
    var comp = el.attributes.comp.nodeValue;
    Toobs.debug('Sorter comp: ' + comp + ' f: ' + $(comp + '.order'));
    if ($(comp + '.order')) {
      $(comp + '.order').value = el.attributes.order.nodeValue;
    }
    Event.stop(evt);
  },

  doAction:           function(url)
  {
    if (this.options.ajaxSort)
    {
      new Ajax.Updater(this.frameElement, url, {
        evalScripts: true,
        onComplete:  this._actionRebindHandler
      });
    }
    else
    {
      window.location = url;
    }
  },

  actionBinder: function()
  {
    this.tableElem = $(this._containerElement);
    if (this.tableElem) {
      this.sortAction = this.tableElem.attributes.sortAction.nodeValue;
      $$(".sort").each( function(e) {
        if (e.attributes.order && e.attributes.order.nodeValue != "")
        {
          if (e.attributes.comp && e.attributes.comp.nodeValue == this._component) {
            Event.observe(e, "click", this._actionListener, false);
          }
        }
      }.bindAsEventListener(this));
    }
    this.disableAction = false;
  },
  
  actionRebind: function()
  {
    if (this.frameElement){
      //Toobs.debug('PagedSorter.actionRebind() new pager: ' + this.pager + ' element: ' + this.pager.pagerElement);
      if (!this.pager.pagerElement) {
        this.pager = new Toobs.UI.Pager(this._pagerElement,this._contentElement, { callback: this._actionRebindHandler, ajaxPage: this.options.ajaxPage, pageParamName: this.options.pageParamName, sizeParamName: this.options.sizeParamName, pageLastParam: this.options.pageLastParam } );
      } else {
        this.pager.bindPager();
      }
      if ($(this._pagerElement)) {
        this.firstResult = $(this._pagerElement).attributes.firstResult.nodeValue;
      }
    }
    this.actionBinder();
    if (this.options.callback) {
      Toobs.debug('PagedSorter.actionRebind() callback');
      this.options.callback();
    }   
  }
    
};