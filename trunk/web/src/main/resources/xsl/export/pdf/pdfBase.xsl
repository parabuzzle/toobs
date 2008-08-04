<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  extension-element-prefixes="compHelper"
  exclude-result-prefixes="compHelper">
  <xsl:output method="xml" version="1.0" encoding="UTF-8"/>
  <xsl:param name="context" select="'/'"/>
  <xsl:param name="exportMode"/>
  <xsl:param name="component"/>
  
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="RuntimeLayout">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:apply-templates select="./Section[@id='body']"/>
    </fo:root>
  </xsl:template>
  
  <xsl:template match="Section">
    <xsl:apply-templates select="node()" >
      <xsl:sort select="@order"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="componentRef[loader/@type='0' and $exportMode='table']">
    <xsl:value-of select="compHelper:componentRef($component, 'xpdf', /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
  </xsl:template>

  <xsl:template match="componentRef[loader/@type='0' and $exportMode='normal']">
    <xsl:value-of select="compHelper:componentRef(@componentId, 'xpdf', /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
  </xsl:template>
  
  <xsl:template match="componentLayoutRef[loader/@type='0' and $exportMode='normal']">
    <xsl:value-of select="compHelper:componentLayoutRef(@layoutId, /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
  </xsl:template>
  
</xsl:stylesheet>