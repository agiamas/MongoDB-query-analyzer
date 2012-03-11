package com.upstreamsystems.ucs.mongo.query.optimizer;

import org.apache.log4j.Logger;

import com.deftlabs.cursor.mongo.TailableCursorImpl;
import com.deftlabs.cursor.mongo.TailableCursorOptions;
import com.mongodb.DBObject;


/**
 * 
 *
 */
public class QueryAnalyzer 
{
	private static final Logger qlogger = Logger.getLogger("com.upstreamsystems.ucs.mongo.query.optimizer.QueryLogger");
	private static final Logger ulogger = Logger.getLogger("com.upstreamsystems.ucs.mongo.query.optimizer.UpdateLogger");
	private static final Logger ilogger = Logger.getLogger("com.upstreamsystems.ucs.mongo.query.optimizer.InsertLogger");
    // slow query threshold in msec
    private static final Integer SLOW_QUERY_THRESHOLD = 2000;
    // response length threshold in Bytes
    private static final Integer RESPONSE_LENGTH_THRESHOLD = 1024;

	public static void main( String[] args ) throws InterruptedException
    {
		String CONNECTED_USER; 
	    String MONGO_DATABASE_URL; 
        if(args[0]==null || args[1]==null) {
        	System.out.println("Usage: java QueryAnalyzer mongodbUrl userToConnectTo");
        	System.exit(-1);
        } 
		
        MONGO_DATABASE_URL = args[0];
        CONNECTED_USER = args[1];
        
        TailableCursorOptions tco = new TailableCursorOptions(MONGO_DATABASE_URL, CONNECTED_USER, "system.profile");
        TailableCursorImpl tci = new TailableCursorImpl(tco);
        tci.start();
        while(tci.isRunning()) {
        	DBObject dbo = tci.nextDoc();
        	Thread.sleep(100);
        	if(dbo.get("op").equals("query") && ((String)dbo.get("ns")).startsWith(CONNECTED_USER)) {
        		// all queries for this user
        		
        		// nscanned > 300% of nreturned
        		if(((Integer)dbo.get("nscanned")) > 3*((Integer)dbo.get("nreturned")) && (getMillis(dbo))>10) {
        			qlogger.debug("Warning nscanned:" + dbo.get("nscanned") + " nreturned:" + dbo.get("nreturned") + " query:" + printQueryFromObject(dbo) + " millis:" + getMillis(dbo));
        		}
        		// millis greater than threshold
        		if(getMillis(dbo) > SLOW_QUERY_THRESHOLD) {
        			qlogger.debug("Warning query slower than threshold:" + SLOW_QUERY_THRESHOLD + " query:" + printQueryFromObject(dbo));
        		}
        		
        		//reslen greater than threshold (maybe we shouldn't return so much data?)
        		if(dbo.get("reslen")!=null && (Integer)dbo.get("reslen") > RESPONSE_LENGTH_THRESHOLD) {
        			qlogger.debug("Warning response length greater than " + RESPONSE_LENGTH_THRESHOLD + " bytes for query:" + printQueryFromObject(dbo));
        		}
        	}
        	
        	if(dbo.get("op").equals("update") && ((String)dbo.get("ns")).startsWith(CONNECTED_USER)) {
        		// all updates
        		
        		// get fastmod , fastmodinsert (good)
        		
        		//get moved (bad, means we are not padding)
        		if(dbo.get("moved")!=null) {
        			ulogger.debug("Warning moved document from query:" + printQueryFromObject(dbo) + " in " + getMillis(dbo) + " millis");
        		}
        		
        	}
        	
        	
        	if(dbo.get("op").equals("insert") && ((String)dbo.get("ns")).startsWith(CONNECTED_USER)) {
        		// all inserts
        		if(((Integer)dbo.get("keyUpdates")) > 0 ) {
        			ilogger.debug("keyUpdates:" + dbo.get("keyUpdates") + " in millis:" + dbo.get("millis") + " " + dbo.toString());
        		}
        	}   
        }
    }
	
	private static Integer getMillis(DBObject dbo) {
		return (Integer)dbo.get("millis");
	}

	static String printQueryFromObject(DBObject dbo) {
		return ((DBObject)dbo.get("query")).toString();
	}
}
