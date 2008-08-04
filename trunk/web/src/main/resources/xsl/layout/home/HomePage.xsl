<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  extension-element-prefixes="compHelper"
  exclude-result-prefixes="compHelper">
  
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>

  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">

    <xsl:if test="./PersonPreferenceVO">
      <xsl:apply-templates select="./PersonPreferenceVO/PortletItemVO"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="PortletItemVO">
    <fieldset class="inner">
      <div class="fieldset">
        <fieldset class="normal">
          <p class="cue">
            <xsl:value-of select="compHelper:componentUrl (./actionText, 'xhtml')" disable-output-escaping="yes" />
          </p>
        </fieldset>
      </div>
    </fieldset>
  </xsl:template>
  
</xsl:stylesheet>