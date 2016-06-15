package com.test.route.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;

import javax.jms.ConnectionFactory;
import java.io.File;

/**
 * Created by Praveen on 14/06/2016.
 */
public class TestRouteMain {
    static String FILE_PATH = "CamelFilePath";

    public static void main(String args[]){
        CamelContext camelContext = new DefaultCamelContext();
        try {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
        camelContext.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        camelContext.addRoutes(new RouteBuilder(){
            @Override
            public void configure(){
                from("file:data?noop=true")
                        .idempotentConsumer(header(FILE_PATH), FileIdempotentRepository
                        .fileIdempotentRepository(new File("data/processed", "/processed.dat")))
                .to("jms:queue:activemq/queue/TestQueue")
                        .to("file:/tmp/out");
                /*from("jms:fileQueue").process(new Processor(){
                    public void process(Exchange exchange) throws Exception{
                        System.out.println("Received File: "
                                +exchange.getIn().getHeader("CamelFileName"));
                    }
                });*/


            }
        });
            camelContext.start();
            Thread.sleep(9000);
            camelContext.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
