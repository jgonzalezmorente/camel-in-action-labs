package chapter02.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import chapter02.processors.FileNameLogger;

@Component
public class FileToJMSRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:src/data?noop=true")
                .process(new FileNameLogger())
                .to("jms:incomingOrders");
    }
}
