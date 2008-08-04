Toobs.UI.PrintOptions = Class.create();
Toobs.UI.PrintOptions.prototype = {
  initialize: function() {
  	Toobs.debug('PrintOptions - init()');
    Toobs.Comp['PrintOptions'] = this;

    try {
      this.form        = $('printOptionsForm');
      this.component   = $('component');
      this.exportMode  = $('exportMode');
      this.exportURI   = $('exportURI');
      
      this.firstResult = $('firstResult');
      this.curPageSize = $('curPageSize');
      this.allPageSize = $('allPageSize');
      this.queryString = $('queryString');

      this._formatOptChange = this.formatOptChange.bindAsEventListener(this);

      var formatLocal = this._formatOptChange; 
      $A(document.getElementsByName('format')).each( function(e) {
        Event.observe(e, "change", formatLocal, false);
      });

      this._optionsChange = this.optionsChange.bindAsEventListener(this);

      var optionsLocal = this._optionsChange; 
      $A(document.getElementsByName('options')).each( function(e) {
        Event.observe(e, "change", optionsLocal, false);
      });
    } catch (x) {
      Toobs.error(x);
    }
  },
  
  formatOptChange: function(evt) {
    var format;
    $A(document.getElementsByName('format')).each( function(e) {
      if (e.checked) format = e.value;
    });
    var tmpltName;
    if (this.exportURI.value != '') {
      tmpltName = this.exportURI.value;
    } else if (this.exportMode.value == 'table') {
      tmpltName = 'ExportTable';
    } else {
      tmpltName = this.component.value;
    }    
    if (format == '.xxls') {
      $('portrait').disabled = 'disabled';
      $('landscape').disabled = 'disabled';
    } else {
      $('portrait').disabled = null;
      $('landscape').disabled = null;
    }
    this.form.action = context + tmpltName + format + '?' + this.queryString.value;
	  Toobs.debug('PrintOptions - this.form.action ' + this.form.action);
  },
  
  optionsChange: function(evt) {
    var option;
    $A(document.getElementsByName('options')).each( function(e) {
      if (e.checked) option = e.value;
    });
    switch(option) {
      case "allPages":
        this.firstResult.disabled = "disabled";
        this.curPageSize.disabled = "disabled";
        this.allPageSize.disabled = "";
        break; 
      case "currentPage":
        this.firstResult.disabled = "";
        this.curPageSize.disabled = "";
        this.allPageSize.disabled = "disabled";
        break;
      default:
        break;
    }
  }
}