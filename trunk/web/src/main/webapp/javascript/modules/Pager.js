Toobs.UI.Pager = Class.create();
Toobs.UI.Pager.prototype = {
  initialize:  function(pagerElement, contentElement)
  {
    Toobs.debug('Pager.initialize() ' + pagerElement);
    this.now = new Date().getTime();
    this.options = Object.extend({
      ajaxPage:            true,
      callback:            null,
      pageParamName:       'firstResult',
      pageLastParam:       ''
		}, arguments[2] || {});

    this.loadPageListener = this.loadPageDispatcher.bindAsEventListener(this);
    this.jumpToListener = this.jumpToDispatcher.bindAsEventListener(this);
    this.pageLast = this._pageLast.bindAsEventListener(this);
    this._changeMaxRows = this.changeMaxRows.bindAsEventListener(this);
    this._bindPager = this.bindPager.bind(this);

    this.pagerElementId   = pagerElement;
    this.contentElementId = contentElement;
    this.pagerElement   = $(pagerElement);
    this.contentElement = $(contentElement);
    if (!this.pagerElement) {
      Toobs.debug('pagerElement missing ' + pagerElement + ' ' + this.pagerElement);
      //alert('pagerElement ' + pagerElement + ' ' + this.pagerElement);
      return;
    }
    if (!this.contentElement && this.options.ajaxPage) {
      Toobs.debug('contentElement missing ' + contentElement + ' ' + this.contentElement);
      //alert('contentElement ' + contentElement + ' ' + this.contentElement);
      return;
    }
    
    this.pagerUrl;
    this.pagerForm;
    if (this.pagerElement.attributes.frame) {
      this.pagerUrl   = this.pagerElement.attributes.frame.nodeValue;
    }
    if (this.pagerElement.attributes.form) {
      this.pagerForm = this.pagerElement.attributes.form.nodeValue;
    }
    
    this._bindPager();
  },

  jumpToDispatcher: function(evt) {
    Toobs.debug('Pager.jumpToDispatcher()');
    try {
      Toobs.debug('Pager.jumpToDispatcher() - event: ' + evt.type);
      
      var input;
      if (evt.type == 'keypress' && evt.keyCode != 13) {
        //Event.stop(evt);
        return true;
      }
      if (evt.type == 'keypress') {
        input = Event.element(evt);
      }
      if (evt.type == 'click') {
        input = $(Event.element(evt).attributes.sib.nodeValue);
      }
      Toobs.debug('Pager.jumpToDispatcher() - input: ' + input);
      
      var pager = input;
      while (!pager.attributes.pageSize) {
        pager = pager.parentNode;
      }
      Toobs.debug('Pager.jumpToDispatcher() - pager: ' + pager);
      
      var pageSize = parseInt(pager.attributes.pageSize.nodeValue);
      var totalRows = parseInt(pager.attributes.totalRows.nodeValue);
      var firstResult = parseInt(pager.attributes.firstResult.nodeValue);
      
      var jumpTo = parseInt(input.value);
      var firstRow;
      if (jumpTo < 0) {
        firstRow = 0;
        input.value = 1;
      } else if (((jumpTo - 1) * pageSize) > totalRows) {
        input.value = Math.ceil((totalRows) / pageSize);
        firstRow = (Math.ceil((totalRows) / pageSize) - 1) * pageSize;
      } else {
        firstRow = (jumpTo - 1) * pageSize;
      } 

      var url = pager.attributes.frame.nodeValue;
      var comp = pager.attributes.comp.nodeValue;
      this.pageTo(url, firstRow, null, comp);
    
    } catch(x) {
      Toobs.error('jumpTo exception');
      Toobs.logObject(x);
    }    
  },
  
  loadPageDispatcher:  function(evt)
  {
    var el = Event.element(evt);
    var pageOrFrame;
    while (!el.attributes.page && !el.attributes.frame)
    {
      el = el.parentNode;
    }
    if (el.attributes.page) {
      pageOrFrame = el.attributes.page.nodeValue;
    }
    if (el.attributes.frame) {
      pageOrFrame = el.attributes.frame.nodeValue;
    }
    switch(pageOrFrame) {
			case "first":
			  var firstRow = 0;
			  break; 
			case "prev":
			  var firstRow = this.firstResult - this.pageSize;			  
			  if (firstRow < 0) firstRow = 0;
			  break; 
			case "refresh":
			  var firstRow = this.firstResult;
			  break; 
			case "next":
			  var firstRow = this.firstResult + this.pageSize;			  
			  if (firstRow >= this.totalRows)
			  {
			    firstRow = this.totalRows - (this.totalRows - this.firstResult);
			  }
			  break; 
			case "last":
			  var firstRow = this.totalRows - this.pageSize;			  
			  if (firstRow < 0) firstRow = 0;
			  break;
			default:
			  var firstRow=0;
			  this.url = pageOrFrame;
			  break;
    }
    if (pageOrFrame == 'refresh' || this.firstResult != firstRow) {
      if (el.attributes.target && el.attributes.target.nodeValue != '') {
        this.pageTo(this.url, firstRow, el.attributes.target.nodeValue);
      } else {
        this.pageTo(this.url, firstRow);
      }
    }

    Event.stop(evt);
  },
  
  _pageLast:          function()
  {
    var url;
    if (this.pagerUrl) {
      url = this.pagerUrl;
    }
    if (this.pagerForm) {
      url = $(this.pagerForm).action;
    }
	  var firstRow = this.totalRows - this.pageSize;			  
	  if (firstRow < 0) firstRow = 0;
    this.pageTo(url, firstRow + this.options.pageLastParam);
  },
  
  pageTo:            function(url, firstRow, target)
  {
    if (url.indexOf('?') == -1) { url = url + "?"; } else { url = url + "&"; }
    url = url + this.options.pageParamName + "=" + firstRow + '&' + this.options.sizeParamName + "=" + this.pageSize;

    Toobs.debug('Pager Time: ' + this.now);
    if (this.pagerUrl) {
      if (target) {
        this.loadPage(url, target);
      } else {
        this.loadPage(url);
      }
    }
    if (this.pagerForm) {
      var form = $(this.pagerForm);
      this.loadForm(form, url);
    }
    var f = $(this.comp + '.firstResult');
    Toobs.debug('Pager comp: ' + this.comp + ' f: ' + f);
    if (f) {
      f.value = firstRow;
    }
    var m = $(this.comp + '.maximumResultSize');
    Toobs.debug('Pager comp: ' + this.comp + ' m: ' + m);
    if (m) {
      m.value = this.pageSize;
      m.disabled = null;
    }
    if (f || m) {
      Toobs.Page.persistFormState(f.parentNode);
    }
  },
  
  loadPage:            function(url, target)
  {
    if (!target) target = this.contentElement
    if (this.options.ajaxPage) {
      new Ajax.Updater(target, url, {
        evalScripts: true,
        onComplete:  this.rescanFrame.bind(this)
      });
    } else {
      window.location = url;    
    }
  },

  loadForm:           function(form, url)
  {
    new Ajax.Updater(this.contentElement, url, {
      method: 'post',
      evalScripts: true,
      parameters:Form.serialize(form),
      onComplete:  this.rescanFrame.bind(this)
    });
  },
  
  changeMaxRows: function(evt) {
    var el = Event.element(evt);

    this.pageSize = el.value;
    this.pagerElement.attributes.pageSize.nodeValue = el.value;
    
    Toobs.Page.doAjaxAction('UpdateSessionParam.xpost?sessionParamName=' + this.comp + '_' + this.options.sizeParamName + '&sessionParamValue='+this.pageSize);
    this.pageTo(this.url, this.firstResult);
  },
  
  bindPager: function()
  {
    Toobs.debug('Pager - bindPager');
    try {
      this.pagerElement   = $(this.pagerElementId);
      //Toobs.debug('Pager - bindPager - this.pagerElement: ' + this.pagerElement);
      if (this.pagerElement) {
        this.contentElement = $(this.contentElementId);
        
        this.firstResult = parseInt(this.pagerElement.attributes.firstResult.nodeValue);
        this.totalRows = parseInt(this.pagerElement.attributes.totalRows.nodeValue);
        this.pageSize = parseInt(this.pagerElement.attributes.pageSize.nodeValue);
        this.comp = this.pagerElement.attributes.comp.nodeValue;
        if (this.pagerElement.attributes.frame) {
          this.url = this.pagerElement.attributes.frame.nodeValue;
        }
        if (this.pagerElement.attributes.form) {
          this.url = $(this.pagerElement.attributes.form.nodeValue).action;
        }
        
        $A(this.pagerElement.getElementsByTagName('li')).each( function(e) {
          if (e.attributes.page && e.attributes.page.nodeValue != "")
          {
            if (e.attributes.page.nodeValue == 'jump') {
              var inp = e.getElementsByTagName('input')[0];
              var but = e.getElementsByTagName('button')[0];
              Event.observe(inp, 'keypress', this.jumpToListener, false);
              Event.observe(but, 'click', this.jumpToListener, false);
            } else {
              Event.observe(e, "click", this.loadPageListener, false);
              e.style.cursor = 'pointer';
            }
          }
          if (e.attributes.frame && e.attributes.frame.nodeValue != "")
          {
            Event.observe(e, "click", this.loadPageListener, false);
            e.style.cursor = 'pointer';
          }
        }.bindAsEventListener(this));
        
        if ($(this.comp + '.pageSize_' + this.pagerElement.attributes.uid.nodeValue)) {
          Event.observe($(this.comp + '.pageSize_' + this.pagerElement.attributes.uid.nodeValue), 'change', this._changeMaxRows, false);
        }
      }
    } catch (x) {
  	  Toobs.error('Pager - bindPager - exception');
  	  Toobs.logEx(x);
    }    
  },

  rescanFrame:         function()
  {
    try {
      this._bindPager();
      if (this.options.callback)
      {
    	  Toobs.debug('Pager callback');
        this.options.callback();
      }
    } catch (x) {
  	  Toobs.error('Pager - rescanFrame - exception ');
  	  Toobs.logEx(x);
    }    
  }
}
