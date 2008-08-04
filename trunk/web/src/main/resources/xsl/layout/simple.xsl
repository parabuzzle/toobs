<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:c="org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="c"
  exclude-result-prefixes="c">

  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:param name="context"/>
  <xsl:param name="pageController"/>
  <xsl:param name="toobs.debug"/>
  
  <xsl:template match="RuntimeLayout" mode="standardLayout">
    <html>
      <head>

      </head>
      
      <body>
        <xsl:if test="/RuntimeLayout/ContentParams/Parameter[@name = 'bodyClass']">
          <xsl:attribute name="class"><xsl:value-of select="/RuntimeLayout/ContentParams/Parameter[@name = 'bodyClass']/@path"/></xsl:attribute>
        </xsl:if>
        <div id="loading" style="display: none;"/>
        <div id="loadingMsg" style="display: none;">
          <div class="msg">Loading...</div>
        </div>
        
        <div class="securityDebug" style="display:none;">
          <span>Page:   <xsl:value-of select="$layoutId"/></span>
          <span>Person: <xsl:value-of select="$personId"/></span>
          <span>Access: <xsl:value-of select="$pageAccess"/></span>
          <span>ContextPath: <xsl:value-of select="$appContext"/></span>
        </div>
        <input type="hidden" id="access.personId" value="{$personId}"/>
        <div id="master">
          <div id="content">
            <div id="headwrap">
              <xsl:apply-templates select="./Section[@id='header']"/>
            </div>
            <div id="menuwrap">
              <xsl:apply-templates select="./Section[@id='menu']"/>
            </div>
            <div id="widewrap">
              <xsl:apply-templates select="./Section[@type='wide']">
                <xsl:sort select="@order"/>
              </xsl:apply-templates>
            </div>
          </div>
        </div>
        <p style="clear:both;"/>
        <div id="footwrap">
          <xsl:apply-templates select="./Section[@id='footer']"/>
        </div>
        <xsl:if test="$toobs.debug='true'">
          <div id="debugContainer">
            <div class="iconWrap">
              <img class="debugClosed" id="debugIcon" src="{$context}img/disclose-closed_12.gif"/>
              <img id="clearIcon" src="{$context}img/delete_user_12.gif" style="display:none;"/>
            </div>
            <ul id="toobsDebug" style="display:none;"/>
          </div>
          <script type="text/javascript">Toobs.debugPanel = $('toobsDebug');</script>
        </xsl:if>
      </body>
    </html>        
  </xsl:template>
  
  <xsl:template match="Section">
    <xsl:apply-templates select="node()" >
      <xsl:sort select="@order"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="componentRef[loader/@type='0']">
    <xsl:value-of select="compHelper:componentRef(@componentId, 'xhtml', /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
  </xsl:template>
  <xsl:template match="componentLayoutRef[loader/@type='0']">
    <xsl:value-of select="compHelper:componentLayoutRef(@layoutId, /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
  </xsl:template>
  
  <xsl:template match="componentRef[loader/@type='1']">
    <div id="{@componentId}Frame">
      <xsl:value-of select="compHelper:componentRef(@componentId, 'xhtml', /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
    </div>
  </xsl:template>
</xsl:stylesheet>