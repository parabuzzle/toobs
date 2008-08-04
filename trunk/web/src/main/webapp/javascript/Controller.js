Toobs.PageController = Class.create();
Toobs.PageController.prototype = {
  ajaxFormElements: {},
  initialize:  function() {
    Toobs.debug('Toobs.PageController - initialize()');
    try {
      this._ajaxComponent = this.ajaxComponent.bind(this);
      this._ajaxForm = this.ajaxForm.bindAsEventListener(this);
      this._ajaxSubmitButton = this.ajaxSubmitButton.bindAsEventListener(this);
    	$$(".ajaxComponent").each( function(e) {
        if (e.id && e.attributes.componentUrl && e.attributes.componentUrl.nodeValue != "")
        {
          this._ajaxComponent(e.id, e.attributes.componentUrl.nodeValue);
        }
      }.bindAsEventListener(this));
      
    	$$(".tag").each( function(a) {
        if (a.attributes.ajaxTarget && a.attributes.ajaxTarget.nodeValue != "")
        {
          Event.observe(a, "click", function(evt) {
            var el = Event.element(evt);
            this._ajaxComponent(el.attributes.ajaxTarget.nodeValue, el.href);
            Event.stop(evt);
          }.bindAsEventListener(this), false);
        }
      }.bindAsEventListener(this));
  
      var ajaxFormLoaded = false;
    	$$(".ajaxSearchForm").each( function(e) {
    	  Toobs.debug('ajaxSearchForm.id ' + e.id);
        if (this.ajaxFormElements[e.id] != e && e.id && e.attributes.ajaxTarget && e.attributes.ajaxTarget.nodeValue != "")
        {
    	    Toobs.debug('ajaxSearchForm.ajaxTarget ' + e.attributes.ajaxTarget.nodeValue);
    	    if (this.ajaxFormElements[e.id])
            Event.stopObserving(this.ajaxFormElements[e.id], "submit", this._ajaxForm, false);
          Event.observe(e, "submit", this._ajaxForm, false);
          this.ajaxFormElements[e.id] = e;
          if (e.attributes.mode && e.attributes.mode.nodeValue == 'table') {
            var comp = e.attributes.comp.nodeValue;
        	  for (var i = 0; i < e.elements.length; i++) {
      	      Toobs.debug('ajaxSearchForm.element.type ' + e.elements[i].type);
        	    if (e.elements[i].type == 'text' || e.elements[i].type == 'select-one') {
                Event.observe(e.elements[i], 'change', function(evt) 
                  {
                    //Toobs.debug('#!#!#!#!#!#!#!# ajaxSearchForm.element.onChange');
                    //Toobs.logObject(evt);
                    //Toobs.logObject(Event.element(evt));
                    $(comp + '.firstResult').value = 0 
                  }.bindAsEventListener(this) , false);
        	    }
        	    if (e.elements[i].type == 'submit') {
                Event.observe(e.elements[i], 'click', function(evt) 
                  { 
                    //Toobs.debug('#!#!#!#!#!#!#!# ajaxSearchForm.element.onClick');
                    //Toobs.logObject(evt);
                    //Toobs.logObject(Event.element(evt));
                    $(comp + '.firstResult').value = 0 
                  }.bindAsEventListener(this) , false);
        	    }
        	  }
          }
        }
        if (this.ajaxFormElements[e.id] != e && e.id && e.attributes.ajaxEvent && e.attributes.ajaxEvent.nodeValue != "")
        {
    	    Toobs.debug('ajaxSearchForm.ajaxEvent ' + e.attributes.ajaxEvent.nodeValue);
    	    if (this.ajaxFormElements[e.id])
            Event.stopObserving(this.ajaxFormElements[e.id], e.attributes.ajaxEvent.nodeValue, this._ajaxForm, false);
          Event.observe(e, e.attributes.ajaxEvent.nodeValue, this._ajaxForm, false);
          this.ajaxFormElements[e.id] = e;
          if (!ajaxFormLoaded && e.hasClassName('init')) {
            this.fireEvent(e, e.attributes.ajaxEvent.nodeValue);
            ajaxFormLoaded = true;
          }
        }
      }.bindAsEventListener(this));
  
    	$$(".ajaxUpdateForm").each( function(e) {
  
    	  Toobs.debug('ajaxUpdateForm.id ' + e.id + ' obs ' + this.ajaxFormElements[e.id] + ' eq ' + (this.ajaxFormElements[e.id] == e));
        if (this.ajaxFormElements[e.id] != e && e.id && e.attributes.ajaxTarget && e.attributes.ajaxTarget.nodeValue != "")
        {
    	    Toobs.debug('ajaxUpdateForm.ajaxTarget ' + e.attributes.ajaxTarget.nodeValue);
    	    if (this.ajaxFormElements[e.id])
            Event.stopObserving(this.ajaxFormElements[e.id], "submit", this._ajaxForm, false);
          Event.observe(e, "submit", this._ajaxForm, false);
      	  for (var i = 0; i < e.elements.length; i++) {
    	      //Toobs.debug('ajaxUpdateForm.element.type ' + e.elements[i].type);
      	    if (e.elements[i].type == 'submit') {
              Event.observe(e.elements[i], 'click', this._ajaxSubmitButton, false);
      	    }
      	  }
          
          this.ajaxFormElements[e.id] = e;
        }
      }.bindAsEventListener(this));

    } catch (x) {
      Toobs.error('ajaxForm init exception');
      Toobs.error(x);
    };
  },

  ajaxComponent: function(elementId, url, callback)
  {
    var el = $(elementId);
    if (el) {
      if (el.hasClassName('runOnce'))
        el.removeClassName('ajaxComponent');
      if (callback) {
        new Ajax.Updater(el, url, { evalScripts: true, onComplete: callback });
      } else if (el.attributes.postEval && el.attributes.postEval.nodeValue != '') {
        new Ajax.Updater(el, url, { 
          evalScripts: true, 
          onComplete: function() {
            eval(el.attributes.postEval.nodeValue);
            if (el.hasClassName('doPage')) Toobs.Page.initialize();
          }.bind(this)
        });
      } else {
        new Ajax.Updater(el, url, { 
          evalScripts: true,
          onComplete: function() {
            if (el.hasClassName('doPage')) Toobs.Page.initialize();
          }.bind(this)
        });
      }
    }
  },
  
  doAjaxAction: function(url, callback)
  {
    new Ajax.Request(url, { onComplete: callback });
  },
  
  ajaxSubmitButton: function(evt)
  {
    Toobs.debug('ajaxSubmitButton');
    var button = Event.element(evt);
    setTimeout( function() { button.disabled = 'disabled' }, 10);
  },
  
  ajaxForm: function(evt)
  {
   	Toobs.debug('ajaxSearchForm event');
   	try {
   	  var ajaxAction;
   	  var target;
   	  var sessionParamName;
   	  var sessionParamValue;
   	  var form = Event.element(evt);

   	  if (form.attributes.ajaxTarget && form.attributes.ajaxTarget.nodeValue) {
   	    target = form.attributes.ajaxTarget.nodeValue
   	  }
   	  if (form.attributes.ajaxAction && form.attributes.ajaxAction.nodeValue) {
   	    ajaxAction = form.attributes.ajaxAction.nodeValue
   	  }
   	  if (form.attributes.sessionParamName && form.attributes.sessionParamName.nodeValue != '') {
   	    sessionParamName = form.attributes.sessionParamName.nodeValue;
     	  if (form.attributes.sessionParamValue && form.attributes.sessionParamValue.nodeValue != '') {
     	    sessionParamValue = form.attributes.sessionParamValue.nodeValue;
         }
   	  }
   	  if (form.attributes.togglechild && form.attributes.togglechild.nodeValue == 'true') {
   	    if (form.childNodes[0].checked) {
   	      form.childNodes[0].checked = false;
         } else {
   	      form.childNodes[0].checked = true;
         }
   	  }
  	  var postEval;
  	  if (form.attributes.postEval) {
  	    postEval = form.attributes.postEval.nodeValue;
  	  }
      while (form.tagName != 'FORM')
      {
        form = form.parentNode;
      }
      if (ajaxAction) form.action = ajaxAction;
      if (form.attributes.mode && form.attributes.mode.nodeValue == 'table' && evt.type != 'submit' && !Event.element(evt).hasClassName('init')) {
        //Toobs.debug('#@#@#@#@#@#@#@# ajaxSearchForm.element.tablemode');
        var comp = form.attributes.comp.nodeValue;
        $(comp + '.firstResult').value = 0;
      }
  	  
      //Toobs.debug('***** ajaxSearchForm form *****');
  	  if (form.hasClassName('confirmUpdate') && form.attributes.confirmed.nodeValue == 'false') {
        Toobs.debug('ajaxForm - confirmed ' + form.attributes.confirmed.nodeValue);
        Event.stop(evt);
        return false;
  	  }
  	  var url = form.action;
      //Toobs.debug('ajaxSearchForm pre target ' + target);

  	  if (!target) target = form.attributes.ajaxTarget.nodeValue;
      //Toobs.debug('ajaxSearchForm norm target ' + target);
  	  if (target != form.attributes.ajaxTarget.nodeValue) {
  	    $(form.attributes.ajaxTarget.nodeValue).style.display = 'none';
        //Toobs.debug('ajaxSearchForm form target ' + form.attributes.ajaxTarget.nodeValue);
  	    form.attributes.ajaxTarget.nodeValue = target;
        //Toobs.debug('ajaxSearchForm form target reset ' + form.attributes.ajaxTarget.nodeValue);
  	  }
      Toobs.debug('ajaxSearchForm form target ' + form.attributes.ajaxTarget.nodeValue);
  	  
  	  /*  Form Pre submit eval code */
  	  if (form.attributes.preEval) {
  	    var preEval = form.attributes.preEval.nodeValue;
        try {
          if (preEval) eval(preEval);
        } catch (x) {
          Toobs.error('ajaxForm preEval exception');
          Toobs.error(x);
        };
  	  }
  	  
  	  if (!postEval && form.attributes.postEval) {
  	    postEval = form.attributes.postEval.nodeValue;
  	  }
  	  if (Toobs.Comp.FCKArea) {
        Toobs.debug('ajaxSearchForm FCK update');
  	    Toobs.Comp.FCKArea.updateLinkedField();
  	  }
  	  var doSubmit = true;
      //Toobs.debug('ajaxSearchForm - elements');
  	  for (var i = 0; i < form.elements.length; i++) {
        //Toobs.debug(form.elements[i]);
        //Toobs.debug('element name: ' + form.elements[i].name + ' value: ' + form.elements[i].value);
  	    if (form.elements[i].attributes.requiredType && form.elements[i].value == '') {
      	  doSubmit = false;
      	  Toobs.Util.pleaseSelectPopup(form.elements[i].attributes.requiredType.nodeValue);
      	  break;
  	    }
  	  }
  	  if (doSubmit) {
    	  new Ajax.Updater(target, url, {
    	      method: 'post',
    	      evalScripts: true,
    	      parameters: Form.serialize(form),
    	      onComplete: function() {
              Toobs.debug('ajaxSearchForm onComplete');
              if (!$(target).attributes.disableShow)
    	          $(target).style.display = 'block';
              Toobs.debug('ajaxSearchForm postEval: ' + postEval);
              try {
    	          if (postEval) eval(postEval);
              } catch (x) {
                Toobs.error('ajaxForm eval exception');
                Toobs.error(x);
              };
              if (sessionParamName && sessionParamValue) {
                Toobs.Page.doAjaxAction('UpdateSessionParam.xpost?sessionParamName=' + sessionParamName + '&sessionParamValue='+sessionParamValue);
              }
              Toobs.Page.persistFormState(form);
              Toobs.debug('ajaxSearchForm onComplete.end');
    	      }
    	  });
      }
    } catch (x) {
      Toobs.error('ajaxForm general exception');
      Toobs.logEx(x);
      Toobs.logObject(Event.element(evt));
      Toobs.logObject(form);
    };
    Event.stop(evt);
  },
  
  persistFormState: function(form) {
    var persistent = form.hasClassName('persistent');
    if (persistent) {
      var formState = Form.serialize(form);
      Toobs.debug('persistFormState: ' + formState);
      var comp = form.attributes.comp.nodeValue;
      try {
        Toobs.Page.doAjaxAction('UpdateFormState.xpost?formStateId=' + $(comp + '.formState').value + '&name=' + comp + '&person=' + $('access.personId').value + '&formState=' + escape(formState));
      } catch (x) {
        Toobs.error('ajaxForm formState exception');
        Toobs.logEx(x);
      }
    }
  },

  fireEvent: function(el, eventName, delay) {
    Toobs.debug('Toobs.Page.fireEvent(' + el + ',' + eventName + ',' + delay + ')');
    try {
      if (document.all) {
        if (delay) {
          setTimeout(function() { el.fireEvent('on'+eventName); }.bind(this), delay);
        } else {
          el.fireEvent('on'+eventName);
        }
      } else {
        if (delay) {
          setTimeout(function() {
            var evObj = document.createEvent('HTMLEvents');
            evObj.initEvent( eventName, true, true );
            //Toobs.logObject(evObj);
            el.dispatchEvent(evObj);
          }.bind(this),delay);
        } else {
          var evObj = document.createEvent('HTMLEvents');
          evObj.initEvent( eventName, true, true );
          //Toobs.logObject(evObj);
          el.dispatchEvent(evObj);
        } 
      }
    } catch (x) {
      Toobs.logEx(x);
    }
  },
  
  handleError: function(message) {
    try {
      Toobs.error(message);
    } catch (x) { /* nothing to do at this point */ }
  }
}

