package consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.UuidGenerator;
import org.slf4j.MDC;

public class Consumer extends RouteBuilder {



    @Override
    public void configure() throws Exception {

        from("jms:queue:testqueue")
                .process(exchange -> {
                    // Extract headers and populate MDC
                    String service = (String) exchange.getIn().getHeader("zoo.service");
                    String tracingId = (String) exchange.getIn().getHeader("zoo.tracing_id");
                    String company = (String) exchange.getIn().getHeader("zoo.company");
                    String route = (String) exchange.getIn().getHeader("zoo.route");

                    MDC.put("zoo.service", service);
                    MDC.put("zoo.tracing_id", tracingId);
                    MDC.put("zoo.company", company);
                    MDC.put("zoo.route", route);

                    // Set headers for logging (optional)
                    exchange.getIn().setHeader("MDC_zoo_service", service);
                    exchange.getIn().setHeader("MDC_zoo_tracing_id", tracingId);
                    exchange.getIn().setHeader("MDC_zoo_company", company);
                    exchange.getIn().setHeader("MDC_zoo_route", route);
                })
                .log("Received message from ${header.MDC_zoo_service} | Tracing ID: ${header.MDC_zoo_tracing_id} | " +
                        "Company: ${header.MDC_zoo_company} | Route: ${header.MDC_zoo_route} | Body: ${body}")
                .process(exchange -> MDC.clear()); // Clear MDC after processing

        // Consumer for testqueue1
//        from("jms:queue:testqueue1")
        from("jms:queue:testqueue1?concurrentConsumers=10")
                .process(exchange -> {
                    // Extract headers and populate MDC
                    String service = (String) exchange.getIn().getHeader("zoo.service");
                    String tracingId = (String) exchange.getIn().getHeader("zoo.tracing_id");
                    String company = (String) exchange.getIn().getHeader("zoo.company");
                    String route = (String) exchange.getIn().getHeader("zoo.route");

                    MDC.put("zoo.service", service);
                    MDC.put("zoo.tracing_id", tracingId);
                    MDC.put("zoo.company", company);
                    MDC.put("zoo.route", route);

                    // Set headers for logging (optional)
                    exchange.getIn().setHeader("MDC_zoo_service", service);
                    exchange.getIn().setHeader("MDC_zoo_tracing_id", tracingId);
                    exchange.getIn().setHeader("MDC_zoo_company", company);
                    exchange.getIn().setHeader("MDC_zoo_route", route);
                })
                .log("Received message from ${header.MDC_zoo_service} | Tracing ID: ${header.MDC_zoo_tracing_id} | " +
                        "Company: ${header.MDC_zoo_company} | Route: ${header.MDC_zoo_route} | Body: ${body}")


                .process(exchange -> MDC.clear()); // Clear MDC after processing
    }


}