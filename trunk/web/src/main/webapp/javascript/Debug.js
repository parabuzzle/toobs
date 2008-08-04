Toobs.UI.Debug = Class.create();
Toobs.UI.Debug.prototype = {

  initialize: function() {
  	Toobs.debug('Debug - init()');
    Toobs.Comp['Debug'] = this;
    this._debugButton = this.debugButton.bindAsEventListener(this);
    this._clearButton = this.clearButton.bindAsEventListener(this);
    try {

      if (Toobs.isDebugEnabled) {
        this.debugContainer = $('debugContainer');
        Event.observe($('debugIcon'), 'click', this._debugButton, false);
        Event.observe($('clearIcon'), 'click', this._clearButton, false);
      }

    } catch (x) {
      Toobs.error(x);
    }
  },

  debugButton: function(evt) {
    var icon = Event.element(evt);
    Toobs.debug('debugButton - click - ' + icon);
    if (icon.hasClassName('debugOpen')) {
      icon.addClassName('debugClosed');
      icon.removeClassName('debugOpen');
      icon.src = context + 'img/disclose-closed_12.gif';
      $('toobsDebug').style.display = 'none';
      $('clearIcon').style.display = 'none';
      this.debugContainer.removeClassName('conOpen');
    } else {
      icon.addClassName('debugOpen');
      icon.removeClassName('debugClosed');
      icon.src = context + 'img/disclose-open_12.gif';
      $('toobsDebug').style.display = 'block';
      $('clearIcon').style.display = 'block';
      this.debugContainer.addClassName('conOpen');
    }
  },

  clearButton: function(evt) {
    var icon = Event.element(evt);
    Toobs.debug('clearButton - click - ' + icon);
    $('toobsDebug').innerHTML = '';
  }
}