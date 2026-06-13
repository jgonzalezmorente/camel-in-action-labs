package chapter01;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FileCopierWithCamel {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("file:data/inbox?noop=true&delay=2000")
                        .to("file:data/outbox");
            }
        });

        try {
            context.start();

            System.out.println("Camel arrancado. Copia ficheros en data/inbox.");
            System.out.println("Pulsa Enter para parar...");
            System.in.read();
        } finally {
            context.stop();
        }
    }
}
