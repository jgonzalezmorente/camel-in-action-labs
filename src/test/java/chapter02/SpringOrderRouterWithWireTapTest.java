package chapter02;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOrderRouterWithWireTapTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("SpringOrderRouterWithWireTapTest.xml");
    }

    @Test
    public void testPlacingOrders() throws Exception {
        this.getMockEndpoint("mock:wiretap").expectedMessageCount(1);
        this.getMockEndpoint("mock:xml").expectedMessageCount(1);
        this.getMockEndpoint("mock:csvOrders").expectedMessageCount(0);
        this.assertMockEndpointsSatisfied();
    }
}
