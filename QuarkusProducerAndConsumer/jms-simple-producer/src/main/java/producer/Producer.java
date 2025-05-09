package producer;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.MDC;

import java.util.UUID;

public class Producer extends RouteBuilder {

/*    @Override
    public void configure() throws Exception {
        // Produce a message to testqueue every 5 seconds
        from("timer:testqueueProducer?period=5000")
                .process(exchange -> {
                    String message = "TestQueue Message: " + UUID.randomUUID();
                    exchange.getMessage().setBody(message);

                    // Add custom MDC values
                    MDC.put("zoo.service", "testqueue-service");
                    MDC.put("zoo.tracing_id", UUID.randomUUID().toString());
                    MDC.put("zoo.company", "TestCompany");
                    MDC.put("zoo.route", "testqueueProducer");
                })
                .log("Sending message to testqueue: ${body}")
                .to("jms:queue:testqueue")
                .process(exchange -> MDC.clear()); // Clear MDC after sending

        // Produce a message to testqueue1 every 5 seconds
        from("timer:testqueue1Producer?period=5000")
                .process(exchange -> {
                    String message = "TestQueue1 Message: " + UUID.randomUUID();
                    exchange.getMessage().setBody(message);

                    // Add custom MDC values
                    MDC.put("zoo.service", UUID.randomUUID().toString());
                    MDC.put("zoo.tracing_id", UUID.randomUUID().toString());
                    MDC.put("zoo.company", UUID.randomUUID().toString());
                    MDC.put("zoo.route", UUID.randomUUID().toString());
                })
                .log("Sending message to testqueue1: ${body}")
                .to("jms:queue:testqueue1")
                .process(exchange -> MDC.clear()); // Clear MDC after sending
    }*/


    @Override
    public void configure() throws Exception {
        // Produce a message to testqueue every 5 seconds
        from("timer:testqueueProducer?period=5000")
                .process(exchange -> {
                    String message = "TestQueue Message: " + UUID.randomUUID();
                    exchange.getMessage().setBody(message);

                    // Add custom MDC values
                    String service = UUID.randomUUID().toString();
                    String tracingId = UUID.randomUUID().toString();
                    String company = UUID.randomUUID().toString();
                    String route = UUID.randomUUID().toString();

                    MDC.put("zoo.service", service);
                    MDC.put("zoo.tracing_id", tracingId);
                    MDC.put("zoo.company", company);
                    MDC.put("zoo.route", route);

                    // Set headers for transmission
                    exchange.getMessage().setHeader("zoo.service", service);
                    exchange.getMessage().setHeader("zoo.tracing_id", tracingId);
                    exchange.getMessage().setHeader("zoo.company", company);
                    exchange.getMessage().setHeader("zoo.route", route);

                    log.info("MDC Values - Service: {}, Tracing ID: {}, Company: {}, Route: {}",
                            service, tracingId, company, route);
                })


                .log("Sending message to testqueue: ${body}")
                .to("jms:queue:testqueue")
                .process(exchange -> MDC.clear()); // Clear MDC after sending

        // Produce a message to testqueue1 every 5 seconds
        //todo: change to send more messages
        from("timer:testqueue1Producer?period=5000")
                .process(exchange -> {
                    String message = "TestQueue1 Message: " + UUID.randomUUID();
                    exchange.getMessage().setBody(message);

                    // Add custom MDC values
                    String service = UUID.randomUUID().toString();
                    String tracingId = UUID.randomUUID().toString();
                    String company = UUID.randomUUID().toString();
                    String route = UUID.randomUUID().toString();

                    MDC.put("zoo.service", service);
                    MDC.put("zoo.tracing_id", tracingId);
                    MDC.put("zoo.company", company);
                    MDC.put("zoo.route", route);

                    // Set headers for transmission
                    exchange.getMessage().setHeader("zoo.service", service);
                    exchange.getMessage().setHeader("zoo.tracing_id", tracingId);
                    exchange.getMessage().setHeader("zoo.company", company);
                    exchange.getMessage().setHeader("zoo.route", route);
                    log.info("MDC Values - Service: {}, Tracing ID: {}, Company: {}, Route: {}",
                            service, tracingId, company, route);
                })
                .log("Sending message to testqueue1: ${body}")
                .to("jms:queue:testqueue1")
                .process(exchange -> MDC.clear()); // Clear MDC after sending
    }

}
