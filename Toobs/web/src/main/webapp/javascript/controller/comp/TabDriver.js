Toobs.UI.TabDriver = Class.create();
Toobs.UI.TabDriver.prototype = {
  lis: {},
  initialize: function() {
    Toobs.debug('TabDriver - init()');
    Toobs.Comp['TabDriver'] = this;
    this._tabOver = this.tabOver.bindAsEventListener(this);
    this._tabOut  = this.tabOut.bindAsEventListener(this);

    this._tabClick  = this.tabClick.bindAsEventListener(this);
    
    this._bindTabs  = this.bindTabs.bind(this);

    this._bindTabs();
  },

  bindTabs: function(component) {
    Toobs.debug('TabDriver - bindTabs()');
    $$("ul.tabs").each( function(ul) {
      if (ul.attributes.preEval && ul.attributes.preEval.nodeValue != '') {
        Toobs.debug('TabDriver - bindTabs() - preEval: ' + ul.attributes.preEval.nodeValue);
        try {
          eval(ul.attributes.preEval.nodeValue);
        } catch (x) {
          Toobs.error('TabDriver - bindTabs() - preEval exception for: ' + ul.attributes.preEval.nodeValue);
          Toobs.error(x);
        }
      }
    }.bindAsEventListener(this));
    $$("ul.tabs li").each( function(li) {
      if (!component || component == li.attributes.component.nodeValue) {
        Event.observe(li, "mouseover", this._tabOver, false);
        Event.observe(li, "mouseout",  this._tabOut, false);
        if (li.hasClassName('active')) {
          $A(li.childNodes).each( function(e) {
            if (e.tagName == 'A') {
              Toobs.Util.setTabTitle('::' + e.title);
            }
          });
        }
      }
    }.bindAsEventListener(this));

    $$(".tablink").each( function(a) {
      if (!component || component == a.parentNode.attributes.component.nodeValue) {
        Event.observe(a, "click", this._tabClick, false);
      }
    }.bindAsEventListener(this));
  },
  
  tabClick: function(evt) {
    Toobs.debug('tabClick');
    var el = Event.element(evt);
    var li = el;
    while (li.tagName != 'LI') {
      li = li.parentNode;
    }
    var component = li.attributes.component.nodeValue;
    
    var preEval;
    if (el.attributes.preEval && el.attributes.preEval.nodeValue != '') {
      preEval = el.attributes.preEval.nodeValue;
    }
    
    var processClick = true;
    if (preEval) { 
      processClick = eval(preEval);
    }

    if (processClick) {
      
      $$("ul.tabs li").each( function(li) {
        if (component == li.attributes.component.nodeValue) {
          li.removeClassName('active');
        }
      });
      li.addClassName('active');
      
      var url = el.attributes.comp.nodeValue;
  
      var postEval;
      if (el.attributes.postEval && el.attributes.postEval.nodeValue != '') {
        postEval = el.attributes.postEval.nodeValue;
      }
      
      this.doAction(url, postEval, component, el.innerHTML);
    }	
  },

  doAction: function(url, postEval, component, tabName)
  {
    Toobs.debug('TabDriver - doAction() ' + url);
    new Ajax.Updater(component + 'Content', url, {
      evalScripts: true,
      onComplete:  function() {
        Toobs.Page.initialize();
        Toobs.Util.setTabTitle('::' + tabName);
        //If you're here wondering why your tab's selected state wont keep look at your postEval statement, make sure your
        //component call looks like this: Toobs.Comp['ContactPopupBind'].initialize(); (single quotes).
        //Toobs.debug('Default&tabSetValue='+tabName);
        //Toobs.debug('postEval='+postEval);
        try {
        if (postEval) eval(postEval);
        } catch (x) { Toobs.error(x); }
      	Toobs.Page.doAjaxAction('UpdateSessionParam.xpost?sessionParamName=' + component + 'Default&sessionParamValue='+tabName);
      }.bind(this)
    });
  },
  
  tabOver: function(evt) {
    var el = Event.element(evt);
    while (el.tagName != 'LI')
    {
      el = el.parentNode;
    }
	//Toobs.debug('TabDriver - tabOver() ' + el.tagName);
    el.addClassName('hover');
  },
  
  tabOut: function(evt) {
    var el = Event.element(evt);
    while (el.tagName != 'LI')
    {
      el = el.parentNode;
    }
	//Toobs.debug('TabDriver - tabOut() ' + el.tagName);
    el.removeClassName('hover');
  }
  
}