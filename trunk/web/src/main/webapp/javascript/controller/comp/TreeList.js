Toobs.loadModule("Sizer");
Toobs.UI.TreeList = Class.create();
Toobs.UI.TreeList.prototype = {
  formInit: false,
  initialize: function() {
  	Toobs.debug('TreeList - init()');
    Toobs.Comp['TreeList'] = this;

    if (!this.formInit) {    

      this._editPopupHandler   = this.editPopupHandler.bind(this);
      this._deletePopupHandler = this.deletePopupHandler.bind(this);
      this._initTreeList       = this.initTreeList.bind(this);

      this._listAdd     = this.listAdd.bindAsEventListener(this);
      this._listEdit    = this.listEdit.bindAsEventListener(this);
      this._listDel     = this.listDel.bindAsEventListener(this);
      //this._listRefresh = this.listRefresh.bindAsEventListener(this);
      this._listClick   = this.listClick.bindAsEventListener(this);
      this._listInit    = this.listInit.bind(this);
      this._listBind    = this.listBind.bind(this);
      this._listClear   = this.listClear.bind(this);

      
      this.formInit = true;
    }

    this._editPopup;
    this._deletePopup;
    this._curListNum;
    this._lists = [];
    this._listCurVal = [];
    
    this._initTreeList();
  },

  initTreeList: function() {
  	Toobs.debug('TreeList - initTreeList()');

    var moreLists = true;
    var listNum = 1;
    while (moreLists) {
      var list = $('treeList_' + listNum);
      if (list) {
        if ($('treeList_add_' + listNum)) {
          Event.observe($('treeList_add_' + listNum), "click", this._listAdd, false);
        }
        if ($('treeList_edit_' + listNum)) {
          Event.observe($('treeList_edit_' + listNum), "click", this._listEdit, false);
        }
        if ($('treeList_del_' + listNum)) {
          Event.observe($('treeList_del_' + listNum), "click", this._listDel, false);
        }
        this._lists[listNum] = list;
        if ($('treeSize_'+listNum))
          new Toobs.UI.Sizer('treeSize_'+listNum, 'treeList_'+listNum, { range: $R(100,250), 
            onSlide: function(value, sizer) {
              sizer.area.parentNode.style[sizer.isVertical() ? 'height' : 'width'] = sizer.translateToPx(value + 2);
            }
          });
      } else {
        moreLists = false;
      }
      listNum++;
    }
    if ($('treeList_1')) {
      try {
        //var foo = new Toobs.UI.Sizer('treeSize_1', 'treeList_1', { range: $R(120,250), value: 180 });
      } catch (x) {
        Toobs.error('sizer ex');
        Toobs.logEx(x);
      }
    }
    /*
    foo.options.onSlide = function(value) {
      Toobs.debug('Slide Value: ' + value);
      $('treeList_1').style.width = (180 * value) + 'px';
    }
    */
    this._listBind(1);

  	Toobs.debug('TreeList - initTreeList().exit');
  },
  
  fixFFOverlap: function(revert) {
    if (Toobs.Util.isGecko()) {
      $$(".treeListCnt").each( function(el) {
        if (revert) {
          el.style.overflowX = 'auto';
          el.style.overflowY = 'scroll';
        } else {
          el.style.overflow = 'hidden';
        }
      });
    }
  },

  listBind: function(listNum) {
  	Toobs.debug('TreeList - listBind() - ' + listNum);
  	
    var openLi;
  	$$('ul.treeList_' + listNum + ' li').each( function(li) {
      Event.observe(li, "click", this._listClick, false);
      if (li.hasClassName('initopen')) {
        openLi = li;
        li.removeClassName('initopen');
      }
    }.bindAsEventListener(this));
    
    if (openLi) {
      openLi.addClassName('open');
      this._listCurVal[listNum] = openLi.id.substring(4);
      if (this._lists[listNum + 1]) {
        this._listInit(listNum + 1);
      }
    } else {
      this._listClear(listNum + 1);
    }
    
  	Toobs.debug('TreeList - listBind().exit');
  },

  listInit: function(listNum, selGuid) {
  	Toobs.debug('TreeList - listInit() - ' + listNum);
    try {
      var thisList = this._lists[listNum];
      var url = context + thisList.attributes.comp.nodeValue;
      if (listNum > 1) {
        if (url.indexOf('?') == -1) { url = url + '?' } else { url = url + '&' }; 
        url = url + 'parentId=' + this._listCurVal[listNum - 1];
      }
      if (selGuid) { 
        if (url.indexOf('?') == -1) { url = url + '?' } else { url = url + '&' }; 
        url = url + 'currentId=' + selGuid;
      }
      new Ajax.Updater(thisList, url, {
          method: 'post',
          evalScripts: false,
          onComplete: function() {
            this._listCurVal[listNum] = null;
            this._listBind(listNum);
            if (this._lists[listNum].attributes.mode.nodeValue == 'edit') {
              $('treeList_' + listNum + '_links').style.display = 'block';
            }
            if (this._lists[listNum].attributes.showOnClick.nodeValue == 'true') {
              this._lists[listNum].style.display = 'block';
              if ($('treeDiv_' + listNum)) { 
                $('treeDiv_' + listNum).style.display = 'block';
              }
              $('treeHead_' + listNum).style.display = 'block';
            }
          }.bind(this)
      });
    } catch (x) {
  	  Toobs.error('TreeList - listInit() - exception');
      Toobs.error(x);
    }
  	Toobs.debug('TreeList - listInit().exit');
  },

  listClear: function(listNum) {
  	Toobs.debug('TreeList - listClear() - ' + listNum);
    for (var i = listNum; i < this._lists.length; i++) {
      this._lists[i].innerHTML = '';
      $('treeList_' + i + '_links').style.display = 'none';
      if (this._lists[i].attributes.showOnClick.nodeValue == 'true') {
        this._lists[i].style.display = 'none';
        if ($('treeDiv_' + i)) { 
          $('treeDiv_' + i).style.display = 'none';
        }
        $('treeHead_' + i).style.display = 'none';
      }
      this._listCurVal[i] = null;
    }
  	Toobs.debug('TreeList - listClear().exit');
  },

  listAdd: function(evt) {
    var a = Event.element(evt);
    var d = a.parentNode;
    
    var type = d.attributes.type.nodeValue;
    var typeDisplay = d.attributes.typeDisplay.nodeValue;
    var listNum = parseInt(d.attributes.listNum.nodeValue);
    var focusInput = d.attributes.focusInput.nodeValue;
    if (d.attributes.popHeight)
      var height = parseInt(d.attributes.popHeight.nodeValue);
    if (d.attributes.popWidth)
      var width  = parseInt(d.attributes.popWidth.nodeValue);
    
    var url = context + a.attributes.comp.nodeValue;
    if (listNum > 1) {
      if (url.indexOf('?') == -1) { url = url + '?' } else { url = url + '&' }; 
      url = url + 'parentId=' + this._listCurVal[listNum - 1];
    }
    this._editPopupHandler(url, typeDisplay, type, listNum, focusInput, height, width);
  },

  listEdit: function(evt) {
    var a = Event.element(evt);
    var d = a.parentNode;
    
    var type = d.attributes.type.nodeValue;
    var typeDisplay = d.attributes.typeDisplay.nodeValue;
    var listNum = parseInt(d.attributes.listNum.nodeValue);
    var focusInput = d.attributes.focusInput.nodeValue;
    if (d.attributes.popHeight)
      var height = parseInt(d.attributes.popHeight.nodeValue);
    if (d.attributes.popWidth)
      var width  = parseInt(d.attributes.popWidth.nodeValue);
    
    if (!this._listCurVal[listNum] || this._listCurVal[listNum] == '') {
      Toobs.Util.pleaseSelectPopup(typeDisplay);
    } else {
      var url = context + a.attributes.comp.nodeValue;
      if (url.indexOf('?') == -1) { url = url + '?' } else { url = url + '&' }; 
      url = url + "currentId=" + this._listCurVal[listNum];
      if (listNum > 1) {
        url = url + "&parentId=" + this._listCurVal[listNum - 1];
      }
      this._editPopupHandler(url, typeDisplay, type, listNum, focusInput, height, width);
    }    
  },

  listDel: function(evt) {
    var a = Event.element(evt);
    var d = a.parentNode;
    
    var type = d.attributes.type.nodeValue;
    var typeDisplay = d.attributes.typeDisplay.nodeValue;
    var listNum = d.attributes.listNum.nodeValue;
    
    if (!this._listCurVal[listNum] || this._listCurVal[listNum] == '') {
      Toobs.Util.pleaseSelectPopup(typeDisplay);
    } else {
      var url = context + a.attributes.comp.nodeValue;
      if (url.indexOf('?') == -1) { url = url + '?' } else { url = url + '&' }; 
      url = url + "currentId=" + this._listCurVal[listNum];
      if (listNum > 1) {
        url = url + "&parentId=" + this._listCurVal[listNum - 1];
      }
      this._deletePopupHandler(url, typeDisplay, type, listNum);
    }    

  },

  listRefreshAdd: function(newGuid) {
  	Toobs.debug('TreeList - listRefreshAdd() - ' + newGuid);
    this._listInit(this._curListNum, newGuid);
    if (this._editPopup)
      this._editPopup.close()
    this._curListNum = null;
  	Toobs.debug('TreeList - listRefreshAdd().exit');
  },

  listRefreshDel: function() {
    this._listInit(this._curListNum);
    this._listClear(this._curListNum);
    if (this._deletePopup)
      this._deletePopup.close()
    this._curListNum = null;
  },

  listClick: function(evt) {
  	Toobs.debug('TreeList - listClick()');
    var li = Event.element(evt);
    while (li.tagName != 'LI')
    {
      li = li.parentNode;
    }
    
    var listNum = parseInt(li.parentNode.attributes.listNum.nodeValue);
    
    var isOpen = li.hasClassName('open');
  	$$('ul.treeList_' + listNum + ' li').each( function(li) {
      li.removeClassName('open');
    });
    if (!isOpen) { 
      li.addClassName('open');
      this._listCurVal[listNum] = li.id.substring(4);
      if (this._lists[listNum + 1]) {
        //setTimeout(function() { this._listInit(listNum + 1) }, 30);
        this._listInit(listNum + 1);
      }
    } else {
      this._listClear(listNum + 1);
    }
  	Toobs.debug('TreeList - listClick().exit');
  },

  editPopupHandler: function(url, typeDisplay, type, listNum, focusName, height, width) {

    var pop = Toobs.Popup.create(url, {
      title: typeDisplay,
      id: 'edit' + type + 'Popup',
      width: width ? width : 500,
      height: height ? height : 280,
      onComplete:  function() {
        Toobs.Page.initialize();
        if (Toobs.Comp.DragDropPair)
          Toobs.Comp.DragDropPair.initialize();
      }.bind(this),
      onShow:  function() {
        if (focusName)
          setTimeout(function() { $(focusName).focus(); }, 250);
        this.fixFFOverlap();
      }.bind(this),
      closeCallback:  function() {
        this.fixFFOverlap(true);
        return true;
      }.bind(this)
    });
    if (pop) this._editPopup = pop;
    this._curListNum = listNum;

  },
  
  deletePopupHandler: function(url, typeDisplay, type, listNum) {

    var pop = Toobs.Popup.create(url, {
      title: 'Delete ' + typeDisplay,
      id: 'delete' + type + 'Popup',
      width: 400,
      height: 200,
      onComplete:  function() {
        Toobs.Page.initialize();
        //Event.observe($('closePopup'), "click", function() { this._deletePopup.close() }.bind(this), false);
      }.bind(this),
      onShow:  function() {
        this.fixFFOverlap();
      }.bind(this),
      closeCallback:  function() {
        this.fixFFOverlap(true);
        return true;
      }.bind(this)
    });
    if (pop) this._deletePopup = pop;
    this._curListNum = listNum;
  }
}