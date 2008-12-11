Toobs.UI.PagedDeleter = Class.create();
Toobs.UI.PagedDeleter.prototype = {
  initialize:  function(pagerElement, contentElement, containerElement)
  {
    this.options = Object.extend({
      ajaxPage:            true,
      ajaxDelete:          true,
      callback:            null,
      deleteMessage:       'Are you sure?',
      pageParamName:       'firstResult',
      pageLastParam:       ''
		}, arguments[3] || {});

    this._pagerElement = pagerElement;
    this._contentElement = contentElement;
    this._containerElement = containerElement;
		this._actionDisable = this.actionDisable.bindAsEventListener(this);
		this._actionRebindHandler = this.actionRebind.bindAsEventListener(this);
    this._actionListener = this.actionDispatcher.bindAsEventListener(this);
	  this.frameElement = $(this._contentElement);
	  if (this.frameElement){
		  this.pager = new Toobs.UI.Pager(this._pagerElement,this._contentElement, { callback: this._actionRebindHandler, ajaxPage: this.options.ajaxPage, pageParamName: this.options.pageParamName, pageLastParam: this.options.pageLastParam } );
      if ($(this._pagerElement)) {
    	  this.firstResult = $(this._pagerElement).attributes.firstResult.nodeValue;
      }
	  }
    if ($(this._pagerElement)) {
      this.actionBinder();
    }
  },
  
  actionDisable: function()
  {
    this.disableAction = true;
  },

  actionDispatcher: function(evt)
  {
	  if (!confirm(this.options.deleteMessage))
	  {
      Event.stop(evt);
	    return;
	  }
    var el = Event.element(evt);
    while (!el.attributes.guid)
    {
      el = el.parentNode;
    }
	  this.pagerElement = $(this._pagerElement);
	  if (this.pagerElement) {
  	  this.firstResult = this.pagerElement.attributes.firstResult.nodeValue;
    }
    this.doAction(this.deleteAction + el.attributes.guid.nodeValue + "&" + this.options.pageParamName + "=" + this.firstResult);
    Event.stop(evt);
  },

  doAction:           function(url)
  {
    if (this.options.ajaxDelete)
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
  	  this.deleteAction = this.tableElem.attributes.deleteAction.nodeValue;
  		$$(".delete").each( function(e) {
        if (e.attributes.guid && e.attributes.guid.nodeValue != "")
        {
          Event.observe(e, "click", this._actionListener, false);
        }
      }.bindAsEventListener(this));
    }
    this.disableAction = false;
  },
  
	actionRebind: function()
	{
	  if (this.frameElement){
		  new Toobs.UI.Pager(this._pagerElement,this._contentElement, this._actionRebindHandler);
  	  this.firstResult = $(this._pagerElement).attributes.firstResult.nodeValue;
	  }
    this.actionBinder();
    if (this.options.callback) {
      this.options.callback();
    }	  
	}
	  
};