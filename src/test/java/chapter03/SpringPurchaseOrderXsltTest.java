package chapter03;

import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;

public class SpringPurchaseOrderXsltTest extends CamelSpringTestSupport {

    @Test
    public void testXslt() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        mock.message(0).predicate(new XPathBuilder(
                "/orderSummary[product='Camel in Action'"
                        + " and quantity=1 and total=6999]"));

        this.template.sendBody(
                "direct:order",
                "<purchaseOrder name=\"Camel in Action\""
                        + " price=\"6999.0\" amount=\"1.0\"/>");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("chapter03/SpringPurchaseOrderXsltTest.xml");
    }

}
