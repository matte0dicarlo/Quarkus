package producer;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Producer extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    @Override
    public void configure() {
        from("timer:testqueueProducer?period=5000")
                .routeId("testqueueProducerRoute")
                .process(exchange -> {
                    String message = "TestQueue Message: " + UUID.randomUUID();
                    exchange.getMessage().setBody(message);

                    // Generate headers for MDC enrichment
                    String service = UUID.randomUUID().toString();
                    String tracingId = UUID.randomUUID().toString();
                    String company = UUID.randomUUID().toString();
                    String route = UUID.randomUUID().toString();

                    exchange.getMessage().setHeader("zoo.service", service);
                    exchange.getMessage().setHeader("zoo.tracing_id", tracingId);
                    exchange.getMessage().setHeader("zoo.company", company);
                    exchange.getMessage().setHeader("zoo.route", route);

                    LOG.info("Set headers for MDC enrichment: Service={}, TracingID={}, Company={}, Route={}",
                            service, tracingId, company, route);
                })

                .log("Sending message to testqueue: ${body}")
                .to("jms:queue:testqueue")
        ;
    }
}
