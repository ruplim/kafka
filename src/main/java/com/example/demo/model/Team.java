package com.example.demo.model;

import java.util.LinkedHashMap;
import java.util.List;

public class Team {
	
	private String teamId;
	private String teamName;
	private List<LinkedHashMap<String,String>> members;
	
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public List<LinkedHashMap<String, String>> getMembers() {
		return members;
	}
	public void setMembers(List<LinkedHashMap<String, String>> members) {
		this.members = members;
	}
	
	

}
