package chapter03;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTransformMethodTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("chapter03/SpringTransformMethodTest.xml");
    }

    @Test
    public void testTransform() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<body>Hello<br/>How are you?</body>");

        this.template.sendBody("direct:start", "Hello\nHow are you?");
        this.assertMockEndpointsSatisfied();
    }
}
