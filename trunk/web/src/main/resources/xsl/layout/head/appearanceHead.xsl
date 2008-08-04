<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="xml" omit-xml-declaration="yes"/>
<xsl:template match="component">
  <tok><xsl:apply-templates select="objects"/></tok>
</xsl:template>
<xsl:template match="objects">
  <xsl:apply-templates select="./AppearanceVO"/>
</xsl:template>    
<xsl:template match="AppearanceVO">
  <xsl:apply-templates select="./AppearanceElementVO"/>
  <xsl:apply-templates select="./FontElementVO"/>
</xsl:template>
<xsl:template match="FontElementVO[name='Body']">
  /* FontElementVO[name='Body'] */
  input.text, input[type='submit'], input.password, select, textarea, button {
  <xsl:apply-templates select="./size"/>
  font-family:<xsl:apply-templates select="./family"/> Arial, Helvetica, sans-serif;
  <xsl:apply-templates select="weight"/>
  }
</xsl:template>
<xsl:template match="FontElementVO[name='FieldHeader']">
  /* FontElementVO[name='FieldHeader'] */
  .formtitle, form.app fieldset legend {
  <xsl:apply-templates select="./color"/>
  <xsl:apply-templates select="./size"/>
  font-family:<xsl:apply-templates select="./family"/> Arial, Helvetica, sans-serif;
  <xsl:apply-templates select="weight" mode="bold"/>
  }
