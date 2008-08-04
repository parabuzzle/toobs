Toobs.Util.getParameter = function(pName)
{
  pName = pName + "=";
  var q = window.top.location.search.substring(1);
  
  if (q.length > 0)
  {
    var b = q.indexOf(pName);
    if (b != -1)
    {
      b += pName.length;
      e =  q.indexOf("&", b);
      if (e == -1) e = q.length;
      return(unescape(q.substring(b,e)));
    }
  }
  return "";
}

function findPos(obj){
  var posX = obj.offsetLeft;
  var posY = obj.offsetTop;
  while(obj.offsetParent){
    posX=posX+obj.offsetParent.offsetLeft;
    posY=posY+obj.offsetParent.offsetTop;
    if(obj==document.getElementsByTagName('body')[0]){break}
    else{obj=obj.offsetParent;}
  }
  return [posX,posY];
} 

//trims whitespace at the start and end of string
Toobs.Util.trim = function(s) {
	return s.replace(/^\s*(.*?)\s*$/,"$1")
}
Toobs.Util.stripPhone = function(s) {
	var phone = s.replace(/\D/g, '')
	alert(phone + '@mhsfax.com');
	return phone + '@mhsfax.com'
}
Toobs.Util.setTitle = function(s, append) {
  Toobs.debug('Toobs.Util.setTitle(' + s + ',' + append + ')');
  if (append) {
	  document.title = document.title + s;
  } else {
	  document.title = s;
  }
}
Toobs.Util.setTabTitle = function(s) {
  var curTitle = document.title;
  var lastCol = curTitle.lastIndexOf('::');
  var firstCol = curTitle.indexOf('::');
  Toobs.debug('Toobs.Util.setTabTitle(' + s + ') - curTitle: ' + curTitle + ' lastCol: ' + lastCol );
  if (firstCol != lastCol) {
    document.title = curTitle.substring(0,lastCol) + s
  } else {
    document.title = curTitle + s
  } 
}
Toobs.Util.pleaseSelectPopup = function(type) {
  var url = context + 'PleaseSelectPopup.xcomp?type='+type;

  var pop = Toobs.Popup.create(url, {
    title: 'Select ' + type,
    id: 'select' + type + 'Popup',
    width: 400,
    height: 110,
    zIndex: 5000,
    onComplete:  function() {
      Event.observe($('closeSelectPopup'), "click", function() { this._selectPopup.close() }.bind(this), false);
    }.bind(this),
    onShow:  function() {
      if (Toobs.Comp.TreeList) Toobs.Comp.TreeList.fixFFOverlap();
    }.bind(this),
    closeCallback:  function() {
      if (Toobs.Comp.TreeList) Toobs.Comp.TreeList.fixFFOverlap(true);
      return true;
    }.bind(this)
  });
  if (pop) this._selectPopup = pop;
}
Toobs.Util.setRangeToday = function(b,e) {
  var today = new Date(); 
	$(b).value = today.print('%m/%d/%Y');
	today.setDate(today.getDate() + 1);
	$(e).value = today.print('%m/%d/%Y');
}
Toobs.Util.setRangeLastWeek = function(b,e) {
  var today = new Date();
  var last = new Date();
  last.setDate(today.getDate() - 7);
  $(b).value = last.print('%m/%d/%Y');
	today.setDate(today.getDate() + 1);
	$(e).value = today.print('%m/%d/%Y');
}
Toobs.Util.setRangeLastMonth = function(b,e) {
  var today = new Date();
  var last = new Date();
  last.setDate(today.getDate() - 30);
  $(b).value = last.print('%m/%d/%Y');
	today.setDate(today.getDate() + 1);
	$(e).value = today.print('%m/%d/%Y');
}
Toobs.Util.insertFCKDataNode = function(type, inner) {
  Toobs.log('Toobs.Util.insertFCKDataNode()');
  for ( i = 0; i < parent.frames.length; ++i )
    if ( parent.frames[i].FCK )
      localFCK = parent.frames[i].FCK;

  Toobs.log(localFCK);
	var data = localFCK.CreateElement( 'DATA' ) ;
  var attr = document.createAttribute('type');
  attr.value = type;
  data.setAttributeNode(attr);
  data.innerHTML=inner;
  var newData = localFCK.InsertElement( data ) ;
}
Toobs.Util.isIE6 = function() {
	return navigator.userAgent.match(/MSIE 6/) == 'MSIE 6';
}
Toobs.Util.isIE = function() {
	return navigator.userAgent.match(/MSIE/) == 'MSIE';
}
Toobs.Util.isGecko = function() {
	return navigator.userAgent.match(/Gecko/) == 'Gecko';
}
Toobs.Util.selectText = function(el) {
  el.select();
}
Toobs.Util.selectRange = function(el) {
  if (Toobs.Util.isGecko()) {
  	var oRange = document.createRange() ;
  	oRange.selectNode( el ) ;
  
  	var oSel = window.getSelection() ;
  	Toobs.logObject(oSel);
  	oSel.removeAllRanges() ;
  	oSel.addRange( oRange ) ;
  }
  if (Toobs.Util.isIE()) {
  	document.selection.empty() ;
  	var oRange ;
  	try 
  	{
  		// Try to select the node as a control.
  		oRange = document.body.createControlRange() ;
  		oRange.addElement( node ) ;
  	} 
  	catch(e) 
  	{
  		// If failed, select it as a text range.
  		oRange = document.selection.createRange() ;
  		oRange.moveToElementText( node ) ;
  	}
  
  	oRange.select() ;
  }
}
$$F = function(el, timeout) {
  try {
    if (timeout) 
      setTimeout(function() {$(el).focus()}, timeout);
    else
      $(el).focus();
  } catch (x) { Toobs.logEx(x) }
}
$$$F = function(el, timeout) {
  try {
    var setFocus = true;
	  $$(".errorLabel").each( function(e) {
	    for (var i = 0; i < e.parentNode.childNodes.length; i++) {
	      if (setFocus) {
  	      if (e.parentNode.childNodes[i].tagName == 'INPUT' || e.parentNode.childNodes[i].tagName == 'SELECT') {
    	      $$F(e.parentNode.childNodes[i], timeout);
            setFocus = false;
            break;
  	      }
        }
	    }
	  }.bindAsEventListener(this));
    if (setFocus && el)
      $$F(el, timeout);
  } catch (x) {}
}
Toobs.Util.disEnableSiblings = function(el, counter) {
  var parent = el.parentNode;
  if (parent.tagName == 'LABEL') {
    parent = parent.parentNode;
  }
  var disabled = null;
  if (!el.checked) {
    disabled = 'disabled';
    $(counter).value = $(counter).value - 1; 
  } else {
    $(counter).value = $(counter).value + 1; 
  }
  for (var i = 0; i < parent.childNodes.length; i++) {
    var ch = parent.childNodes[i];
    Toobs.debug('disEnableSiblings: ' + ch.tagName + ' type: ' + ch.type);
    if ((ch.tagName == 'INPUT' || ch.tagName == 'SELECT') && ch.type != 'CHECKBOX') {
      ch.disabled = disabled;
    }
  }
}
Toobs.Util.logFormat = function(logTime) {
  var format = logTime.getHours() + ':' + logTime.getMinutes() + '.' + logTime.getSeconds() + '.';
  if (logTime.getMilliseconds() < 10)  format += '0';
  if (logTime.getMilliseconds() < 100) format += '0';
  format += logTime.getMilliseconds();
  return format;
}