<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:fo="http://www.w3.org/1999/XSL/Format" 
  version="2.0"
  xmlns:dateHelper="org.toobs.framework.transformpipeline.xslExtentions.DateHelper"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:dyn="http://exslt.org/dynamic"
  exclude-result-prefixes="xalan dateHelper dyn">

  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="component"/>
  <xsl:param name="queryString"/>
  <xsl:param name="printTitle"><xsl:value-of select="$component"/> Export</xsl:param>
  
  <xsl:param name="firstResult"/>
  <xsl:param name="totalRows"/>
  <xsl:param name="pageSize"/>
  <xsl:param name="noRowsMessage">No results found</xsl:param>
  <xsl:param name="orient">portrait</xsl:param>
  
  <xsl:param name="columns"/>
  <xsl:variable name="columnCount" select="count(xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)])"/>
  <xsl:variable name="pageHeight">
    <xsl:choose>
      <xsl:when test="$orient='portrait'">11</xsl:when>
      <xsl:when test="$orient='landscape'">8.5</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="pageWidth">
    <xsl:choose>
      <xsl:when test="$orient='portrait'">8.5</xsl:when>
      <xsl:when test="$orient='landscape'">11</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="pdfColumnWidth" select="number($pageWidth - 1) div $columnCount"/>
  <xsl:template name="pdfTable">
    <xsl:param name="cssClass"/>
    <xsl:param name="rowData"/>

    
    <fo:layout-master-set>
      <fo:simple-page-master master-name="cover" page-height="{$pageHeight}in" page-width="{$pageWidth}in" margin=".5in">
        <fo:region-body region-name="coverBody" margin-top="1in"/>
      </fo:simple-page-master>
      <fo:simple-page-master master-name="content" page-height="{$pageHeight}in" page-width="{$pageWidth}in" margin=".5in">
        <fo:region-body   region-name="contentBody" margin-top="0.5in" margin-bottom="0.5in" />
        <fo:region-before region-name="contentTop" extent="1in" />
        <fo:region-after  region-name="contentBtm" extent="0.25in" />
      </fo:simple-page-master>
    </fo:layout-master-set>
    
    <fo:page-sequence master-reference="content">
      <fo:static-content flow-name="contentTop">
        <fo:block text-align="center" font-size="21pt" font-weight="bold" color="#8b524b">
          <xsl:value-of select="$printTitle"/>
        </fo:block>
      </fo:static-content>
      <fo:static-content flow-name="contentBtm">
        <fo:block text-align="right">
          Prepared By Bouler Technology Capital
        </fo:block>
      </fo:static-content>
      <fo:flow flow-name="contentBody">
        <fo:table  border-collapse="collapse" font-size="12pt" table-layout="fixed" width="100%">
          
          <xsl:apply-templates select="xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)]" mode="pdfColumns"/>
          
          <fo:table-header>
            <fo:table-row>
              <xsl:apply-templates select="xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)]" mode="pdfHead"/>
            </fo:table-row>
          </fo:table-header>
          
          <fo:table-body>
            <xsl:apply-templates select="$rowData" mode="pdfBody"/>
            <xsl:if test="count($rowData) = 0">
              <fo:table-row>
                <fo:table-cell number-columns-spanned="{$columnCount}" border="0.5pt solid black" padding="2pt 5pt">
                  <fo:block font-weight="bold" font-style="italic" text-align="center">
                    <xsl:value-of select="$noRowsMessage"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </xsl:if>
          </fo:table-body>
        </fo:table>
      </fo:flow>
    </fo:page-sequence>
    
  </xsl:template>
  
  <xsl:template match="column" mode="pdfColumns">
    <fo:table-column>
      <xsl:attribute name="column-width">
        <xsl:choose>
          <xsl:when test="@printPct">
            <xsl:value-of select="@printPct * (number($pageWidth) - 1)"/>in
          </xsl:when>
          <xsl:when test="@printWidth">
            <xsl:value-of select="@printWidth"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$pdfColumnWidth"/>in
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    </fo:table-column>
  </xsl:template>
  
  <xsl:template match="column" mode="pdfHead">
    <fo:table-cell border="0.5pt solid black" padding="2pt 5pt">
      <fo:block font-weight="bold">
        <xsl:value-of select="@name"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>
  
  <xsl:template match="node()" mode="pdfBody">
    <xsl:variable name="currentNode" select="."/>
    <fo:table-row>
      <xsl:for-each select="xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)]">
        <xsl:call-template name="pdfBodyColumn">
          <xsl:with-param name="col" select="."/>
          <xsl:with-param name="currentNode" select="$currentNode"/>
        </xsl:call-template>
      </xsl:for-each>
    </fo:table-row>
  </xsl:template>
  
  <xsl:template name="pdfBodyColumn">
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <fo:table-cell border="0.5pt solid black" padding="2pt 5pt">
      <fo:block>
        <xsl:for-each select="$col/node()">
          <xsl:variable name="thisNode" select="."/>
          <xsl:choose>
            <xsl:when test="name()='attribute'">
              <xsl:variable name="cnt"><xsl:number/></xsl:variable>
              <xsl:variable name="valuePath">$currentNode/<xsl:value-of select="@name"/></xsl:variable>
              <xsl:call-template name="pdfConversion">
                <xsl:with-param name="valuePath" select="$valuePath"/>
                <xsl:with-param name="attr" select="."/>
                <xsl:with-param name="col" select="$col"/>
                <xsl:with-param name="currentNode" select="$currentNode"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="name()='collection'">
              <xsl:for-each select="$currentNode/descendant::node()[name() = $thisNode/@name]">
                <xsl:variable name="collectNode" select="."/>
                <xsl:for-each select="$thisNode/attribute">
                  <xsl:variable name="valuePath">$currentNode/<xsl:value-of select="@name"/></xsl:variable>
                  <xsl:call-template name="pdfConversion">
                    <xsl:with-param name="valuePath" select="$valuePath"/>
                    <xsl:with-param name="attr" select="."/>
                    <xsl:with-param name="col" select="$col"/>
                    <xsl:with-param name="currentNode" select="$collectNode"/>
                  </xsl:call-template>
                </xsl:for-each>
              </xsl:for-each>
            </xsl:when>
          </xsl:choose>
        </xsl:for-each>
      </fo:block>
    </fo:table-cell>
  </xsl:template>
  
  <xsl:template name="pdfPageInfo">
    <xsl:param name="uid"/>
    <!-- <span><xsl:value-of select="$firstResult"/>:<xsl:value-of select="$totalRows"/>:<xsl:value-of select="$pageSize"/></span> -->
    <xsl:variable name="calcTotal"><xsl:value-of select="$firstResult + $pageSize + 1"/></xsl:variable>
    <xsl:variable name="calcStart"><xsl:if test="$totalRows = 0">0</xsl:if><xsl:if test="$totalRows != 0"><xsl:value-of select="$firstResult + 1"/></xsl:if></xsl:variable>
    <span class="pageinfo">
      <xsl:if test="$calcTotal &gt; $totalRows">
        <xsl:value-of select="$calcStart"/> - <xsl:value-of select="$totalRows"/> of <xsl:value-of select="$totalRows"/>
      </xsl:if>
      <xsl:if test="$calcTotal &lt;= $totalRows">
        <xsl:value-of select="$calcStart"/> - <xsl:value-of select="$firstResult + $pageSize"/> of <xsl:value-of select="$totalRows"/>
      </xsl:if>
    </span>
  </xsl:template>

  <xsl:template name="pdfConversion">
    <xsl:param name="valuePath"/>
    <xsl:param name="attr"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:variable name="value" select="dyn:evaluate($valuePath)"/>
    <xsl:if test="$value != ''">
      <xsl:choose>
        <xsl:when test="$attr/@conversion='customstatus'">
          <xsl:call-template name="customstatus">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
          </xsl:call-template>        
        </xsl:when>
        <xsl:when test="$attr/@conversion='status'">
          <xsl:if test="$value='true'">Active</xsl:if>
          <xsl:if test="$value='false'">Inactive</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='taxstatus'">
          <xsl:if test="$value='1'">Active</xsl:if>
          <xsl:if test="$value='2'">Pending</xsl:if>
          <xsl:if test="$value='0'">Inactive</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='bidstatus'">
          <xsl:if test="$value='0'">New</xsl:if>
          <xsl:if test="$value='1'">Approved</xsl:if>
          <xsl:if test="$value='2'">Disapproved</xsl:if>
          <xsl:if test="$value='3'">Out of date</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='invtype'">
          <xsl:if test="$value='0'">New Inventory</xsl:if>
          <xsl:if test="$value='1'">Inventory Update</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='quotetype'">
          <xsl:if test="$value='false'">Quote</xsl:if>
          <xsl:if test="$value='true'">Order</xsl:if>
        </xsl:when>     
        <xsl:when test="$attr/@conversion='orderstatus'">
          <xsl:if test="$value='false'">Open</xsl:if>
          <xsl:if test="$value='true'">Complete</xsl:if>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='selector'">

        </xsl:when>
        <xsl:when test="$attr/@conversion='fid'">
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="format-number($value,'000000')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='pct'">
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="format-number(number($value * 100),'##0.0')"/>%
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='formatnumber'">
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="format-number($value,$attr/@pattern)"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='quoted'">
          <xsl:apply-templates select="@prepend"/>
          "<xsl:value-of select="$value"/>"
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='timestamp'">
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy HH:mm z')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='date'">
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='time'">
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'HH:mm z')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>           
        <xsl:when test="$attr/@conversion='longtime'">
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'MMM dd yyyy HH:mm aa')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='email'">
          <xsl:apply-templates select="@prepend"/>
          <a href="mailto:{$value}"><xsl:value-of select="$value"/></a>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='image'">
          <xsl:apply-templates select="@prepend"/>
          <img src="{$value}" alt="">
            <xsl:if test="$col/@imgstyle"><xsl:attribute name="style"><xsl:value-of select="$col/@imgstyle"/></xsl:attribute></xsl:if>
          </img>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="$value"/>
          <xsl:apply-templates select="@append"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="(not($value) or $value = '') and $attr/@default">
      <xsl:apply-templates select="@prepend"/>
      <xsl:value-of select="$attr/@default" disable-output-escaping="yes"/>
      <xsl:apply-templates select="@append"/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="@prepend">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
  <xsl:template match="@append">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
  <xsl:template name="customstatus">
    <xsl:param name="currentNode"/>
    <xsl:param name="value"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
  </xsl:template>
</xsl:stylesheet>