package com.upstreamsystems.ucs.mongo.dao;

import java.net.UnknownHostException;
import java.util.Date;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Entity;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@Entity("system.profile")
public class ProfileObject {

	Date ts;
	String op;
	String ns;
	DBObject command;
	Integer nscanned;
	Integer nreturned;
	Integer responseLength;
	Integer millis;
	String client;
	

	public Date getTs() {
		return ts;
	}


	public void setTs(Date ts) {
		this.ts = ts;
	}


	public String getOp() {
		return op;
	}


	public void setOp(String op) {
		this.op = op;
	}


	public String getNs() {
		return ns;
	}


	public void setNs(String ns) {
		this.ns = ns;
	}


	public DBObject getCommand() {
		return command;
	}


	public void setCommand(DBObject command) {
		this.command = command;
	}


	public Integer getNscanned() {
		return nscanned;
	}


	public void setNscanned(Integer nscanned) {
		this.nscanned = nscanned;
	}


	public Integer getNreturned() {
		return nreturned;
	}


	public void setNreturned(Integer nreturned) {
		this.nreturned = nreturned;
	}


	public Integer getResponseLength() {
		return responseLength;
	}


	public void setResponseLength(Integer responseLength) {
		this.responseLength = responseLength;
	}


	public Integer getMillis() {
		return millis;
	}


	public void setMillis(Integer millis) {
		this.millis = millis;
	}


	public String getClient() {
		return client;
	}


	public void setClient(String client) {
		this.client = client;
	}


	Datastore getDs() throws UnknownHostException, MongoException {
		Mongo mongo = new Mongo("localhost", 27031);
		Morphia morphia = new Morphia();
		morphia.map(ProfileObject.class);
		Datastore ds = morphia.createDatastore(mongo, "test");
		return ds;
	}
	
	
}
