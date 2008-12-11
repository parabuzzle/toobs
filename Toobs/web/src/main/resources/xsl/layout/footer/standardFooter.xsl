<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="pageController"/>

  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div id="footer">
      <div id="footerleft">&#xa9; 
        <a href="">Privacy Policy</a>&#xa0;&#xB7;&#xa0;<a href="">Terms of use</a>&#xa0;&#xB7;&#xa0;<a href="">Support</a>
      </div>
      <div id="footerright">Powered by Toobs</div>
    </div>
    <script type="text/javascript">
      Toobs.Controller.use("<xsl:value-of select="$pageController"/>");
    </script>
  </xsl:template>  
</xsl:stylesheet>