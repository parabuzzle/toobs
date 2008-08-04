<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:param name="context"/>
  <xsl:template match="component">
    <tok><xsl:apply-templates select="objects"/></tok>
  </xsl:template>
  <xsl:template match="objects">
    <div id="leftcol">
      <div class="navbar">
        <!-- *********************************Start Menu****************************** -->
        <div class="mainDiv">
          <div class="topItem">Entity &amp; Contact Management</div>
          <div class="dropMenu">
            <!-- -->
            <div class="subMenu" style="display:inline;">
              <div class="subItem">
                <a href="{$context}BusinessRecordNew.action">New Business</a>
              </div>
              <div class="subItem">
                <a href="{$context}BusinessRecordSearch.action">Business Search</a>
              </div>
              <div class="subItem">
                <a href="{$context}BusinessSearch.xhtml">New Business Search</a>
              </div>
              <div class="subItem">
                <a href="{$context}PrepareEmployeeEdit.action">New Employee</a>
              </div>
              <div class="subItem">
                <a href="{$context}Employees.action">Employee Search</a>
              </div>
            </div>
          </div>
        </div>
        <div class="mainDiv">
          <div class="topItem">Customer Service Delivery</div>
          <div class="dropMenu">
            <div class="subMenu" style="display:none;">
              <div class="subItem">
                <a href="{$context}HomePage.action">Common Questions</a>
              </div>
              <div class="subItem">
                <a href="{$context}SupportResourcesHome.xhtml">Support Resources</a>
              </div>

              <div class="subListItem">
                <a href="{$context}HomePage.action">Support Info</a>
              </div>
              <div class="subListItem">
                <a href="{$context}HomePage.action">Document</a>
              </div>
              <div class="subListItem">
                <a href="{$context}HomePage.action">File Downloads</a>
              </div>
              <div class="subListItem">
                <a href="{$context}HelpTickets.action">Help Ticket Search</a>
              </div>
              <div class="subListItem">
                <a href="{$context}HelpTicketEdit.action">New Help Ticket</a>
              </div>
              <div class="subItem">
                <a href="{$context}HomePage.action">Information Center</a>
              </div>
            </div>
          </div>
        </div>

        <div class="mainDiv">
          <div class="topItem">Website Management</div>
          <div class="dropMenu">
            <!-- -->
            <div class="subMenu" style="display:inline;">
              <div class="subItem">
                <a href="{$context}IRInquiries.action">Leads &amp; Inquiries</a>
              </div>
              <div class="subItem">
                <a href="#">Form Configuration</a>
              </div>
              <div class="subListItem">
                <a href="{$context}ContactUsConfig.action">Contact Us</a>
              </div>
              <div class="subListItem">
                <a href="{$context}InfoRequestConfig.action">Information Requests</a>
              </div>
              <div class="subListItem">
                <a href="{$context}NewsletterConfig.action">Newsletters</a>
              </div>
              <div class="subItem">
                <a href="{$context}IRAnalysis.action">Analysis</a>
              </div>
            </div>
          </div>
        </div>

        <!--
            <div class="mainDiv" >
            <div class="topItem">Customer Service Admin</div>        
            <div class="dropMenu" >
            <div class="subMenu" style="display:none;">
            <div class="subItem">
            <a href="{$context}HomePage.action">Dashboard</a>
            </div>
            <div class="subItem">
            <a href="{$context}HomePage.action">Clients</a>
            </div>
            <div class="subItem">
            <a href="{$context}HelpTickets.action">Help Tickets</a>
            </div>
            <div class="subItem">
            <a href="{$context}HomePage.action">Downloads</a>
            </div>
            <div class="subItem">
            <a href="{$context}HomePage.action">Bulletin Board</a>
            </div>
            <div class="subItem">
            <a href="{$context}HomePage.action">Documentation</a>
            </div>
            </div>
            </div>
            </div>
          -->

        <div class="mainDiv">
          <div class="topItem">System Admin</div>
          <div class="dropMenu">
            <div class="subMenu" style="display:none;">
              <div class="subItem">
                <a href="{$context}BusinessTypes.xhtml">Business Types</a>
              </div>
              <div class="subItem">
                <a href="{$context}Departments.xhtml">Departments</a>
              </div>
              <div class="subItem">
                <a href="{$context}Positions.xhtml">Positions</a>
              </div>
              <div class="subItem">
                <a href="{$context}Products.xhtml">Products</a>
              </div>
              <div class="subItem">
                <a href="{$context}AppearanceEdit.xhtml">Appearance</a>
              </div>
              <!-- These are just temps so we can go both ways for a while -->
              <div class="subItem">
                <a href="{$context}ProfileEdit.xhtml">Profile</a>
              </div>
              <div class="subItem">
                <a href="{$context}BusinessSearch.xhtml">Business Search</a>
              </div>
            </div>
          </div>
        </div>

        <!-- *********************************End Menu****************************** -->
      </div>
      <script type="text/javascript" src="javascript/xpmenuv21.js"/>
    </div>
    <div id="middlecol" style="">
      <a href="#" onClick="hidemenu();">
        <img border="0" src="img/menu/left-nav-hide.png"/>
      </a>
    </div>
    <div id="middlecolnomenu" style="display:none;">
      <a href="#" onClick="showmenu();">
        <img border="0" src="img/menu/left-nav-show.png"/>
      </a>
    </div>
  </xsl:template>
</xsl:stylesheet>
