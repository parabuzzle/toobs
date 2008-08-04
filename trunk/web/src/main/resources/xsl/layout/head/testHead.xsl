<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="pageTitle"/>
  <xsl:param name="toobs.debug"/>
  <xsl:param name="clientName" select="'Boulder Technology Capital'"/>
  <xsl:param name="appContext"/>
  <xsl:param name="deployTime"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <title>Test Layout</title>
    <!--
      <link href="{$context}css/widgets.css?dt={$deployTime}" type="text/css" rel="stylesheet" />
      <link href="{$context}css/site/wiki.css?dt={$deployTime}" type="text/css" rel="stylesheet" />
      <link href="{$context}javascript/jscalendar/calendar-blue.css?dt={$deployTime}" type="text/css" rel="stylesheet"/>
      <xsl:comment><![CDATA[[if lte IE 7]>
      <link href="]]><xsl:value-of select="$context"/><![CDATA[css/site/iefix.css?dt=]]><xsl:value-of select="$deployTime"/><![CDATA[" type="text/css" rel="stylesheet" />
      <![endif]]]></xsl:comment>
      <xsl:comment><![CDATA[[if gte IE 7]>
      <link href="]]><xsl:value-of select="$context"/><![CDATA[css/site/ie7fix.css?dt=]]><xsl:value-of select="$deployTime"/><![CDATA[" type="text/css" rel="stylesheet" />
      <![endif]]]></xsl:comment>
      
      <script type="text/javascript">var context = "<xsl:value-of select="$context"/>"; var deployTime=<xsl:value-of select="$deployTime"/>;</script>
      <script type="text/javascript" src="{$context}javascript/lib/prototype.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
      <script type="text/javascript" src="{$context}javascript/lib/scriptaculous.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
      <script type="text/javascript" src="{$context}javascript/Toobs.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
      <xsl:apply-templates select="./AppearanceHeadVO"/>
    <style type="text/css">
      html, body {
      height:100%;
      margin:0px;
      overflow:hidden;
      width:100%;
      }
    </style>
    -->
    <xsl:if test="$toobs.debug='true'">
      <link href="{$context}css/site/debug.css?dt={$deployTime}" type="text/css" rel="stylesheet" />
      <script type="text/javascript">Toobs.isDebugEnabled=true;Toobs.Controller.useComp("Debug");</script>
    </xsl:if>
    
  </xsl:template>
  <xsl:template match="AppearanceHeadVO">
    <link href="{./cssPath}" type="text/css" rel="stylesheet"/>
    <style type="text/css">
      <xsl:if test="$appContext='website'">
        #maincontainer {border-left: 0;border-right: 0; }
      </xsl:if>
    </style>
  </xsl:template>
</xsl:stylesheet>