Toobs.require(context + "javascript/lib/window.js?dt="+deployTime);
Toobs.require(context + "javascript/lib/window_effects.js?dt="+deployTime);
Toobs.require(context + "javascript/lib/tooltip.js?dt="+deployTime);
Toobs.PopupController = Class.create();
Toobs.PopupController.prototype = {
  popupElements: {},
  initialize:  function() {
    Toobs.debug('Toobs.PopupController - initialize()');
    this._onPopupClose = this.onPopupClose.bindAsEventListener(this);
  },
  create: function(url) {
    var id = arguments[1].id;
    if (!id) {
      alert('No id specified for Popup creation');
    }
    
    if (this.popupElements[id] || $(id)) {
      Toobs.debug('Popup with id: ' + id + ' already open');
      Toobs.debug('Local: ' + this.popupElements[id]);
      Toobs.debug('DOM  : ' + $(id));
      return;
    }
    this.popupElements[id] = id;
    var onClose = arguments[1].onCloseCallback;
    
    var options = Object.extend({
      className:         "mac_os_x",
      closable:          true,
      draggable:         true,
      minimizable:       false,
      maximizable:       false,
      resizable:         false,
      recenterAuto:      false,
      destroyOnClose:    true,
      modal:             true,
      showEffectOptions: {duration:.2},
      onClose:           function(evt) {
        this._onPopupClose(id);
        try {
          if (onClose) onClose(evt);
        } catch (x) {
          Toobs.error('Popup onClose callback exception:');
          Toobs.error(x);
        }
      }.bindAsEventListener(this)
    }, arguments[1] || {});

    var popup = new Window(options);
    this.popupElements[options.id] = popup;

    popup.setAjaxContent(url, {
        onComplete:  options.onComplete || Prototype.emptyFunction
      },
      true,
      options.modal
    );
    
    return popup;
  },
  popUp: function(pTitle,pName,pURL,pHeight,pWidth,pTop,pLeft,center,modal,postEval) {
  
    if (!pName) {
      alert('No id specified for Popup creation');
    }
    try {    
      if (this.popupElements[pName] || $(pName)) {
        Toobs.debug('Popup with id: ' + pName + ' already open');
        Toobs.debug('Local: ' + this.popupElements[pName]);
        Toobs.debug('DOM  : ' + $(pName));
        return;
      }
      this.popupElements[pName] = pName;
      var win = new Window({className: "mac_os_x",
                        title: pTitle,
                        id: pName,
                        width: pWidth,
                        height: pHeight,
                        top: pTop,
                        left: pLeft,
                        minimizable: true,
                        maximizable: true,
                        showEffectOptions: {duration:.2},
                        destroyOnClose: true,
                        onClose:           function() {
                          this._onPopupClose(pName);
                        }.bindAsEventListener(this)
                      });
                        
      this.popupElements[pName] = win;
  
      if (postEval || pURL.indexOf('.xcomp') != -1) {
        win.setAjaxContent(pURL, {
          onComplete:  function() {
              try {
                eval(postEval);
                win.show();
              } catch (x) {
                Toobs.error('popUp eval exception for: ' + postEval);
                Toobs.logEx(x);
              };
            }.bind(this)
          },
          true,
          true);
      } else {
        win.setURL(pURL);
        if (center) {
          win.showCenter(modal);
        } else {
          win.show();
        }
      }
    } catch (x) {
      Toobs.error('ajaxForm general exception');
      Toobs.logEx(x);
    };
  },
  close: function(pName) {
    Toobs.debug('Toobs.PopupController - close() - ' + pName);
    try {
      Windows.close(pName);
    } catch (x) { 
      Toobs.error('Toobs.Popup.close() exception') 
      Toobs.logEx(x) 
    }
  },

  closeReload: function(pName) {
    Toobs.debug('Toobs.PopupController - closeReload()');
    Windows.close(pName);
    window.setTimeout('window.location = self.location;',1000);
  },
  onPopupClose:  function(id) {
    Toobs.debug('Toobs.PopupController - onPopupClose() - id: ' + id);
    try {
      this.popupElements[id] = null;
    } catch (x) {
      Toobs.error('Toobs.Popup.onPopupClose() exception') 
      Toobs.logEx(x) 
    }
  }  
}

