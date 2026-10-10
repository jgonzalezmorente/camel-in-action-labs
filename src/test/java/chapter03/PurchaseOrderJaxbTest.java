package chapter03;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import chapter03.model.PurchaseOrder;

public class PurchaseOrderJaxbTest extends CamelTestSupport {

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
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                // this.getContext().setTracing(true);
                JaxbDataFormat jaxb = new JaxbDataFormat();
                jaxb.setContextPath("chapter03.model");
                from("direct:order")
                        .routeId("serializeOrder")
                        .log("[MARSHAL] Antes: tipo=${body.class.name}, contenido=${body}")
                        .marshal(jaxb)
                        .log("[MARSHAL] Después: tipo=${body.class.name}, contenido=${body}")
                        .to("seda:queue:order");

                from("seda:queue:order")
                        .routeId("deserializeOrder")
                        .log("[UNMARSHAL] Antes: tipo=${body.class.name}, contenido=${body}")
                        .unmarshal(jaxb)
                        .log("[UNMARSHAL] Después: tipo=${body.class.name}, contenido=${body}")
                        .to("mock:order");
            }
        };
    }

}
