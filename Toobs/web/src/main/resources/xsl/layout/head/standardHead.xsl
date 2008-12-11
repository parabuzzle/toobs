<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="pageTitle"/>
  <xsl:param name="toobs.debug"/>
  <xsl:param name="deployTime"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <title><xsl:value-of select="$pageTitle"/></title>

    <link href="{$context}css/base/baseStyle.css?dt={$deployTime}" type="text/css" rel="stylesheet" />
    <link href="{$context}javascript/jscalendar/calendar-blue.css?dt={$deployTime}" type="text/css" rel="stylesheet"/>
    <xsl:comment><![CDATA[[if lte IE 7]>
    <link href="]]><xsl:value-of select="$context"/><![CDATA[css/site/iestyle.css?dt=]]><xsl:value-of select="$deployTime"/><![CDATA[" type="text/css" rel="stylesheet" />
    <![endif]]]></xsl:comment>
    <xsl:comment><![CDATA[[if lte IE 6]>
    <link href="]]><xsl:value-of select="$context"/><![CDATA[css/site/ie6style.css?dt=]]><xsl:value-of select="$deployTime"/><![CDATA[" type="text/css" rel="stylesheet" />
    <![endif]]]></xsl:comment>
    <xsl:comment><![CDATA[[if gte IE 7]>
    <link href="]]><xsl:value-of select="$context"/><![CDATA[css/site/ie7style.css?dt=]]><xsl:value-of select="$deployTime"/><![CDATA[" type="text/css" rel="stylesheet" />
    <![endif]]]></xsl:comment>
    
    <script type="text/javascript">var context = "<xsl:value-of select="$context"/>"; var deployTime=<xsl:value-of select="$deployTime"/>;</script>
    <script type="text/javascript" src="{$context}javascript/lib/prototype.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
    <script type="text/javascript" src="{$context}javascript/lib/scriptaculous.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
    <script type="text/javascript" src="{$context}javascript/Toobs.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
    <xsl:if test="$toobs.debug='true'">
      <link href="{$context}css/base/debug.css?dt={$deployTime}" type="text/css" rel="stylesheet" />
      <script type="text/javascript">Toobs.isDebugEnabled=true;Toobs.Controller.useComp("Debug");</script>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>