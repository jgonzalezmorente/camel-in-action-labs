package chapter03;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CustomExpressionTransformTest extends CamelTestSupport {
    @Test
    public void testTransform() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<body>Hello<br/>How are you?</body>");
        this.template.sendBody("direct:start", "Hello\nHow are you?");
        this.assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);
                from("direct:start")
                        .transform(new Expression() {
                            @Override
                            public <T> T evaluate(Exchange exchange, Class<T> type) {
                                String body = exchange.getIn().getBody(String.class);
                                String html = "<body>" + body.replace("\n", "<br/>") + "</body>";
                                return exchange.getContext().getTypeConverter()
                                        .convertTo(type, exchange, html);
                            }
                        })
                        .to("mock:result");
            }
        };
    }
}
