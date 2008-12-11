<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:dyn="http://exslt.org/dynamic"
  xmlns:c="org.toobs.framework.pres.component.util.ComponentHelper"
  exclude-result-prefixes="dyn c">
  
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="typeName"/>
  <xsl:param name="idPrefix"/>
  <xsl:param name="inputName"/>
  <xsl:param name="sourceNodePath"/>
  <xsl:param name="targetNodePath"/>
  <xsl:param name="displayItems" select="6"/>
  <xsl:param name="namePath" select="'name'"/>
  <xsl:param name="nameTemplate">name</xsl:param>
  <xsl:param name="srcListWidth">260</xsl:param>
  <xsl:param name="tgtListWidth">260</xsl:param>
  <xsl:param name="srcLabelXsl"/>
  <xsl:param name="tgtLabelXsl"/>
  <xsl:param name="srcListXsl"/>
  <xsl:param name="tgtListXsl"/>
  <xsl:param name="tgtCustClass"/>
  <xsl:param name="showPicker">false</xsl:param>
  <xsl:param name="showAddAll">true</xsl:param>
  <xsl:param name="pickerLabel"/>
  <xsl:param name="pickerUrl"/>
  <xsl:param name="pickerParam"/>
  <xsl:param name="pickerFEval"/>
  <xsl:param name="pickerOffset">38</xsl:param>
  <xsl:param name="emptyMessage"/>
  <xsl:param name="dropcat"/>
  
  <xsl:variable name="rootNode" select="./component"/>
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div id="selectedPositions" class="fleft droppable" style="width:{$tgtListWidth}px;margin-right:0.5em;">
      <xsl:if test="$tgtLabelXsl">
        <xsl:value-of select="c:inlineUrl($tgtLabelXsl, '')" disable-output-escaping="yes"/>
      </xsl:if>
      <xsl:if test="not($tgtLabelXsl)">
        <div>Selected <xsl:value-of select="$typeName"/></div>
        <xsl:if test="$showPicker='true'">
          <div style="height:20px;"/>
        </xsl:if>
      </xsl:if>
      <ul id="{$idPrefix}Target" class="droplist droptarget {$tgtCustClass}" style="height:{($displayItems * 25) + 6}px;" inputName="{$inputName}">
        <xsl:if test="$dropcat != ''"><xsl:attribute name="dropcat"><xsl:value-of select="$dropcat"/></xsl:attribute></xsl:if>
        <xsl:if test="$tgtListXsl">
          <xsl:value-of select="c:inlineUrl($tgtListXsl, $rootNode)" disable-output-escaping="yes"/>
        </xsl:if>
        <xsl:if test="not($tgtListXsl)">
          <xsl:apply-templates select="dyn:evaluate($targetNodePath)" mode="target">
            <xsl:sort select="./displayOrder"/>
            <xsl:sort select="./name"/>
          </xsl:apply-templates>
        </xsl:if>
      </ul>
    </div>
    <div id="availablePositions" class="fleft droppable" style="width:{$srcListWidth}px;height:{($displayItems * 25) + 6}px;">
      <xsl:if test="$srcLabelXsl">
        <xsl:value-of select="c:inlineUrl($srcLabelXsl, '')" disable-output-escaping="yes"/>
      </xsl:if>
      <xsl:if test="not($srcLabelXsl)">
        <div>Available <xsl:value-of select="$typeName"/></div>
        <xsl:if test="$showPicker='true'">
          <div style="height:20px;"><span class="bgsL" style="width:3em;"><xsl:value-of select="$pickerLabel"/>:</span><input id="srcPicker" type="text" class="text fright" name="{$pickerParam}" pickerUrl="{$pickerUrl}" pickerParam="{$pickerParam}" focusEval="{$pickerFEval}" style="width:{number($srcListWidth) - number($pickerOffset)}px;margin-right:0;"/></div>
        </xsl:if>
      </xsl:if>
      <ul id="{$idPrefix}Source" class="droplist dropsource" style="height:{($displayItems * 25) + 6}px;width:{number($srcListWidth) - 2}px;position:relative;">
        <xsl:if test="$dropcat != ''"><xsl:attribute name="dropcat"><xsl:value-of select="$dropcat"/></xsl:attribute></xsl:if>
        <xsl:if test="$srcListXsl">
          <xsl:value-of select="c:inlineUrl($srcListXsl, $rootNode)" disable-output-escaping="yes"/>
        </xsl:if>
        <xsl:if test="not($srcListXsl)">
          <xsl:apply-templates select="dyn:evaluate($sourceNodePath)" mode="source"/>
        </xsl:if>
        <xsl:if test="count(dyn:evaluate($sourceNodePath)) = 0 and $emptyMessage != ''">
          <li style="text-align:center;">
            <span class="lbl"><xsl:value-of select="$emptyMessage"/></span>
          </li>
        </xsl:if>
      </ul>
      <a href="javascript:void(0);" id="srcAddAll" idPrefix="{$idPrefix}">&lt;&lt; Add All</a>
    </div>
  </xsl:template>  
  <xsl:template match="node()" mode="source">
    <xsl:variable name="thisGuid" select="./guid"/>
    <xsl:if test="not(dyn:evaluate($targetNodePath)[./guid=$thisGuid])">
      <li class="dragli" id="dli-{./guid}">
        <img class="fleft dragimg" src="{$context}img/drag.png" alt=""/>
        <div class="top">
          <span class="lbl">
            <xsl:choose>
              <xsl:when test="$nameTemplate='firstLast'">
                <xsl:call-template name="firstLast"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="name"/>
              </xsl:otherwise>
            </xsl:choose>
            <input id="ddi-{./guid}" type="hidden" name="{$inputName}" value="{./guid}" disabled="disabled"/>
          </span>
        </div>
      </li>
    </xsl:if>    
  </xsl:template>
  <xsl:template match="node()" mode="target">
    <li class="dragli" id="dli-{./guid}">
      <img class="fleft dragimg" src="{$context}img/drag.png" alt=""/>
      <div class="top">
        <span class="lbl">
          <xsl:choose>
            <xsl:when test="$nameTemplate='firstLast'">
              <xsl:call-template name="firstLast"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="name"/>
            </xsl:otherwise>
          </xsl:choose>
          <input id="ddi-{./guid}" type="hidden" name="{$inputName}" value="{./guid}"/>
        </span>
      </div>
    </li>
  </xsl:template>
  <xsl:template name="name">
    <xsl:value-of select="node()[name()=$namePath]"/>
  </xsl:template>
  <xsl:template name="firstLast">
    <xsl:value-of select="./firstName"/>&#xa0;<xsl:value-of select="./lastName"/>    
  </xsl:template>
</xsl:stylesheet>