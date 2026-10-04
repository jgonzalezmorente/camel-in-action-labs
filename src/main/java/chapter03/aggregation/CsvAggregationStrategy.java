package chapter03.aggregation;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CsvAggregationStrategy implements AggregationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(CsvAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String originalCsv = oldExchange.getIn().getBody(String.class);
        LOG.info("[AGGREGATION] Original: {}", originalCsv);
        if (newExchange == null) {
            LOG.info("[AGGREGATION] Sin datos adicionales; se conserva el original.");
            return oldExchange;
        }
        String additionalCsv = newExchange.getIn().getBody(String.class);
        LOG.info("[AGGREGATION] Datos adicionales: {}", additionalCsv);
        oldExchange.getIn().setBody(originalCsv + "\n" + additionalCsv);
        LOG.info("[AGGREGATION] Resultado:\n{}", oldExchange.getIn().getBody());
        return oldExchange;
    }
}
