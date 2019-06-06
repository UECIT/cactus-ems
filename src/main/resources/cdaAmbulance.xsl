<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n1="urn:hl7-org:v3" xmlns:npfitlc="NPFIT:HL7:Localisation" xmlns:n2="urn:hl7-org:v3/meta/voc" xmlns:voc="urn:hl7-org:v3/voc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="n1 n2 voc npfitlc xsi">
	<xsl:output method="html" indent="yes" version="4.01" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="-//W3C//DTD HTML 4.01 Transitional//EN"/>
	<!-- CDA document -->
	<xsl:variable name="title">
		<xsl:choose>
			<xsl:when test="/n1:AmbulanceRequest/n1:title">
				<xsl:value-of select="/n1:AmbulanceRequest/n1:title"/>
			</xsl:when>
			<xsl:otherwise>Ambulance Request</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:param name="debug" select="'yes'"/>
	<xsl:param name="p_doc" select="n1:AmbulanceRequest"/>
	<xsl:template match="/">
		<xsl:apply-templates select="n1:AmbulanceRequest"/>
	</xsl:template>
	<xsl:template match="n1:AmbulanceRequest">
		<html>
			<head>
				<xsl:comment>$trasformVersion=1.0 $updateDate=29/06/2012 Interoperability Team</xsl:comment>
				<xsl:comment>$trasformVersion=1.0 $updateDate=12/10/2012 Interoperability Team - branched for NHS111</xsl:comment>	
				<!-- <meta name='Generator' content='&CDA-Stylesheet;'/> -->
				<xsl:comment> Do NOT edit this HTML directly, it was generated via an XSLT transformation from the original release 2 CDA
          Document. </xsl:comment>
				<title>
					<xsl:value-of select="$title"/>
				</title>
				<style type="text/css">
						body { background-color:#f0f4f5; color: #000000; font-size: 10pt; line-height: normal; font-family: Verdana, Arial, sans-serif; margin: 0; }
						a { color: #0099ff; text-decoration: none }
						.input { color: #003366; font-size: 10pt; font-family: Verdana, Arial, sans-serif; background-color: #ffffff; border: solid 1px }
						div.titlebar { background-color: #eeeeff; border: 1px solid #000000; padding: 3px; margin-bottom: 20px;}
						div.doctitle { font-size: 14pt; margin-bottom: 10px; color:#FFFFFF; background-color:#005eb8; height:70px}
						div.mainContainer { margin-left:18%; margin-right:18%; height: 100%}
						div.logo { width:80px; float:left; margin-top:20px; margin-right:20px; }
						div.vCentred { margin:auto; padding-top:25px; padding-bottom:20px; }
						div.header { font-size: 8pt; margin-bottom: 30px; border: 1px solid #000000; background-color: #ffffee;}
						div.footer { padding:16px; font-size: 8pt; color:#425563; background-color:#d8dde0; min-height:140px; width:100%;}
						.content { position: relative; font-size: 14pt; margin-bottom: 10px; margin-top:30px; }
						p {margin-top: 2px; margin-bottom: 6px;}
						h1 { display:inline; font-weight:normal; font-size: 30pt; color: #000000; }
						h2 { display:inline; font-size: 12pt; font-weight: normal; color: #000000; margin-top: 20px; margin-bottom: 6px; }
						h3 { font-size: 10pt; font-weight: bold; color: #000000; margin-top: 15px; margin-bottom: 6px; }
						h4 { font-size: 10pt; font-weight: bold; text-decoration: underline; color: #000000; margin-top: 6px; margin-bottom: 6px; }
						h5 { font-size: 10pt; font-weight: normal; text-decoration: underline;  color: #000000; margin-top: 4px; margin-bottom: 4px; }
						h6 { font-size: 10pt; font-weight: normal; color: #000000; margin-top: 2px; margin-bottom: 2px; }
						span { padding-right:20px; font-weight:normal }
						table { border: 1px solid #000000; }
						th.default {padding: 3px; color: #000000; background-color: #dddddd; text-align: left;}
						th {padding: 3px; color: #000000; background-color: #dddddd;}
						td {padding: 3px; background-color: #eeeeee;}
						table.titlebar { border: 0px; background-color: #eeeeff; }
						td.titlebar {color: #000000; background-color: #eeeeff; font-weight: bold; }
						th.titlebar {color: #000000; background-color: #eeeeff; font-weight: normal; font-style: italic; text-align: left;}
						th.sectitle {color: #000000; background-color: #ffffee; font-weight: bold; text-align: left;}
						th.participant {color: #000000; background-color: #ffffee; font-weight: bold; font-style: italic; text-align: left;}
						table.header { border: 0px; background-color: #ffffee; }
						td.header {color: #000000; background-color: #ffffee; font-weight: bold;}
						th.header {color: #000000; background-color: #ffffee; font-weight: normal; text-align: left; font-style:italic;}
						/*Classes below map to CDA styleCodes*/
						.Bold {font-weight: bold;}
						.Underline {text-decoration:underline;}
						.Italics {font-style:italic;}
						.Emphasis {font-style:italic;}
						.Rrule {border-right: 1px solid black;}
						.Lrule {border-left: 1px solid black;}
						.Toprule {border-top: 1px solid black;}
						.Botrule {border-right: 1px solid black;}
						.flex {display:flex; margin-bottom:10px;}
						/*Banner styles*/
						div.banner { font-size: 8pt; margin-bottom: 30px; border: 1px solid #000000; background-color: #ffffee;}
						div.banner TABLE { border: 0px; background-color: #ffffee; font-weight: bold; }
						div.banner TD { background-color: #ffffee; vertical-align: top; padding-right: 1em;}
						div.banner TABLE P {margin: 0;}
						.marginless {margin-bottom:0}
						.label {height:100%; float:left; font-style:italic; font-weight: normal; min-width:260px;}
						.footerLabel {height:100%; float:left; font-style:italic; font-weight: normal; min-width:140px;}
						ul {background-color:#FFFFFF; color: #555; font-size: 12px; padding: 0 !important; width: 100%; margin-block-start: 0 !important; margin-block-end: 0 !important; font-family: courier, monospace; border: 1px solid #dedede;}
						li {display:flex; list-style: none; border-bottom: 1px dotted #ccc; padding: 10px; padding-left:50px; height:auto; text-transform: capitalize;}
						.lines {border-left: 1px solid #ffaa9f; border-right: 1px solid #ffaa9f; width: 2px; position:absolute; height: 100%; margin-left: 40px;}​​
				</style>
			</head>
			<xsl:comment>Derived from HL7 Finland R2 Tyylitiedosto: Tyyli_R2_B3_01.xslt</xsl:comment>
			<xsl:comment>Updated by Calvin E. Beebe, Mayo Clinic - Rochester Mn. </xsl:comment>
			<xsl:comment>Updated by Keith W. Boone, Dictaphone - Burlington, MA </xsl:comment>
			<xsl:comment>Updated by Kai U. Heitmann, Heitmann Consulting &amp; Service, NL for VHitG, Germany </xsl:comment>
			<xsl:comment>Updated by Rene Spronk, translate back to english-language labels</xsl:comment>
			<xsl:comment>Updated by Dick Donker, Philips Medical Systems include linkHtml</xsl:comment>
			<xsl:comment>Updated by Tim Pilkington - NHS CFH</xsl:comment>
			<xsl:comment>Updated by Aled Greenhalgh - NHS CFH</xsl:comment>
			<xsl:comment>Updated by Prashant Trivedi - NHS CFH</xsl:comment>
			<xsl:comment>Updated by Dave Barnet - branched for NHS111</xsl:comment>	
			<body>
				<!--moved title to here daba-->
				<div class="doctitle">
					<div class="mainContainer">
						<div class="logo">
							<svg class="nhsuk-logo nhsuk-logo--white" xmlns="http://www.w3.org/2000/svg" role="presentation" focusable="false" viewBox="0 0 40 16">
								  <path fill="#fff" d="M0 0h40v16H0z"></path>
								  <path fill="#005eb8" d="M3.9 1.5h4.4l2.6 9h.1l1.8-9h3.3l-2.8 13H9l-2.7-9h-.1l-1.8 9H1.1M17.3 1.5h3.6l-1 4.9h4L25 1.5h3.5l-2.7 13h-3.5l1.1-5.6h-4.1l-1.2 5.6h-3.4M37.7 4.4c-.7-.3-1.6-.6-2.9-.6-1.4 0-2.5.2-2.5 1.3 0 1.8 5.1 1.2 5.1 5.1 0 3.6-3.3 4.5-6.4 4.5-1.3 0-2.9-.3-4-.7l.8-2.7c.7.4 2.1.7 3.2.7s2.8-.2 2.8-1.5c0-2.1-5.1-1.3-5.1-5 0-3.4 2.9-4.4 5.8-4.4 1.6 0 3.1.2 4 .6"></path>
									<image src="https://assets.nhs.uk/images/nhs-logo.png"></image>
							</svg>
						</div>
						<div class="vCentred">
							<xsl:value-of select="$title"/>	
						</div>
					</div>
				</div>
				<div class="mainContainer content">
					<!-- <xsl:call-template name="patientBanner"/> -->
					<!-- NEW templates -->
					<xsl:call-template name="author"/>
					<xsl:call-template name="callBackContact"/>
					<xsl:call-template name="informationRecipient"/>
					<xsl:call-template name="pertinentInformation"/>
					<xsl:call-template name="pertinentInformation1"/>
					<xsl:call-template name="pertinentInformation2"/>
					<xsl:call-template name="pertinentInformation3"/>
					<xsl:call-template name="pertinentInformation4"/>
					<xsl:call-template name="pertinentInformation5"/>
					<xsl:call-template name="pertinentInformation6"/>
					<xsl:call-template name="pertinentInformation7"/>
					<xsl:call-template name="pertinentInformation8"/>
					<xsl:call-template name="pertinentInformation9"/>
					<xsl:call-template name="reason"/>
				</div>
				<xsl:call-template name="footer"/>
			</body>
		</html>
	</xsl:template>
	<!-- Author -->
	<xsl:template name="author">
		<xsl:if test="/n1:AmbulanceRequest/n1:author">
			<div class="content">
				<span><h1>Author</h1></span>
			</div>
			
			<div id="patientBanner" class="content">
				<xsl:if test="/n1:AmbulanceRequest/n1:author/n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedPerson/n1:name">
					<div class="flex">
						<!-- assignedPerson -->
						<div class="label">assignedPerson: </div>
						<div>
							<xsl:call-template name="cuiName">
								<xsl:with-param name="name" select="/n1:AmbulanceRequest/n1:author/n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedPerson/n1:name"/>
							</xsl:call-template>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="/n1:AmbulanceRequest/n1:author/n1:COCD_TP145200GB01.AssignedAuthor/n1:representedOrganization/n1:name">
					<div class="flex">
						<!-- representedOrganization -->
						<div class="label">representedOrganization: </div>
						<div>
							<xsl:call-template name="cuiName">
								<xsl:with-param name="name" select="/n1:AmbulanceRequest/n1:author/n1:COCD_TP145200GB01.AssignedAuthor/n1:representedOrganization/n1:name"/>
							</xsl:call-template>
						</div>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
	<!-- CallBackContact -->
	<xsl:template name="callBackContact">
		<xsl:text>test</xsl:text>
		<xsl:if test="/n1:AmbulanceRequest/n1:callBackContact">
				<div class="content">
					<span><h1>CallBackContact</h1></span>
				</div>
		</xsl:if>
	</xsl:template>
	<!-- InformationRecipient -->
	<xsl:template name="informationRecipient">
		<xsl:for-each select="/n1:AmbulanceRequest/n1:informationRecipient">
				<div class="content">
					<span><h1>InformationRecipient</h1></span>
				</div>
				
				<div id="patientBanner" class="content">
				
				
				<!-- COCD_TP145203GB03.IntendedRecipient -->
				<xsl:if test="n1:COCD_TP145203GB03.IntendedRecipient/n1:representedOrganization/n1:name">
					<div class="flex">
						<!-- representedOrganization -->
						<div class="label">representedOrganization: </div>
						<div>
							<xsl:call-template name="cuiName">
								<xsl:with-param name="name" select="n1:COCD_TP145203GB03.IntendedRecipient/n1:representedOrganization/n1:name"/>
							</xsl:call-template>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="n1:COCD_TP145203GB03.IntendedRecipient/n1:telecom">
					<div class="flex">
						<!-- telecom -->
						<div>
							<xsl:for-each select="/n1:AmbulanceRequest/n1:informationRecipient/n1:COCD_TP145203GB03.IntendedRecipient/n1:telecom">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='H'">Home </xsl:when>
											<xsl:when test="@use='HP'">Home </xsl:when>
											<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
											<xsl:when test="@use='WP'">Work </xsl:when>
											<xsl:when test="@use='DIR'">Direct </xsl:when>
											<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
											<xsl:when test="@use='BAD'">Bad </xsl:when>
											<xsl:when test="@use='TMP'">Temporary </xsl:when>
											<xsl:when test="@use='AS'">Answer </xsl:when>
											<xsl:when test="@use='EC'">Emergency </xsl:when>
											<xsl:when test="@use='MC'">Mobile </xsl:when>
											<xsl:when test="@use='PG'">Pager </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
										<xsl:choose>
											<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
											<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
											<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
											<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
											<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
											<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
									</div>
									<div>
										<xsl:choose>
											<!-- don't display 'tel:' part of value -->
											<xsl:when test="contains(@value, ':')">
												<xsl:value-of select="substring-after(@value, ':')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="@value"/>
											</xsl:otherwise>
										</xsl:choose>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="n1:COCD_TP145203GB03.IntendedRecipient/n1:addr">
					<div class="flex">
						<!-- telecom -->
						<div>
							<xsl:for-each select="n1:COCD_TP145203GB03.IntendedRecipient/n1:addr">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
											<xsl:when test="@use='H'">Home Address</xsl:when>
											<xsl:when test="@use='WP'">Work Address</xsl:when>
											<xsl:when test="@use='PST'">Postal Address</xsl:when>
											<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
											<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
											<xsl:when test="@use='DIR'">Direct Address</xsl:when>
											<xsl:when test="@use='PUB'">Public Address</xsl:when>
											<xsl:when test="@use='BAD'">Bad Address</xsl:when>
											<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
											<xsl:otherwise>Address</xsl:otherwise>
										</xsl:choose>
									</div>	
									<div>
										<xsl:for-each select="n1:*">
											<xsl:value-of select="."/>
											<xsl:if test="not(position() = last())">
												<br/>
											</xsl:if>
										</xsl:for-each>
									</div>	
								</div>				
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				
				
				<!-- COCD_TP145202GB02.IntendedRecipient -->
				<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:representedOrganization/n1:name">
					<div class="flex">
						<!-- representedOrganization -->
						<div class="label">representedPerson: </div>
						<div>
							<xsl:call-template name="cuiName">
								<xsl:with-param name="name" select="n1:COCD_TP145202GB02.IntendedRecipient/n1:representedOrganization/n1:name"/>
							</xsl:call-template>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:telecom">
					<div class="flex">
						<!-- telecom -->
						<div>
							<xsl:for-each select="/n1:AmbulanceRequest/n1:informationRecipient/n1:COCD_TP145202GB02.IntendedRecipient/n1:telecom">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='H'">Home </xsl:when>
											<xsl:when test="@use='HP'">Home </xsl:when>
											<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
											<xsl:when test="@use='WP'">Work </xsl:when>
											<xsl:when test="@use='DIR'">Direct </xsl:when>
											<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
											<xsl:when test="@use='BAD'">Bad </xsl:when>
											<xsl:when test="@use='TMP'">Temporary </xsl:when>
											<xsl:when test="@use='AS'">Answer </xsl:when>
											<xsl:when test="@use='EC'">Emergency </xsl:when>
											<xsl:when test="@use='MC'">Mobile </xsl:when>
											<xsl:when test="@use='PG'">Pager </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
										<xsl:choose>
											<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
											<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
											<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
											<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
											<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
											<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
									</div>
									<div>
										<xsl:choose>
											<!-- don't display 'tel:' part of value -->
											<xsl:when test="contains(@value, ':')">
												<xsl:value-of select="substring-after(@value, ':')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="@value"/>
											</xsl:otherwise>
										</xsl:choose>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:addr">
					<div class="flex">
						<!-- telecom -->
						<div>
							<xsl:for-each select="n1:COCD_TP145202GB02.IntendedRecipient/n1:addr">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
											<xsl:when test="@use='H'">Home Address</xsl:when>
											<xsl:when test="@use='WP'">Work Address</xsl:when>
											<xsl:when test="@use='PST'">Postal Address</xsl:when>
											<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
											<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
											<xsl:when test="@use='DIR'">Direct Address</xsl:when>
											<xsl:when test="@use='PUB'">Public Address</xsl:when>
											<xsl:when test="@use='BAD'">Bad Address</xsl:when>
											<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
											<xsl:otherwise>Address</xsl:otherwise>
										</xsl:choose>
									</div>	
									<div>
										<xsl:for-each select="n1:*">
											<xsl:value-of select="."/>
											<xsl:if test="not(position() = last())">
												<br/>
											</xsl:if>
										</xsl:for-each>
									</div>	
								</div>				
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
			</div>
		</xsl:for-each>
	</xsl:template>
	<!-- pertinentInformation -->
	<xsl:template name="pertinentInformation">
		<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation">
			<div class="content">
				<span><h1>pertinentInformation - Trauma</h1></span>
			</div>
			<xsl:call-template name="cuiFlagCode">
				<xsl:with-param name="code" select="n1:pertinentTraumaFlag/n1:code/@displayName"/>
			</xsl:call-template>
			<xsl:call-template name="cuiFlagValue">
				<xsl:with-param name="value" select="n1:pertinentTraumaFlag/n1:value/@value"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<!-- pertinentInformation1 -->
	<xsl:template name="pertinentInformation1">
		<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation1">
			<div class="content">
				<span><h1>pertinentInformation1 - Fire</h1></span>
			</div>
			<xsl:call-template name="cuiFlagCode">
				<xsl:with-param name="code" select="n1:pertinentFireFlag/n1:code/@displayName"/>
			</xsl:call-template>
			<xsl:call-template name="cuiFlagValue">
				<xsl:with-param name="value" select="n1:pertinentFireFlag/n1:value/@value"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<!-- pertinentInformation2 -->
	<xsl:template name="pertinentInformation2">
		<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation2">
			<div class="content">
				<span><h1>pertinentInformation2 - SceneSafe</h1></span>
			</div>
			<xsl:call-template name="cuiFlagCode">
				<xsl:with-param name="code" select="n1:pertinentSceneSafeFlag/n1:code/@displayName"/>
			</xsl:call-template>
			<xsl:call-template name="cuiFlagValue">
				<xsl:with-param name="value" select="n1:pertinentSceneSafeFlag/n1:value/@value"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<!-- pertinentInformation3 -->
	<xsl:template name="pertinentInformation3">
		<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation3">
			<div class="content">
				<span><h1>pertinentInformation3 - Police</h1></span>
			</div>
			<xsl:call-template name="cuiFlagCode">
				<xsl:with-param name="code" select="n1:pertinentPoliceFlag/n1:code/@displayName"/>
			</xsl:call-template>
			<xsl:call-template name="cuiFlagValue">
				<xsl:with-param name="value" select="n1:pertinentPoliceFlag/n1:value/@value"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<!-- pertinentInformation4 -->
	<xsl:template name="pertinentInformation4">
		<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation4">
			<div class="content">
				<span><h1>pertinentInformation4 - Trapped</h1></span>
			</div>
			<xsl:call-template name="cuiFlagCode">
				<xsl:with-param name="code" select="n1:pertinentTrappedFlag/n1:code/@displayName"/>
			</xsl:call-template>
			<xsl:call-template name="cuiFlagValue">
				<xsl:with-param name="value" select="n1:pertinentTrappedFlag/n1:value/@value"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<!-- pertinentInformation5 -->
	<xsl:template name="pertinentInformation5">
		<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5">
			<div class="content">
				<span><h1>pertinentInformation5 - EncounterEvent</h1></span>
			</div>
			
			<!-- Informant  -->
			<div class="content">
				<span><h2>Informant</h2></span>
			</div>
			<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:informant/n1:COCD_TP145007UK03.RelatedEntity/n1:relationshipHolder/n1:name">
				<div class="flex">
					<!-- assignedPerson -->
					<div class="label">relationshipHolder: </div>
					<div>
						<xsl:call-template name="cuiName">
							<xsl:with-param name="name" select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:informant/n1:COCD_TP145007UK03.RelatedEntity/n1:relationshipHolder/n1:name"/>
						</xsl:call-template>
					</div>
				</div>
			</xsl:if>
			<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:informant/n1:COCD_TP145007UK03.RelatedEntity/n1:telecom">
					<div class="flex">
						<!-- telecom -->
						<div>
							<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:informant/n1:COCD_TP145007UK03.RelatedEntity/n1:telecom">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='H'">Home </xsl:when>
											<xsl:when test="@use='HP'">Home </xsl:when>
											<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
											<xsl:when test="@use='WP'">Work </xsl:when>
											<xsl:when test="@use='DIR'">Direct </xsl:when>
											<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
											<xsl:when test="@use='BAD'">Bad </xsl:when>
											<xsl:when test="@use='TMP'">Temporary </xsl:when>
											<xsl:when test="@use='AS'">Answer </xsl:when>
											<xsl:when test="@use='EC'">Emergency </xsl:when>
											<xsl:when test="@use='MC'">Mobile </xsl:when>
											<xsl:when test="@use='PG'">Pager </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
										<xsl:choose>
											<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
											<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
											<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
											<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
											<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
											<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
									</div>
									<div>
										<xsl:choose>
											<!-- don't display 'tel:' part of value -->
											<xsl:when test="contains(@value, ':')">
												<xsl:value-of select="substring-after(@value, ':')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="@value"/>
											</xsl:otherwise>
										</xsl:choose>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:informant/n1:COCD_TP145007UK03.RelatedEntity/n1:addr">
					<div class="flex">
						<div>
							<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:informant/n1:COCD_TP145007UK03.RelatedEntity/n1:addr">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
											<xsl:when test="@use='H'">Home Address</xsl:when>
											<xsl:when test="@use='WP'">Work Address</xsl:when>
											<xsl:when test="@use='PST'">Postal Address</xsl:when>
											<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
											<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
											<xsl:when test="@use='DIR'">Direct Address</xsl:when>
											<xsl:when test="@use='PUB'">Public Address</xsl:when>
											<xsl:when test="@use='BAD'">Bad Address</xsl:when>
											<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
											<xsl:otherwise>Address</xsl:otherwise>
										</xsl:choose>
									</div>	
									<div>
										<xsl:for-each select="n1:*">
											<xsl:value-of select="."/>
											<xsl:if test="not(position() = last())">
												<br/>
											</xsl:if>
										</xsl:for-each>
									</div>	
								</div>				
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				
				<!-- Location  -->
				<div class="content">
					<span><h2>Event Location</h2></span>
				</div>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:location/n1:COCD_TP145222GB02.HealthCareFacility/n1:location/n1:name">
					<div class="flex">
						<!-- assignedPerson -->
						<div class="label">locationName: </div>
						<div>
							<xsl:call-template name="cuiName">
								<xsl:with-param name="name" select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:location/n1:COCD_TP145222GB02.HealthCareFacility/n1:location/n1:name"/>
							</xsl:call-template>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:location/n1:COCD_TP145222GB02.HealthCareFacility/n1:location/n1:addr">
					<div class="flex">
						<div>
							<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:location/n1:COCD_TP145222GB02.HealthCareFacility/n1:location/n1:addr">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
											<xsl:when test="@use='H'">Home Address</xsl:when>
											<xsl:when test="@use='WP'">Work Address</xsl:when>
											<xsl:when test="@use='PST'">Postal Address</xsl:when>
											<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
											<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
											<xsl:when test="@use='DIR'">Direct Address</xsl:when>
											<xsl:when test="@use='PUB'">Public Address</xsl:when>
											<xsl:when test="@use='BAD'">Bad Address</xsl:when>
											<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
											<xsl:otherwise>Address</xsl:otherwise>
										</xsl:choose>
									</div>	
									<div>
										<xsl:for-each select="n1:*">
											<xsl:value-of select="."/>
											<xsl:if test="not(position() = last())">
												<br/>
											</xsl:if>
										</xsl:for-each>
									</div>	
								</div>				
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				
				<!-- RecordTarget  -->
				<div class="content">
					<span><h2>RecordTarget</h2></span>
				</div>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:name">
					<div class="flex">
						<!-- assignedPerson -->
						<div class="label">patient name: </div>
						<div>
							<xsl:call-template name="cuiName">
								<xsl:with-param name="name" select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:name"/>
							</xsl:call-template>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:name">
					<div class="flex">
						<!-- assignedPerson -->
						<div class="label">patient gender: </div>
						<div>
							<xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:administrativeGenderCode/@displayName"/>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:name">
					<div class="flex">
						<!-- assignedPerson -->
						<div class="label">patient language: </div>
						<div>
							<xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:languageCommunication/n1:languageCode/@code"/>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:telecom">
					<div class="flex">
						<!-- telecom -->
						<div>
							<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:telecom">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='H'">Home </xsl:when>
											<xsl:when test="@use='HP'">Home </xsl:when>
											<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
											<xsl:when test="@use='WP'">Work </xsl:when>
											<xsl:when test="@use='DIR'">Direct </xsl:when>
											<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
											<xsl:when test="@use='BAD'">Bad </xsl:when>
											<xsl:when test="@use='TMP'">Temporary </xsl:when>
											<xsl:when test="@use='AS'">Answer </xsl:when>
											<xsl:when test="@use='EC'">Emergency </xsl:when>
											<xsl:when test="@use='MC'">Mobile </xsl:when>
											<xsl:when test="@use='PG'">Pager </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
										<xsl:choose>
											<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
											<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
											<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
											<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
											<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
											<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
											<xsl:otherwise/>
										</xsl:choose>
									</div>
									<div>
										<xsl:choose>
											<!-- don't display 'tel:' part of value -->
											<xsl:when test="contains(@value, ':')">
												<xsl:value-of select="substring-after(@value, ':')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="@value"/>
											</xsl:otherwise>
										</xsl:choose>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:addr">
					<div class="flex">
						<div>
							<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation5/n1:pertinentEncounterEvent/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:addr">
								<div class="flex">
									<div class="label">
										<xsl:choose>
											<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
											<xsl:when test="@use='H'">Home Address</xsl:when>
											<xsl:when test="@use='WP'">Work Address</xsl:when>
											<xsl:when test="@use='PST'">Postal Address</xsl:when>
											<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
											<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
											<xsl:when test="@use='DIR'">Direct Address</xsl:when>
											<xsl:when test="@use='PUB'">Public Address</xsl:when>
											<xsl:when test="@use='BAD'">Bad Address</xsl:when>
											<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
											<xsl:otherwise>Address</xsl:otherwise>
										</xsl:choose>
									</div>	
									<div>
										<xsl:for-each select="n1:*">
											<xsl:value-of select="."/>
											<xsl:if test="not(position() = last())">
												<br/>
											</xsl:if>
										</xsl:for-each>
									</div>	
								</div>				
							</xsl:for-each>
						</div>
					</div>
				</xsl:if>
				
				
		</xsl:if>
	</xsl:template>
	<!-- pertinentInformation6 -->
	<xsl:template name="pertinentInformation6">
		<xsl:for-each select="/n1:AmbulanceRequest/n1:pertinentInformation6">
			<div class="content">
				<span><h1>pertinentInformation6 - TriageDisposition</h1></span>
			</div>
			<div class="flex">
				<!-- displayName -->
				<div class="label">code: </div>
				<div>
					<xsl:value-of select="n1:pertinentTriageDisposition/n1:value/@code"/>
				</div>
			</div>
			<div class="flex">
				<!-- displayName -->
				<div class="label">codeSystem: </div>
				<div>
					<xsl:value-of select="n1:pertinentTriageDisposition/n1:value/@codeSystem"/>
				</div>
			</div>
			<div class="flex">
				<!-- displayName -->
				<div class="label">displayName: </div>
				<div>
					<xsl:value-of select="n1:pertinentTriageDisposition/n1:value/@displayName"/>
				</div>
			</div>
		</xsl:for-each>
	</xsl:template>
	<!-- pertinentInformation7 -->
	<xsl:template name="pertinentInformation7">
		<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation7">
				<div class="content">
					<span><h1>pertinentInformation7 - Notes</h1></span>
				</div>
				<div class="flex">
				<div class="label"><xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation7/n1:pertinentAdditionalNotes/n1:code/@displayName"/>: </div>
				<div>
					<xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation7/n1:pertinentAdditionalNotes/n1:text/Notes"/>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	<!-- pertinentInformation8 -->
	<xsl:template name="pertinentInformation8">
		<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation8">
				<div class="content">
					<span><h1>pertinentInformation8 - PermissionToView</h1></span>
				</div>
				
				<div class="flex">
					<div class="label">effectiveFrom: </div>
					<div>
						<xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation8/n1:COCD_TP146050GB01.PermissionToView/n1:effectiveTime/n1:low/@value"/>
					</div>
				</div>
				
				<div class="flex">
					<div class="label">effectiveTo: </div>
					<div>
						<xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation8/n1:COCD_TP146050GB01.PermissionToView/n1:effectiveTime/n1:high/@value"/>
					</div>
				</div>
				
				<div class="flex">
					<div class="label"><xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation8/COCD_TP146050GB01.PermissionToView/value/@value"/>: </div>
					<div>
						<xsl:value-of select="/n1:AmbulanceRequest/n1:pertinentInformation8/COCD_TP146050GB01.PermissionToView/value/@code"/>
					</div>
				</div>
				
		</xsl:if>
	</xsl:template>
	<!-- pertinentInformation9 -->
	<xsl:template name="pertinentInformation9">
		<xsl:if test="/n1:AmbulanceRequest/n1:pertinentInformation9">
				<div class="content">
					<span><h1>pertinentInformation9</h1></span>
				</div>
		</xsl:if>
	</xsl:template>
	<!-- Reason -->
	<xsl:template name="reason">
		<xsl:if test="/n1:AmbulanceRequest/n1:reason">
				<div class="content">
					<span><h1>Reason</h1></span>
				</div>
		</xsl:if>
	</xsl:template>
	<!-- Get a Name  -->
	<xsl:template name="getName">
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$name/n1:family">
				<xsl:if test="$name/n1:prefix">
					<xsl:value-of select="$name/n1:prefix"/>
					<xsl:text> </xsl:text>
				</xsl:if>
				<xsl:value-of select="$name/n1:given"/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="$name/n1:family"/>
				<xsl:if test="$name/n1:suffix">
					<xsl:text> </xsl:text>
					<xsl:value-of select="$name/n1:suffix"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$name"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Format name as per CUI guidance  -->
	<xsl:template name="cuiFlagCode">
		<xsl:param name="code"/>
		<xsl:if test="$code">
			<div class="flex">
				<!-- displayName -->
				<div class="label">displayName: </div>
				<div>
					<xsl:value-of select="$code"/>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="cuiFlagValue">
		<xsl:param name="value"/>
		<xsl:if test="$value">
			<div class="flex">
				<!-- value -->
				<div class="label">value: </div>
				<div>
					<xsl:value-of select="$value"/>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	
	<!-- Format name as per CUI guidance  -->
	<xsl:template name="cuiName">
		<xsl:param name="name"/>
		<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'"/>
		<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
		<xsl:choose>
			<xsl:when test="$name/n1:family">
				<xsl:value-of select="translate($name/n1:family, $smallcase, $uppercase)"/>
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$name/n1:given"/>
				<xsl:if test="$name/n1:suffix">
					<xsl:text> </xsl:text>
					<xsl:value-of select="$name/n1:suffix"/>
				</xsl:if>
				<xsl:if test="$name/n1:prefix">
					<xsl:text> </xsl:text>(<xsl:value-of select="$name/n1:prefix"/>)
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$name"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	
	<!-- Format NHS number as per CUI guidance: ### ### #### -->
	<xsl:template name="cuiNHSNo">
		<xsl:param name="nhsNo"/>
		<xsl:value-of select="substring($nhsNo, 1, 3)"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="substring($nhsNo, 4, 3)"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="substring($nhsNo, 7)"/>
	</xsl:template>
	<!--  Format Date 
    outputs a date in Month Day, Year form
    -->
	<xsl:template name="formatDate">
		<xsl:param name="date"/>
		<xsl:variable name="month" select="substring ($date, 5, 2)"/>
		<xsl:value-of select="substring ($date, 7, 2)"/>
		<xsl:if test="substring ($date, 7, 2)">
			<xsl:text>-</xsl:text>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="$month='01'">
				<xsl:text>Jan</xsl:text>
			</xsl:when>
			<xsl:when test="$month='02'">
				<xsl:text>Feb</xsl:text>
			</xsl:when>
			<xsl:when test="$month='03'">
				<xsl:text>Mar</xsl:text>
			</xsl:when>
			<xsl:when test="$month='04'">
				<xsl:text>Apr </xsl:text>
			</xsl:when>
			<xsl:when test="$month='05'">
				<xsl:text>May</xsl:text>
			</xsl:when>
			<xsl:when test="$month='06'">
				<xsl:text>Jun</xsl:text>
			</xsl:when>
			<xsl:when test="$month='07'">
				<xsl:text>Jul</xsl:text>
			</xsl:when>
			<xsl:when test="$month='08'">
				<xsl:text>Aug</xsl:text>
			</xsl:when>
			<xsl:when test="$month='09'">
				<xsl:text>Sep</xsl:text>
			</xsl:when>
			<xsl:when test="$month='10'">
				<xsl:text>Oct</xsl:text>
			</xsl:when>
			<xsl:when test="$month='11'">
				<xsl:text>Nov</xsl:text>
			</xsl:when>
			<xsl:when test="$month='12'">
				<xsl:text>Dec</xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:text>-</xsl:text>
		<xsl:value-of select="substring ($date, 1, 4)"/>
		<xsl:if test="string-length($date)>8">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="substring ($date, 9, 2)"/>
			<xsl:text>:</xsl:text>
			<xsl:value-of select="substring ($date, 11, 2)"/>
		</xsl:if>
	</xsl:template>
	<!-- Patient Banner as per CUI guidelines -->
	<xsl:template name="patientBanner">
		<div id="patientBanner" class="content">
			<xsl:if test="/n1:AmbulanceRequest/n1:author/n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedPerson/n1:name">
				<h1>
					<span>
						<xsl:call-template name="cuiName">
							<xsl:with-param name="name" select="/n1:AmbulanceRequest/n1:author/n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedPerson/n1:name"/>
						</xsl:call-template>
					</span>
				</h1>
			</xsl:if>
			<h2>
				<span>
					Patient
				</span>
			</h2>
			<xsl:if test="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatientPatient/n1:birthTime">
				<h2>
					<span>
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date" select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:birthTime/@value"/>
						</xsl:call-template>
					</span>
				</h2>
			</xsl:if>
			<xsl:if test="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:administrativeGenderCode">
				<h2>
					<span>
						<xsl:value-of select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:patientPatient/n1:administrativeGenderCode/@displayName"/>
					</span>
				</h2>
			</xsl:if>
		</div>
		<div class="content">
			<xsl:if test="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:id/@root='2.16.840.1.113883.2.1.4.1'">
				<div class="flex">
					<!-- Verified NHS Number -->
					<div class="label">NHS No.</div>
					<div>
						<xsl:call-template name="cuiNHSNo">
						
							<xsl:with-param name="nhsNo" select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:id[@root='2.16.840.1.113883.2.1.4.1']/@extension"/>
						</xsl:call-template>
					</div>
				</div>
			</xsl:if>
			<xsl:if test="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:id/@root='2.16.840.1.113883.2.1.3.2.4.18.23'">
				<div class="flex">
				<!-- Unverified NHS Number -->
					<div class="label">Unverified NHS No.</div>
					<div>
						<xsl:call-template name="cuiNHSNo">
							<xsl:with-param name="nhsNo" select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:id[@root='2.16.840.1.113883.2.1.3.2.4.18.23']/@extension"/>
						</xsl:call-template>
					</div>
				</div>
			</xsl:if>
			<xsl:for-each select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:id[@root='2.16.840.1.113883.2.1.3.2.4.18.24']">
				<!-- Local ID -->
				<div class="flex">
					<div class="label">Local Patient ID</div>
					<div>
						<xsl:value-of select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:id[@root='2.16.840.1.113883.2.1.3.2.4.18.24']/@extension"/>
					</div>
				</div>
			</xsl:for-each>
		</div>
		<div class="content">
			<xsl:for-each select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:addr">
				<div class="flex">
					<div class="label">
						<xsl:choose>
							<xsl:when test="@use='TMP'">Temporary Address</xsl:when>
							<xsl:when test="@use='H'">Home Address</xsl:when>
							<xsl:when test="@use='WP'">Work Address</xsl:when>
							<xsl:when test="@use='PST'">Postal Address</xsl:when>
							<xsl:when test="@use='HP'">Primary Home Address</xsl:when>
							<xsl:when test="@use='HV'">Holiday Home Address</xsl:when>
							<xsl:when test="@use='DIR'">Direct Address</xsl:when>
							<xsl:when test="@use='PUB'">Public Address</xsl:when>
							<xsl:when test="@use='BAD'">Bad Address</xsl:when>
							<xsl:when test="@use='PHYS'">Visit Address</xsl:when>
							<xsl:otherwise>Address</xsl:otherwise>
						</xsl:choose>
					</div>
					<div>
						<xsl:for-each select="n1:*">
							<xsl:value-of select="."/>
							<br/>
						</xsl:for-each>
					</div>
				</div>
			</xsl:for-each>
			<xsl:for-each select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:telecom">
				<div class="flex">
					<div class="label">
						<xsl:choose>
							<xsl:when test="@use='H'">Home </xsl:when>
							<xsl:when test="@use='HP'">Home </xsl:when>
							<xsl:when test="@use='HV'">Vacation/Temporary </xsl:when>
							<xsl:when test="@use='WP'">Work </xsl:when>
							<xsl:when test="@use='DIR'">Direct </xsl:when>
							<xsl:when test="@use='PUB'">Switchboard/Office </xsl:when>
							<xsl:when test="@use='BAD'">Bad </xsl:when>
							<xsl:when test="@use='TMP'">Temporary </xsl:when>
							<xsl:when test="@use='AS'">Answer </xsl:when>
							<xsl:when test="@use='EC'">Emergency </xsl:when>
							<xsl:when test="@use='MC'">Mobile </xsl:when>
							<xsl:when test="@use='PG'">Pager </xsl:when>
							<xsl:otherwise/>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
							<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
							<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
							<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
							<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
							<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
							<xsl:otherwise/>
						</xsl:choose>
					</div>
					<div>
						<xsl:choose>
							<!-- don't display 'tel:' part of value -->
							<xsl:when test="contains(@value, ':')">
								<xsl:value-of select="substring-after(@value, ':')"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@value"/>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</div>
			</xsl:for-each>
			<xsl:if test="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:providerOrganization">
				<div class="flex">	
					<div class="label">
						<xsl:value-of select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:providerOrganization/n1:standardIndustryClassCode/@displayName"/>
					</div>
					<div>
						<xsl:value-of select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:providerOrganization/n1:name"/>
						<br/>
						<xsl:for-each select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:providerOrganization/n1:addr/n1:*">
							<xsl:value-of select="."/>
							<br/>
						</xsl:for-each>
					</div>
				</div>
				<xsl:for-each select="/n1:AmbulanceRequest/n1:recordTarget/n1:COCD_TP145201GB01.PatientRole/n1:providerOrganization/n1:telecom">
					<div class="flex">
						<div class="label">
							<xsl:choose>
								<xsl:when test="contains(@value, 'mailto')">Email </xsl:when>
								<xsl:when test="contains(@value, 'tel')">Phone </xsl:when>
								<xsl:when test="contains(@value, 'fax')">Fax </xsl:when>
								<xsl:when test="contains(@value, 'tty')">Textphone </xsl:when>
								<xsl:when test="contains(@value, 'sms')">SMS </xsl:when>
								<xsl:when test="contains(@value, 'voice')">Voice </xsl:when>
								<xsl:otherwise/>
							</xsl:choose>
						</div>
						<div>
							<xsl:choose>
								<!-- don't display 'tel:' part of value -->
								<xsl:when test="contains(@value, ':')">
									<xsl:value-of select="substring-after(@value, ':')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@value"/>
								</xsl:otherwise>
							</xsl:choose>
						</div>
					</div>
				</xsl:for-each>
			</xsl:if>
		</div>
	</xsl:template>
	<!--  Header  -->
	<xsl:template name="header">
	</xsl:template>
	<!--  Footer  -->
	<xsl:template name="footer">
		<div class="footer">
			<div class="mainContainer">
				<div style="float:right">
					<div class="flex marginless" >
						<div class="footerLabel">
							<xsl:text>Document Created</xsl:text>
						</div>
						<div>
							<xsl:call-template name="formatDate">
								<xsl:with-param name="date" select="/n1:AmbulanceRequest/n1:effectiveTime/@value"/>
							</xsl:call-template>
						</div>
					</div>
					<div class="flex marginless">
						<div class="footerLabel">		
							<xsl:text>Document Owner</xsl:text>
						</div>
						<div>
							<xsl:value-of select="/n1:AmbulanceRequest/n1:custodian/n1:COCD_TP145018UK03.AssignedCustodian/n1:representedCustodianOrganization/n1:name"/>
						</div>
					</div>
					<xsl:for-each select="/n1:AmbulanceRequest/n1:author">
						<div class="flex marginless" >
							<div class="footerLabel">
								<xsl:text>Authored by</xsl:text>
							</div>
							<div>
								<xsl:choose>
									<xsl:when test="n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedPerson/n1:name">
										<xsl:call-template name="getName">
											<xsl:with-param name="name" select="n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedPerson/n1:name"/>
										</xsl:call-template>
										<xsl:text> - </xsl:text>
										<xsl:value-of select="n1:COCD_TP145200GB01.AssignedAuthor/n1:code/@displayName"/>
										<xsl:text>, </xsl:text>
										<xsl:value-of select="n1:COCD_TP145200GB01.AssignedAuthor/n1:representedOrganization/n1:name"/>
									</xsl:when>
									<xsl:when test="n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedAuthoringDevice/n1:manufacturerModelName">
										<xsl:value-of select="n1:COCD_TP145200GB01.AssignedAuthor/n1:assignedAuthoringDCOCD_TP145200GB01.AssignedAuthoracturerModelName"/>
										<xsl:if test="n1:COCD_TP145200GB01.AssignedAuthor/n1:representedOrganization/n1:name">
											<xsl:text> at </xsl:text>
											<xsl:value-of select="n1:COCD_TP145200GB01.AssignedAuthor/n1:representedOrganization/n1:name"/>
										</xsl:if>
									</xsl:when>
									<xsl:when test="n1:COCD_TP145200GB01.AssignedAuthor/n1:representedOrganization/n1:name">
										<xsl:value-of select="n1:COCD_TP145200GB01.AssignedAuthor/n1:representedOrganization/n1:name"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>Unknown</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:text> on </xsl:text>
								<xsl:call-template name="formatDate">
									<xsl:with-param name="date" select="n1:time/@value"/>
								</xsl:call-template>
							</div>
						</div>
					</xsl:for-each>
					<xsl:for-each select="/n1:AmbulanceRequest/n1:authenticator">
						<div class="flex marginless" >
							<div class="footerLabel">
								<xsl:text>Authenticated by</xsl:text>
							</div>
							<div>
								<xsl:if test="n1:assignedEntity/n1:assignedPerson/n1:name">
									<xsl:call-template name="getName">
										<xsl:with-param name="name" select="n1:assignedEntity/n1:assignedPerson/n1:name"/>
									</xsl:call-template>
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>
								</xsl:if>
								<xsl:text> on </xsl:text>
								<xsl:call-template name="formatDate">
									<xsl:with-param name="date" select="n1:time/@value"/>
								</xsl:call-template>
							</div>
						</div>			
					</xsl:for-each>
					<xsl:for-each select="/n1:AmbulanceRequest/n1:dataEnterer">
						<div class="flex marginless" >
							<div class="footerLabel">
								<xsl:text>Entered by</xsl:text>
							</div>
							<div>
								<xsl:if test="n1:assignedEntity/n1:assignedPerson/n1:name">
									<xsl:call-template name="getName">
										<xsl:with-param name="name" select="n1:assignedEntity/n1:assignedPerson/n1:name"/>
									</xsl:call-template>
									<!--<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>-->
								</xsl:if>
								<xsl:if test="n1:assignedEntity/n1:code">
									<xsl:text> - </xsl:text>
									<xsl:value-of select="n1:assignedEntity/n1:code/@displayName"/>
								</xsl:if>
								<xsl:if test="n1:assignedEntity/n1:representedOrganization/n1:name">
									<xsl:text> , </xsl:text>
									<xsl:value-of select="n1:assignedEntity/n1:representedOrganization/n1:name"/>
								</xsl:if>
								<xsl:if test="n1:time">
									<xsl:text> on </xsl:text>
									<xsl:call-template name="formatDate">
										<xsl:with-param name="date" select="n1:time/@value"/>
									</xsl:call-template>
								</xsl:if>
							</div>
						</div>		
					</xsl:for-each>
					<xsl:call-template name="performer"/>
							<!--PRTR1 Get consent here-->
					<xsl:for-each select="/n1:AmbulanceRequest/n1:authorization">
						<xsl:sort select="@typeCode"/>	
						<div class="flex marginless" >
							<div class="footerLabel">
								<xsl:choose>
									<xsl:when test="@typeCode='AUTH' and not(preceding-sibling::*/@typeCode='AUTH')">
										<xsl:choose>
											<xsl:when test="count(following-sibling::*[@typeCode='AUTH'])=0">
												<xsl:text>Consent Status</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>Consent Statuses</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text/>
									</xsl:otherwise>
								</xsl:choose>
							</div>
							<div>
								<xsl:value-of select="n1:COCD_TP146226GB02.Consent/n1:code/@displayName"/>
							</div>
						</div>
					</xsl:for-each>	
					<div class="flex marginless" >
						<div class="footerLabel">	
							<xsl:text>Document ID</xsl:text>
						</div>
						<div>
							<xsl:value-of select="/n1:AmbulanceRequest/n1:id/@root"/>
						</div>	
					</div>
					<div class="flex marginless" >
						<div class="footerLabel">	
							<xsl:text>Version</xsl:text>
						</div>
						<div>
							<xsl:value-of select="/n1:AmbulanceRequest/n1:versionNumber/@value"/>
						</div>
					</div>
					<xsl:for-each select="n1:informationRecipient">
						<xsl:sort select="@typeCode"/>
						<div class="flex marginless" >
							<div class="footerLabel">	
								<xsl:choose>
									<xsl:when test="@typeCode='PRCP'and not(preceding-sibling::*/@typeCode='PRCP')">
										<xsl:choose>
											<xsl:when test="count(following-sibling::*[@typeCode='PRCP'])=0">
												<xsl:text>Primary Recipient</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>Primary Recipients</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:when test="@typeCode='TRC'and not(preceding-sibling::*/@typeCode='TRC')">
										<xsl:choose>
											<xsl:when test="count(following-sibling::*[@typeCode='TRC'])=0">
												<xsl:text>Copy Recipient</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>Copy Recipients</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text/>
									</xsl:otherwise>
								</xsl:choose>
							</div>
							<div>
								<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:assignedPerson/n1:name">
									<xsl:call-template name="getName">
										<xsl:with-param name="name" select="n1:COCD_TP145202GB02.IntendedRecipient/n1:assignedPerson/n1:name"/>
									</xsl:call-template>
								</xsl:if>
								<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/npfitlc:recipientRoleCode/@displayName">
									<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:assignedPerson/n1:name">
										<xsl:text> - </xsl:text>
									</xsl:if>
									<xsl:value-of select="n1:COCD_TP145202GB02.IntendedRecipient/n2:recipientRoleCode/@displayName"/>
								</xsl:if>
								<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:representedOrganization/n1:name">
									<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:assignedPerson/n1:name or n1:COCD_TP145202GB02.IntendedRecipient/n2:recipientRoleCode/@displayName">
										<xsl:text>, </xsl:text>
									</xsl:if>
									<xsl:call-template name="getName">
										<xsl:with-param name="name" select="n1:COCD_TP145202GB02.IntendedRecipient/n1:representedOrganization/n1:name"/>
									</xsl:call-template>
								</xsl:if>
				
								<xsl:if test="n1:COCD_TP145202GB02.IntendedRecipient/n1:telecom">
									<xsl:choose>
										<xsl:when test="contains(n1:COCD_TP145202GB02.IntendedRecipient/n1:telecom/@value, 'mailto')">Email</xsl:when>
										<xsl:when test="contains(n1:COCD_TP145202GB02.IntendedRecipient/n1:telecom/@value, 'tel')">Phone</xsl:when>
									</xsl:choose>
								</xsl:if>
				
								<xsl:value-of select="substring-after(n1:COCD_TP145202GB02.IntendedRecipient/n1:telecom/@value,':')"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="translateCode">
		<xsl:param name="code"/>
		<!--xsl:value-of select="document('voc.xml')/systems/system[@root=$code/@codeSystem]/code[@value=$code/@code]/@displayName"/-->
		<!--xsl:value-of select="document('codes.xml')/*/code[@code=$code]/@display"/-->
		<xsl:choose>
			<!-- lookup table Telecom URI -->
			<xsl:when test="$code='tel'">
				<xsl:text>Tel</xsl:text>
			</xsl:when>
			<xsl:when test="$code='fax'">
				<xsl:text>Fax</xsl:text>
			</xsl:when>
			<xsl:when test="$code='HP'">
				<xsl:text>Home</xsl:text>
			</xsl:when>
			<xsl:when test="$code='WP'">
				<xsl:text>Workplace</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>{$code}?</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- 
    Contact Information
  -->
	<xsl:template name="getContactInfo">
		<xsl:param name="contact"/>
		<xsl:apply-templates select="$contact/n1:addr"/>
		<xsl:apply-templates select="$contact/n1:telecom"/>
	</xsl:template>
	<xsl:template match="n1:addr">
		<xsl:for-each select="n1:streetAddressLine">
			<xsl:value-of select="."/>
			<br/>
		</xsl:for-each>
		<xsl:if test="n1:streetName">
			<xsl:value-of select="n1:streetName"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="n1:houseNumber"/>
			<br/>
		</xsl:if>
		<xsl:value-of select="n1:postalCode"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="n1:city"/>
		<xsl:if test="n1:state">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="n1:state"/>
		</xsl:if>
		<br/>
	</xsl:template>
	<xsl:template match="n1:telecom">
		<xsl:variable name="type" select="substring-before(@value, ':')"/>
		<xsl:variable name="value" select="substring-after(@value, ':')"/>
		<xsl:if test="$type">
			<xsl:call-template name="translateCode">
				<xsl:with-param name="code" select="$type"/>
			</xsl:call-template>
			<xsl:text>: </xsl:text>
			<xsl:text> </xsl:text>
			<xsl:value-of select="$value"/>
			<xsl:if test="@use">
				<xsl:text> (</xsl:text>
				<xsl:call-template name="translateCode">
					<xsl:with-param name="code" select="@use"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</xsl:if>
			<br/>
		</xsl:if>
	</xsl:template>
	<xsl:template name="support">
		<table width="100%">
			<xsl:for-each select="/n1:AmbulanceRequest/n1:participant[@typeCode='IND']">
				<tr>
					<td>
						<b>
							<xsl:for-each select="n1:associatedEntity/n1:code">
								<xsl:call-template name="translateCode">
									<xsl:with-param name="code" select="."/>
								</xsl:call-template>
								<xsl:text> </xsl:text>
							</xsl:for-each>
						</b>
					</td>
					<td>
						<xsl:call-template name="getName">
							<xsl:with-param name="name" select="n1:associatedEntity/n1:associatedPerson/n1:name"/>
						</xsl:call-template>
					</td>
				</tr>
				<tr>
					<td/>
					<td>
						<xsl:call-template name="getContactInfo">
							<xsl:with-param name="contact" select="n1:associatedEntity"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	<!--PRTR1 This is for Service Event template [0..*] and each service event can have multiple performers [0..*]-->
	<xsl:template name="performer">
		<xsl:for-each select="//n1:documentationOf">
			<div class="flex marginless">
				<div class="footerLabel">
					<xsl:choose>
						<xsl:when test="@typeCode='DOC' and not(preceding-sibling::*/@typeCode='DOC')">
							<xsl:choose>
								<xsl:when test="count(following-sibling::*[@typeCode='DOC'])=0">
									<xsl:text>Report of</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>Reports of</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text/>
						</xsl:otherwise>
					</xsl:choose>
				</div>
				<div>
					<xsl:if test="n1:COCD_TP146227GB02.ServiceEvent/n1:code">
						<xsl:value-of select="n1:COCD_TP146227GB02.ServiceEvent/n1:code/@displayName"/>
					</xsl:if>
					<xsl:if test="n1:COCD_TP146227GB02.ServiceEvent/n1:effectiveTime">
						<xsl:text> - </xsl:text>
						<xsl:text>From </xsl:text>
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date" select="n1:COCD_TP146227GB02.ServiceEvent/n1:effectiveTime/n1:low/@value"/>
						</xsl:call-template>
						<xsl:text>  To </xsl:text>
						<xsl:call-template name="formatDate">
							<xsl:with-param name="date" select="n1:COCD_TP146227GB02.ServiceEvent/n1:effectiveTime/n1:high/@value"/>
						</xsl:call-template>
					</xsl:if>
					<!--<xsl:if test="n1:serviceEvent/n1:performer/n1:assignedEntity/n1:assignedPerson/n1:name">
						<xsl:text> , </xsl:text>
						<xsl:call-template name="getName">
							<xsl:with-param name="name" select="n1:serviceEvent/n1:performer/n1:assignedEntity/n1:assignedPerson/n1:name"/>
						</xsl:call-template>
					</xsl:if>-->
				<!--</td>-->
				<!--PRTR1 Get the performer loop here-->
				</div>
			</div>
			<xsl:for-each select="n1:COCD_TP146227GB02.ServiceEvent/n1:performer">
				<xsl:sort select="@typeCode"/>
				<div class="flex marginless">
					<div class="footerLabel">
						<xsl:choose>
							<xsl:when test="@typeCode='PRF' and not(preceding-sibling::*/@typeCode='PRF')">
								<xsl:choose>
									<xsl:when test="count(following-sibling::*[@typeCode='PRF'])=0">
									   <span class="label">
											<xsl:text>Performed by </xsl:text>
										</span> 
									</xsl:when>
									<xsl:otherwise>
									    <span class="label">
											<xsl:text>Performed by </xsl:text> <!--PRTR1 Changed with comment from KZS-->
										</span>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text/>
							</xsl:otherwise>
						</xsl:choose>
					</div>
					<div>
						<xsl:call-template name="getName">
								<xsl:with-param name="name" select="n1:COCD_TP145210GB01.AssignedEntity/n1:assignedPerson/n1:name"/>
						</xsl:call-template>
						<xsl:if test="not(position() = last())">
							<xsl:text>, </xsl:text>
						</xsl:if>
					</div>
				</div>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>