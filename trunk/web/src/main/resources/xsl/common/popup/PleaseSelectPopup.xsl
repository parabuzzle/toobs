<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="type"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div class="ibody">
      <div class="popupTitle" id="pagetitle">Please Select a <xsl:value-of select="$type"/></div>
      <button class="button" id="closeSelectPopup" type="button" style="margin: 0 43%">Close</button>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
