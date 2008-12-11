<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0"
  xmlns:dateHelper="org.toobs.framework.transformpipeline.xslExtentions.DateHelper"
  xmlns:compHelper="org.toobs.framework.pres.component.util.ComponentHelper"
  xmlns:xalan="http://xml.apache.org/xalan"  
  xmlns:dyn="http://exslt.org/dynamic"
  exclude-result-prefixes="dateHelper compHelper xalan dyn"
  xmlns:x="urn:schemas-microsoft-com:office:excel"
  xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
  
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="component"/>
  <xsl:param name="queryString"/>
  <xsl:param name="printTitle"><xsl:value-of select="$component"/> Export</xsl:param>
  
  <xsl:param name="firstResult"/>
  <xsl:param name="totalRows"/>
  <xsl:param name="pageSize"/>
  <xsl:param name="noRowsMessage">No results found</xsl:param>
  
  <xsl:param name="columns"/>
  <xsl:variable name="xslColumnCount" select="count(xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)])"/>
  <xsl:variable name="xlsColumnWidth" select="600 div $xslColumnCount"/>
  
  <xsl:template name="xlsTable">
    <xsl:param name="cssClass"/>
    <xsl:param name="rowData"/>
    
    <Styles>
      <xsl:call-template name="xlsStyle"/>
    </Styles>
    
    <Worksheet ss:Name="{$printTitle}">
      <Table>
        <xsl:apply-templates select="xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)]" mode="xlsColumns"/>
        <Row ss:Height="25.0">
          <Cell ss:MergeAcross="{$xslColumnCount - 1}" ss:StyleID="bHead">
            <Data ss:Type="String">
              <xsl:value-of select="$printTitle"/>
            </Data>
          </Cell>
        </Row>
        <Row>
          <xsl:apply-templates select="xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)]" mode="xlsHead"/>
        </Row>
        <xsl:apply-templates select="$rowData" mode="xlsBody"/>
        <xsl:if test="count($rowData) = 0">
          <Row>
            <Cell ss:MergeAcross="{$xslColumnCount - 1}" ss:StyleID="bHead">
              <Data ss:Type="String">
                <xsl:value-of select="$noRowsMessage"/>
              </Data>
            </Cell>
          </Row>
        </xsl:if>
        <!--
        <Row>
          <Cell ss:MergeAcross="{$xslColumnCount}" ss:StyleID="bHead">
            <Data ss:Type="String">
              Prepared By Bouler Technology Capital
            </Data>
          </Cell>
        </Row>
        -->
      </Table>
    </Worksheet>
  </xsl:template>
  
  <xsl:template match="column" mode="xlsColumns">
    <Column ss:Width="{$xlsColumnWidth}"/>
  </xsl:template>

  <xsl:template match="column" mode="xlsHead">
    <Cell ss:StyleID="tblHead">
      <Data ss:Type="String">
        <xsl:value-of select="@name"/>
      </Data>
    </Cell>
  </xsl:template>
  
  <xsl:template match="node()" mode="xlsBody">
    <xsl:variable name="currentNode" select="."/>
    <Row>
      <xsl:for-each select="xalan:nodeset($columns)/columns/column[@printable != 'false' or not(@printable)]">
        <xsl:call-template name="xlsBodyColumn">
          <xsl:with-param name="col" select="."/>
          <xsl:with-param name="currentNode" select="$currentNode"/>
        </xsl:call-template>
      </xsl:for-each>
    </Row>
  </xsl:template>
  
  <xsl:template name="xlsBodyColumn">
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <Cell ss:StyleID="tbl">
      <xsl:choose>
        <xsl:when test="count($col/node()) &gt; 1 or node()[name()='collection']">
          <xsl:attribute name="ss:StyleID">tblWrap</xsl:attribute>
          <Data ss:Type="String">
            <xsl:for-each select="$col/node()">
              <xsl:variable name="thisNode" select="."/>
              <xsl:choose>
                <xsl:when test="name()='attribute'">
                  <xsl:variable name="cnt"><xsl:number/></xsl:variable>
                  <xsl:variable name="valuePath">$currentNode/<xsl:value-of select="@name"/></xsl:variable>
                  <xsl:call-template name="xlsConversion">
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
                      <xsl:call-template name="xlsConversion">
                        <xsl:with-param name="valuePath" select="$valuePath"/>
                        <xsl:with-param name="attr" select="."/>
                        <xsl:with-param name="col" select="$col"/>
                        <xsl:with-param name="currentNode" select="$collectNode"/>
                      </xsl:call-template><!-- <xsl:if test="position()&lt;=last()"><nl/></xsl:if>-->
                    </xsl:for-each>
                  </xsl:for-each>
                </xsl:when>
              </xsl:choose><!-- <xsl:if test="position()!=last()"><nl/></xsl:if> -->
            </xsl:for-each>
          </Data>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="$col/node()">
            <xsl:variable name="thisNode" select="."/>
            <xsl:choose>
              <xsl:when test="name()='attribute'">
                <xsl:variable name="cnt"><xsl:number/></xsl:variable>
                <xsl:variable name="valuePath">$currentNode/<xsl:value-of select="@name"/></xsl:variable>
                <xsl:call-template name="xlsConversionSingle">
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
                    <xsl:call-template name="xlsConversionSingle">
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
        </xsl:otherwise>
      </xsl:choose>
    </Cell>
  </xsl:template>

  <xsl:template name="xlsConversion">
    <xsl:param name="valuePath"/>
    <xsl:param name="attr"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:variable name="value" select="dyn:evaluate($valuePath)"/>
    <xsl:if test="$value != '' or $attr/@ignoreNull">
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
        <xsl:when test="$attr/@conversion='orderstatus'">
          <xsl:if test="$value='false'">Open</xsl:if>
          <xsl:if test="$value='true'">Complete</xsl:if>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='recstatus'">
          <xsl:if test="$value='0'">Open</xsl:if>
          <xsl:if test="$value='1'">Closed</xsl:if>
          <xsl:if test="$value='2'">Approved</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='metalstatus'">
          <xsl:if test="$value='false'">Out of Stock</xsl:if>
          <xsl:if test="$value='true'">In Stock</xsl:if>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='unittype'">
          <xsl:if test="$value='lbs'"> pcs </xsl:if>
          <xsl:if test="$value='ft'"> ft </xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='perunittype'">
          <xsl:if test="$value='lbs'"> / lb </xsl:if>
          <xsl:if test="$value='ft'"> / ft </xsl:if>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='quotetype'">
          <xsl:if test="$value='false'">Quote</xsl:if>
          <xsl:if test="$value='true'">Order</xsl:if>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='warehouse'">
          <xsl:if test="$value='ATZ'">Atlanta</xsl:if>
          <xsl:if test="$value='BRZ'">Bristol</xsl:if>
          <xsl:if test="$value='CHZ'">Chicago</xsl:if>
          <xsl:if test="$value='HOZ'">Houston</xsl:if>
          <xsl:if test="$value='LAZ'">Los Angeles</xsl:if>
          <xsl:if test="$value='PHZ'">New Jersey</xsl:if>
          <xsl:if test="$value='SEZ'">Seattle</xsl:if>
          <xsl:if test="$value='TAZ'">Tampa</xsl:if>
        </xsl:when>      
        <xsl:when test="$attr/@conversion='fid'">
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          <xsl:value-of select="format-number($value,'000000')"/>
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='formatnumber'">
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          <xsl:value-of select="format-number($value,$attr/@pattern)"/>
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='quoted'">
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          "<xsl:value-of select="$value"/>"
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='timestamp'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy HH:mm z')"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='date'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy')"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='time'">
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'HH:mm z')"/>
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='longtime'">
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'MMM dd yyyy HH:mm aa')"/>
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='email'">
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          <xsl:value-of select="$value"/>
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='image'">
        </xsl:when>
        <xsl:when test="$attr/@conversion='small'">
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          <xsl:value-of select="$value"/>
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
          <xsl:value-of select="$value"/>
          <xsl:value-of select="@append" disable-output-escaping="yes"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="(not($value) or $value = '') and $attr/@default and not($attr/@ignoreNull)">
      <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
      <xsl:if test="$attr/@default!='&#xa0;'">
        <xsl:value-of select="$attr/@default" disable-output-escaping="yes"/>
      </xsl:if>
      <xsl:value-of select="@append" disable-output-escaping="yes"/>
    </xsl:if>
  </xsl:template>
  <xsl:template name="xlsConversionSingle">
    <xsl:param name="valuePath"/>
    <xsl:param name="attr"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:variable name="value" select="dyn:evaluate($valuePath)"/>
    <xsl:if test="$value != '' or $attr/@ignoreNull">
      <xsl:choose>
        <xsl:when test="$attr/@conversion='customstatus'">
          <Data ss:Type="String">
            <xsl:call-template name="customstatus">
              <xsl:with-param name="value" select="$value"/>
              <xsl:with-param name="currentNode" select="$currentNode"/>
              <xsl:with-param name="col" select="$col"/>
              <xsl:with-param name="attr" select="$attr"/>
            </xsl:call-template>
          </Data>        
        </xsl:when>
        <xsl:when test="$attr/@conversion='status'">
          <Data ss:Type="String">
            <xsl:if test="$value='true'">Active</xsl:if>
            <xsl:if test="$value='false'">Inactive</xsl:if>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='taxstatus'">
          <Data ss:Type="String">
            <xsl:if test="$value='1'">Active</xsl:if>
            <xsl:if test="$value='2'">Pending</xsl:if>
            <xsl:if test="$value='0'">Inactive</xsl:if>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='bidstatus'">
          <Data ss:Type="String">
            <xsl:if test="$value='0'">New</xsl:if>
            <xsl:if test="$value='1'">Approved</xsl:if>
            <xsl:if test="$value='2'">Disapproved</xsl:if>
            <xsl:if test="$value='3'">Out of date</xsl:if>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='invtype'">
          <Data ss:Type="String">
            <xsl:if test="$value='0'">New Inventory</xsl:if>
            <xsl:if test="$value='1'">Inventory Update</xsl:if>
          </Data>
        </xsl:when>    
        <xsl:when test="$attr/@conversion='orderstatus'">
          <Data ss:Type="String">
            <xsl:if test="$value='false'">Open</xsl:if>
            <xsl:if test="$value='true'">Complete</xsl:if>
          </Data>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='recstatus'">
          <Data ss:Type="String">
            <xsl:if test="$value='0'">Open</xsl:if>
            <xsl:if test="$value='1'">Closed</xsl:if>
            <xsl:if test="$value='2'">Approved</xsl:if>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='metalstatus'">
          <Data ss:Type="String">
            <xsl:if test="$value='false'">Out of Stock</xsl:if>
            <xsl:if test="$value='true'">In Stock</xsl:if>
          </Data>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='unittype'">
          <Data ss:Type="String">
            <xsl:if test="$value='lbs'"> pcs </xsl:if>
            <xsl:if test="$value='ft'"> ft </xsl:if>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='perunittype'">
          <Data ss:Type="String">
            <xsl:if test="$value='lbs'"> / lb </xsl:if>
            <xsl:if test="$value='ft'"> / ft </xsl:if>
          </Data>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='quotetype'">
          <Data ss:Type="String">
            <xsl:if test="$value='false'">Quote</xsl:if>
            <xsl:if test="$value='true'">Order</xsl:if>
          </Data>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='warehouse'">
          <Data ss:Type="String">
            <xsl:if test="$value='ATZ'">Atlanta</xsl:if>
            <xsl:if test="$value='BRZ'">Bristol</xsl:if>
            <xsl:if test="$value='CHZ'">Chicago</xsl:if>
            <xsl:if test="$value='HOZ'">Houston</xsl:if>
            <xsl:if test="$value='LAZ'">Los Angeles</xsl:if>
            <xsl:if test="$value='PHZ'">New Jersey</xsl:if>
            <xsl:if test="$value='SEZ'">Seattle</xsl:if>
            <xsl:if test="$value='TAZ'">Tampa</xsl:if>
          </Data>
        </xsl:when>      
        <xsl:when test="$attr/@conversion='fid'">
          <xsl:attribute name="ss:StyleID">idFmt</xsl:attribute>
          <Data ss:Type="Number">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="format-number($value,'000000')"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='formatnumber'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="format-number($value,$attr/@pattern)"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='quoted'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            "<xsl:value-of select="$value"/>"
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='timestamp'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy HH:mm z')"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='date'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy')"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='time'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="dateHelper:getFormattedDate($value, 'HH:mm z')"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='longtime'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="dateHelper:getFormattedDate($value, 'MMM dd yyyy HH:mm aa')"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='email'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="$value"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='image'">
          <Data ss:Type="String"></Data>
        </xsl:when>
        <xsl:when test="$attr/@conversion='small'">
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="$value"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:when>
        <xsl:otherwise>
          <Data ss:Type="String">
            <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
            <xsl:value-of select="$value"/>
            <xsl:value-of select="@append" disable-output-escaping="yes"/>
          </Data>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="(not($value) or $value = '') and $attr/@default and not($attr/@ignoreNull)">
      <Data ss:Type="String">
        <xsl:value-of select="@prepend" disable-output-escaping="yes"/>
        <xsl:if test="$attr/@default!='&#xa0;'">
          <xsl:value-of select="$attr/@default" disable-output-escaping="yes"/>
        </xsl:if>
        <xsl:value-of select="@append" disable-output-escaping="yes"/>
      </Data>
    </xsl:if>
  </xsl:template>
  <xsl:template name="customstatus">
    <xsl:param name="currentNode"/>
    <xsl:param name="value"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
  </xsl:template>
</xsl:stylesheet>