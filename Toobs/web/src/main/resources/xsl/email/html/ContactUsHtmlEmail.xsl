<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:c="org.toobs.framework.pres.component.util.ComponentHelper"
  extension-element-prefixes="c"
  exclude-result-prefixes="c">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div>
      <unescape>
        <!-- <xsl:value-of select="./EmailTemplateVO/body"/> -->
        <xsl:value-of select="c:transformEmail('email/baseEmailData',./EmailTemplateVO/body,/)"/>
      </unescape>
    </div>
  </xsl:template>  
</xsl:stylesheet>