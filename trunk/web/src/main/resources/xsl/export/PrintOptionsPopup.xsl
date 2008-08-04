<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="closePopup"/>
  <xsl:param name="printTitle"/>

  <xsl:param name="component"/>
  <xsl:param name="firstResult"/>
  <xsl:param name="totalRows"/>
  <xsl:param name="pageSize"/>
  <xsl:param name="sortOrder"/>
  <xsl:param name="queryString"/>
  <xsl:param name="exportURI"/>
  <xsl:param name="exportMode"/>
  <xsl:param name="format"/>
  <xsl:param name="showPage"/>
  <xsl:param name="showSimple"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div class="popupTitle" id="pagetitle">Print Options</div>
    <xsl:variable name="ext">
      <xsl:choose>
        <xsl:when test="$format='acrobat'">xpdf</xsl:when>
        <xsl:when test="$format='word'">xrtf</xsl:when>
        <xsl:when test="$format='excel'">xxls</xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="tmpltName">
      <xsl:choose>
        <xsl:when test="$exportURI != ''"><xsl:value-of select="$exportURI"/></xsl:when>
        <xsl:when test="$exportMode='table'">ExportTable</xsl:when>
        <xsl:otherwise><xsl:value-of select="$component"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <div class="ibody">
      <form id="printOptionsForm" action="{$context}{$tmpltName}.{$ext}?exportMode={$exportMode}&amp;{$queryString}" method="post" class="app vform">
  
        <input type="hidden" id="exportURI" value="{$exportURI}" />
        <input type="hidden" id="exportMode" value="{$exportMode}" />
        <input type="hidden" id="component" value="{$component}" />
        
        <input type="hidden" id="firstResult" name="f" value="{$firstResult}" disabled="disabled"/>
        <input type="hidden" id="curPageSize" name="m" value="{$pageSize}" disabled="disabled"/>
        <input type="hidden" id="allPageSize" name="m" value="9999" />
        <input type="hidden" id="sortOrder"   name="s" value="{$sortOrder}" />
        <input type="hidden" id="queryString" name="s" value="{$queryString}" />
        
        <div class="normal">
          <fieldset class="normal">
            <legend><span>Format</span></legend>
            <p>
              <label for="acrobat" style="width:6em;">
                <input type="radio" class="radio" id="acrobat" name="format" value=".xpdf">
                  <xsl:if test="$format='acrobat'"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if>
                </input>
                Acrobat
              </label>
              <label for="word" style="width:6em;">
                <input type="radio" class="radio" id="word" name="format" value=".xrtf">
                  <xsl:if test="$format='word'"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if>
                </input>
                Word
              </label>
              <label for="excel" style="width:6em;">
                <input type="radio" class="radio" id="excel" name="format" value=".xxls">
                  <xsl:if test="$format='excel'"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if>
                </input>
                Excel
              </label>
            </p>
          </fieldset>
          <fieldset class="normal">
            <legend><span>Options</span></legend>
            <xsl:if test="$showPage='true'">
              <p class="cue">
                <label for="allPages" style="width:8em;">
                  <input type="radio" class="radio" id="allPages" name="options" value="allPages" checked="checked"/>
                  All Pages
                </label>
                <label for="currentPage" style="width:8em;">
                  <input type="radio" class="radio" id="currentPage" name="options" value="currentPage"/>
                  Current Page
                </label>
              </p>
            </xsl:if>
            <xsl:if test="$showSimple='true'">
              <p class="cue">
                <label for="simple" style="width:8em;">
                  <input type="radio" class="radio" id="simple" name="simFull" value="simple" checked="checked"/>
                  Simple
                </label>
                <label for="full" style="width:8em;">
                  <input type="radio" class="radio" id="full" name="simFull" value="full"/>
                  Full
                </label>
              </p>
            </xsl:if>
            <p class="cue">
              <label for="portrait" style="width:8em;">
                <input type="radio" class="radio" id="portrait" name="orient" value="portrait" checked="checked">
                  <xsl:if test="$format='excel'"><xsl:attribute name="disabled">disabled</xsl:attribute></xsl:if>
                </input>
                Portrait
              </label>
              <label for="landscape" style="width:8em;">
                <input type="radio" class="radio" id="landscape" name="orient" value="landscape">
                  <xsl:if test="$format='excel'"><xsl:attribute name="disabled">disabled</xsl:attribute></xsl:if>
                </input>
                Landscape
              </label>
            </p>
          </fieldset>
        </div>
        <div class="bbar">
          <div class="left">
            <button class="button" type="submit">Export</button>
            <button class="button" type="button" onclick="Toobs.Popup.close('export_popup');">Close</button>
          </div>
        </div>
      </form>
    </div>
    <xsl:if test="$closePopup='true'">
      <script type="text/javascript">
        Toobs.Popup.closeReload('export_popup');
      </script>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>