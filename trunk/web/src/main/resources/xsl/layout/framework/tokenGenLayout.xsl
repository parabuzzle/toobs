<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"> 
  <xsl:output method="html" omit-xml-declaration="yes" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
  <xsl:param name="outputFormat"/>
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="tok">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="unescape">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
</xsl:stylesheet>