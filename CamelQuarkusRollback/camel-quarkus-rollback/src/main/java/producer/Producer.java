package producer;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

public class Producer extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .log("Exception occurred while processing message: ${exception.message}")
                .markRollbackOnly()
                .handled(false)
                .redeliveryDelay(1000)
                .maximumRedeliveries(5)
        ;

        rest("/api")
                .post("/person")
                .id("createPerson")
                .consumes("application/json")
                .param()
                .name("body")
                .type(RestParamType.body)
                .required(true)
                .endParam()
                .to("direct:createPerson");

        from("direct:createPerson")
                .routeId("create-person-route")
                .wireTap("direct:sendToQueue")
                .setBody(simple("${null}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("202"))
        ;
        from("direct:sendToQueue")
                .routeId("amqp-producer-partnerrolle-route")
                .setExchangePattern(ExchangePattern.InOnly)
                //.removeHeaders("*")
                .to("amqp:queue:demo.exampleQueue.v1")
        ;

        from("jms:queue:demo.exampleQueue.v1?transacted=true")
                .transacted()
                .routeId("amq-endpoint-route")
                .setExchangePattern(ExchangePattern.InOnly)
                .log("Received message from AMQP queue: ${body}")
                .log("Redelivery attempt: ${header.JMSXDeliveryCount}")
                .process(exchange -> {
                    throw new RuntimeException("Simulated processing failure for rollback");
                });

    }
}