</xsl:template>
<xsl:template match="FontElementVO[name='PageTitle']">
  /* FontElementVO[name='PageTitle'] */
  #pagetitle, .popupTitle {
  <xsl:apply-templates select="./color"/>
  <xsl:apply-templates select="./size"/>
  font-family:<xsl:apply-templates select="./family"/> Arial, Helvetica, sans-serif;
  <xsl:apply-templates select="weight" mode="bold"/>
  }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Body']">
  /* AppearanceElementVO[name='Body'] */
  body, .base_content, .mac_os_x_content {
  background: <xsl:apply-templates select="." mode="background"/>;
  }
  body, a, .formtitle, form.app fieldset legend {
  <xsl:apply-templates select="./color"/>
  }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Header']">
  /* AppearanceElementVO[name='Header'] */
  #header {
  <xsl:apply-templates select="." mode="border"/>
  <xsl:apply-templates select="./background"/>
  }
  #header, #header a {
  <xsl:apply-templates select="./color"/>
  }
  <!-- TODO Conditionalize debug mode -->
  div.securityDebug {
  <xsl:apply-templates select="." mode="border"/>
  }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Container']">
  /* AppearanceElementVO[name='Container'] */
  #maincontainer {
  border-left: <xsl:apply-templates select="." mode="cborder"/>;
  border-right: <xsl:apply-templates select="." mode="cborder"/>;
  <xsl:apply-templates select="./background"/>
  }
  #maincontainer, #maincontainer a {
  <xsl:apply-templates select="./color"/>
  }
  
  #middlecol{ <xsl:apply-templates select="." mode="borderleft"/> }
  div.wikiMenu ul li { <xsl:apply-templates select="." mode="border"/> border-top: 0; }
  
  hr {
  color: <xsl:apply-templates select="." mode="hrcolor"/>;
  background-color: <xsl:apply-templates select="." mode="hrcolor"/>;
  }
  .tab_contents_header {
  <xsl:apply-templates select="." mode="bordertop"/>
  }
  div.wikiComment
  {
  border-top-color:<xsl:value-of select="./borderColor"/>;
  }
  .TabContainer {
  <xsl:apply-templates select="." mode="bordertop"/>
  }
  form.app div.bbar {
  border: 1px solid <xsl:apply-templates select="." mode="background"/>;
  }
  div.normal {
  border: 1px solid <xsl:apply-templates select="." mode="background"/>;
  }
  <xsl:choose>
    <xsl:when test="../themeInd=0">
      div.fieldset {
      border: 1px solid <xsl:apply-templates select="." mode="background"/>;
      }
    </xsl:when>
    <xsl:when test="../themeInd=1">
      div.fieldset {
      margin-top: 1.2em;
      border-color: #036;
      <xsl:apply-templates select="." mode="border"/>
      }
      
      div.fieldset legend span {
      position:relative;
      top: -0.75em;
      padding: 0.2em 0.5em;
      background: <xsl:apply-templates select="." mode="background"/>;
      border-width: 1px;
      <xsl:apply-templates select="." mode="border"/>
      }
      
      <xsl:comment><![CDATA[[if lte IE 6]>
div.fieldset legend span { position:absolute; top: -1.5em; margin-left: 0.2em; }
      <![endif]]]></xsl:comment>
      
      div.fieldset legend.italics span {
      position:relative;
      top: 0;
      padding: 0;
      <xsl:apply-templates select="../AppearanceElementVO[name='Fieldset']/background | ./background"/>
      border-width: 0;
      }
    </xsl:when>
  </xsl:choose>
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Footer']">
  /* AppearanceElementVO[name='Footer'] */
  #footer, #footer a { <xsl:apply-templates select="./color"/> }
  #footer { <xsl:apply-templates select="." mode="border"/> <xsl:apply-templates select="./background"/> }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Menu']">
  /* AppearanceElementVO[name='Menu'] */
  .mainDiv, .dropMenu { <xsl:apply-templates select="./background"/> }
  .dropMenu { <xsl:apply-templates select="." mode="border"/> border-width: <xsl:apply-templates select="." mode="menuborder"/>; }
  .topItem,.topItemClose,.subItem,.subItem a,.subListItem,.subListItem a { <xsl:apply-templates select="./color"/> }
  .topItem,.topItemClose,.topItemOver,.topItemCloseOver { <xsl:apply-templates select="." mode="border"/> border-width: <xsl:apply-templates select="." mode="menuborder"/>; }
  .navbar { <xsl:apply-templates select="." mode="bordertop"/> }
  .topItemOver,.topItemCloseOver,.subItemOver,.subItemOver a,.subListItemOver,.subListItemOver a { <xsl:apply-templates select="./altColor"/> }
  <xsl:if test="not(../AppearanceElementVO[name='Tabs'])">
    .stab a, .silvertab .active a, .silvertab .hover a { <xsl:apply-templates select="./color"/> }
  </xsl:if>
  div.msg { <xsl:apply-templates select="./background"/><xsl:apply-templates select="./color"/><xsl:apply-templates select="." mode="border"/>}
  <xsl:choose>
    <xsl:when test="../themeInd=0">
    </xsl:when>
    <xsl:when test="../themeInd=1">
      div.fieldset { <xsl:apply-templates select="./background"/> }
    </xsl:when>
  </xsl:choose>
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Tabs']">
  /* AppearanceElementVO[name='Tabs'] */
  .stab a, .silvertab .active a, .silvertab .hover a { <xsl:apply-templates select="./color"/> }
  .stab a { <xsl:apply-templates select="./altColor"/> }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Controls']">
  input.text, input[type='submit'], input.password, select, textarea, button { <xsl:apply-templates select="." mode="borderimp"/> <xsl:apply-templates select="./background"/> <xsl:apply-templates select="./color"/> }
  <xsl:choose>
    <xsl:when test="./borderColor != ''">ul.droplist {border-color:<xsl:value-of select="./borderColor"/>;}</xsl:when>
    <xsl:when test="../AppearanceElementVO[name='Body']/borderColor != ''">ul.droplist {border-color:<xsl:value-of select="../AppearanceElementVO[name='Body']/borderColor"/>;}</xsl:when>
  </xsl:choose>
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Data Table Header']">
  table.dt th { <xsl:apply-templates select="./background"/> <xsl:apply-templates select="./color"/> }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Data Table Even']">
  table.dt tr.even { <xsl:apply-templates select="./background"/> <xsl:apply-templates select="./color"/> }
  table.dt tr.even a { <xsl:apply-templates select="./color"/> }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Data Table Odd']">
  table.dt tr.odd { <xsl:apply-templates select="./background"/> <xsl:apply-templates select="./color"/> }
  table.dt tr.odd a { <xsl:apply-templates select="./color"/> }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Fieldset']">
  form.app div.fieldset { <xsl:apply-templates select="./background"/> <xsl:apply-templates select="./color"/> }
