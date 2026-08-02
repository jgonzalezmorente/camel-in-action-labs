package chapter02;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class OrderRouterWithMulticastSOETest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("activemq-data");
        super.setUp();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        // create CamelContext
        CamelContext camelContext = super.createCamelContext();

        // connect to embedded ActiveMQ JMS broker
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost");
        camelContext.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        return camelContext;
    }

    @Test
    public void testPlacingOrders() throws Exception {
        // Accounting empieza a procesar el mensaje
        getMockEndpoint("mock:accounting_before_exception")
                .expectedMessageCount(1);

        // Pero falla antes de llegar al final
        getMockEndpoint("mock:accounting")
                .expectedMessageCount(0);

        // stopOnException impide ejecutar el segundo destino
        getMockEndpoint("mock:production")
                .expectedMessageCount(0);

        // La excepción impide continuar después del multicast
        getMockEndpoint("mock:end")
                .expectedMessageCount(0);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // Carga los pedidos desde fichero y los envía a JMS
                from("file:src/data_multicast?noop=true")
                        .to("jms:incomingOrders");

                // Content-Based Router
                from("jms:incomingOrders")
                        .choice()
                            .when(header("CamelFileName").endsWith(".xml"))
                                .to("jms:xmlOrders")
                            .when(header("CamelFileName").regex("^.*\\.(csv|csl)$"))
                                .to("jms:csvOrders")
                            .otherwise()
                                .to("jms:badOrders");

                /*
                 * El pedido XML llega mediante JMS, pero los destinos
                 * del multicast son Direct para que el procesamiento
                 * sea síncrono.
                 */
                from("jms:xmlOrders")
                        .multicast()
                            .stopOnException()
                            .to("direct:accounting", "direct:production")
                        .end()
                        .to("mock:end");

                /*
                 * Direct es síncrono:
                 * la excepción vuelve inmediatamente al multicast.
                 */
                from("direct:accounting")
                        .to("mock:accounting_before_exception")
                        .throwException(Exception.class, "I failed!")
                        .log("Accounting received order: ${header.CamelFileName}")
                        .to("mock:accounting");

                from("direct:production")
                        .log("Production received order: ${header.CamelFileName}")
                        .to("mock:production");
            }
        };
    }
}
