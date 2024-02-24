package com.example.demo.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Member;
import com.example.demo.model.Team;
import com.example.demo.service.Producer;

@RestController
public class MessageController {
	
	@Autowired
	Producer producer;
	
	/** 
	 * Publish simple string values to topic
	 * 
	 */
	@GetMapping("/publishSimpleString")
	public String produce() {
		producer.sendSimpleMessage("GCB001", "001");
		return "done";
	}

	/**
	 *  Publish Simple Avro message
	 */
	@GetMapping("/publishSimpleAvro/{teamId}")
	public String produceAvro(@PathVariable String teamId) {
		
		Team team = new Team();		
		team.setTeamId(teamId);
		team.setTeamName("CB-Bots:"+ teamId);
		
		producer.sendSimpleAvroMessage(team);
		
		return "done";
	}
	
    /**
     * Publish Complex (Array) type to topic
     * 
     */
	@PostMapping("/team")
	public String process(@RequestBody Team team) {
		producer.sendComplexArrayMessage(team);
		return "done";
	}

    /**
     * Publish Complex (Array) type as Map to topic
     * 
     */
	@PostMapping("/teamAsMap")
	public String processAsMap(@RequestBody Team team) {
		producer.sendAvroMapMessage(team);
		return "done";
	}

}
