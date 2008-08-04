<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>

  <xsl:param name="parentId"/>
  <xsl:param name="entityVONode"/>
  <xsl:param name="entityName"/>
  <xsl:param name="entityDao"/>
  <xsl:param name="entityClass"/>
  <xsl:param name="successUri"/>
  <xsl:param name="errorUri"/>
  <xsl:param name="displayName"/>
  <xsl:param name="closePopup"/>
  <xsl:param name="closeCallback"/>
  
  <xsl:variable name="entityVO" select="/component/objects/node()[name()=$entityVONode] | /component/errorobjects/node()[name()=$entityVONode]"/>
  
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div class="ibody">
      <div class="popupTitle" id="pagetitle">Delete <xsl:value-of select="$displayName"/></div>
      <form id="delete{$entityName}Form" action="{$context}DeleteSimpleEntity.xpost" method="post" class="app ajaxUpdateForm" ajaxTarget="delete{$entityName}Popup_content" postEval="Toobs.Page.initialize();">
        <input type="hidden" name="parentId" value="{$parentId}"/>
        <input type="hidden" name="entityId" value="{$entityVO/guid | $entityVO/id}"/>
        <input type="hidden" name="entityVO" value="{$entityVONode}"/>
        <input type="hidden" name="entityDao" value="{$entityDao}"/>
        <input type="hidden" name="entityClass" value="{$entityClass}"/>
        <input type="hidden" name="successUri" value="{$successUri}"/>
        <input type="hidden" name="errorUri" value="{$errorUri}"/>
        <div>  
          <p style="text-align:center;">
            Are you sure you want to permanently delete the <xsl:value-of select="$displayName"/><xsl:text> </xsl:text><xsl:value-of select="$entityVO/name"/>
          </p>
          <p class="spc"/>
          <div class="bbar">
            <div class="center" style="width: 75%;">
              <button class="button" type="submit">Delete <xsl:value-of select="$displayName"/></button>
              <button class="button" id="closePopup" type="button" onclick="Toobs.Popup.close('delete{$entityName}Popup');">Cancel</button>
            </div>
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
