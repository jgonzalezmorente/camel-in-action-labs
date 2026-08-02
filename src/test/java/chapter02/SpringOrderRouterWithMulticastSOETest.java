package chapter02;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOrderRouterWithMulticastSOETest extends CamelSpringTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("activemq-data");
        super.setUp();
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("./SpringOrderRouterWithMulticastSOETest.xml");
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
}