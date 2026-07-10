package chapter02;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.jms.ConnectionFactory;

public class OrderRouterWithFilterTest extends CamelTestSupport {
    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        return camelContext;
    }

    @Test
    public void testPlacingOrders() throws Exception {
        this.getMockEndpoint("mock:xml").expectedMessageCount(1);
        this.assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:src/data_filter?noop=true").to("jms:incomingOrders");

                from("jms:incomingOrders")
                        .choice()
                            .when(header("CamelFileName").endsWith(".xml"))
                                .to("jms:xmlOrders")
                            .when(header("CamelFileName").regex("^*.\\.(csv|csl)$"))
                                .to("jms:csvOrders")
                            .otherwise()
                                .to("jms:badOrders");

                from("jms:xmlOrders")
                        .filter(xpath("/order[not(@test)]"))
                        .log("Received XML order: ${header.CamelFileName}")
                        .to("mock:xml");
            }
        };
    }
}
