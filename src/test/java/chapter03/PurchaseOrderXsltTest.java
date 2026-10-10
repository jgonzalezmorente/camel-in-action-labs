package chapter03;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class PurchaseOrderXsltTest extends CamelTestSupport {

    @Test
    public void testXslt() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        mock.message(0).predicate(new XPathBuilder(
                "/orderSummary[product='Camel in Action'"
                        + " and quantity=2 and total=13998]"));

        this.template.sendBody(
                "direct:order",
                "<purchaseOrder name=\"Camel in Action\""
                        + " price=\"6999.0\" amount=\"2.0\"/>");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {

            @Override
            public void configure() {
                from("direct:order")
                        .to("xslt:chapter03/xslt/order-to-summary.xsl")
                        .log("XML transformado: ${body}")
                        .to("mock:result");
            }
        };

    }
}
