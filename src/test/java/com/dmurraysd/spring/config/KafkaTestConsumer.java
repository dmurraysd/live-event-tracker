package com.dmurraysd.spring.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Import({KafkaTestConfig.class})
public class KafkaTestConsumer {

    private final ConcurrentLinkedQueue<ConsumerRecord<String, String>> consumerRecords = new ConcurrentLinkedQueue<>();


    @KafkaListener(
            topics = {"${kafka.topic.outbound}"},
            containerFactory = "testKafkaListenerContainerFactory")
    private void receive(ConsumerRecord<String, String> consumerRecord) {
        consumerRecords.add(consumerRecord);
    }

    public boolean hasRecords() {
        return !consumerRecords.isEmpty();
    }

    public ConsumerRecord<String, String> getLastMessage() {
        return getAllRecords().getLast();
    }

    public List<ConsumerRecord<String, String>> getAllRecords() {
        return new ArrayList<>(consumerRecords);
    }

    public void clearQueue() {
        consumerRecords.clear();
    }
}
