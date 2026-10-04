package chapter03;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContentEnricherTest extends CamelSpringTestSupport {
    private static final String ORIGINAL = "1001,2026-10-03,C001";
    private static final String ADDITIONAL = "1002,2026-10-03,C002";

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("chapter03/SpringContentEnricherTest.xml");
    }

    @Test
    public void testEnrich() throws Exception {
        this.getMockEndpoint("mock:enrich").expectedBodiesReceived(ORIGINAL + "\n" + ADDITIONAL);
        this.template.sendBody("direct:enrich", ORIGINAL);
        this.assertMockEndpointsSatisfied();
    }

    @Test
    public void testPollEnrich() throws Exception {
        this.getMockEndpoint("mock:pollEnrich").expectedBodiesReceived(ORIGINAL + "\n" + ADDITIONAL);
        this.template.sendBody("seda:additionalOrders", ADDITIONAL);
        this.template.sendBody("direct:pollEnrich", ORIGINAL);
        this.assertMockEndpointsSatisfied();
    }

    @Test
    public void testPollEnrichWithoutAdditionalOrders() throws Exception {
        this.getMockEndpoint("mock:pollEnrich").expectedBodiesReceived(ORIGINAL);
        this.template.sendBody("direct:pollEnrich", ORIGINAL);
        this.assertMockEndpointsSatisfied();
    }
}
