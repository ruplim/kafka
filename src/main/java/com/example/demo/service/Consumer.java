package com.example.demo.service;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
	
	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
	
	
	@KafkaListener(topics = "teams")
    public void read(ConsumerRecord<String, String> record){
        String key = record.key();
        String val = record.value();
        logger.info("Message received for key : " + key + " value : " + val);
    }
    
	
	@KafkaListener(topics = "nested-teams", groupId = "group_id_1", errorHandler = "TeamErrorHandler")
    public void readMessage(ConsumerRecord<String, GenericRecord> record){
        readTopicMessage(record);
    }
	
	@KafkaListener(topics = "team-members", groupId = "group_id_1", errorHandler = "TeamErrorHandler")
    public void readTeamMemberMessage(ConsumerRecord<String, GenericRecord> record){
        readTopicMessage(record);
    }

	@KafkaListener(topics = "member-as-map", groupId = "group_id_1", errorHandler = "TeamErrorHandler")
    public void readMemberAsMapMessage(ConsumerRecord<String, GenericRecord> record){
        readTopicMessage(record);
    }

	private void readTopicMessage(ConsumerRecord<String, GenericRecord> record) {
		try {
        	logger.info("reading message........");
			String key = record.key();
	        GenericRecord val = record.value();
	        logger.info("Message received for key : " + key + " value : " + val);
        } catch(Exception e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
        }
	}
}
