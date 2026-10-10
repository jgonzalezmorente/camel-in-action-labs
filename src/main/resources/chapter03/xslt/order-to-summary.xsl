<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="/purchaseOrder">
        <orderSummary>
            <product>
                <xsl:value-of select="@name"/>
            </product>
            <quantity>
                <xsl:value-of select="@amount"/>
            </quantity>
            <total>
                <xsl:value-of select="@price * @amount"/>
            </total>
        </orderSummary>
    </xsl:template>

</xsl:stylesheet>