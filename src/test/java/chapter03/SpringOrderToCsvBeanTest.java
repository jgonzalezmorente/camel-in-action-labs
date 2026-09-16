package chapter03;

import java.io.File;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOrderToCsvBeanTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("chapter03/SpringOrderToCsvBeanTest.xml");
    }

    @Test
    public void testOrderToCsvBean() throws Exception {
        String inhouse = "0000005555000001144120091209  2319@1108";
        this.template.sendBodyAndHeader("direct:start", inhouse, "Date", "20091209");

        File file = new File("target/orders/received/report-20091209-spring.csv");
        assertTrue("File should exist", file.exists());

        String body = context.getTypeConverter().convertTo(String.class, file);
        assertEquals("0000005555,20091209,0000011441,2319,1108", body);
    }
}
