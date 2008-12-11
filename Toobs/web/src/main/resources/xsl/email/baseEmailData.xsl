<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:template match="emaildata">
    <xsl:apply-templates select="EmailContent"/>    
  </xsl:template>  
  <xsl:template match="EmailContent">
    <xsl:apply-templates select="@* | node()" mode="content"/>
  </xsl:template>
  <xsl:template match="node() | @*" mode="content">
    <xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
  </xsl:template>
  
  <xsl:template match="data[@type='contact.firstName']">
    <xsl:value-of select="/emaildata/component/objects/ContactVO/firstName"/>    
  </xsl:template>  
  <xsl:template match="data[@type='contact.lastName']">
    <xsl:value-of select="/emaildata/component/objects/ContactVO/lastName"/>    
  </xsl:template>  
  <xsl:template match="data[@type='record.firstName']">
    <xsl:value-of select="/emaildata/objects/InfoRequestRecordVO/firstName"/>    
  </xsl:template>  
  <xsl:template match="data[@type='record.lastName']">
    <xsl:value-of select="/emaildata/objects/InfoRequestRecordVO/lastName"/>    
  </xsl:template>  
</xsl:stylesheet>