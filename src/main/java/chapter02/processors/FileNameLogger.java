package chapter02.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class FileNameLogger implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
        System.out.println("Processing file: " + fileName + "!");
    }
}
