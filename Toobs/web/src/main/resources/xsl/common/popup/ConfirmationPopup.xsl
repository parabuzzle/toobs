<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="title"/>
  <xsl:param name="message"/>
  <xsl:param name="action" select="''"/>
  <xsl:param name="searchForm" select="''"/>
  <xsl:param name="ajaxTarget" select="''"/>
  <xsl:param name="postEval" select="''"/>
  <xsl:param name="closePopup"/>
  <xsl:param name="closeScript"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div class="ibody">
      <div class="popupTitle" id="pagetitle">
        <xsl:value-of select="$title"/>
      </div>
      <form id="confirmationForm" method="post" class="app" action="{$action}" ajaxTarget="{$ajaxTarget}" postEval="{$postEval}">
        <xsl:if test="$searchForm != ''"><xsl:attribute name="searchForm"><xsl:value-of select="$searchForm"/></xsl:attribute></xsl:if>
        <div class="normal">
          <fieldset class="normal">
            <p>
              <xsl:value-of select="$message"/>
            </p>
            <p class="spc"/>
            <p>
              Click &quot;OK&quot; to confirm or click &quot;Cancel&quot; to abort the operation. 
            </p>
            <p class="spc"/>
          </fieldset>
        </div>
        <div class="bbar">
          <div class="center">
            <button id="confirmPopupOK" class="button" type="button">OK</button>
            <button id="confirmPopupCancel" class="button" type="button">Cancel</button>
          </div>
        </div>
      </form>
    </div>
    <xsl:if test="$closePopup='true' and $closeScript != ''">
      <script type="text/javascript">
        <xsl:value-of select="$closeScript"/>
      </script>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
