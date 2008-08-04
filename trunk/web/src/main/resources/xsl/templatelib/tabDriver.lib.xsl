<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:dyn="http://exslt.org/dynamic"
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  exclude-result-prefixes="xalan dyn compHelper">
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="component"/>
  
  <xsl:param name="tabs"/>
  <xsl:param name="tabstyle"/>
  <xsl:param name="tabdefault"/>

  <xsl:variable name="activeTab" select="compHelper:getParam(concat($component,'Tab'),$tabdefault)"/>
  
  <xsl:template name="tabBar">
    <xsl:variable name="tabsNode" select="xalan:nodeset($tabs)/tabs"/>
    <xsl:variable name="emWidth" select="substring($tabsNode/@class,2)"/>
    <div class="TabButtonWrapper">
      <div>
        <xsl:choose>
          <xsl:when test="$tabstyle='silverslant'">
            <xsl:attribute name="class">stab silvertab</xsl:attribute>
          </xsl:when>
          <xsl:when test="$tabstyle='silverblock'">
            <xsl:attribute name="class">btab silverblock</xsl:attribute>
          </xsl:when>
        </xsl:choose>
        <ul class="tabs {$tabsNode/@class}" preEval="{$tabsNode/@preEval}">
          <xsl:variable name="widthCount">
            <xsl:choose>
              <xsl:when test="$tabsNode/@max"><xsl:value-of select="$tabsNode/@max"/></xsl:when>
              <xsl:otherwise><xsl:value-of select="count($tabsNode/tab)"/></xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:variable name="widthMod">
            <xsl:choose>
              <xsl:when test="$tabsNode/@mod"><xsl:value-of select="$tabsNode/@mod"/></xsl:when>
              <xsl:otherwise>6</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <!-- <xsl:if test="$emWidth != ''"><xsl:attribute name="style">width:<xsl:value-of select="(number($emWidth)+number($widthMod)) * $widthCount"/>em;</xsl:attribute></xsl:if> -->
          <xsl:apply-templates select="$tabsNode/tab"/>
        </ul>
      </div>
    </div>  
    <!--
      <span>Default: <xsl:value-of select="$tabdefault"/></span>
      <span>Active: <xsl:value-of select="$activeTab"/></span>
      <span>emWidth: <xsl:value-of select="$emWidth"/></span>
    -->
    <div id="{$component}Content" class="TabContainer">
      <xsl:choose>
        <xsl:when test="$tabsNode/@mode='ajax'">
          <xsl:attribute name="class">TabContainer ajaxComponent runOnce doPage</xsl:attribute>
          <xsl:choose>
            <xsl:when test="xalan:nodeset($tabs)/tabs/tab[@name=$activeTab]/@comp">
              <xsl:attribute name="componentUrl">
                <xsl:value-of select="xalan:nodeset($tabs)/tabs/tab[@name=$activeTab]/@comp"/>
              </xsl:attribute>
              <xsl:attribute name="postEval">
                <xsl:value-of select="xalan:nodeset($tabs)/tabs/tab[@name=$activeTab]/@postEval"/>
              </xsl:attribute>
            </xsl:when>
            <xsl:when test="xalan:nodeset($tabs)/tabs/tab[position()=1]">
              <xsl:attribute name="componentUrl">
                <xsl:value-of select="xalan:nodeset($tabs)/tabs/tab[position()=1]/@comp"/>
              </xsl:attribute>
              <xsl:attribute name="postEval">
                <xsl:value-of select="xalan:nodeset($tabs)/tabs/tab[position()=1]/@postEval"/>
              </xsl:attribute>
            </xsl:when>
            <xsl:otherwise/>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="xalan:nodeset($tabs)/tabs/tab[@name=$activeTab]/@comp">
              <xsl:value-of select="compHelper:componentUrl(xalan:nodeset($tabs)/tabs/tab[@name=$activeTab]/@comp, 'xhtml')" disable-output-escaping="yes" />
            </xsl:when>
            <xsl:when test="xalan:nodeset($tabs)/tabs/tab[position()=1]">
              <xsl:value-of select="compHelper:componentUrl(xalan:nodeset($tabs)/tabs/tab[position()=1]/@comp, 'xhtml')" disable-output-escaping="yes" />
            </xsl:when>
            <xsl:otherwise/>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>
  <xsl:template match="tab">
    <li class="li1" component="{$component}">
      <xsl:if test="@id"><xsl:attribute name="id" select="@id"/></xsl:if>
      <xsl:attribute name="class"><xsl:if test="@name=$activeTab">active </xsl:if>li<xsl:value-of select="position()"/><xsl:if test="../@class"><xsl:text> </xsl:text><xsl:value-of select="../@class"/></xsl:if><xsl:if test="position()=last()"><xsl:text> </xsl:text>last</xsl:if></xsl:attribute>
      <a id="{$component}-a-{position()}" class="tablink" href="javascript:void(0);" comp="{@comp}" preEval="{@preEval}" postEval="{@postEval}" title="{@name}">
        <xsl:choose>
          <xsl:when test="@display"><xsl:value-of select="@display"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
        </xsl:choose>
      </a>
    </li>
  </xsl:template>
  
</xsl:stylesheet>