package chapter03;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import chapter03.model.PurchaseOrder;

public class SpringPurchaseOrderJaxbTest extends CamelSpringTestSupport {

    @Test
    public void testJaxb() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:order");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(PurchaseOrder.class);

        PurchaseOrder order = new PurchaseOrder();
        order.setName("Camel in Action");
        order.setPrice(6999);
        order.setAmount(1);

        this.template.sendBody("direct:order", order);

        this.assertMockEndpointsSatisfied();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("chapter03/SpringOrderJaxbTest.xml");
    }

}
