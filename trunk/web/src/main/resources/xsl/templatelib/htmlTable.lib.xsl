<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:dateHelper="org.toobs.framework.transformpipeline.xslExtentions.DateHelper"
  xmlns:s="org.toobs.framework.transformpipeline.xslExtentions.XMLEncodeHelper"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:dyn="http://exslt.org/dynamic"
  exclude-result-prefixes="xalan dateHelper s dyn">
  
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="component"/>
  <xsl:param name="popupName"/>
  <xsl:param name="pageEval"/>
  <xsl:param name="queryString"/>
  
  <xsl:param name="firstResult"/>
  <xsl:param name="totalRows"/>
  <xsl:param name="pageSize"/>
  <xsl:param name="sortOrder"/>
  <xsl:param name="ajaxTable"/>
  <xsl:param name="showPager" select="'true'"/>
  <xsl:param name="showPagerJump" select="'false'"/>
  <xsl:param name="showPageSize" select="'false'"/>
  <xsl:param name="showPagerTop"/>
  <xsl:param name="showPagerBot"/>
  <xsl:param name="showExport" select="'true'"/>
  
  <xsl:param name="noRowsMessage">No results found</xsl:param>
  <xsl:param name="printTitle"/>
  
  <xsl:param name="columns"/>
  <xsl:param name="actions"/>
  <xsl:param name="buttons"/>
  <xsl:param name="links">
    <toplinks>
      <toplink name="All"      action="select"/>
      <toplink name="None"     action="unselect"/>
      <toplink name="Active"   action="conditional" selector="activeInd" value="true" />
      <toplink name="Inactive" action="conditional" selector="activeInd" value="false" />
    </toplinks>
  </xsl:param>
  
  <xsl:variable name="qString0">
    <xsl:choose>
      <xsl:when test="contains($queryString,'&amp;s=')">
        <xsl:value-of select="substring-before($queryString,concat('&amp;s=',$sortOrder))"/><xsl:value-of select="substring-after($queryString,concat('s=',$sortOrder))"/>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$queryString"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="qString1">
    <xsl:choose>
      <xsl:when test="contains($qString0,'&amp;m=')">
        <xsl:value-of select="substring-before($qString0,concat('&amp;m=',$pageSize))"/><xsl:value-of select="substring-after($qString0,concat('m=',$pageSize))"/>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$qString0"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="qString">
    <xsl:choose>
      <xsl:when test="contains($qString1,'&amp;f=')">
        <xsl:value-of select="substring-before($qString1,concat('&amp;f=',$firstResult))"/><xsl:value-of select="substring-after($qString1,concat('f=',$firstResult))"/>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$qString1"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="actionCount" select="count(xalan:nodeset($actions)/actions/action)"/>

  <xsl:template name="htmlTable">
    <xsl:param name="cssClass"/>
    <xsl:param name="tableComp"/>
    <xsl:param name="totalCount"/>
    
    <xsl:param name="rowData"/>

    <xsl:variable name="thisComp">
      <xsl:if test="$tableComp"><xsl:value-of select="$tableComp"/></xsl:if>
      <xsl:if test="not($tableComp)"><xsl:value-of select="$component"/></xsl:if>
    </xsl:variable>
    <xsl:variable name="rowCount">
      <xsl:if test="$totalCount"><xsl:value-of select="$totalCount"/></xsl:if>
      <xsl:if test="not($totalCount)"><xsl:value-of select="$totalRows"/></xsl:if>
    </xsl:variable>
    <xsl:variable name="columnCount" select="count(xalan:nodeset($columns)/columns/column)"/>
    <xsl:if test="count(xalan:nodeset($links)/toplinks/toplink) &gt; 0 or $showPager='true' or $showPagerTop='true'">
      <div class="dtHeader">
        <xsl:call-template name="toplinks">
          <xsl:with-param name="thisComp" select="$thisComp"/>
        </xsl:call-template>
        <xsl:if test="$showPager='true' or $showPagerTop='true'">
          <xsl:call-template name="pager">
            <xsl:with-param name="uid" select="'head'"/>
            <xsl:with-param name="thisComp" select="$thisComp"/>
            <xsl:with-param name="rowCount" select="$rowCount"/>
          </xsl:call-template>
        </xsl:if>
      </div>
    </xsl:if>
    <xsl:call-template name="dtButtons">
      <xsl:with-param name="topbuttons" select="xalan:nodeset($buttons)/buttons[not(@position) or @position='top' or @position='both']"/>
      <xsl:with-param name="thisComp" select="$thisComp"/>
    </xsl:call-template>
    <!--
    <p>Sort: <xsl:value-of select="$sortOrder"/></p>
    <p>Page: <xsl:value-of select="$pageSize"/></p>
    <p>Orig: <xsl:value-of select="$queryString"/></p>
    <p>Zero: <xsl:value-of select="$qString0"/></p>
    <p>One : <xsl:value-of select="$qString1"/></p>
    <p>Mod : <xsl:value-of select="$qString"/></p>
    <p>CssClass: <xsl:value-of select="$cssClass"/></p>
    -->
    <input type="hidden" id="{$thisComp}QueryString" value="{$qString}"/>
    <table id="{$thisComp}Table" cellpadding="0" cellspacing="0" class="dt" comp="{$thisComp}" ajax="{$ajaxTable}">
      <xsl:if test="$cssClass"><xsl:attribute name="class"><xsl:value-of select="$cssClass"/></xsl:attribute></xsl:if>
      <xsl:if test="$pageEval"><xsl:attribute name="pageEval"><xsl:value-of select="$pageEval"/></xsl:attribute></xsl:if>
      <xsl:if test="$popupName"><xsl:attribute name="popup"><xsl:value-of select="$popupName"/></xsl:attribute></xsl:if>
      <xsl:attribute name="sortAction">
        <xsl:choose>
          <xsl:when test="$ajaxTable='true'"><xsl:value-of select="$context"/><xsl:value-of select="$thisComp"/>Table.xcomp?<xsl:value-of select="$qString"/>&amp;s=</xsl:when>
          <xsl:otherwise><xsl:value-of select="$context"/><xsl:value-of select="$layoutId"/>.xhtml?<xsl:value-of select="$qString"/>&amp;s=</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:if test="$showExport='true'">
        <xsl:attribute name="exportAction"><xsl:value-of select="$context"/>PrintOptionsPopup.xcomp?exportMode=table&amp;component=<xsl:value-of select="$thisComp"/>Table&amp;firstResult=<xsl:value-of select="$firstResult"/>&amp;totalRows=<xsl:value-of select="$rowCount"/>&amp;pageSize=<xsl:value-of select="$pageSize"/>&amp;sortOrder=<xsl:value-of select="$sortOrder"/>&amp;printTitle=<xsl:value-of select="$printTitle"/>&amp;<xsl:value-of select="$qString"/></xsl:attribute>
      </xsl:if>
      <thead>
        <tr>
          <xsl:if test="$actionCount &gt; 0">
            <th>&#xa0;</th><!-- Action Col -->
          </xsl:if>
          <xsl:for-each select="xalan:nodeset($columns)/columns/column">
            <xsl:call-template name="headColumn">
              <xsl:with-param name="column" select="."/>
              <xsl:with-param name="thisComp" select="$thisComp"/>
            </xsl:call-template>
          </xsl:for-each>
        </tr>
      </thead>
      <tbody>
        <!-- <xsl:apply-templates select="$rowData" mode="dtBody"/>-->
        <xsl:for-each select="$rowData">
          <xsl:call-template name="dtBody">
            <xsl:with-param name="currentNode" select="."/>
            <xsl:with-param name="thisComp" select="$thisComp"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:if test="count($rowData) = 0">
          <tr class="odd">
            <td colspan="{2 + $columnCount}" class="dtMsg">
              <xsl:value-of select="$noRowsMessage"/>
            </td>
          </tr>
        </xsl:if>
      </tbody>
      <tfoot>
        <xsl:call-template name="dtFooter">
          <xsl:with-param name="rowData" select="$rowData"/>
        </xsl:call-template>
        <xsl:if test="$showPager='true' or $showExport='true' or $showPagerBot='true'">
          <tr>
            <td colspan="{$columnCount + 2}" style="padding-left:0;">
              <xsl:if test="$showExport='true'">
                <div class="fleft">
                  <a href="javascript:void(0);" class="hul expFmt" format="acrobat" comp="{$thisComp}">
                    <img src="{$context}img/ico_file_pdf.png" alt=""/><span>Acrobat</span>
                  </a>
                  <a href="javascript:void(0);" class="hul expFmt" format="word" comp="{$thisComp}">
                    <img src="{$context}img/ico_file_rtf.png" alt=""/><span>Word</span>
                  </a>
                  <a href="javascript:void(0);" class="hul expFmt" format="excel" comp="{$thisComp}">
                    <img src="{$context}img/ico_file_excel.png" alt=""/><span>Excel</span>
                  </a>
                </div>
              </xsl:if>
              <xsl:if test="$showPager='true' and $showPagerBot!='false'">
                <xsl:call-template name="pager">
                  <xsl:with-param name="uid" select="'foot'"/>
                  <xsl:with-param name="thisComp" select="$thisComp"/>
                  <xsl:with-param name="rowCount" select="$rowCount"/>
                </xsl:call-template>
              </xsl:if>
            </td>
          </tr>
        </xsl:if>
      </tfoot>
    </table>
    <xsl:call-template name="dtButtons">
      <xsl:with-param name="topbuttons" select="xalan:nodeset($buttons)/buttons[@position='bottom' or @position='both']"/>
      <xsl:with-param name="thisComp" select="$thisComp"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="dtBody">
    <xsl:param name="currentNode"/>
    <xsl:param name="thisComp"/>
    <tr class="odd">
      <xsl:if test="position() mod 2 = 0"><xsl:attribute name="class">even</xsl:attribute></xsl:if>
      <xsl:variable name="rowNum"><xsl:value-of select="position()"/></xsl:variable>
      <xsl:if test="$actionCount &gt; 0">
        <td class="dtActions" style="width:8.5%;">
          <xsl:for-each select="xalan:nodeset($actions)/actions/action">
            <xsl:call-template name="actionlink">
              <xsl:with-param name="currentNode" select="$currentNode"/>
              <xsl:with-param name="action" select="."/>
              <xsl:with-param name="thisComp" select="$thisComp"/>
            </xsl:call-template>
          </xsl:for-each>
        </td>
      </xsl:if>
      <xsl:for-each select="xalan:nodeset($columns)/columns/column">
        <xsl:call-template name="bodyColumn">
          <xsl:with-param name="col" select="."/>
          <xsl:with-param name="currentNode" select="$currentNode"/>
          <xsl:with-param name="rowNum" select="$rowNum"/>
        </xsl:call-template>
      </xsl:for-each>
    </tr>
  </xsl:template>
  
  <xsl:template name="dtFooter">
    <xsl:param name="rowData"/>
    <xsl:variable name="colNodes" select="xalan:nodeset($columns)/columns"/>
    <xsl:if test="count($colNodes/column/footer) &gt; 0">
      <tr class="dtFoot">
        <xsl:if test="$actionCount &gt; 0">
          <td>&#xa0;</td>
        </xsl:if>
        <xsl:for-each select="$colNodes/column">
          <xsl:if test="./footer">
            <xsl:call-template name="bodyColumn">
              <xsl:with-param name="col" select="./footer"/>
              <xsl:with-param name="currentNode" select="$rowData[position()=1]"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:if test="not(./footer)"><td>&#xa0;</td></xsl:if>
        </xsl:for-each>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template name="bodyColumn">
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="rowNum"/>
    <td class="bodyColumn">
      <xsl:if test="position()=last()"><xsl:attribute name="class">dtLast</xsl:attribute></xsl:if>
      <xsl:if test="$col/@gid"><xsl:attribute name="id"><xsl:value-of select="$col/@gid"/><xsl:value-of select="$currentNode/guid | $currentNode/id"/></xsl:attribute></xsl:if>
      <xsl:for-each select="$col/node()">
        <xsl:variable name="thisNode" select="."/>
        <xsl:choose>
          <xsl:when test="name()='attribute'">
            <xsl:variable name="cnt"><xsl:number/></xsl:variable>
            <xsl:variable name="valuePath">$currentNode/<xsl:value-of select="@name"/></xsl:variable>
            <xsl:call-template name="conversion">
              <xsl:with-param name="valuePath" select="$valuePath"/>
              <xsl:with-param name="attr" select="."/>
              <xsl:with-param name="col" select="$col"/>
              <xsl:with-param name="currentNode" select="$currentNode"/>
              <xsl:with-param name="rowNum" select="$rowNum"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="name()='collection'">
            <xsl:for-each select="$currentNode/descendant::node()[name() = $thisNode/@name]">
              <xsl:variable name="collectNode" select="."/>
              <xsl:for-each select="$thisNode/attribute">
                <xsl:variable name="valuePath">$currentNode/<xsl:value-of select="@name"/></xsl:variable>
                <xsl:call-template name="conversion">
                  <xsl:with-param name="valuePath" select="$valuePath"/>
                  <xsl:with-param name="attr" select="."/>
                  <xsl:with-param name="col" select="$col"/>
                  <xsl:with-param name="currentNode" select="$collectNode"/>
                  <xsl:with-param name="rowNum" select="$rowNum"/>
                </xsl:call-template>
              </xsl:for-each>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </td>  
  </xsl:template>
  
  <xsl:template name="headColumn">
    <xsl:param name="column"/>
    <xsl:param name="thisComp"/>
    <th>
      <xsl:if test="position()=last()"><xsl:attribute name="class">dtLast</xsl:attribute></xsl:if>
      <span comp="{$thisComp}">
        <xsl:if test="@sortable='true'">
          <xsl:variable name="sortAttr"><xsl:value-of select="$column/@criteria | $column/attribute[position()=1]/@name"/></xsl:variable>
          <xsl:variable name="sortAttrAsc"><xsl:value-of select="s:splitAppend($sortAttr, ';', '_ASC')"/></xsl:variable>
          <xsl:variable name="sortAttrDsc"><xsl:value-of select="s:splitAppend($sortAttr, ';', '_DSC')"/></xsl:variable>
          <xsl:choose>
            <xsl:when test="$sortAttrAsc = $sortOrder">
              <xsl:attribute name="order"><xsl:value-of select="$sortAttrDsc"/></xsl:attribute>
              <xsl:attribute name="class">sort arwup</xsl:attribute>
            </xsl:when>
            <xsl:when test="$sortAttrDsc = $sortOrder">
              <xsl:attribute name="order"><xsl:value-of select="$sortAttrAsc"/></xsl:attribute>
              <xsl:attribute name="class">sort arwdwn</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="order"><xsl:value-of select="$sortAttrAsc"/></xsl:attribute>
              <xsl:attribute name="class">sort arwoff</xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:value-of select="$column/@name" disable-output-escaping="yes"/>
        <xsl:if test="$column/@name=''">&#xa0;</xsl:if>
      </span>
    </th>
  </xsl:template>
  
  <xsl:template name="pager">
    <xsl:param name="uid"/>
    <xsl:param name="thisComp"/>
    <xsl:param name="rowCount"/>
    <ul class="pager" id="{$thisComp}Pager_{$uid}" firstResult="{$firstResult}" totalRows="{$rowCount}" pageSize="{$pageSize}" comp="{$thisComp}" uid="{$uid}">
      <xsl:if test="$rowCount &lt;= $pageSize"><xsl:attribute name="style">display:none;</xsl:attribute></xsl:if>        
      <xsl:attribute name="frame">
        <xsl:choose>
          <xsl:when test="$ajaxTable='true'"><xsl:value-of select="$context"/><xsl:value-of select="$thisComp"/>Table.xcomp?<xsl:value-of select="$qString"/>&amp;s=<xsl:value-of select="$sortOrder"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="$context"/><xsl:value-of select="$layoutId"/>.xhtml?<xsl:value-of select="$qString"/>&amp;s=<xsl:value-of select="$sortOrder"/></xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <li page="first" >
        <img src="{$context}img/arrow-first_12.gif" />
      </li>
      <li page="prev" >
        <img src="{$context}img/arrow-left_12.gif" />
      </li>
      <!-- <li page="refresh"><img src="{$context}img/bar_12.gif" /></li> -->
      <xsl:if test="$showPagerJump='true'">
        <xsl:variable name="calcPage"><xsl:if test="$rowCount = 0">1</xsl:if><xsl:if test="$rowCount != 0"><xsl:value-of select="floor(($firstResult + $pageSize) div $pageSize)"/></xsl:if></xsl:variable>
        <li page="jump">
          <input id="{$thisComp}Go_{$uid}" type="text" class="text go" value="{$calcPage}" size="4"/>
          <button type="button" class="button go" sib="{$thisComp}Go_{$uid}">Go</button>
        </li>
      </xsl:if>
      <li page="next">
        <img src="{$context}img/arrow-right_12.gif" />
      </li>
      <li page="last">
        <img src="{$context}img/arrow-last_12.gif" />
      </li>
    </ul>
    <!-- <span><xsl:value-of select="$firstResult"/>:<xsl:value-of select="$rowCount"/>:<xsl:value-of select="$pageSize"/></span> -->
    <xsl:variable name="calcTotal"><xsl:value-of select="$firstResult + $pageSize + 1"/></xsl:variable>
    <xsl:variable name="calcStart"><xsl:if test="$rowCount = 0">0</xsl:if><xsl:if test="$rowCount != 0"><xsl:value-of select="$firstResult + 1"/></xsl:if></xsl:variable>
    <span class="pageinfo">
      <xsl:if test="$calcTotal &gt; $rowCount">
        <xsl:value-of select="$calcStart"/> - <xsl:value-of select="$rowCount"/> of <xsl:value-of select="$rowCount"/>
      </xsl:if>
      <xsl:if test="$calcTotal &lt;= $rowCount">
        <xsl:value-of select="$calcStart"/> - <xsl:value-of select="$firstResult + $pageSize"/> of <xsl:value-of select="$rowCount"/>
      </xsl:if>
    </span>
    <xsl:if test="$showPageSize = 'true' and $uid='head'">
      <span class="pagesize">
        <span># of Items:</span>
        <select id="{$thisComp}.pageSize_{$uid}">
          <option value="5"><xsl:if test="$pageSize=5"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if>5</option>
          <option value="10"><xsl:if test="$pageSize=10"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if>10</option>
          <option value="20"><xsl:if test="$pageSize=20"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if>20</option>
          <option value="50"><xsl:if test="$pageSize=50"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if>50</option>
          <option value="100"><xsl:if test="$pageSize=100"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if>100</option>
        </select>
      </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="toplinks">
    <xsl:param name="thisComp"/>
    <xsl:if test="count(xalan:nodeset($links)/toplinks/toplink) &gt; 0">
      <div class="toplinks">
        <span>Select:</span>
        <!-- <xsl:apply-templates select="xalan:nodeset($links)/toplinks/toplink"/> -->
        <xsl:for-each select="xalan:nodeset($links)/toplinks/toplink">
          <xsl:call-template name="toplink">
            <xsl:with-param name="link" select="."/>
            <xsl:with-param name="thisComp" select="$thisComp"/>
          </xsl:call-template>
        </xsl:for-each>
      </div>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="toplink">
    <xsl:param name="link"/>
    <xsl:param name="thisComp"/>
    <a class="toplink" href="javascript:void(0);" action="{$link/@action}" selector="{$link/@selector}" value="{$link/@value}" comp="{$thisComp}"><xsl:value-of select="$link/@name" /></a>
    <xsl:if test="position()!=last()"> / </xsl:if>
  </xsl:template>

  <xsl:template name="actionlink">
    <xsl:param name="currentNode"/>
    <xsl:param name="action"/>
    <xsl:param name="thisComp"/>
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$action/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal" select="dyn:evaluate($selectPath)"/>
    <xsl:variable name="showAction">
      <xsl:choose>
        <xsl:when test="not($action/@selector)">true</xsl:when>
        <xsl:when test="not($action/@compare) and $action/@value=$nodeVal">true</xsl:when>
        <xsl:when test="$action/@compare = 'greater' and $nodeVal &gt; $action/@value">true</xsl:when>
        <xsl:when test="$action/@compare = 'lesser' and $nodeVal &lt; $action/@value">true</xsl:when>
        <xsl:otherwise>false</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!--
    NodeVal: <xsl:value-of select="$nodeVal"/><br/>
    Value  : <xsl:value-of select="$action/@value"/><br/>
    Compare: <xsl:value-of select="$action/@compare"/>
    -->
    <xsl:if test="$showAction = 'true'">
      <a class="dtAction" comp="{$thisComp}">
        <xsl:if test="$action/@id"><xsl:attribute name="id"><xsl:value-of select="$action/@id"/></xsl:attribute></xsl:if>
        <xsl:if test="$action/@class"><xsl:attribute name="class">dtAction <xsl:value-of select="$action/@class"/></xsl:attribute></xsl:if>
        <xsl:if test="$action/@title"><xsl:attribute name="title"><xsl:value-of select="$action/@title"/></xsl:attribute></xsl:if>
        <xsl:attribute name="guid">
          <xsl:choose>
            <xsl:when test="$action/@guidPath">
              <xsl:call-template name="evaluate">
                <xsl:with-param name="currentNode" select="$currentNode"/>
                <xsl:with-param name="evalPath">$currentNode/<xsl:value-of select="$action/@guidPath"/></xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$currentNode/guid | $currentNode/id"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:choose>
          <xsl:when test="starts-with(@action,'javascript:')">
            <xsl:attribute name="href">javascript:void(0);</xsl:attribute>
            <xsl:attribute name="onclick"><xsl:value-of select="$action/@action"/></xsl:attribute>
          </xsl:when>
          <xsl:when test="starts-with(@action,'ajax:')">
            <xsl:attribute name="href">javascript:void(0);</xsl:attribute>
            <xsl:attribute name="ajaxAction"><xsl:value-of select="substring-after($action/@action,'ajax:')"/></xsl:attribute>
            <xsl:attribute name="class">dtAction ajaxAction <xsl:if test="$action/@class"> <xsl:value-of select="$action/@class"/></xsl:if></xsl:attribute>
          </xsl:when>
          <xsl:when test="starts-with(@action,'pro:')">
            <xsl:attribute name="href">javascript:void(0);</xsl:attribute>
            <xsl:attribute name="ajaxAction"><xsl:value-of select="substring-after($action/@action,'pro:')"/></xsl:attribute>
            <xsl:attribute name="class">dtAction <xsl:if test="$action/@class"> <xsl:value-of select="$action/@class"/></xsl:if></xsl:attribute>
          </xsl:when>
          <xsl:when test="starts-with(@action,'popup:')">
            <xsl:attribute name="href">javascript:void(0);</xsl:attribute>
            <xsl:attribute name="ajaxAction"><xsl:value-of select="substring-after($action/@action,'popup:')"/></xsl:attribute>
            <xsl:attribute name="class">dtAction popupAction <xsl:if test="$action/@class"> <xsl:value-of select="$action/@class"/></xsl:if></xsl:attribute>
          </xsl:when>
          <xsl:when test="starts-with(@action,'confirm:')">
            <xsl:attribute name="href">javascript:void(0);</xsl:attribute>
            <xsl:variable name="action0"><xsl:value-of select="substring-after($action/@action,'confirm:')"/></xsl:variable>
            <xsl:variable name="theAction">
              <xsl:choose>
                <xsl:when test="contains(@action, '[guid]')"><xsl:value-of select="s:encodeURL(concat(substring-before($action0,'[guid]'),concat($currentNode/guid | $currentNode/id,substring-after($action0,'[guid]'))))"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="s:encodeURL($action0)"/></xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:attribute name="onclick">
              <xsl:choose>
                <xsl:when test="contains(@comp,'?')">
                  Toobs.Popup.popUp('','confirmationPopUp','<xsl:value-of select="$context"/><xsl:value-of select="@comp"/>&amp;action=<xsl:value-of select="$theAction"/>',220,450,150,300,true,true,'Toobs.Comp.ConfirmUpdatePopup.initConfirmPopup();');
                </xsl:when>
                <xsl:otherwise>
                  Toobs.Popup.popUp('','confirmationPopUp','<xsl:value-of select="$context"/><xsl:value-of select="@comp"/>?action=<xsl:value-of select="$theAction"/>',220,450,150,300,true,true,'Toobs.Comp.ConfirmUpdatePopup.initConfirmPopup();');
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="href">
              <xsl:choose>
                <xsl:when test="contains(@action, '[guid]')"><xsl:value-of select="concat(substring-before(@action,'[guid]'),concat($currentNode/guid | $currentNode/id,substring-after(@action,'[guid]')))"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="@action"/></xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="@name"/>
      </a><br/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="dtButtons">
    <xsl:param name="topbuttons"/>
    <xsl:param name="thisComp"/>
    <xsl:if test="$topbuttons/topbutton">
      <div class="dtButtons">
        <xsl:apply-templates select="$topbuttons/topbutton[@align='left']"/>
        <xsl:apply-templates select="$topbuttons/topbutton[@align='right']"/>
        <xsl:for-each select="$topbuttons/topbutton[@align='left']">
          <xsl:call-template name="topbutton">
            <xsl:with-param name="button" select="."/>
            <xsl:with-param name="thisComp" select="$thisComp"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:for-each select="$topbuttons/topbutton[@align='right']">
          <xsl:call-template name="topbutton">
            <xsl:with-param name="button" select="."/>
            <xsl:with-param name="thisComp" select="$thisComp"/>
          </xsl:call-template>
        </xsl:for-each>
      </div>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="topbutton">
    <xsl:param name="button"/>
    <xsl:param name="thisComp"/>
    <xsl:variable name="rightFlush">
      <xsl:if test="$button/@align='right' and position()=1"> flush</xsl:if>
    </xsl:variable>
    <button class="dtButton dt{$button/@align} button{$rightFlush}" comp="{$thisComp}" type="button">
      <xsl:if test="$button/@id"><xsl:attribute name="id"><xsl:value-of select="$button/@id"/></xsl:attribute></xsl:if>
      <xsl:if test="$button/@title"><xsl:attribute name="title"><xsl:value-of select="$button/@title"/></xsl:attribute></xsl:if>
      <xsl:choose>
        <xsl:when test="starts-with($button/@action,'javascript:')">
          <xsl:attribute name="onclick"><xsl:value-of select="$button/@action"/></xsl:attribute>
        </xsl:when>
        <xsl:when test="starts-with($button/@action,'ajax:')">
          <xsl:attribute name="ajaxAction"><xsl:value-of select="substring-after($button/@action,'ajax:')"/></xsl:attribute>
          <xsl:attribute name="class">dtButton ajaxButton dt<xsl:value-of select="$button/@align"/> button<xsl:value-of select="$rightFlush"/></xsl:attribute>
        </xsl:when>
        <xsl:when test="starts-with($button/@action,'confirm:')">
          <xsl:attribute name="href">javascript:void(0);</xsl:attribute>
          <xsl:variable name="theAction"><xsl:value-of select="substring-after($button/@action,'confirm:')"/></xsl:variable>
          <!--
          <xsl:variable name="theAction">
            <xsl:choose>
              <xsl:when test="contains(@action, '[guid]')"><xsl:value-of select="s:encodeURL(concat(substring-before($action0,'[guid]'),concat($currentNode/guid | $currentNode/id,substring-after($action0,'[guid]'))))"/></xsl:when>
              <xsl:otherwise><xsl:value-of select="s:encodeURL($action0)"/></xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          -->
          <xsl:attribute name="onclick">Toobs.Popup.popUp('','confirmationPopUp','<xsl:value-of select="$context"/><xsl:value-of select="@comp"/>?action=<xsl:value-of select="$theAction"/>',220,450,150,300,true,true,'Toobs.Comp.ConfirmUpdatePopup.initConfirmPopup();');</xsl:attribute>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
      <xsl:value-of select="$button/@name"/>
    </button>
  </xsl:template>
  
</xsl:stylesheet>