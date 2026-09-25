package chapter03;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTransformScriptTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("chapter03/SpringTransformScriptTest.xml");
    }

    @Test
    public void testTransform() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello Camel how are you?");
        this.template.sendBody("direct:start", "Camel");
        this.assertMockEndpointsSatisfied();
    }
}
