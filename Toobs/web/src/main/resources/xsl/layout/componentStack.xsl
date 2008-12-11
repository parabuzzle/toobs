<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  extension-element-prefixes="compHelper">
  <xsl:output method="html"/>
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  
  <xsl:template match="RuntimeLayout">
    <xsl:apply-templates select="./Section">
      <xsl:sort select="@order"/>
    </xsl:apply-templates>
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