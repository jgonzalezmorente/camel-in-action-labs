package chapter02;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.jms.ConnectionFactory;

public class OrderRouterTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost");

        // Registra el componente JMS usando AUTO_ACKNOWLEDGE:
        // los mensajes consumidos se confirman automáticamente al broker
        camelContext.addComponent(
                "jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory)
        );
        return camelContext;
    }

    @Test
    public void testPlacingOrders() throws Exception {
        getMockEndpoint("mock:xml").expectedMessageCount(3);
        getMockEndpoint("mock:csv").expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws  Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:src/data?noop=true").to("jms:incomingOrders");

                from("jms:incomingOrders")
                        .choice()
                            .when(header("CamelFileName").endsWith(".xml"))
                                .to("jms:xmlOrders")
                            .when(header("CamelFileName").endsWith(".csv"))
                                .to("jms:csvOrders");

                from("jms:xmlOrders")
                        .log("Received XML order: ${header.CamelFileName}")
                        .to("mock:xml");

                from("jms:csvOrders")
                        .log("Received CSV order: ${header.CamelFileName}")
                        .to("mock:csv");
            }
        };
    }

}