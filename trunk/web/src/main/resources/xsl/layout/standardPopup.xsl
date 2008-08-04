<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  xmlns:dateHelper="org.toobs.framework.pres.component.util.DateHelper"
  extension-element-prefixes="compHelper dateHelper">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="pageController"/>
  
  <xsl:template match="RuntimeLayout">
    <html>
      <head>
        <xsl:apply-templates select="./Section[@id='head']"/>
      </head>
      
      <body>
        <xsl:if test="/RuntimeLayout/ContentParams/Parameter[@name = 'bodyClass']">
          <xsl:attribute name="class"><xsl:value-of select="/RuntimeLayout/ContentParams/Parameter[@name = 'bodyClass']/@path"/></xsl:attribute>
        </xsl:if>
        <div class="ibody">
          <xsl:apply-templates select="./Section[@type='wide']">
            <xsl:sort select="@order"/>
          </xsl:apply-templates>
        </div>        
        <script type="text/javascript">
          Toobs.Controller.use("<xsl:value-of select="$pageController"/>");
        </script>
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