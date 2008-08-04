<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  extension-element-prefixes="compHelper"
  exclude-result-prefixes="compHelper">
  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:param name="context"/>
  
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" />
    </xsl:copy>
  </xsl:template>
  <xsl:template match="RuntimeLayout">
    <html>
      <head>
        <xsl:apply-templates select="./Section[@id='head']"/>
      </head>
      
      <body>
        <table cellpadding="0" cellspacing="0">
          <tr>
            <td>
              <xsl:apply-templates select="./Section[@id='header']"/>
            </td>
          </tr>
          <tr>
            <td>
              <xsl:apply-templates select="./Section[@type='wide']">
                <xsl:sort select="@order"/>
              </xsl:apply-templates>
            </td>
          </tr>
          <tr>
            <td>
              <xsl:apply-templates select="./Section[@id='footer']"/>
            </td>
          </tr>
        </table>
      </body>
    </html>        
  </xsl:template>
  
  <xsl:template match="Section">
    <xsl:apply-templates select="node()" >
      <xsl:sort select="@order"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="componentRef[loader/@type='0']">
    <xsl:value-of select="compHelper:componentRef(@componentId, 'xhtml', /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
  </xsl:template>
  <xsl:template match="componentLayoutRef[loader/@type='0']">
    <xsl:value-of select="compHelper:componentLayoutRef(@layoutId, /RuntimeLayout/ContentParams/Parameter | ./parameterMapping/parameter)" disable-output-escaping="yes" />
  </xsl:template>
  
</xsl:stylesheet>