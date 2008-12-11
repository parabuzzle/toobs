<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:param name="currentId"/>
  <xsl:param name="listNum"/>
  <xsl:param name="listNode"/>
  <xsl:param name="displayNode"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <ul class="treeList_{$listNum} tTree" listNum="{$listNum}">
      <xsl:apply-templates select="node()[name()=$listNode]" mode="li">
        <xsl:sort select="node()[name()=$displayNode]"/>
      </xsl:apply-templates>
    </ul>
  </xsl:template>
  
  <xsl:template match="node()" mode="li">
    <li id="tli-{./guid}" class="closed">
      <xsl:if test="./guid=$currentId"><xsl:attribute name="class">closed initopen</xsl:attribute></xsl:if>
      <span class="txt"><xsl:value-of select="node()[name()=$displayNode]"/></span>
    </li>
  </xsl:template>
</xsl:stylesheet>