</xsl:template>
<xsl:template match="AppearanceElementVO[name='Legend']">
  form.app fieldset.normal legend span { <xsl:apply-templates select="./background"/> <xsl:apply-templates select="./color"/> }
  form.app fieldset.normal legend.italics span { 
  <xsl:apply-templates select="../AppearanceElementVO[name='Fieldset']/background | ../AppearanceElementVO[name='Container']/background"/>
  }
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="border">
  <xsl:if test="./borderStyle != ''">border:<xsl:value-of select="./borderWidth"/>px <xsl:value-of select="./borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="./borderColor"/>;</xsl:if>
  <xsl:if test="./borderStyle = '' and ../AppearanceElementVO[name='Body']/borderStyle != ''">border:<xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px <xsl:value-of select="../AppearanceElementVO[name='Body']/borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="../AppearanceElementVO[name='Body']/borderColor"/>;</xsl:if>
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="borderimp">
  <xsl:if test="./borderStyle != ''">border:<xsl:value-of select="./borderWidth"/>px <xsl:value-of select="./borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="./borderColor"/> !important;</xsl:if>
  <xsl:if test="./borderStyle = '' and ../AppearanceElementVO[name='Body']/borderStyle != ''">border:<xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px <xsl:value-of select="../AppearanceElementVO[name='Body']/borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="../AppearanceElementVO[name='Body']/borderColor"/> !important;</xsl:if>
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="bordertop">
  <xsl:if test="./borderStyle != ''">border-top:<xsl:value-of select="./borderWidth"/>px <xsl:value-of select="./borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="./borderColor"/>;</xsl:if>
  <xsl:if test="./borderStyle = '' and ../AppearanceElementVO[name='Body']/borderStyle != ''">border-top:<xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px <xsl:value-of select="../AppearanceElementVO[name='Body']/borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="../AppearanceElementVO[name='Body']/borderColor"/>;</xsl:if>
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="borderleft">
  <xsl:if test="./borderStyle != ''">border-left:<xsl:value-of select="./borderWidth"/>px <xsl:value-of select="./borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="./borderColor"/>;</xsl:if>
  <xsl:if test="./borderStyle = '' and ../AppearanceElementVO[name='Body']/borderStyle != ''">border-left:<xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px <xsl:value-of select="../AppearanceElementVO[name='Body']/borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="../AppearanceElementVO[name='Body']/borderColor"/>;</xsl:if>
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="cborder">
  <xsl:if test="./borderStyle != ''"><xsl:value-of select="./borderWidth"/>px <xsl:value-of select="./borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="./borderColor"/></xsl:if>
  <xsl:if test="./borderStyle = '' and ../AppearanceElementVO[name='Body']/borderStyle != ''"><xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px <xsl:value-of select="../AppearanceElementVO[name='Body']/borderStyle"/><xsl:text> </xsl:text><xsl:value-of select="../AppearanceElementVO[name='Body']/borderColor"/></xsl:if>
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="menuborder">
  <xsl:if test="./borderStyle != ''">0 <xsl:value-of select="./borderWidth"/>px <xsl:value-of select="./borderWidth"/>px <xsl:value-of select="./borderWidth"/>px</xsl:if>
  <xsl:if test="./borderStyle = '' and ../AppearanceElementVO[name='Body']/borderStyle != ''">0 <xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px <xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px <xsl:value-of select="../AppearanceElementVO[name='Body']/borderWidth"/>px</xsl:if>
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="hrcolor">
  <xsl:if test="./borderStyle != ''"><xsl:value-of select="./borderColor"/></xsl:if>
  <xsl:if test="./borderStyle = '' and ../AppearanceElementVO[name='Body']/borderStyle != ''"><xsl:value-of select="../AppearanceElementVO[name='Body']/borderColor"/></xsl:if>
</xsl:template>
<xsl:template match="AppearanceElementVO" mode="background">
  <xsl:choose>
    <xsl:when test="./background != ''"><xsl:value-of select="./background"/></xsl:when>
    <xsl:when test="../AppearanceElementVO[name='Body']/background != ''"><xsl:value-of select="../AppearanceElementVO[name='Body']/background"/></xsl:when>
    <xsl:otherwise>#fff;</xsl:otherwise>
  </xsl:choose>
</xsl:template>
<xsl:template match="family">
  <xsl:if test=".!=''">
    <xsl:if test="contains(.,' ')">'</xsl:if><xsl:value-of select="."/><xsl:if test="contains(.,' ')">'</xsl:if>,
  </xsl:if>
</xsl:template>
<xsl:template match="background">
  <xsl:if test=". != ''">background: <xsl:value-of select="."/>;</xsl:if>
</xsl:template>
<xsl:template match="color | altColor">
  <xsl:if test=". != ''">color: <xsl:value-of select="."/>;</xsl:if>
</xsl:template>
<xsl:template match="size">
  <xsl:if test=". != ''">font-size:<xsl:value-of select="."/>;</xsl:if>
</xsl:template>
<xsl:template match="weight">
  <xsl:if test=". != ''">font-weight:<xsl:value-of select="."/>;</xsl:if>
</xsl:template>
<xsl:template match="weight" mode="bold">
  font-weight:<xsl:if test=". != ''"><xsl:value-of select="."/></xsl:if><xsl:if test=". = ''">bold</xsl:if>;
</xsl:template>
<xsl:template match="AppearanceElementVO"/>
<xsl:template match="FontElementVO"/>
</xsl:stylesheet>