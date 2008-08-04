<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:s="org.toobs.framework.transformpipeline.xslExtentions.XMLEncodeHelper"><xsl:output method="text"/><xsl:param name="context"/><xsl:param name="host"></xsl:param><xsl:template match="component"><tok><xsl:apply-templates select="objects"/></tok></xsl:template>
  <xsl:template match="objects">
    <xsl:value-of select="s:stripTags(./EmailTemplateVO/body,true())"/>
    
    <xsl:apply-templates select="./InfoRequestRecordDocVO/doc"/>
  </xsl:template>
  <xsl:template match="doc">
    <xsl:value-of select="./docLabel"/>: http://<xsl:value-of select="$host"/><xsl:value-of select="$context"/>attachment/<xsl:value-of select="./document/guid"/>
  </xsl:template>
</xsl:stylesheet>