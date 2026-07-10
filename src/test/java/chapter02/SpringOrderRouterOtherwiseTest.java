package chapter02;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOrderRouterOtherwiseTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("SpringOrderRouterOtherwiseTest.xml");
    }

    @Test
    public void testPlacingOrders() throws Exception {
        this.getMockEndpoint("mock:xml").expectedMessageCount(1);
        this.getMockEndpoint("mock:csv").expectedMessageCount(2);
        this.getMockEndpoint("mock:bad").expectedMessageCount(1);

        this.assertMockEndpointsSatisfied();
    }
}
