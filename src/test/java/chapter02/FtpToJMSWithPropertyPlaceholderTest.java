package chapter02;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.jms.ConnectionFactory;

public class FtpToJMSWithPropertyPlaceholderTest extends CamelTestSupport {
    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        PropertiesComponent prop = camelContext.getComponent("properties", PropertiesComponent.class);
        prop.setLocation("classpath:rider-test.properties");
        return camelContext;
    }

    @Test
    public void testPlacingOrders() throws Exception {
        getMockEndpoint("mock:incomingOrders").expectedMessageCount(1);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:src/data?noop=true")
                        .to("jms:{{myDest}}");

                from("jms:incomingOrders")
                        .to("mock:incomingOrders");
            }
        };
    }
}
