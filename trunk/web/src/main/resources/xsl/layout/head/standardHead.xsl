<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="pageTitle"/>
  <xsl:param name="toobs.debug"/>
  <xsl:param name="clientName"/>
  <xsl:param name="appContext"/>
  <xsl:param name="deployTime"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <title><xsl:value-of select="$clientName"/><xsl:if test="$pageTitle">::<xsl:value-of select="$pageTitle"/></xsl:if></title>
    <meta name="description" content="ASocial"/>

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
    
    <link href="{$context}css/pap/pap.css?dt={$deployTime}" type="text/css" rel="stylesheet" />
    
    
    <script type="text/javascript">var context = "<xsl:value-of select="$context"/>"; var deployTime=<xsl:value-of select="$deployTime"/>;</script>
    <script type="text/javascript" src="{$context}javascript/lib/prototype.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
    <script type="text/javascript" src="{$context}javascript/lib/scriptaculous.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
    <script type="text/javascript" src="{$context}javascript/Toobs.js?dt={$deployTime}"><xsl:comment></xsl:comment></script>
    <xsl:if test="$toobs.debug='true'">
      <link href="{$context}css/base/debug.css?dt={$deployTime}" type="text/css" rel="stylesheet" />
      <script type="text/javascript">Toobs.isDebugEnabled=true;Toobs.Controller.useComp("Debug");</script>
    </xsl:if>
    <xsl:apply-templates select="./AppearanceHeadVO"/>
    <style type="text/css">
      .menuToggle { filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='<xsl:value-of select="$context"/>img/menu/left-nav-hide.png', sizingMethod='scale'); }
      .menuHidden { filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='<xsl:value-of select="$context"/>img/menu/left-nav-show.png', sizingMethod='scale'); }
      /* Window classes */
      * html .mac_os_x_nw { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/TL_Main.png", sizingMethod="crop"); }
      * html .mac_os_x_n  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/T_Main.png", sizingMethod="scale"); }
      * html .mac_os_x_ne { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/TR_Main.png", sizingMethod="crop"); }
      * html .mac_os_x_w  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/L_Main.png", sizingMethod="scale"); }
      * html .mac_os_x_e  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/R_Main.png", sizingMethod="scale"); }
      * html .mac_os_x_sw { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/BL_Main.png", sizingMethod="crop"); }
      * html .mac_os_x_s  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/B_Main.png", sizingMethod="scale"); }
      * html .mac_os_x_se { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/BR_Main.png", sizingMethod="crop"); }
      * html .mac_os_x_sizer { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/BR_Main.png", sizingMethod="crop"); }
      
      * html .blur_os_x_nw { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/TL.png", sizingMethod="crop"); }
      * html .blur_os_x_n  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/T.png", sizingMethod="scale"); }
      * html .blur_os_x_ne { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/TR.png", sizingMethod="crop"); }
      * html .blur_os_x_w  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/L.png", sizingMethod="scale"); }
      * html .blur_os_x_e  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/R.png", sizingMethod="scale"); }
      * html .blur_os_x_sw { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/themes/mac_os_x/BL.png", sizingMethod="crop"); }
      * html .blur_os_x_s  { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/themes/mac_os_x/B.png", sizingMethod="scale"); }
      * html .blur_os_x_se { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/BR.png", sizingMethod="crop"); }
      * html .blur_os_x_sizer { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="<xsl:value-of select="$context"/>img/mac_os_x/BR.png", sizingMethod="crop"); }
    </style>
    <!--
      <link href="{$context}css/test/appearance-test.css" type="text/css" rel="stylesheet" />
    -->
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