<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:import href="templatelib/validation.lib.xsl"/>

  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>

  <xsl:param name="parentId"/>
  <xsl:param name="parentName"/>
  <xsl:param name="alternateId"/>
  <xsl:param name="alternateName"/>
  <xsl:param name="entityVONode"/>
  <xsl:param name="entityName"/>
  <xsl:param name="entityDao"/>
  <xsl:param name="entityClass"/>
  <xsl:param name="successUri"/>
  <xsl:param name="errorUri"/>
  <xsl:param name="displayName"/>
  <xsl:param name="closePopup"/>
  <xsl:param name="closeCallback"/>
  
  <xsl:variable name="isEdit" select="boolean(/component/objects/node()[name()=$entityVONode])"/>
  <xsl:variable name="entityVO" select="/component/objects/node()[name()=$entityVONode] | /component/errorobjects/node()[name()=$entityVONode]"/>

  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div class="ibody">
      <div class="popupTitle" id="pagetitle">
        <xsl:if test="$isEdit">Edit <xsl:value-of select="$displayName"/></xsl:if>
        <xsl:if test="not($isEdit)">New <xsl:value-of select="$displayName"/></xsl:if>
      </div>
      <form id="edit{$entityName}Form" action="{$context}UpdateSimpleEntity.xpost" method="post" class="app ajaxUpdateForm" ajaxTarget="edit{$entityName}Popup_content" postEval="Toobs.Page.initialize();">
        <input type="hidden" id="entityId" name="entityId" value="{$entityVO/guid}"/>
        <input type="hidden" name="entityVO" value="{$entityVONode}"/>
        <input type="hidden" name="parentId" value="{$parentId}"/>
        <input type="hidden" name="{$parentName}" value="{$parentId}"/>
        <xsl:if test="$alternateId != '' and $alternateName != ''">
          <input type="hidden" name="{$alternateName}" value="{$alternateId}"/>
        </xsl:if>
        <input type="hidden" name="entityDao" value="{$entityDao}"/>
        <input type="hidden" name="entityClass" value="{$entityClass}"/>
        <input type="hidden" name="successUri" value="{$successUri}"/>
        <input type="hidden" name="errorUri" value="{$errorUri}"/>
        <div class="fieldset">
          <fieldset class="normal">
            <legend class="fieldGroup"><span><xsl:value-of select="$displayName"/> Information</span></legend>
            <p>
              <label for="entity.name" class="fieldTitle"><xsl:value-of select="$displayName"/> Name:</label>
              <input type="text" class="text" id="entity.name" name="entity.name" style="width:24em;" value="{$entityVO/name}"/>
              <xsl:apply-templates select="../errors/FieldError[objectName=$entityName and field='name']"/>
            </p>
            <p>
              <label for="entity.description" class="fieldTitle">Definition:</label>
              <textarea id="entity.description" name="entity.description" cols="50" rows="6" style="width:24em;">
                <xsl:value-of select="$entityVO/description"/>
              </textarea>
            </p>
          </fieldset>
        </div>
        <div class="bbar">
          <div class="mid">
            <button class="button" type="submit">
              <xsl:if test="$isEdit">Save <xsl:value-of select="$displayName"/></xsl:if>
              <xsl:if test="not($isEdit)">Create <xsl:value-of select="$displayName"/></xsl:if>
            </button>
            <button class="button" id="closePopup" type="button" onclick="Toobs.Popup.close('edit{$entityName}Popup');">Cancel</button>
          </div>
        </div>
      </form>
    </div>
    <xsl:if test="$closePopup='true'">
      <script type="text/javascript">
        <xsl:if test="$closeCallback != ''">
          <xsl:value-of select="$closeCallback"/>
        </xsl:if>
        <!-- Toobs.Popup.close('edit<xsl:value-of select="$entityName"/>Popup'); -->
      </script>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
