package chapter03;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import chapter03.model.PurchaseOrderXStream;

public class PurchaseOrderXStreamTest extends CamelTestSupport {

    @Test
    public void testXStream() throws Exception {
        MockEndpoint mock = this.getMockEndpoint("mock:order");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(PurchaseOrderXStream.class);

        PurchaseOrderXStream order = new PurchaseOrderXStream();
        order.setName("Camel in Action");
        order.setPrice(6999);
        order.setAmount(1);

        this.template.sendBody("direct:order", order);

        assertMockEndpointsSatisfied();

        PurchaseOrderXStream received = (PurchaseOrderXStream) mock.getExchanges().get(0).getIn().getBody();

        assertEquals(order.getName(), received.getName());
        assertEquals(order.getPrice(), received.getPrice(), 0.0);
        assertEquals(order.getAmount(), received.getAmount(), 0.0);
        assertNotSame(order, received);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {
            @Override
            public void configure() {
                XStreamDataFormat xstream = new XStreamDataFormat();

                // Autoriza la reconstrucción de esta clase.
                xstream.setPermissions(PurchaseOrderXStream.class.getName());

                from("direct:order")
                        .routeId("serializeOrder")
                        .log("[MARSHAL] Antes: tipo=${body.class.name}")
                        .marshal(xstream)
                        .log("[MARSHAL] Después: tipo=${body.class.name}, XML=${body}")
                        .to("seda:orders");

                from("seda:orders")
                        .routeId("deserializeOrder")
                        .unmarshal(xstream)
                        .log("[UNMARSHAL] Después: tipo=${body.class.name}")
                        .to("mock:order");
            }
        };
    }
}
