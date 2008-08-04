<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  
  <xsl:import href="templatelib/conversion.lib.xsl"/>
  <xsl:import href="templatelib/htmlTable.lib.xsl"/>
  <xsl:import href="templatelib/pdfTable.lib.xsl"/>
  <xsl:import href="templatelib/xlsStyles.lib.xsl"/>
  <xsl:import href="templatelib/xlsTable.lib.xsl"/>
  
  <xsl:param name="outputFormat"/>
  
  <xsl:template name="displayTable">
    <xsl:param name="rowData"/>
    <xsl:param name="cssClass"/>
    <xsl:param name="tableComp"/>
    <xsl:param name="totalCount"/>
    <xsl:choose>
      <xsl:when test="$outputFormat='xpdf'">
        <xsl:call-template name="pdfTable">
          <xsl:with-param name="rowData" select="$rowData"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$outputFormat='xxls'">
        <xsl:call-template name="xlsTable">
          <xsl:with-param name="rowData" select="$rowData"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$outputFormat='xrtf'">
        <xsl:call-template name="pdfTable">
          <xsl:with-param name="rowData" select="$rowData"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>        
        <xsl:call-template name="htmlTable">
          <xsl:with-param name="rowData" select="$rowData"/>
          <xsl:with-param name="tableComp" select="$tableComp"/>
          <xsl:with-param name="totalCount" select="$totalCount"/>
          <xsl:with-param name="cssClass" select="$cssClass"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>