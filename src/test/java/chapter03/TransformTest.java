package chapter03;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TransformTest extends CamelTestSupport {

    @Test
    public void testTransform() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello<br/>How are you?");

        template.sendBody("direct:start", "Hello\nHow are you?");

        this.assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                from("direct:start")
                        .transform(this.body().regexReplaceAll("\n", "<br/>"))
                        .to("mock:result");
            }
        };
    }


}
