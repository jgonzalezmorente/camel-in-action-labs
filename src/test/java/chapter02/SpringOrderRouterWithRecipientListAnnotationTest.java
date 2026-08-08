package chapter02;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOrderRouterWithRecipientListAnnotationTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("SpringOrderRouterWithRecipientListAnnotationTest.xml");
    }

    @Test
    public void testPlacingOrders() throws Exception {
        this.getMockEndpoint("mock:accounting").expectedMessageCount(2);
        this.getMockEndpoint("mock:production").expectedMessageCount(1);
        this.assertMockEndpointsSatisfied();
    }
}
