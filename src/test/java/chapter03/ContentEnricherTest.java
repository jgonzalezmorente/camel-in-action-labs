package chapter03;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ContentEnricherTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ContentEnricherTest.class);

    private static final String ORIGINAL = "1001,2026-10-03,C001";
    private static final String ADDITIONAL = "1002,2026-10-03,C002";

    private static class CsvAggregationStrategy implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            String originialCsv = oldExchange.getIn().getBody(String.class);
            LOG.info("[AGREGACIÓN] Cuerpo original: {}", originialCsv);

            if (newExchange == null) {
                LOG.info(
                        "[AGREGACIÓN] Sin datos adicionales. "
                        + "Se conserva el mensaje original."
                );
                return oldExchange;
            }

            String additionalCsv = newExchange.getIn().getBody(String.class);
            LOG.info("[AGREGACIÓN] Datos adicionales: {}", additionalCsv);

            String combinedCsv = originialCsv + "\n" + additionalCsv;
            oldExchange.getIn().setBody(combinedCsv);

            LOG.info("[AGREGACIÓN] Resultado: \n{}", combinedCsv);
            return oldExchange;
        }
    }

    @Test
    public void testEnrich() throws Exception {
        LOG.info("===== ENRICH: INVOCACIÓN Y RESPUESTA =====");

        this.getMockEndpoint("mock:enrich")
                .expectedBodiesReceived(ORIGINAL + "\n" + ADDITIONAL);

        this.template.sendBody("direct:enrich", ORIGINAL);

        this.assertMockEndpointsSatisfied();
    }

    @Test
    public void testPollEnrich() throws Exception {
        LOG.info("===== POLL ENRICH: MENSAJE DISPONIBLE =====");

        this.getMockEndpoint("mock:pollEnrich")
                .expectedBodiesReceived(ORIGINAL + "\n" + ADDITIONAL);

        LOG.info("[PRUEBA] Depositando en SEDA: {}", ADDITIONAL);
        this.template.sendBody("seda:additionalOrders", ADDITIONAL);

        LOG.info("[PRUEBA] Enviando el mensaje original: {}", ORIGINAL);
        this.template.sendBody("direct:pollEnrich", ORIGINAL);

        this.assertMockEndpointsSatisfied();
    }

    @Test
    public void testPollEnrichWithoutAdditionalOrders() throws Exception {
        LOG.info("===== POLL ENRICH: COLA VACÍA =====");

        this.getMockEndpoint("mock:pollEnrich")
                .expectedBodiesReceived(ORIGINAL);

        LOG.info("[PRUEBA] La cola SEDA está vacía.");
        this.template.sendBody("direct:pollEnrich", ORIGINAL);

        this.assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
//                context.setTracing(true);

                from("direct:enrich")
                        .routeId("enrich-orders")
                        .log("[ENRICH] Antes de consultar: ${body}")
                        .enrich(
                                "direct:additionalOrders",
                                new CsvAggregationStrategy()
                        )
                        .log("[ENRICH] Después de agregar:\n${body}")
                        .to("mock:enrich");

                from("direct:additionalOrders")
                        .routeId("additional-orders-service")
                        .log("[SERVICIO] Petición recibida: ${body}")
                        .setBody(constant(ADDITIONAL))
                        .log("[SERVICIO] Respuesta enviada: ${body}");

                from("direct:pollEnrich")
                        .routeId("poll-enrich-orders")
                        .log("[POLL ENRICH] Mensaje original: ${body}")
                        .log(
                                "[POLL ENRICH] Intentando recoger un mensaje "
                                + "de SEDA sin esperar."
                        )
                        .pollEnrich(
                                "seda:additionalOrders",
                                0,
                                new CsvAggregationStrategy()
                        )
                        .log("[POLL ENRICH] Resultado:\n${body}")
                        .to("mock:pollEnrich");
            }
        };
    }
}
