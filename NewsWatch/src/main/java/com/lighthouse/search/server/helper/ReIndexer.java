package com.lighthouse.search.server.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReIndexer extends Thread implements Runnable {

	Logger logger = Logger.getLogger(ReIndexer.class.getName());

	public void run() {
		logger.log(Level.INFO, "In ReIndexer Thread Class : Run method......  ");
		try {
			while (true) {
			
					logger.log(Level.INFO, "In run() method : performing reindexing.........");
					SetupIndexHelper helper = new SetupIndexHelper();
					helper.indexErrornousRecords();
					logger.log(Level.INFO, "In run() method : reindexing task performed....");
					logger.log(Level.INFO, "In run method: now sleeping");
					Thread.sleep(86400000);
					//	Thread.sleep(120000);
					logger.log(Level.INFO, "In run method: reindexing thread woke up.....");
			}
		}
		catch (Exception e) {
			logger.log(Level.INFO, "In run method: Error : "+e.getMessage());
			e.printStackTrace();
		}
	}
}