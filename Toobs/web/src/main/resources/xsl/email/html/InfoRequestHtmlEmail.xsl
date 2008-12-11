<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="host"/>
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div>
    <unescape>
      <xsl:value-of select="./EmailTemplateVO/body"/>
    </unescape>
    </div>
    <div>
      <xsl:apply-templates select="./InfoRequestRecordDocVO/doc"/>
    </div>
  </xsl:template>
  <xsl:template match="doc">
    <a href="http://{$host}{$context}attachment/{./document/guid}" title="{./docLabel}"><xsl:value-of select="./docLabel"/></a>
  </xsl:template>
</xsl:stylesheet>