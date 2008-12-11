<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:dyn="http://exslt.org/dynamic"
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  exclude-result-prefixes="xalan dyn compHelper">
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="component"/>
  
  <xsl:param name="mode">edit</xsl:param>
  <xsl:param name="listWidth">230</xsl:param>
  <xsl:param name="resizable">false</xsl:param>
  <xsl:param name="showOnClick">false</xsl:param>

  <xsl:param name="columns"/>
  
  <xsl:template name="treeList">
    <xsl:variable name="columnsNode" select="xalan:nodeset($columns)/columns"/>
    <table class="threeC" cellpadding="0" cellspacing="0" style="width:100%;">
      <thead>
        <tr>
          <xsl:apply-templates select="$columnsNode/column" mode="head"/>
        </tr>
      </thead>
      <tbody>
        <tr>
          <xsl:apply-templates select="$columnsNode/column" mode="body"/>
        </tr>
      </tbody>
      <tfoot>
        <tr>
          <xsl:apply-templates select="$columnsNode/column" mode="foot"/>
        </tr>
        <xsl:call-template name="footerCallback"/>
      </tfoot>
    </table>
  </xsl:template>
  
  <xsl:template name="footerCallback"/>
    
  <xsl:template match="column" mode="head">
    <th class="treeListTh">
      <span id="treeHead_{position()}">
        <xsl:attribute name="style"><xsl:if test="$showOnClick='true' and position() &gt; 1">display:none;</xsl:if></xsl:attribute>
        <xsl:value-of select="@title"/>
      </span>
    </th>
    <xsl:if test="position() != last()">
      <th>
        <xsl:if test="$resizable='true'"><xsl:attribute name="style">width:6px;</xsl:attribute></xsl:if>
        <span/>
      </th>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="column" mode="body">
    <td class="treeCol" id="treeCol_{position()}">
      <xsl:attribute name="style"><xsl:if test="$listWidth != ''">width:<xsl:value-of select="$listWidth + 2"/>px;</xsl:if></xsl:attribute>
      <div id="treeList_{position()}" class="treeListCnt" comp="{@comp}" showOnClick="{$showOnClick}" mode="{$mode}">
        <xsl:attribute name="style"><xsl:if test="$showOnClick='true' and position() &gt; 1">display:none;</xsl:if><xsl:if test="$listWidth != ''">width:<xsl:value-of select="$listWidth"/>px;</xsl:if></xsl:attribute>
        <xsl:if test="position() = 1">
          <xsl:value-of select="compHelper:componentUrl(@comp, 'xhtml')" disable-output-escaping="yes" />
        </xsl:if>
      </div>
    </td>
    <xsl:if test="position() != last()">
      <td>
        <div id="treeDiv_{position()}" class="treeListDiv">
          <xsl:if test="$resizable='true'"><xsl:attribute name="class">treeSizeDiv</xsl:attribute></xsl:if>
          <xsl:attribute name="style"><xsl:if test="$showOnClick='true' and position() &gt; 1">display:none;</xsl:if></xsl:attribute>
          <xsl:if test="$resizable='true'">
            <img id="treeSize_{position()}" class="treeSizer" src="{$context}img/drag-sizer.png" alt="" style="width:6px;"/>
          </xsl:if>
        </div>
      </td>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="column" mode="foot">
    <td>
      <div id="treeList_{position()}_links" type="{@type}" typeDisplay="{@typeDisplay}" focusInput="{@focusInput}" listNum="{position()}">
        <xsl:attribute name="style"><xsl:if test="position() != 1 or $mode!='edit'">display:none;</xsl:if><xsl:if test="@linkHide='true'">visibility:hidden;</xsl:if></xsl:attribute>
        <xsl:if test="@popHeight"><xsl:attribute name="popHeight"><xsl:value-of select="@popHeight"/></xsl:attribute></xsl:if>
        <xsl:if test="@popWidth"><xsl:attribute name="popWidth"><xsl:value-of select="@popWidth"/></xsl:attribute></xsl:if>
        <xsl:variable name="colPos" select="position()"/>
        <xsl:for-each select="./link">
          <xsl:call-template name="link">
            <xsl:with-param name="link" select="./link"/>
            <xsl:with-param name="pos" select="$colPos"/>
          </xsl:call-template>
        </xsl:for-each>
      </div>
    </td>
    <xsl:if test="position() != last()">
      <td><span/></td>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="link">
    <xsl:param name="link"/>
    <xsl:param name="pos"/>
    <a href="javascript:void(0)" id="treeList_{@action}_{$pos}" comp="{@comp}">
      <xsl:value-of select="@title"/>
    </a>
    <xsl:if test="position() != last()">
      / 
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>