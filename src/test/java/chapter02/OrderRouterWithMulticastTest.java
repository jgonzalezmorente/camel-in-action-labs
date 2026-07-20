package chapter02;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.jms.ConnectionFactory;

public class OrderRouterWithMulticastTest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("activemq-data");
        super.setUp();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

        // Registra el componente JMS usando AUTO_ACKNOWLEDGE:
        // los mensajes consumidos se confirman automáticamente al broker
        camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        return camelContext;
    }

    @Test
    public void testPlacingOrders() throws Exception {
        getMockEndpoint("mock:accounting").expectedMessageCount(1);
        getMockEndpoint("mock:production").expectedMessageCount(1);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // load file orders from src/data into the JMS queue
                from("file:src/data_multicast?noop=true").to("jms:incomingOrders");

                // content-based router
                from("jms:incomingOrders")
                        .choice()
                            .when(header("CamelFileName").endsWith(".xml"))
                                .to("jms:xmlOrders")
                            .when(header("CamelFileName").regex("^.*\\.(csv|csl)"))
                                .to("jms:csvOrders")
                            .otherwise()
                                .to("jms:badOrders");

                from("jms:xmlOrders")
                        .multicast()
                            .to("jms:accounting", "jms:production");

                from("jms:accounting")
                        .log("Accounting received order: ${header.CamelFileName}")
                        .to("mock:accounting");

                from("jms:production")
                        .log("Production received order: ${header.CamelFileName}")
                        .to("mock:production");
            }
        };
    }
}
