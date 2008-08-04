// The base object, used everywhere
//var ToobsCompController = Class.create();

var Toobs = Object.extend(Scriptaculous,{
  Version: '0.01a',
  Page:    null,
  Comp:    [],
  hasController: false,
  hasCompController: false,
  isDebugEnabled: false,
  debugPanel: null,
  deployTime: 0,
 
  basePath:    context + "javascript/",
  libPath:     context + "javascript/lib/",
  modulePath:  context + "javascript/modules/",
  pagePath:    context + "javascript/controller/",
  compPath:    context + "javascript/controller/comp/",
  
  load:          function()
  {
     this.require(this.basePath + "Controller.js?dt="+deployTime);
     this.require(this.basePath + "CompController.js?dt="+deployTime);
     this.require(this.basePath + "Util.js?dt="+deployTime);
  },
  
  loadModule:    function(name) { this.require(this.modulePath + name + ".js?dt="+deployTime); },
  
  log: function(message, level) {
    var logTime = new Date();
    if (!level) level = 'INFO';
    if (window.console) {
      window.console.log(Toobs.Util.logFormat(logTime) + ' '+ level + ' ' + message);
    } else if (this.isDebugEnabled) {
      //if (!this.debugPanel) this.debugPanel = $('toobsDebug');
      if (Toobs.debugPanel) {
        var li = document.createElement('li');
        li.innerHTML = '<span class="'+level+'" >'+ Toobs.Util.logFormat(logTime) + ' '+ level + ' ' + message +'</span>';
        Toobs.debugPanel.appendChild(li);
      }
    }
  },
  logObject: function(object) {
    if (this.isDebugEnabled) {
      if (window.console) {
        window.console.log(object);
      } else {
        this.log(object, 'DEBUG');
      }
    }
  },
  logEx: function(x) {
    if (window.console) {
      window.console.log(x);
    } else {
      this.log(x.message, 'ERROR');
    }
  },
  debug: function(message) {
    if (this.isDebugEnabled) {
      this.log(message, 'DEBUG');
    }
  },
  error: function(message) {
    this.log(message, 'ERROR');
  }
});

Toobs.Controller = {
  use:  function(controllerName)
  {
    
    if (!Toobs.hasController)
    {
      if (controllerName != '') 
      {
        Toobs.require(Toobs.pagePath + controllerName + ".js?dt="+deployTime);
      }
      Toobs.hasController = true;
      Event.observe(window, "load", Toobs.Controller.load, false)

    }
  },
  useComp:  function(controllerName)
  {
    /*
    if (!Toobs.hasCompController) {
      ToobsCompController.prototype = Object.extend(new Toobs.CompController(), {});
    }
    */
    if (controllerName != '')
    {
      Toobs.debug('Require comp: ' + controllerName);
      Toobs.require(Toobs.compPath + controllerName + ".js?dt="+deployTime);
    }
    Event.observe(window, "load", function() { Toobs.Controller.loadComp(controllerName) }.bind(this), false)
  },
  load: function() 
  { 
    try
    {
      Toobs.Page = new ToobsPageController(); 
    } catch (x)
    {
      Toobs.Page = new Toobs.PageController();
    }
    Toobs.Popup = new Toobs.PopupController();
  },
  loadComp: function(controllerName)
  { 
    try
    {
      if (!Toobs.Comp[controllerName]) {
        Toobs.Comp[controllerName] = eval('new Toobs.UI.'+ controllerName +'()');
      }
    } catch (x)
    {
      Toobs.error('Toobs.loadComp exception');
      Toobs.logEx(x);
    }
  },
  
  rebind: function() {
  	if(Toobs.Page) {
   		Toobs.Page.rebind();
   	}
  },
  
  lazyLoadComp: function(containerId, url) {
    Event.observe(window, 'load', 
			function(evt) {
			  new Ajax.Updater(containerId , url , {evalScripts: true, onComplete:function(){Toobs.Controller.rebind()}});
			}, false);
  }
}

Toobs.UI   = {};
Toobs.Util = {};
Toobs.load();