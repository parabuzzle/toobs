<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="hideMenu"/>

  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div id="leftcol">
      <xsl:if test="$hideMenu='true'"><xsl:attribute name="style">display:none;</xsl:attribute></xsl:if>
      <div id="navbar" class="navbar">
        <xsl:if test="not(./MenuItemVO)">
          <xsl:attribute name="style">height:1em;</xsl:attribute>
        </xsl:if>
        <xsl:if test="./MenuItemVO">
          <xsl:apply-templates select="./MenuItemVO[parentWidgetId='']" mode="topitem">
            <xsl:sort select="./displayOrder" data-type="number"/>
          </xsl:apply-templates>
        </xsl:if>
      </div>
    </div>
    <div id="middlecol">
      <a href="javascript:void(0);" id="menuToggle">
        <xsl:attribute name="class">
          <xsl:if test="$hideMenu='true'">menuToggle menuHidden</xsl:if>
          <xsl:if test="$hideMenu='false'">menuToggle</xsl:if>
        </xsl:attribute>
      </a>
    </div>
  </xsl:template>
  <xsl:template match="MenuItemVO" mode="topitem">
    <div id="md-{./displayOrder}" class="mainDiv">
      <div id="ti-{./displayOrder}" class="topItem" dropMenu="dm-{./displayOrder}"><xsl:value-of select="./displayName"/></div>
      <div id="dm-{./displayOrder}" class="dropMenu">
        <div id="sm-{./displayOrder}" class="subMenu" style="display:inline;">
          <xsl:variable name="thisId" select="./guid"/>
          <xsl:apply-templates select="../MenuItemVO[parentWidgetId=$thisId]" mode="subitem">
            <xsl:sort select="./displayOrder" data-type="number"/>
          </xsl:apply-templates>
        </div>
      </div>
    </div>        
  </xsl:template>
  <xsl:template match="MenuItemVO" mode="subitem">
    <div class="subItem">
      &#9830;<a>
        <xsl:attribute name="href">
          <xsl:choose>
            <xsl:when test="./actionMode = '1'">
                <xsl:choose>
                  <xsl:when test="./actionText"><xsl:value-of select="$context"/><xsl:value-of select="./actionText"/></xsl:when>
                  <xsl:otherwise>javascript:void(0);</xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>#</xsl:otherwise>           
          </xsl:choose>
        </xsl:attribute>
        <xsl:choose>
          <xsl:when test="./actionMode = '2'">
            <xsl:attribute name="onClick">
              <xsl:value-of select="./actionText"/>
            </xsl:attribute>
          </xsl:when>  
        </xsl:choose>      
        <xsl:value-of select="./displayName"/>
      </a>
    </div>
    <xsl:variable name="thisId" select="./guid"/>
    <xsl:apply-templates select="../MenuItemVO[parentWidgetId=$thisId]" mode="sublistitem">
      <xsl:sort select="./displayOrder" data-type="number"/>
    </xsl:apply-templates>
  </xsl:template>
  <xsl:template match="MenuItemVO" mode="sublistitem">
    <div class="subListItem">
      <xsl:choose>
        <xsl:when test="./actionMode = '1'">
          <xsl:text> - </xsl:text><a href="{$context}{./actionText}"><xsl:value-of select="./displayName"/></a>
        </xsl:when>
        <xsl:when test="./actionMode = '2'">
          <xsl:text> - </xsl:text><a href="#" onClick="{./actionText}"><xsl:value-of select="./displayName"/></a>
        </xsl:when>
      </xsl:choose>
    </div>
  </xsl:template>
</xsl:stylesheet>