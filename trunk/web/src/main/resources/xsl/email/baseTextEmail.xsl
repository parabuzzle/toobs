<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  extension-element-prefixes="compHelper"
  exclude-result-prefixes="compHelper">
  <xsl:output method="text"/>

  <xsl:param name="context"/>
  
  <xsl:template match="RuntimeLayout">
    <xsl:apply-templates select="./Section[@type='wide']">
      <xsl:sort select="@order"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="./Section[@id='footer']"/>
  </xsl:template>
  
  <xsl:template match="Section">
    <xsl:apply-templates select="node()" >
      <xsl:sort select="@order"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="componentRef[loader/@type='0']">
    <xsl:value-of select="compHelper:componentRef(@componentId, 'text', /RuntimeLayout/ContentParams/Parameter)" disable-output-escaping="yes" />
  </xsl:template>
  
</xsl:stylesheet>