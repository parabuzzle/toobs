<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:dateHelper="org.toobs.framework.transformpipeline.xslExtentions.DateHelper"
  xmlns:s="org.toobs.framework.transformpipeline.xslExtentions.XMLEncodeHelper"
  xmlns:c="org.toobs.framework.pres.component.util.ComponentHelper"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:dyn="http://exslt.org/dynamic"
  exclude-result-prefixes="xalan dateHelper dyn s c">
  
  <xsl:param name="context"/>
  <xsl:param name="layoutId"/>
  <xsl:param name="component"/>
  <xsl:param name="queryString"/>
  
  <xsl:template name="conversion">
    <xsl:param name="valuePath"/>
    <xsl:param name="attr"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="rowNum"/>
    
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
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='true'">Active</xsl:if>
          <xsl:if test="$value='false'">Inactive</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='truefalse'">
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='true'">True</xsl:if>
          <xsl:if test="$value='false'">False</xsl:if>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='taxstatus'">
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='1'">Active</xsl:if>
          <xsl:if test="$value='2'">Pending</xsl:if>
          <xsl:if test="$value='0'">Inactive</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='bidstatus'">
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='0'">New</xsl:if>
          <xsl:if test="$value='1'">Approved</xsl:if>
          <xsl:if test="$value='2'">Disapproved</xsl:if>
          <xsl:if test="$value='3'">Out of date</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='invtype'">
          <xsl:attribute name="style">width:15%;</xsl:attribute>
          <xsl:if test="$value='0'">New Inventory</xsl:if>
          <xsl:if test="$value='1'">Inventory Update</xsl:if>
        </xsl:when>    
        <xsl:when test="$attr/@conversion='orderstatus'">
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='false'">Open</xsl:if>
          <xsl:if test="$value='true'">Complete</xsl:if>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='recstatus'">
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='0'">Open</xsl:if>
          <xsl:if test="$value='1'">Closed</xsl:if>
          <xsl:if test="$value='2'">Approved</xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='metalstatus'">
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='false'">Out of Stock</xsl:if>
          <xsl:if test="$value='true'">In Stock</xsl:if>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='unittype'">
          <xsl:attribute name="style">width:1%;</xsl:attribute>
          <xsl:if test="$value='lbs'"> pcs </xsl:if>
          <xsl:if test="$value='ft'"> ft </xsl:if>
        </xsl:when>
        <xsl:when test="$attr/@conversion='perunittype'">
          <xsl:attribute name="style">width:1%;</xsl:attribute>
          <xsl:if test="$value='lbs'"> / lb </xsl:if>
          <xsl:if test="$value='ft'"> / ft </xsl:if>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='quotetype'">
          <xsl:attribute name="style">width:10%;</xsl:attribute>
          <xsl:if test="$value='false'">Quote</xsl:if>
          <xsl:if test="$value='true'">Order</xsl:if>
        </xsl:when> 
        <xsl:when test="$attr/@conversion='warehouse'">
          <xsl:attribute name="style">width:6%;</xsl:attribute>
          <xsl:if test="$value='ATZ'">Atlanta</xsl:if>
          <xsl:if test="$value='BRZ'">Bristol</xsl:if>
          <xsl:if test="$value='CHZ'">Chicago</xsl:if>
          <xsl:if test="$value='HOZ'">Houston</xsl:if>
          <xsl:if test="$value='LAZ'">Los Angeles</xsl:if>
          <xsl:if test="$value='PHZ'">New Jersey</xsl:if>
          <xsl:if test="$value='SEZ'">Seattle</xsl:if>
          <xsl:if test="$value='TAZ'">Tampa</xsl:if>
        </xsl:when>      
        <xsl:when test="$attr/@conversion='selector'">
          <xsl:attribute name="style">width:2%;padding-top:2px;</xsl:attribute>
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="selector">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
          </xsl:call-template>        
        </xsl:when>
        <xsl:when test="$attr/@conversion='deleteor'">
          <xsl:attribute name="style">width:2%;padding-top:2px;</xsl:attribute>
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="deleteor">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
          </xsl:call-template>        
        </xsl:when>
        <xsl:when test="$attr/@conversion='textor'">
          <!--<xsl:attribute name="style">width:5%;padding-top:2px;</xsl:attribute>-->
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="textor">
            <xsl:with-param name="prepend" select="@prepend"/>
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
            <xsl:with-param name="append" select="@append"/>
            <xsl:with-param name="inputSize" select="@inputSize"/>
          </xsl:call-template>        
        </xsl:when> 
        <xsl:when test="$attr/@conversion='hideor'">
          <!--<xsl:attribute name="style">width:0%;</xsl:attribute>-->
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="hideor">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
          </xsl:call-template>        
        </xsl:when> 
        <xsl:when test="$attr/@conversion='metalor'">
          <xsl:attribute name="style">width:5%;padding-right:2px;</xsl:attribute>
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="metalor">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
          </xsl:call-template>        
        </xsl:when>  
        <xsl:when test="$attr/@conversion='feetor'">
          <xsl:attribute name="style">padding-right:2px;</xsl:attribute>
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="feetor">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
            <xsl:with-param name="append" select="@append"/>
          </xsl:call-template>        
        </xsl:when> 
        <xsl:when test="$attr/@conversion='inchor'">
          <xsl:attribute name="style">padding-right:2px;</xsl:attribute>
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="inchor">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
            <xsl:with-param name="append" select="@append"/>
          </xsl:call-template>        
        </xsl:when>     
        <xsl:when test="$attr/@conversion='fractionor'">
          <xsl:attribute name="style">padding-right:2px;</xsl:attribute>
          <xsl:variable name="selectorPath">$currentNode/<xsl:value-of select="$col/@selector"/></xsl:variable>
          <xsl:call-template name="fractionor">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="selectorPath" select="$selectorPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
            <xsl:with-param name="append" select="@append"/>
          </xsl:call-template>    
        </xsl:when>  
        <xsl:when test="$attr/@conversion='link'">
          <xsl:variable name="linkPath">$currentNode/<xsl:value-of select="$attr/@urlattribute"/></xsl:variable>
          <xsl:call-template name="link">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="linkPath" select="$linkPath"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
            <xsl:with-param name="col" select="$col"/>
            <xsl:with-param name="attr" select="$attr"/>
          </xsl:call-template>    
        </xsl:when>  
        <xsl:when test="$attr/@conversion='fid'">
          <xsl:attribute name="style">font-size:90%;<xsl:if test="$col/@style"><xsl:value-of select="$col/@style"/></xsl:if></xsl:attribute>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="format-number($value,'000000')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='pct'">
          <xsl:attribute name="style">width:10%;<xsl:if test="$col/@style"><xsl:value-of select="$col/@style"/></xsl:if></xsl:attribute>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="format-number(number($value * 100),'##0.0')"/>%
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='formatnumber'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="format-number($value,$attr/@pattern)"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='quoted'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          "<xsl:value-of select="$value"/>"
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='timestamp'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy HH:mm z')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='date'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'MM/dd/yyyy')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='time'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'HH:mm z')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>        
        <xsl:when test="$attr/@conversion='longtime'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="dateHelper:getFormattedDate($value, 'MMM dd yyyy HH:mm aa')"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='email'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <a href="mailto:{$value}"><xsl:value-of select="$value"/></a>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='image'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <img src="{$value}" alt="">
            <xsl:if test="$col/@imgstyle"><xsl:attribute name="style"><xsl:value-of select="$col/@imgstyle"/></xsl:attribute></xsl:if>
          </img>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='small'">
          <xsl:attribute name="style"><xsl:if test="$col/@style"><xsl:value-of select="$col/@style"/></xsl:if></xsl:attribute>
          <xsl:apply-templates select="@prepend"/>
          <span class="dtSm"><xsl:value-of select="$value"/></span>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:when test="$attr/@conversion='trunc'">
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="c:getSummary($value,120)"/>
          <xsl:apply-templates select="@append"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
          <xsl:apply-templates select="@prepend"/>
          <xsl:value-of select="$value"/>
          <xsl:apply-templates select="@append"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="(not($value) or $value = '') and $attr/@default and not($attr/@ignoreNull)">
      <xsl:if test="$col/@style"><xsl:attribute name="style"><xsl:value-of select="$col/@style"/></xsl:attribute></xsl:if>
      <xsl:apply-templates select="@prepend"/>
      <xsl:value-of select="$attr/@default" disable-output-escaping="yes"/>
      <xsl:apply-templates select="@append"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="@prepend">
    <xsl:value-of select="." disable-output-escaping="yes"/>
    <!--
    <xsl:choose>
      <xsl:when test="../@prependWidth">
        <span class="fleft" style="display:block;width:{../@prependWidth};"><xsl:value-of select="." disable-output-escaping="yes"/></span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="." disable-output-escaping="yes"/>
      </xsl:otherwise>
    </xsl:choose>
    -->
  </xsl:template>
  <xsl:template match="@append">
    <xsl:value-of select="." disable-output-escaping="yes"/>
    <!--
    <xsl:choose>
      <xsl:when test="../@appendWidth">
        <span style="display:block;width:{../@appendWidth};"><xsl:value-of select="." disable-output-escaping="yes"/></span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="." disable-output-escaping="yes"/>
      </xsl:otherwise>
    </xsl:choose>
    -->
  </xsl:template>

  <xsl:template name="selector">
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$selectPath"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
      <input class="dtSelect checkbox" type="checkbox" value="{$value | $attr/@default}" comp="{$component}" name="{$attr/@inputname}">
        <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
      </input>
      <xsl:if test="$attr/@default">
        <input type="hidden" name="{$attr/@inputname}" value="false"/>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="link">
    <xsl:param name="value"/>
    <xsl:param name="linkPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$linkPath"/>
      </xsl:call-template>
    </xsl:variable>
    <a guid="{$currentNode/guid | $currentNode/id}">
      <xsl:if test="$attr/@gid"><xsl:attribute name="id"><xsl:value-of select="$attr/@gid"/><xsl:value-of select="$currentNode/guid | $currentNode/id"/></xsl:attribute></xsl:if>
      <xsl:attribute name="href">
        <xsl:choose>
          <xsl:when test="$nodeVal != ''"><xsl:value-of select="$context"/><xsl:value-of select="$nodeVal"/></xsl:when>
          <xsl:otherwise>javascript:void(0);</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:if test="$attr/@linkClass">
        <xsl:attribute name="class"><xsl:value-of select="$attr/@linkClass"/></xsl:attribute>
      </xsl:if>
      <xsl:value-of select="$value"/>
    </a>
  </xsl:template>
  
  <xsl:template name="deleteor">
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$selectPath"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
      <input class="dtSelect checkbox mirror" type="checkbox" comp="{$component}" mirror="d-{$value}" value="true">
        <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
      </input>
    </xsl:if>
    <input id="d-{$value}" type="hidden" name="{$attr/@inputname}" value="false"/>
  </xsl:template>

  <xsl:template name="textor">
    <xsl:param name="prepend"/>
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    <xsl:param name="append"/>
    <xsl:param name="inputSize"/>
    
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$selectPath"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
      <xsl:apply-templates select="@prepend"/>
      <input class="dtSelect text" type="text" value="{$value}" comp="{$component}" name="{$attr/@inputname}" style="width :{$inputSize};">
        <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
      </input>  
      <xsl:apply-templates select="@append"/>  
    </xsl:if>
  </xsl:template>  
  
  <xsl:template name="metalor">
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$selectPath"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
      <select class="dtSelect text" type="text" value="{$value}" comp="{$component}" name="{$attr/@inputname}">
        <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
        <xsl:for-each select="$currentNode/../MetalWarehouseVO | $currentNode/../../MetalWarehouseVO">
          <option value="{./guid}">
            <xsl:if test="(./name='LAZ' and not($value)) or ./name=$value"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if>
            <xsl:value-of select="./displayName"/>
          </option>
        </xsl:for-each>
      </select>   
    </xsl:if>
  </xsl:template> 
  
  <xsl:template name="feetor">
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    <xsl:param name="append"/>
    
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$selectPath"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
      <select class="dtSelect text" type="text" value="{$value}" comp="{$component}" name="{$attr/@inputname}">
        <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
        <option value="0" selected="true">0</option>
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
        <option value="6">6</option>
        <option value="7">7</option>
        <option value="8">8</option>
        <option value="9">9</option>
        <option value="10">10</option>
        <option value="11">11</option>
        <option value="12">12</option>
        <option value="13">13</option>
        <option value="14">14</option>
        <option value="15">15</option>
        <option value="16">16</option>
        <option value="18">17</option>
        <option value="18">18</option>
        <option value="19">19</option>
        <option value="20">20</option>        
      </select>   
      <xsl:apply-templates select="@append"/> 
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="inchor">
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    <xsl:param name="append"/>
      
  <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
  <xsl:variable name="nodeVal">
    <xsl:call-template name="evaluate">
      <xsl:with-param name="currentNode" select="$currentNode"/>
      <xsl:with-param name="evalPath" select="$selectPath"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
    <select class="dtSelect text" type="text" value="{$value}" comp="{$component}" name="{$attr/@inputname}">
      <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
      <option value="0" selected="true">0</option>
      <option value="1">1</option>
      <option value="2">2</option>
      <option value="3">3</option>
      <option value="4">4</option>
      <option value="5">5</option>
      <option value="6">6</option>
      <option value="7">7</option>
      <option value="8">8</option>
      <option value="9">9</option>
      <option value="10">10</option>
      <option value="11">11</option>     
    </select>   
    <xsl:apply-templates select="@append"/> 
  </xsl:if>
  </xsl:template>  
  
  <xsl:template name="fractionor">
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    <xsl:param name="append"/>
    
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$selectPath"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
      <select class="dtSelect text" type="text" value="{$value}" comp="{$component}" name="{$attr/@inputname}">
        <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
        <option value="0" selected="true">0</option>
        <option value=".0625" >1/16</option>
        <option value=".125">1/8</option>
        <option value=".1875">3/16</option>
        <option value=".25">1/4</option>
        <option value=".3125" >5/16</option>
        <option value=".375">3/8</option>
        <option value=".4375">7/16</option>
        <option value=".5">1/2</option>
        <option value=".5625">9/16</option>
        <option value=".625">5/8</option>
        <option value=".6875">11/16</option>
        <option value=".75">3/4</option>
        <option value=".8125" >13/16</option>
        <option value=".875">7/8</option>
        <option value=".9375">15/16</option>   
      </select> 
      <xsl:apply-templates select="@append"/>  
    </xsl:if>
  </xsl:template>  
  
  <xsl:template name="hideor">
    <xsl:param name="value"/>
    <xsl:param name="selectorPath"/>
    <xsl:param name="currentNode"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
    
    <xsl:variable name="selectPath">$currentNode/<xsl:value-of select="$attr/@selector"/></xsl:variable>
    <xsl:variable name="nodeVal">
      <xsl:call-template name="evaluate">
        <xsl:with-param name="currentNode" select="$currentNode"/>
        <xsl:with-param name="evalPath" select="$selectPath"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="not($attr/@selector) or $attr/@value=$nodeVal">
      <input type="hidden" value="{$value}" comp="{$component}" name="{$attr/@inputname}">
        <xsl:attribute name="{$col/@selector}"><xsl:value-of select="dyn:evaluate($selectorPath)"/></xsl:attribute>
        <xsl:if test="$attr/@id"><xsl:attribute name="id"><xsl:value-of select="$attr/@id"/>-<xsl:value-of select="$currentNode/guid | $currentNode/id"/></xsl:attribute></xsl:if>
      </input>    
    </xsl:if>
  </xsl:template>    
  
  <xsl:template name="evaluate">
    <xsl:param name="currentNode"/>
    <xsl:param name="evalPath"/>
    <xsl:value-of select="dyn:evaluate($evalPath)"/>
  </xsl:template>

  <xsl:template name="customstatus">
    <xsl:param name="currentNode"/>
    <xsl:param name="value"/>
    <xsl:param name="col"/>
    <xsl:param name="attr"/>
  </xsl:template>
</xsl:stylesheet>