<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"> 
  <xsl:output method="xml"/>
  <xsl:param name="outputFormat"/>
  <xsl:template match="node() | @*"><xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy></xsl:template>
  <xsl:template match="node()[name()='tok']"><xsl:apply-templates/></xsl:template>
  <xsl:template match="node()[name()='unescape']"><xsl:value-of select="." disable-output-escaping="yes"/></xsl:template>
  <xsl:template match="node()[name()='nl']"><xsl:value-of select="'&amp;'" disable-output-escaping="yes"/>#10;</xsl:template>
  <xsl:template match="node()[name()='br']">
    <xsl:choose>
      <xsl:when test="$outputFormat='xpdf'"><xsl:value-of select="'&lt;fo:block/&gt;'" disable-output-escaping="yes"/></xsl:when>
      <xsl:when test="$outputFormat='xrtf'"><xsl:value-of select="'&lt;fo:block/&gt;'" disable-output-escaping="yes"/></xsl:when>
      <xsl:when test="$outputFormat='xxls'"><xsl:value-of select="'&amp;'" disable-output-escaping="yes"/>#10;</xsl:when>
      <xsl:otherwise><xsl:copy/></xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="node()[name()='span']">
    <xsl:choose>
      <xsl:when test="$outputFormat='xpdf'">
        <xsl:value-of select="'&lt;fo:inline&gt;'" disable-output-escaping="yes"/>
        <xsl:apply-templates/>
        <xsl:value-of select="'&lt;/fo:inline&gt;'" disable-output-escaping="yes"/>
      </xsl:when>
      <xsl:when test="$outputFormat='xrtf'">
        <xsl:value-of select="'&lt;fo:inline&gt;'" disable-output-escaping="yes"/>
        <xsl:apply-templates/>
        <xsl:value-of select="'&lt;/fo:inline&gt;'" disable-output-escaping="yes"/>
      </xsl:when>
      <xsl:when test="$outputFormat='xxls'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise><xsl:copy/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>