Ajax.Responders.register({
  onCreate: function() {
    if($('busy') && Ajax.activeRequestCount>0) {
      Effect.Appear('busy',{duration:0.5,queue:'end'});
    }
    if ($('loading') && Ajax.activeRequestCount>0) {
      $('loading').style.display = 'block';
      Effect.Appear('loadingMsg',{duration:0.1,queue:'end'});
      //$('loadingMsg').style.display = 'block';
    }
    //Toobs.debug('############### onCreate: ' + Ajax.activeRequestCount);
  },
  onComplete: function(responder, request) {
    Toobs.debug('global onComplete');
    if (request && request.getResponseHeader("MPSTimeOut") == 'true')
    {
      try {
        window.location = context + 'index.xhtml';
      } catch(x) {
        Toobs.error('x ' + x);
      }
    }

    if($('busy') && Ajax.activeRequestCount==0) {
      Effect.Fade('busy',{duration:0.5,queue:'end'});
    }
    if($('loading') && Ajax.activeRequestCount==0) {
      $('loading').style.display = 'none';
      Effect.Fade('loadingMsg',{duration:0.1,queue:'end'});
      //$('loadingMsg').style.display = 'none';
    }
    Toobs.debug('global onComplete.end');
    //Toobs.debug('@@@@@@@@@@@@@@ onComplete: ' + Ajax.activeRequestCount);
  },
  onLoaded: function(responder, request) {
    if (request && request.getResponseHeader("MPSTimeOut") == 'true')
    {
      try {
        window.location = context + 'index.xhtml';
      } catch(x) {
        Toobs.error('x ' + x);
      }
    }
  }
});
