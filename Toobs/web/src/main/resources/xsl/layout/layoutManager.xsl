<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:import href="layout/standardLayout.xsl"/>
  <xsl:import href="layout/testLayout.xsl"/>
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="pageAccess"/>
  <xsl:param name="personId"/>
  <xsl:param name="hideMenu"/>
  <xsl:param name="layoutStyle"/>
  <xsl:param name="appContext"/>
  
  <xsl:template match="RuntimeLayout">
    <xsl:choose>
      <xsl:when test="$layoutStyle='testMode'">
        <xsl:apply-templates select="." mode="testLayout"/>
      </xsl:when>
      <xsl:when test="$layoutStyle='businessPortal'"></xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="standardLayout"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>