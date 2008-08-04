<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:param name="errPadding">9.25em</xsl:param>
  
  <xsl:template match="FieldError">
    <span class="errorLabel" style="padding-left: {$errPadding};"><xsl:value-of select="./defaultMessage"/></span>
  </xsl:template>

  <xsl:template match="ObjectError">
    <span class="errorLabel" style="padding-left: {$errPadding};"><xsl:value-of select="./defaultMessage"/></span>
  </xsl:template>
  
  <xsl:template name="customError">
    <xsl:param name="message"/>
    <xsl:param name="class" select="'errorLabel'"/>
    <xsl:param name="style"/>
    <xsl:if test="$message != ''">
      <span class="{$class}" style="{$style}"><xsl:value-of select="$message"/></span>
    </xsl:if>
  </xsl:template>
  
  <!-- Template for validated state selection -->
  <xsl:template name="vStateSelect">
    <xsl:param name="selected"/>
    <xsl:for-each select="./StateVO">
      <option value="{./id}">
        <xsl:if test="./id=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
        <xsl:value-of select="./name"/>
      </option>
    </xsl:for-each>
  </xsl:template>

  <!-- Template for validated country selection -->
  <xsl:template name="vCountrySelect">
    <xsl:param name="selected"/>
    <xsl:for-each select="./CountryVO">
      <option value="{./id}">
        <xsl:if test="./id=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
        <xsl:value-of select="./name"/>
      </option>
    </xsl:for-each>
  </xsl:template>
  
  <!-- Template for validated business selection -->
  <xsl:template name="vBusinessSelect">
    <xsl:param name="selected"/>
    <xsl:for-each select="./BusinessVO">
      <xsl:if test="./activeInd=true or ./guid=$selected">
        <option value="{./guid}">
          <xsl:if test="./guid=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
          <xsl:value-of select="./name"/>
        </option>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <!-- Template for validated position selection -->
  <xsl:template name="vBusinessGroupSelect">
    <xsl:param name="groups"/>
    <xsl:param name="selected"/>
    <xsl:param name="level"/>
    <xsl:for-each select="$groups">
      <xsl:if test="./activeInd='true' or ./guid=$selected">
        <xsl:variable name="thisPar"><xsl:value-of select="./guid"/></xsl:variable>
        <option value="{./guid}">
          <xsl:if test="./guid=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
          <xsl:value-of select="$level"/><xsl:value-of select="./name"/>
        </option>
        <xsl:call-template name="vBusinessGroupSelect">
          <xsl:with-param name="groups" select="/component/objects/BusinessGroupNameVO[parentId=$thisPar]"/>
          <xsl:with-param name="selected" select="$selected"/>
          <xsl:with-param name="level" select="concat($level,'&#xa0;')"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <!-- Template for validated position selection -->
  <xsl:template name="vDepartmentSelect">
    <xsl:param name="selected"/>
    <xsl:for-each select="./DepartmentNameVO">
      <xsl:if test="./activeInd='true' or ./guid=$selected">
        <option value="{./guid}">
          <xsl:if test="./guid=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
          <xsl:value-of select="./name"/>
        </option>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <!-- Template for validated position selection -->
  <xsl:template name="vDepartmentAreaSelect">
    <xsl:param name="selected"/>
    <xsl:for-each select="./DepartmentAreaNameVO">
      <xsl:if test="./activeInd='true' or ./guid=$selected">
        <option value="{./guid}">
          <xsl:if test="./guid=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
          <xsl:value-of select="./name"/>
        </option>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <!-- Template for validated position selection -->
  <xsl:template name="vPositionSelect">
    <xsl:param name="selected"/>
    <xsl:param name="showSystem">false</xsl:param>
    <xsl:for-each select="./PositionVO[./isSystem='false']">
      <xsl:sort select="./name"/>
      <xsl:if test="./activeInd='true' or ./guid=$selected">
        <option value="{./guid}">
          <xsl:if test="./guid=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
          <xsl:value-of select="./name"/>
        </option>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="$showSystem = 'true'">
      <option value="">--------</option>
      <xsl:for-each select="./PositionVO[./isSystem='true']">
        <xsl:sort select="./name"/>
        <xsl:if test="./activeInd='true' or ./guid=$selected">
          <option value="{./guid}">
            <xsl:if test="./guid=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
            <xsl:value-of select="./name"/>
          </option>
        </xsl:if>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>
    
  <!-- Template for validated person selection -->
  <xsl:template name="vContactSelect">
    <xsl:param name="selected"/>
    <xsl:for-each select="./ContactVO">
      <xsl:if test="./activeInd='true' or ./guid=$selected">
        <option value="{./guid}">
          <xsl:if test="./guid=$selected"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if> 
          <xsl:value-of select="./firstName"/>&#xa0;<xsl:value-of select="./lastName"/>
        </option>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>  

</xsl:stylesheet>