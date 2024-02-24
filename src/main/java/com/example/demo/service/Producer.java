package com.example.demo.service;



import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.example.demo.model.Member;
import com.example.demo.model.Team;

@Service
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private static final String TOPIC = "teams";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaAvroTemplate;

	/** 
	 * Publish simple string values to topic
	 * 
	 */
    public void sendSimpleMessage(String key, String value) {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, value);
             future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                 @Override
                 public void onSuccess(SendResult<String, String> result) {
                     logger.info(String.format("Produced event to topic %s: key = %-10s value = %s", TOPIC, key, value));
                 }
                 @Override
                 public void onFailure(Throwable ex) {
                     ex.printStackTrace();
                 }
             });
    }

    /**
     *  Publishes Avro message where array type is a map
     */
    public void sendAvroMapMessage(Team team) {
    	
    	String key = team.getTeamId();
    	String teamAvroSchema = "{"
    			+ "  \"type\": \"record\","
    			+ "  \"namespace\": \"com.honer.message\","
    			+ "  \"name\": \"NestedMapTeam\","
    			+ "  \"fields\": ["
    			+ "    {"
    			+ "      \"name\": \"teamId\","
    			+ "      \"type\": \"string\""
    			+ "    },"
    			+ "    {"
    			+ "      \"name\": \"teamName\","
    			+ "      \"type\": \"string\""
    			+ "    },"
    			+ "    {"
    			+ "      \"name\": \"members\","
    			+ "      \"type\": {"
    			+ "	    \"type\" : \"array\","
    			+ "		\"items\": { "
    			+ "		    \"type\": \"map\","
    			+ "		    \"values\": \"string\""
    			+ "		} "
    			+ "	  }"
    			+ "    }"
    			+ "  ]"
    			+ "}";
    	
    	Schema.Parser parser = new Schema.Parser();
    	Schema schema = parser.parse(teamAvroSchema);
    	
    	GenericRecord avroRecord = new GenericData.Record(schema);
    	
    	avroRecord.put("teamId", team.getTeamId());
    	avroRecord.put("teamName", team.getTeamName());
    	avroRecord.put("members", team.getMembers());
    	
    	ProducerRecord<String, Object> record = new ProducerRecord<>("member-as-map", key, avroRecord);
    	publishRecord(key, record);
    }
    
    /**
     * Publish Complex (Array) type to topic
     * 
     */
    public void sendComplexArrayMessage(Team team) {
    	
    	String key = team.getTeamId();
    	String teamAvroSchema = "{"
    			+ "  \"type\": \"record\", "
    			+ "  \"namespace\": \"com.honer.message\", "
    			+ "  \"name\": \"NestedTeam\", "
    			+ "  \"fields\": [ "
    			+ "    { "
    			+ "      \"name\": \"teamId\", "
    			+ "      \"type\": \"string\" "
    			+ "    }, "
    			+ "    { "
    			+ "      \"name\": \"teamName\", "
    			+ "      \"type\": \"string\" "
    			+ "    }, "
    			+ "    { "
    			+ "      \"name\": \"members\", "
    			+ "      \"type\": { "
    			+ "	    \"type\" : \"array\", "
    			+ "		\"items\": {  "
    			+ "		    \"type\": \"record\", "
    			+ "		    \"name\": \"members\", "
    			+ "	        \"fields\": [ "
    			+ "                { "
    			+ "                   \"name\": \"memberId\", "
    			+ "                   \"type\": \"string\" "
    			+ "                }, "
    			+ "                { "
    			+ "                   \"name\": \"memberName\", "
    			+ "                   \"type\": \"string\" "
    			+ "                } "
    			+ "            ]"
    			+ "		} "
    			+ "	  }"
    			+ "    }"
    			+ "  ]"
    			+ "}";
    	
    	Schema.Parser parser = new Schema.Parser();
    	Schema teamSchema = parser.parse(teamAvroSchema);    	
    	Schema memberSchema = teamSchema.getField("members").schema().getElementType();

    	
    	GenericRecord avroRecord = new GenericData.Record(teamSchema);
    	
    	avroRecord.put("teamId", team.getTeamId());
    	avroRecord.put("teamName", team.getTeamName());

    	List<GenericRecord> memberRecordList = new ArrayList<>();
    	
    	List<LinkedHashMap<String,String>> members = team.getMembers();
    	
    	for(LinkedHashMap<String,String> memberMap: members) {    		
    		
    		GenericRecord avroMemberRecord = new GenericData.Record(memberSchema);    		
    		Set<String> memberKeys = memberMap.keySet();            
    		for (String memberKey : memberKeys) {
            	avroMemberRecord.put(memberKey, memberMap.get(memberKey) );            	
            }    		
    		memberRecordList.add(avroMemberRecord);
    	}             	
    	avroRecord.put("members", memberRecordList);
    	
    	ProducerRecord<String, Object> record = new ProducerRecord<>("team-members", key, avroRecord);        
    	publishRecord(key, record);
    	
    }
    
    
    /**
     * Publish Simple Avro message
     * 
     */
    public void sendSimpleAvroMessage(Team team) {
    	
    	
    	String key = team.getTeamId();
    	
    	String userSchema = "{"
    			+ "  \"name\": \"NestedTeam\","
    			+ "  \"namespace\": \"com.honer.message\","
    			+ "  \"type\": \"record\","
    			+ "  \"fields\": ["
    			+ "    {"
    			+ "      \"name\": \"teamId\","
    			+ "      \"type\": \"string\""
    			+ "    },"
    			+ "    {"
    			+ "      \"name\": \"teamName\","
    			+ "      \"type\": \"string\""
    			+ "    }"
    			+ "  ]"
    			+ "}";
    	
    	Schema.Parser parser = new Schema.Parser();
    	Schema schema = parser.parse(userSchema);
    	
    	GenericRecord avroRecord = new GenericData.Record(schema);
    	
    	avroRecord.put("teamId", team.getTeamId());
    	avroRecord.put("teamName", team.getTeamName());

    	ProducerRecord<String, Object> record = new ProducerRecord<>("nested-teams", key, avroRecord);
    
    	publishRecord(key, record);
      
    }


	private void publishRecord(String key, ProducerRecord<String, Object> record) {
		ListenableFuture<SendResult<String, Object>> future = kafkaAvroTemplate.send(record);        
    	future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                logger.info(String.format("Produced event to topic %s: key = %-10s", TOPIC, key));
            }
            @Override
            public void onFailure(Throwable ex) {
                ex.printStackTrace();
            }
        });
	}

	
	
}