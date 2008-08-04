<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  extension-element-prefixes="compHelper"
  exclude-result-prefixes="compHelper">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="pageAccess"/>
  <xsl:param name="personId"/>
  <xsl:param name="hideMenu"/>
  <xsl:param name="appContext"/>
  <xsl:param name="pageController"/>
  <xsl:param name="toobs.debug"/>
  
  <xsl:template match="RuntimeLayout" mode="testLayout">
    <xsl:variable name="noMenu">
      <xsl:choose>
        <xsl:when test="$appContext='website'">true</xsl:when>
        <xsl:otherwise><xsl:value-of select="$hideMenu"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <html>
      <head>
        <xsl:apply-templates select="./Section[@id='head']"/>
      </head>
      
      <body>
        <div style="height:1000px;">
          
        </div>
        <xsl:if test="$toobs.debug='true'">
          <div id="debugContainer">
            <div class="iconWrap">
              <img class="debugClosed" id="debugIcon" src="{$context}img/disclose-closed_12.gif"/>
            </div>
            <ul id="toobsDebug" style="display:none;"/>
          </div>
          <script type="text/javascript">
            Toobs.Controller.useComp("Debug");
          </script>
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