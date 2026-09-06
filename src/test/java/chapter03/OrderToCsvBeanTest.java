package chapter03;

import chapter03.beans.OrderToCsvBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.File;

public class OrderToCsvBeanTest extends CamelTestSupport {

    @Test
    public void testOrderToCsvBean() throws Exception {
        String inhouse = "0000005555000001144120091209  2319@1108";
        this.template.sendBodyAndHeader("direct:start", inhouse, "Date", "20091209");

        File file = new File("target/orders/received/report-20091209.csv");
        assertTrue("File should exist", file.exists());

        String body = this.context.getTypeConverter().convertTo(String.class, file);
        assertEquals("0000005555,20091209,0000011441,2319,1108", body);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .bean(new OrderToCsvBean())
                        .to("file://target/orders/received?fileName=report-${header.Date}.csv");
            }
        };
    }
}
