<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0"
  xmlns:x="urn:schemas-microsoft-com:office:excel"
  xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
  
  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:template name="xlsStyle">
    <Style ss:ID="Default" ss:Name="Normal">
      <Alignment ss:Vertical="Bottom"/>
      <Borders/>
      <Font ss:FontName="Verdana"/>
      <Interior/>
      <NumberFormat/>
      <Protection/>
    </Style>
    <Style ss:ID="idFmt">
      <NumberFormat ss:Format="000000"/>
      <Borders>
        <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
      </Borders>
    </Style>
    <Style ss:ID="dFmt">
      <NumberFormat ss:Format="Short Date"/>
    </Style>
    <Style ss:ID="bNorm">
      <Font ss:FontName="Verdana" ss:Bold="1"/>
    </Style>
    <Style ss:ID="bHead">
      <Alignment ss:Horizontal="Center" ss:Vertical="Bottom"/>
      <Font ss:FontName="Verdana" ss:Size="20.0" ss:Bold="1"/>
    </Style>
    <Style ss:ID="tblHead">
      <Borders>
        <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="2"/>
        <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
      </Borders>
      <Font ss:FontName="Verdana" ss:Bold="1"/>
    </Style>
    <Style ss:ID="tbl">
      <Borders>
        <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
      </Borders>
      <Font ss:FontName="Verdana"/>
    </Style>
    <Style ss:ID="tblWrap">
      <Alignment ss:Vertical="Bottom" ss:WrapText="1"/>
      <Borders>
        <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
        <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
      </Borders>
      <Font ss:FontName="Verdana"/>
    </Style>
    <Style ss:ID="rptHead">
      <Borders>
        <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
      </Borders>
      <Alignment ss:Horizontal="Center" ss:Vertical="Bottom"/>
      <Font ss:FontName="Verdana" ss:Bold="1"/>
    </Style>
    <Style ss:ID="rpt">
      <Font ss:FontName="Verdana"/>
    </Style>
    <Style ss:ID="rptSubTot">
      <Borders>
        <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
      </Borders>
      <Font ss:FontName="Verdana" ss:Bold="1"/>
    </Style>
    <Style ss:ID="rptTot">
      <Borders>
        <Border ss:Position="Top" ss:LineStyle="Double" ss:Weight="3"/>
      </Borders>
      <Font ss:FontName="Verdana" ss:Bold="1"/>
    </Style>
    <Style ss:ID="rptB">
      <Font ss:FontName="Verdana" ss:Bold="1"/>
    </Style>
    <Style ss:ID="rptL1">
      <Font ss:FontName="Verdana" ss:Bold="1"/>
    </Style>
    <Style ss:ID="rptL2">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="1"/>
    </Style>
    <Style ss:ID="rptL3">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="2"/>
    </Style>
    <Style ss:ID="rptL4">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="3"/>
    </Style>
    <Style ss:ID="rptL5">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="4"/>
    </Style>
    <Style ss:ID="rptL6">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="5"/>
    </Style>
    <Style ss:ID="rptL7">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="6"/>
    </Style>
    <Style ss:ID="rptL8">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="7"/>
    </Style>
    <Style ss:ID="rptL9">
      <Font ss:FontName="Verdana"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="8"/>
    </Style>
    <Style ss:ID="rptL2B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="1"/>
    </Style>
    <Style ss:ID="rptL3B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="2"/>
    </Style>
    <Style ss:ID="rptL4B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="3"/>
    </Style>
    <Style ss:ID="rptL5B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="4"/>
    </Style>
    <Style ss:ID="rptL6B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="5"/>
    </Style>
    <Style ss:ID="rptL7B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="6"/>
    </Style>
    <Style ss:ID="rptL8B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="7"/>
    </Style>
    <Style ss:ID="rptL9B">
      <Font ss:FontName="Verdana" ss:Bold="1"/><Alignment ss:Horizontal="Left" ss:Vertical="Bottom" ss:Indent="8"/>
    </Style>
    <Style ss:ID="rptRed">
      <Font ss:FontName="Verdana" ss:Color="red"/>
    </Style>
    <Style ss:ID="rptRedB">
      <Font ss:FontName="Verdana" ss:Color="red" ss:Bold="1"/>
    </Style>
    <Style ss:ID="rptSubTotRed">
      <Borders>
        <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
      </Borders>
      <Font ss:FontName="Verdana" ss:Color="red" ss:Bold="1"/>
    </Style>
    <Style ss:ID="rptTotRed">
      <Borders>
        <Border ss:Position="Top" ss:LineStyle="Double" ss:Weight="3"/>
      </Borders>
      <Font ss:FontName="Verdana" ss:Color="red" ss:Bold="1"/>
    </Style>
  </xsl:template>  
</xsl:stylesheet>